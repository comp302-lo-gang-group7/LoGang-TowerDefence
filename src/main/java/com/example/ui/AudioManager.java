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
 * Manages audio playback for background music and sound effects.
 */
public class AudioManager {
    private static MediaPlayer backgroundPlayer;
    private static final Map<String, AudioClip> clipCache = new HashMap<>();
    private static int musicVolume;
    private static int sfxVolume;

    static {
        reloadSettings();
    }

    /**
     * Reloads audio settings from the {@link SettingsManager}.
     */
    public static void reloadSettings() {
        Settings s = SettingsManager.load();
        musicVolume = s.musicVolume;
        sfxVolume = s.sfxVolume;
        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(musicVolume / 100.0);
        }
    }

    /**
     * Retrieves an {@link AudioClip} from the specified file path.
     * If the clip is not already cached, it is loaded and cached.
     *
     * @param path the relative path to the audio file
     * @return the {@link AudioClip} object, or {@code null} if the file cannot be found
     */
    private static AudioClip getClip(String path) {
        return clipCache.computeIfAbsent(path, p -> {
            URL url = AudioManager.class.getResource(p);
            return url == null ? null : new AudioClip(url.toExternalForm());
        });
    }

    /**
     * Plays a sound effect from the specified file path.
     * The playback respects the current sound effects volume setting.
     *
     * @param path the relative path to the sound effect file
     */
    public static void playSoundEffect(String path) {
        AudioClip clip = getClip(path);
        if (clip != null) {
            clip.setVolume(sfxVolume / 100.0);
            clip.play();
        }
    }

    /**
     * Plays a random sound effect from the provided file paths.
     * The playback respects the current sound effects volume setting.
     *
     * @param paths an array of relative paths to sound effect files
     */
    public static void playRandomSoundEffect(String... paths) {
        if (paths == null || paths.length == 0) return;
        int idx = new Random().nextInt(paths.length);
        playSoundEffect(paths[idx]);
    }

    /**
     * Starts playing background music from the specified file path.
     * If a track is already playing, it is stopped and replaced.
     * The playback respects the current music volume setting.
     *
     * @param path the relative path to the background music file
     * @param loop {@code true} to loop the track indefinitely, {@code false} to play it once
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

    /**
     * Stops and disposes of the currently playing background music, if any.
     */
    public static void stopBackgroundMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
            backgroundPlayer.dispose();
            backgroundPlayer = null;
        }
    }
}