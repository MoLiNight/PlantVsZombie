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
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/GameDB?useSSL=false","root","Zxx20040806*");
        Statement statement=connection.createStatement();
        statement.executeUpdate("TRUNCATE TABLE card_data");
        statement.executeUpdate("TRUNCATE TABLE plant_data");
        statement.executeUpdate("TRUNCATE TABLE zombie_data");

        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("MenuScene.fxml"));
        stage.setTitle("初始界面");
        stage.setScene(new Scene(fxmlLoader.load(),800,600));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}