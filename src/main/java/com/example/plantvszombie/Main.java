package com.example.plantvszombie;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("MenuScene.fxml"));
        stage.setTitle("初始界面");
        stage.setScene(new Scene(fxmlLoader.load(),800,600));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}