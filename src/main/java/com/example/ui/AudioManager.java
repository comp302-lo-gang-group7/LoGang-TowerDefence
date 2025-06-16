package com.example.ui;

import com.example.storage_manager.SettingsManager;
import com.example.storage_manager.SettingsManager.Settings;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Simple utility for playing background music and sound effects.
 */
public class AudioManager {
    private static MediaPlayer backgroundPlayer;
    private static final Map<String, AudioClip> clipCache = new HashMap<>();
    private static int musicVolume;
    private static int sfxVolume;

    static {
        reloadSettings();
    }

    /** Reload volume settings from {@link SettingsManager}. */
    public static void reloadSettings() {
        Settings s = SettingsManager.load();
        musicVolume = s.musicVolume;
        sfxVolume = s.sfxVolume;
        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(musicVolume / 100.0);
        }
    }

    private static AudioClip getClip(String path) {
        return clipCache.computeIfAbsent(path, p -> {
            URL url = AudioManager.class.getResource(p);
            return url == null ? null : new AudioClip(url.toExternalForm());
        });
    }

    /** Play a sound effect once respecting SFX volume. */
    public static void playSoundEffect(String path) {
        AudioClip clip = getClip(path);
        if (clip != null) {
            clip.setVolume(sfxVolume / 100.0);
            clip.play();
        }
    }

    /**
     * Play one clip chosen randomly from the supplied paths.
     */
    public static void playRandomSoundEffect(String... paths) {
        if (paths == null || paths.length == 0) return;
        int idx = new Random().nextInt(paths.length);
        playSoundEffect(paths[idx]);
    }

    /**
     * Start playing background music, replacing any currently playing track.
     * If {@code loop} is true, the track will repeat indefinitely.
     */
    public static void playBackgroundMusic(String path, boolean loop) {
        stopBackgroundMusic();
        URL url = AudioManager.class.getResource(path);
        if (url == null) return;
        Media media = new Media(url.toExternalForm());
        backgroundPlayer = new MediaPlayer(media);
        backgroundPlayer.setVolume(musicVolume / 100.0);
        if (loop) backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundPlayer.play();
    }

    /** Stop and dispose the currently playing background music, if any. */
    public static void stopBackgroundMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
            backgroundPlayer.dispose();
            backgroundPlayer = null;
        }
    }
}