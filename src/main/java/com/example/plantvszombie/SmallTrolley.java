package com.example.plantvszombie;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class SmallTrolley {
    private Connection connection;

    public AnchorPane anchorPane;

    boolean[] use_trolley=new boolean[10];
    public SmallTrolley(AnchorPane pane) throws SQLException {
        anchorPane=pane;
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/GameDB?useSSL=false","root","Zxx20040806*");
        File file=new File("src/main/resources/images/SmallTrolleys.gif");
        Image image=new Image(file.toURI().toString());
        for(int r=1;r<=5;r++){
            ImageView imageView=new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setX(100);
            imageView.setY(r*100);
            use_trolley[r]=false;

            Timer timer=new Timer();
            int finalR = r;
            TimerTask timerTask=new TimerTask() {
                @Override
                public void run() {
                    if(!use_trolley[finalR]){
                        try {
                            for(int T=0;T<=2;T++){
                                PreparedStatement preparedStatement=connection.prepareStatement("SELECT * FROM zombie_data WHERE myrow = ? AND mycol < ? AND isAbleToEat = ?",Statement.RETURN_GENERATED_KEYS);
                                preparedStatement.setInt(1, finalR);
                                if(T==1){
                                    preparedStatement.setInt(2,100);
                                }
                                else{
                                    preparedStatement.setInt(2,20);
                                }
                                preparedStatement.setInt(3,T);
                                preparedStatement.executeQuery();
                                ResultSet resultSet=preparedStatement.getResultSet();
                                if(resultSet.next()){
                                    use_trolley[finalR]=true;
                                    Timer timer_attack=new Timer();
                                    TimerTask task=new TimerTask() {
                                        @Override
                                        public void run() {
                                            try {
                                                imageView.setX(imageView.getX()+25);
                                                if(imageView.getX()>1000){
                                                    imageView.setVisible(false);
                                                    timer_attack.cancel();
                                                }
                                                PreparedStatement statement=connection.prepareStatement("UPDATE zombie_data SET die_reason = 2 WHERE myrow = ? AND mycol < ?",Statement.RETURN_GENERATED_KEYS);
                                                statement.setInt(1,finalR);
                                                statement.setDouble(2,imageView.getX());
                                                statement.executeUpdate();
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    };
                                    timer_attack.schedule(task,0,50);
                                }
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            };
            timer.schedule(timerTask,0,100);

            anchorPane.getChildren().add(imageView);
        }
    }
}
