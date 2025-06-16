module com.example.game {
	requires javafx.controls;
	requires javafx.fxml;
    requires jdk.jdi;
	requires com.fasterxml.jackson.databind;
	requires org.junit.jupiter.api;
	requires java.desktop;
    requires javafx.media;

	exports com.example.ui;

    exports com.example.entity;

	exports com.example.map;
	opens com.example.map to javafx.fxml, com.fasterxml.jackson.databind;

	exports com.example.main;
	opens com.example.main to javafx.fxml;

	exports com.example.storage_manager;
	opens com.example.storage_manager to com.fasterxml.jackson.databind;

	exports com.example.game;
	opens com.example.game to javafx.fxml;

	exports com.example.utils;
	opens com.example.utils to javafx.fxml;

	exports com.example.config;
	opens com.example.config to com.fasterxml.jackson.databind;

	exports com.example.controllers;
	opens com.example.controllers to javafx.fxml;

	opens com.example.entity to com.fasterxml.jackson.databind, javafx.fxml;
	opens com.example.test to org.junit.platform.commons;

	exports com.example.player;
	opens com.example.player to javafx.fxml;

}
