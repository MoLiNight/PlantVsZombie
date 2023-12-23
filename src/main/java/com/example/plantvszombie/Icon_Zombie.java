package com.example.plantvszombie;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class Icon_Zombie extends Zom {
    public static final String name = "Icon_Zombie";
    public int code;//code : 1 -> walk  code : 2 -> eat  code : 3 ->  die
    public static Image image_eat;
    public static Image image_die;
    public static Image image_walk;

    //attack -- 60 , speed -- 2

    static {
        File file = new File("src/main/resources/images/Icon_Walk.gif");
        image_walk = new Image(file.toURI().toString());
        file = new File("src/main/resources/images/Icon_Attack.gif");
        image_eat = new Image(file.toURI().toString());
        file = new File("src/main/resources/images/Ashes.gif");
        image_die = new Image(file.toURI().toString());
    }

    public Icon_Zombie(int r, double c, AnchorPane pane){
        super(r, c, 60, 2, pane);
        ImageView imageView = new ImageView();
        imageView.setX(col);
        imageView.setY((double)(40 + (row - 1) * 95));
        imageView.setFitWidth(120);
        imageView.setFitHeight(145);
        imageView.setImage(image_walk);
        pane.getChildren().add(imageView);
        code = 1;
        walk(pane, imageView);
        eat(pane, imageView);
        die(pane, imageView);
    }

    void walk(AnchorPane pane, ImageView imageView){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(!cheak_eat(row, col) && alive){
                    if(code != 1){
                        imageView.setImage(image_walk);
                        code = 1;
                    }
                    // 任务执行的代码
                    try {
                        col -= speed;
                        // 创建PreparedStatement对象
                        String sqlQuery = "update zombie_data set myrow = ?, mycol = ? where id = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sqlQuery);

                        // 设置参数
                        pstmt.setInt(1, row); // 设置myrow
                        pstmt.setDouble(2, col); // 设置mycol
                        pstmt.setInt(3, id);

                        // 执行更新操作
                        pstmt.executeUpdate();

                        // 关闭连接和Statement对象
                        pstmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    pane.setTopAnchor(imageView, (double)(40 +(row - 1)* 95)); // 设置图片距离AnchorPane顶部的距离为50像素
                    pane.setLeftAnchor(imageView, col);// 设置图片距离AnchorPane左侧的距离为100像素
                }
            }
        };
        // 启动任务
        timer.schedule(task, 0, 150);

    }

    void eat(AnchorPane pane, ImageView imageView){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(cheak_eat(row, col) && alive){
                    if(code != 2){
                        imageView.setImage(image_eat);
                        code = 2;
                    }
                    int hp = 0;
                    try{
                        String sqlQuery = "select current_hp from plant_data where id = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sqlQuery);

                        // 设置参数
                        pstmt.setInt(1, plantBeAttacked_id); // 设置myrow
                        // 执行更新操作
                        ResultSet rs = pstmt.executeQuery();
                        while(rs.next()){
                            hp = rs.getInt("current_hp");
                            System.out.println(hp);
                        }
                        rs.close();
                        // 关闭连接和Statement对象
                        pstmt.close();
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if(hp <= 0){
                        try{
                            String sqlQuery = "delete from plant_data where id = ?";
                            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
                            // 设置参数
                            pstmt.setInt(1, plantBeAttacked_id); // 设置myrow
                            // 执行更新操作
                            pstmt.executeUpdate();
                            // 关闭连接和Statement对象
                            pstmt.close();
                        }catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            hp -= attack;
                            // 创建PreparedStatement对象
                            String sqlQuery = "update plant_data set current_hp = ? where id = ?";
                            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);

                            // 设置参数
                            pstmt.setInt(1, hp); // 设置myrow
                            pstmt.setInt(2, plantBeAttacked_id); // 设置mycol


                            // 执行更新操作
                            pstmt.executeUpdate();

                            // 关闭连接和Statement对象
                            pstmt.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        // 启动任务
        timer.schedule(task, 0, 500);
    }

    void die(AnchorPane pane, ImageView imageView){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(alive){
                    getDie_reason(row, col);
                    if(die_reason != 0){
                        try {
                            // 创建PreparedStatement对象
                            String sqlQuery = "delete from zombie_data where id = ?";
                            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);

                            // 设置参数
                            pstmt.setInt(1, id);

                            // 执行更新操作
                            pstmt.executeUpdate();

                            // 关闭连接和Statement对象
                            pstmt.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if(die_reason == 1){
                        imageView.setImage(null);
                        alive = false;
                    }else if(die_reason == 2){
                        imageView.setImage(image_die);
                        alive = false;
                        try {
                            Thread.sleep(800);  // 暂停执行 5 秒钟（5000 毫秒）
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        imageView.setImage(null);
                    }
                }
            }
        };
        // 启动任务
        timer.schedule(task, 0, 50);

    }
    boolean cheak_eat(int row, double col){
        boolean isEat = false;
        double _min = 100000;
        try {
            String sqlQuery = "select mycol, id from plant_data where myrow = ?";
            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
            pstmt.setInt(1, row);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                double plant_col = rs.getDouble("mycol");
                int plant_id = rs.getInt("id");
                if(Math.abs(plant_col - col) < 10){
                    if(Math.abs(plant_col - col) < _min){
                        _min = Math.abs(plant_col - col);
                        isEat = true;
                        plantBeAttacked_id = plant_id;
                    }
                }
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isEat;
    }

    public void getDie_reason(int row, double col){
        try{
            String sqlQuery = "select die_reason from zombie_data where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                die_reason = rs.getInt("die_reason");
            }
            rs.close();
            pstmt.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
