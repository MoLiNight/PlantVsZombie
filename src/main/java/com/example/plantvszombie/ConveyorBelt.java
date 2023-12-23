package com.example.plantvszombie;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConveyorBelt {
    public int card_num=0;

    private Connection connection;

    public AnchorPane anchorPane;
    File[] files=new File[10];
    Image[] images=new Image[10];
    public Plant[] plants=new Plant[1000];

    public ConveyorBelt(AnchorPane pane) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/GameDB?useSSL=false","root","Zxx20040806*");

        files[0]=new File("src/main/resources/images/Flower_Card.png");
        files[1]=new File("src/main/resources/images/Pumpkin_Card.png");
        files[2]=new File("src/main/resources/images/Cherry_Card.png");
        files[3]=new File("src/main/resources/images/Flower_Wait.gif");
        files[4]=new File("src/main/resources/images/Pumpkin_One.gif");
        files[5]=new File("src/main/resources/images/Cherry.gif");
        for(int i=0;i<6;i++){
            images[i]=new Image(files[i].toURI().toString());
        }
        anchorPane=pane;
    }

    public int GetSequence(int card_id) throws SQLException {
        Statement statement=connection.createStatement();
        statement.executeQuery("SELECT * FROM card_data");
        ResultSet resultSet=statement.getResultSet();
        int ans=0;
        while (resultSet.next()){
            ans++;
            if(resultSet.getInt(1)==card_id){
                break;
            }
        }
        return ans;
    }

    public void AddCard(Pane pane,int card_id){
        ImageView imageView=new ImageView();
        Random random=new Random();
        int rand=random.nextInt(100);
        if(rand<50){
            imageView.setImage(images[0]);
        }
        else
            if(rand<85){
                imageView.setImage(images[1]);
            }
            else{
                imageView.setImage(images[2]);
            }

        imageView.setFitWidth(58);
        imageView.setFitHeight(82);
        imageView.setX(730);
        imageView.setY(0);

        imageView.setOnMouseDragged(new EventHandler<MouseEvent>() {
            int delX=0,delY=0;
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (imageView.getImage() == images[0]) {
                    imageView.setImage(images[3]);
                    delX=40;
                    delY=60;
                    imageView.setFitWidth(120);
                    imageView.setFitHeight(120);
                }
                else
                if (imageView.getImage() == images[1]) {
                    imageView.setImage(images[4]);
                    delX=35;
                    delY=20;
                    imageView.setFitWidth(75);
                    imageView.setFitHeight(75);
                }
                else
                if (imageView.getImage() == images[2]) {
                    imageView.setImage(images[5]);
                    delX=35;
                    delY=20;
                    imageView.setFitWidth(75);
                    imageView.setFitHeight(75);
                }
                imageView.setX(mouseEvent.getSceneX()-delX);
                imageView.setY(mouseEvent.getSceneY()-delY);
            }
        });

        imageView.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int putX=1,putY=1;
                int frontX=0,frontY=0;
                int minX=1920,minY=1080;
                if(imageView.getImage()==images[3]){
                    frontX=0;
                    frontY=70;
                }
                if(imageView.getImage()==images[4]){
                    frontX=5;
                    frontY=120;
                }
                if(imageView.getImage()==images[5]){
                    frontX=-15;
                    frontY=105;
                }

                int IX=(int)imageView.getX();
                int IY=(int)imageView.getY();

                for(int r=1;r<=5;r++){
                    int disY=Math.abs(IY-frontY-(r-1)*95);
                    if(disY<minX){
                        minX=disY;
                        putY=r;
                    }
                }

                for(int c=1;c<=9;c++){
                    int disX=Math.abs(IX-frontX-(c+1)*80);
                    if(disX<minY){
                        minY=disX;
                        putX=c;
                    }
                }

                //imageView.setX(frontX+(putX+1)*80);
                //imageView.setY(frontY+(putY-1)*95);
                imageView.setVisible(false);

                try {
                    PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM plant_data WHERE myrow = ? AND mycol = ?",Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setInt(1,putY);
                    preparedStatement.setInt(2,frontX+(putX+1)*80);
                    preparedStatement.executeQuery();
                    ResultSet resultSet=preparedStatement.getResultSet();

                    switch (frontX){
                        case 0:{
                            if(resultSet.next()){
                                System.out.println("The Card has been destroyed");
                                break;
                            }

                            Flower flower=new Flower(putY,frontX+(putX+1)*80,anchorPane);
                            plants[flower.id]=flower;

                            preparedStatement.setInt(2,frontX+5+(putX+1)*80);
                            preparedStatement.executeQuery();
                            resultSet=preparedStatement.getResultSet();
                            if(resultSet.next()){
                                Pumpkin pumpkin= (Pumpkin) plants[resultSet.getInt(1)];
                                pumpkin.viewgif.toFront();
                            }
                            break;
                        }
                        case 5:{
                            if(resultSet.next()){
                                System.out.println("The Card has been destroyed");
                                break;
                            }

                            Pumpkin pumpkin=new Pumpkin(putY,frontX+(putX+1)*80,anchorPane);
                            plants[pumpkin.id]=pumpkin;
                            break;
                        }
                        case -15:{

                            Cherry cherry=new Cherry(putY,frontX+(putX+1)*80,anchorPane);
                            plants[cherry.id]=cherry;
                            break;
                        }
                    }
                    DeleteCard(card_id);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        pane.getChildren().add(imageView);

        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (imageView.getY() == 0) {
                                if (imageView.getX()>=143+GetSequence(card_id)*58)
                                    imageView.setX(imageView.getX() - 1.16);
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask,0,20);

        card_num++;
    }
    public void DeleteCard(int card_id) throws SQLException {
        card_num--;
        PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM card_data WHERE id = (?)",Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1,card_id);
        preparedStatement.executeUpdate();
    }
}
