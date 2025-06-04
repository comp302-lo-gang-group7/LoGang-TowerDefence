module com.example.game {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.base;
	requires jdk.jdi;
	requires com.fasterxml.jackson.databind;
	requires org.junit.jupiter.api;
	requires java.desktop;

	exports com.example.entity;

	exports com.example.map;
	opens com.example.map to javafx.fxml, com.fasterxml.jackson.databind;

	exports com.example.main;
	opens com.example.main to javafx.fxml;

	exports com.example.game;
	opens com.example.game to javafx.fxml;

	exports com.example.utils;
	opens com.example.utils to javafx.fxml;

	exports com.example.controllers;
	opens com.example.controllers to javafx.fxml;
	opens com.example.entity to com.fasterxml.jackson.databind, javafx.fxml;

	opens com.example.test to org.junit.platform.commons;

}
