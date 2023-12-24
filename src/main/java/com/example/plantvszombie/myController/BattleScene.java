package com.example.plantvszombie.myController;

import com.example.plantvszombie.*;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.io.File;
import java.sql.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.DriverManager;

public class BattleScene {

    @FXML
    private Pane beltPane;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Button menuButton;

    @FXML
    private ImageView shovelImage;

    @FXML
    private ImageView shovelBackground;

    @FXML
    private AnchorPane gameStage;

    @FXML
    private ImageView startAnimation;

    @FXML
    private ImageView failView;

    private int card_id=0;
    private Connection connection;

    int zoms_num=0;
    Zom[] zoms=new Zom[10000];

    public void initialize() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/GameDB?useSSL=false","root","Zxx20040806*");

        File[] file=new File[15];
        file[0]=new File("src/main/resources/images/Card_Background.png");
        file[1]=new File("src/main/resources/images/Grass.jpg");
        file[2]=new File("src/main/resources/images/Button.png");
        file[3]=new File("src/main/resources/images/Shovel.png");
        file[4]=new File("src/main/resources/images/SmallTrolleys.gif");
        file[5]=new File("src/main/resources/images/ReadyToStart.gif");
        file[6]=new File("src/main/resources/images/Success.png");
        file[7]=new File("src/main/resources/images/Failure.png");
        file[8]=new File("src/main/resources/images/Flower_Card.png");
        file[9]=new File("src/main/resources/images/Pumpkin_Card.png");
        file[10]=new File("src/main/resources/images/Cherry_Card.png");
        file[11]=new File("src/main/resources/images/ReadyToStart.gif");

        Image[] images=new Image[15];
        for(int i=0;i<12;i++){
            images[i]=new Image(file[i].toURI().toString());
        }

        beltPane.setStyle("-fx-background-color: #444444");
        shovelBackground.setImage(images[0]);
        backgroundImage.setImage(images[1]);
        menuButton.setBackground(new Background(new BackgroundImage(images[2],null,null,null,null)));
        shovelImage.setImage(images[3]);
        startAnimation.setImage(images[11]);
        failView.setImage(images[7]);
        failView.setVisible(false);
        SmallTrolley smallTrolley=new SmallTrolley(gameStage);

        Timer failTimer=new Timer();
        TimerTask failTask=new TimerTask() {
            @Override
            public void run() {
                try {
                    PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM zombie_data WHERE mycol < ?",Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setDouble(1,0);
                    preparedStatement.executeQuery();
                    ResultSet resultSet=preparedStatement.getResultSet();
                    if(resultSet.next()){
                        failView.setX(430);
                        failView.setY(240);
                        failView.setFitWidth(140);
                        failView.setFitHeight(120);
                        failView.setVisible(true);
                        failView.toFront();
                        Timer viewTimer=new Timer();
                        TimerTask viewTask=new TimerTask() {
                            @Override
                            public void run() {
                                if(failView.getFitHeight()>420){
                                    viewTimer.cancel();
                                }
                                failView.setX(failView.getX()-14);
                                failView.setY(failView.getY()-12);
                                failView.setFitHeight(failView.getFitHeight()+24);
                                failView.setFitWidth(failView.getFitWidth()+28);
                            }
                        };
                        viewTimer.schedule(viewTask,0,100);

                        failTimer.cancel();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        failTimer.schedule(failTask,3000,1000);

        ConveyorBelt conveyorBelt=new ConveyorBelt(gameStage);
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation.setVisible(false);
                        if(conveyorBelt.card_num<9){
                            conveyorBelt.AddCard(gameStage,card_id);

                            try {
                                PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO card_data values (?)",Statement.RETURN_GENERATED_KEYS);
                                preparedStatement.setInt(1,card_id);
                                preparedStatement.executeUpdate();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                            card_id++;
                        }
                    }
                });
            }
        };
        timer.schedule(task,3000,2500);

        double shovelX=shovelImage.getX();
        double shovelY=shovelImage.getY();
        shovelImage.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                shovelImage.setX(mouseEvent.getSceneX()-830);
                shovelImage.setY(mouseEvent.getSceneY()-40);
            }
        });

        shovelImage.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int putX=0,putY=0;
                int minX=1920,minY=1080;
                int IX=(int)shovelImage.getX()+750;
                int IY=(int)shovelImage.getY();

                for(int r=1;r<=5;r++){
                    int disY=Math.abs(IY-r*95);
                    if(disY<minX){
                        minX=disY;
                        putY=r;
                    }
                }

                for(int c=1;c<=9;c++){
                    int disX=Math.abs(IX-c*85);
                    if(disX<minY){
                        minY=disX;
                        putX=c;
                    }
                }

                try {
                    PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM plant_data WHERE myrow = ? AND mycol = ?",Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setInt(1,putY);
                    preparedStatement.setDouble(2,(putX+1)*80);
                    preparedStatement.executeQuery();
                    ResultSet resultSet=preparedStatement.getResultSet();
                    if(resultSet.next()){
                        conveyorBelt.plants[resultSet.getInt(1)].cleanup();
                    }
                    else{
                        preparedStatement.setDouble(2,(putX+1)*80+5);
                        preparedStatement.executeQuery();
                        resultSet=preparedStatement.getResultSet();
                        if(resultSet.next()){
                            conveyorBelt.plants[resultSet.getInt(1)].cleanup();
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                shovelImage.setX(shovelX);
                shovelImage.setY(shovelY);
            }
        });

        Random random_range=new Random();
        Random random_row=new Random();
        Timer zom_timer=new Timer();
        TimerTask zom_task=new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        switch (random_range.nextInt(4)){
                            case 0:{
                                zoms[zoms_num++]=new Normal_Zombie(random_row.nextInt(5)+1,1000,gameStage);
                                break;
                            }
                            case 1:{
                                zoms[zoms_num++]=new Icon_Zombie(random_row.nextInt(5)+1,1000,gameStage);
                                break;
                            }
                            case 2:{
                                zoms[zoms_num++]=new Jump_Zombie(random_row.nextInt(5)+1,1000,gameStage);
                                break;
                            }
                            case 3:{
                                zoms[zoms_num++]=new IceCar_Zombie(random_row.nextInt(5)+1,1000,gameStage);
                                break;
                            }
                        }
                    }
                });
            }
        };
        zom_timer.schedule(zom_task,3000,2500);
    }
}
