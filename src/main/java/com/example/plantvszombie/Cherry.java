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

public class Cherry extends Plant{
    public ImageView viewgif;
    public Image cherry;
    public Image boom;

    public Cherry(int row, double col, AnchorPane parentPane) throws SQLException {
        super(row,col,parentPane);

        PreparedStatement statement = conn.prepareStatement("UPDATE plant_data set type = 'cherry' WHERE id = ?");
        statement.setInt(1, id);

        int rowsAffected  = statement.executeUpdate();

        File p = new File("src/main/resources/images/Cherry.gif");
        File q = new File("src/main/resources/images/Cherry_Boom.gif");

        cherry = new Image(p.toURI().toString());
        boom = new Image(q.toURI().toString());

        //对row和col进行转换（to do）
        viewgif = new ImageView(cherry);

        //设置图片大小
        viewgif.setFitHeight(75);
        viewgif.setFitWidth(100);

        //对部分参数初始化

        viewgif.setX(col);
        viewgif.setY(105 +(row - 1)* 95);
        parentPane.getChildren().add(viewgif);

        viewgif.setImage(cherry);
        Timer imageSwitchTimer = new Timer();
        imageSwitchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 更改图片为 digestImage
                viewgif.setX(col - 75);
                viewgif.setY(25 +(row - 1)* 95);
                viewgif.setFitHeight(250);
                viewgif.setFitWidth(250);
                viewgif.setImage(boom);
                // ...其他操作
            }
        }, 700);

        Timer afterboom = new Timer();
        afterboom.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    cleanup();
                    PreparedStatement deleteStatement = conn.prepareStatement("UPDATE zombie_data set die_reason = 2 WHERE myrow <= ? + 1 and myrow >= ? - 1 and mycol < ? + 125 and mycol > ? - 125 and die_reason = 0");
                    deleteStatement.setInt(1, getRow());
                    deleteStatement.setInt(2,getRow());
                    deleteStatement.setDouble(3,getCol());
                    deleteStatement.setDouble(4,getCol());

                    int rowsAffected_1 = deleteStatement.executeUpdate();

                    if (rowsAffected_1 > 0) {
                        System.out.println("Successfully deleted zombie information.");
                    } else {
                        System.out.println("No zombie information deleted.");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        },800);

        System.out.println("cherry is created!");
    }

    @Override
    public void cleanup() throws SQLException {

        PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM plant_data WHERE id = ?");
        deleteStatement.setInt(1, id);

        int rowsAffected = deleteStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Successfully deleted cherry information.");
        } else {
            System.out.println("No cherry information deleted.");
        }
        deleteStatement.close();

        Platform.runLater(() -> {
            // 在 JavaFX 应用程序线程上执行与 JavaFX 场景图相关的操作
            getParentpane().getChildren().remove(viewgif); // 从 AnchorPane 中移除 Flower 对象
            // 其他清理操作...
        });
    }
}
