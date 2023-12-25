package com.example.plantvszombie;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class Flower extends Plant{

    public ImageView viewgif;
    public Image attackimage;
    public Image waitimage;
    public Image digestImage;
    private int type;//1是等待，2是消化中
    private int digesting_time;
    private Timer monitorTimer;
    private Timer hpCheckTimer;

    public Flower(int row,double col,AnchorPane parentPane) throws SQLException {
        super(row,col,parentPane);

        PreparedStatement statement = conn.prepareStatement("UPDATE plant_data set type = 'flower' WHERE id = ?");
        statement.setInt(1, id);

        int rowsAffected  = statement.executeUpdate();

        File waitfile = new File("src/main/resources/images/Flower_Wait.gif");
        File attackfile = new File("src/main/resources/images/Flower_Attack.gif");
        File digestfile = new File("src/main/resources/images/Flower_Digest.gif");

        waitimage = new Image(waitfile.toURI().toString());
        attackimage = new Image(attackfile.toURI().toString());
        digestImage = new Image(digestfile.toURI().toString());

        //对row和col进行转换（to do）
        viewgif = new ImageView(waitimage);

        //设置图片大小
        viewgif.setFitHeight(110);
        viewgif.setFitWidth(120);

        //对部分参数初始化
        type = 1;
        digesting_time = 0;

        viewgif.setX(col);
        viewgif.setY(70 +(row - 1)* 95);
        parentPane.getChildren().add(viewgif);
        viewgif.setImage(waitimage);

        startMonitoringZombies();
        startHPCheckTimer();
        System.out.println("flower is created!");
    }

    private void startMonitoringZombies() {
        monitorTimer = new Timer();
        // 设定定时任务，每隔一段时间执行一次查询操作
        monitorTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    //如果处于等待的状态
                    if(type == 1){
                        for(int T=1;T<=2;T++){
                            PreparedStatement statement = conn.prepareStatement("SELECT * FROM zombie_data WHERE myrow = ? and mycol < ? and mycol > ? and die_reason = 0 and isAbleToEat = ?");
                            statement.setInt(1, getRow()); // 使用 Flower 的行数进行查询
                            if(T==1){
                                statement.setDouble(2,getCol() + 100);
                                statement.setDouble(3,getCol());
                            }
                            else{
                                statement.setDouble(2,getCol() + 20);
                                statement.setDouble(3,getCol() - 100);
                            }
                            statement.setInt(4,T);

                            ResultSet resultSet = statement.executeQuery();

                            if (resultSet.next()) {
                                PreparedStatement statement_1 = conn.prepareStatement("UPDATE zombie_data set die_reason = 1 WHERE id = ?");
                                statement_1.setInt(1, resultSet.getInt(1));

                                int rowsAffected  = statement_1.executeUpdate();

                                if (rowsAffected > 0) {
                                    System.out.println("Successfully deleted zombie information.");
                                } else {
                                    System.out.println("No zombie information deleted.");
                                }

                                statement_1.close();

                                type = 2;
                                viewgif.setImage(attackimage);

                                //停留一段时间后切换图片
                                Timer imageSwitchTimer = new Timer();
                                imageSwitchTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        // 更改图片为 digestImage
                                        viewgif.setImage(digestImage);
                                        // ...其他操作
                                    }
                                }, 500);
                            }

                            resultSet.close();
                            statement.close();
                            if(type==2){
                                break;
                            }
                        }
                    }
                    //如果处于消化的状态
                    else{
                        digesting_time++;
                        if(digesting_time >= 10){
                            viewgif.setImage(waitimage);
                            digesting_time = 0;
                            type = 1;
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000); // 每5秒执行一次查询操作，可以根据需要调整时间间隔
    }

    //停止检测函数
    public void stopMonitoring() {
        if (monitorTimer != null) {
            monitorTimer.cancel(); // 取消定时器
            monitorTimer.purge(); // 清除已取消的任务

            monitorTimer = null; // 将定时器变量置空
        }
        if (hpCheckTimer != null) {
            hpCheckTimer.cancel(); // 取消定时器
            hpCheckTimer.purge(); // 清除已取消的任务

            hpCheckTimer = null; // 将定时器变量置空
        }
    }

    private void startHPCheckTimer() {
        hpCheckTimer = new Timer();

        // 设定定时任务，每隔一段时间检查 HP 是否小于零
        hpCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int currentHP = 0; // 获取当前 HP
                try {
                    currentHP = getHP();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (currentHP <= 0) {
                    // 如果 HP 小于等于零，执行清理操作
                    try {
                        cleanup();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 0, 500); // 每秒执行一次检查操作，可以根据需要调整时间间隔
    }

    @Override
    public void cleanup() throws SQLException {
        stopMonitoring();

        //删除数据库内的内容
        PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM plant_data WHERE id = ?");
        deleteStatement.setInt(1, id);

        int rowsAffected = deleteStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Successfully deleted flower information.");
        } else {
            System.out.println("No flower information deleted.");
        }
        deleteStatement.close();


        Platform.runLater(() -> {
            // 在 JavaFX 应用程序线程上执行与 JavaFX 场景图相关的操作
            getParentpane().getChildren().remove(viewgif); // 从 AnchorPane 中移除 Flower 对象
            // 其他清理操作...
        });
    }
}
