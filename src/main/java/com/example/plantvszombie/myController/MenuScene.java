package com.example.plantvszombie.myController;

import com.example.plantvszombie.ConveyorBelt;
import com.example.plantvszombie.Main;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

public class MenuScene {
    @FXML
    private Button startButton;

    @FXML
    private ImageView menuImage;

    public void initialize(){
        File file_one=new File("src/main/resources/images/Menu.png");
        Image image_one=new Image(file_one.toURI().toString());
        menuImage.setImage(image_one);

        startButton.setOpacity(0);
    }

    public void GameStart() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/GameDB?useSSL=false","root","Zxx20040806*");
        Statement statement=connection.createStatement();
        statement.executeUpdate("TRUNCATE TABLE card_data");
        statement.executeUpdate("TRUNCATE TABLE plant_data");
        statement.executeUpdate("TRUNCATE TABLE zombie_data");

        FXMLLoader loader=new FXMLLoader(Main.class.getResource("BattleScene.fxml"));
        Stage pre_stage=(Stage)startButton.getScene().getWindow();
        pre_stage.close();

        Stage stage=new Stage();
        stage.setTitle("植物大战僵尸");
        stage.setScene(new Scene(loader.load(),1000,600));
        stage.show();
    }
}
