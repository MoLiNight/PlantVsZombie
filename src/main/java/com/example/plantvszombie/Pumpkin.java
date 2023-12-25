package com.example.plantvszombie;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class Pumpkin extends Plant{
    public ImageView viewgif;
    public Image fullHP;
    private Timer hpCheckTimer;
    public Pumpkin(int row, double col, AnchorPane parentPane) throws SQLException {
        super(row,col,parentPane);

        PreparedStatement statement = conn.prepareStatement("UPDATE plant_data set type = 'pumpkin' ,current_hp = 3333 WHERE id = ?");
        statement.setInt(1, id);

        int rowsAffected  = statement.executeUpdate();

        File p = new File("src/main/resources/images/Pumpkin_One.gif");

        fullHP = new Image(p.toURI().toString());

        //对row和col进行转换（to do）
        viewgif = new ImageView(fullHP);

        //设置图片大小
        viewgif.setFitHeight(75);
        viewgif.setFitWidth(100);

        //对部分参数初始化
        viewgif.setX(col-10);
        viewgif.setY(120 +(row - 1)* 95);

        viewgif.toFront();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                parentPane.getChildren().add(viewgif);
            }
        });

        viewgif.toFront();

        viewgif.setImage(fullHP);

        startHPCheckTimer();

        System.out.println("pumpkin is created!");
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

        PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM plant_data WHERE id = ?");
        deleteStatement.setInt(1, id);

        int rowsAffected = deleteStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Successfully deleted pumpkin information.");
        } else {
            System.out.println("No pumpkin information deleted.");
        }
        deleteStatement.close();

        Platform.runLater(() -> {
            // 在 JavaFX 应用程序线程上执行与 JavaFX 场景图相关的操作
            getParentpane().getChildren().remove(viewgif); // 从 AnchorPane 中移除 Flower 对象
            // 其他清理操作...
        });
    }

    public void stopMonitoring() {
        if (hpCheckTimer != null) {
            hpCheckTimer.cancel(); // 取消定时器
            hpCheckTimer.purge(); // 清除已取消的任务

            hpCheckTimer = null; // 将定时器变量置空
        }
    }
}
