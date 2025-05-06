module com.example.game {
	requires javafx.controls;
	requires javafx.fxml;
	requires com.fasterxml.jackson.databind;
	requires java.desktop;


	exports com.example.entity;
    opens com.example.entity to javafx.fxml;
	exports com.example.map;
	opens com.example.map to javafx.fxml;
	exports com.example.main;
	opens com.example.main to javafx.fxml;
	exports com.example.game;
	opens com.example.game to javafx.fxml;
	exports com.example.utils;
	opens com.example.utils to javafx.fxml;
	exports com.example.controllers;
	opens com.example.controllers to javafx.fxml;
}