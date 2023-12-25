package com.example.plantvszombie;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;



public class Jump_Zombie extends Zom{
    public static final String name = "Normal_Zombie";
    public int code;//code : 1 -> walk  code : 2 -> eat  code : 3 ->  die  code : 4 -> run code : 5 -> jump
    public static Image image_eat;
    public static Image image_die;
    public static Image image_walk;
    public static Image image_run;
    public static Image image_jump;
    public int plantBeJumped_id;
    public int cnt;

    boolean isJump;

    //attack -- 50 , speed -- 1

    static {
        File file = new File("src/main/resources/images/Jump_Walk.gif");
        image_walk = new Image(file.toURI().toString());
        file = new File("src/main/resources/images/Jump_Run.gif");
        image_run = new Image(file.toURI().toString());
        file = new File("src/main/resources/images/Jump_Attack.gif");
        image_eat = new Image(file.toURI().toString());
        file = new File("src/main/resources/images/Ashes.gif");
        image_die = new Image(file.toURI().toString());
        file = new File("src/main/resources/images/Jump_Jump.gif");
        image_jump = new Image(file.toURI().toString());
    }

    public Jump_Zombie(int r, double c, AnchorPane pane){
        super(r, c, 50, 1, pane);
        isJump = false;
        cnt = 0;
        ImageView imageView = new ImageView();
        imageView.setX(col - 40);
        imageView.setY((double)(5 + (row - 1) * 95));
        imageView.setFitWidth(280);
        imageView.setFitHeight(200);
        imageView.setImage(image_run);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pane.getChildren().add(imageView);
            }
        });

        code = 4;
        try{
            String sqlQuery = "update zombie_data set isAbleToEat = ? where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
            pstmt.setInt(1, 0);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            pstmt.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        Run(pane, imageView);
        Jump(pane, imageView);
        walk(pane, imageView);
        eat(pane, imageView);
        die(pane, imageView);
    }

    public void Run(AnchorPane pane, ImageView imageView){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(cheak_run(row, col) && !isJump && alive){
                    // 任务执行的代码
                    try {
                        col -= 2 * speed;
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
                    pane.setTopAnchor(imageView, (double)(5 +(row - 1)* 95)); // 设置图片距离AnchorPane顶部的距离为50像素
                    pane.setLeftAnchor(imageView, col - 40);// 设置图片距离AnchorPane左侧的距离为100像素
                }
            }
        };

        timer.schedule(task, 0, 75);
    }

    public void Jump(AnchorPane pane, ImageView imageView){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if((!cheak_run(row, col) || code == 5) && !isJump && alive){
                    code = 5;
                    imageView.setImage(image_jump);
                    imageView.setY(imageView.getY()-40);
                    cnt += 1;
                    // 任务执行的代码
                    try {
                        col -= speed * 2;
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
                    pane.setTopAnchor(imageView, (double)((row - 1)* 95)); // 设置图片距离AnchorPane顶部的距离为50像素
                    pane.setLeftAnchor(imageView, col - 40);// 设置图片距离AnchorPane左侧的距离为100像素
                    if(cnt == 35) {
                        try {
                            isJump = true;
                            col -= 80;
                            // 创建PreparedStatement对象
                            String sqlQuery = "update zombie_data set myrow = ?, mycol = ?, isAbleToEat = ? where id = ?";
                            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);

                            // 设置参数
                            pstmt.setInt(1, row); // 设置myrow
                            pstmt.setDouble(2, col); // 设置mycol
                            pstmt.setInt(3,2);
                            pstmt.setInt(4, id);

                            // 执行更新操作
                            pstmt.executeUpdate();

                            // 关闭连接和Statement对象
                            pstmt.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        pane.setTopAnchor(imageView, (double)(40 +(row - 1)* 95)); // 设置图片距离AnchorPane顶部的距离为50像素
                        pane.setLeftAnchor(imageView, col - 40);// 设置图片距离AnchorPane左侧的距离为100像素
                    }
                }
            }
        };

        timer.schedule(task, 0, 75);

    }
    public void walk(AnchorPane pane, ImageView imageView){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(!cheak_eat(row, col) && alive && isJump){
                    if(code != 1){
                        imageView.setImage(image_walk);
                        imageView.setY(imageView.getY()+40);

                        code = 1;
                    }
                    // 任务执行的代码
                    try {
                        col -= speed ;
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
                    pane.setTopAnchor(imageView, (double)(5 +(row - 1)* 95)); // 设置图片距离AnchorPane顶部的距离为50像素
                    pane.setLeftAnchor(imageView, col - 40);// 设置图片距离AnchorPane左侧的距离为100像素
                }
            }
        };
        // 启动任务
        timer.schedule(task, 0, 75);

    }

    public void eat(AnchorPane pane, ImageView imageView){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(cheak_eat(row, col) && alive && isJump){
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
                    pane.setTopAnchor(imageView, (double)(5 +(row - 1)* 95)); // 设置图片距离AnchorPane顶部的距离为50像素
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

    public void die(AnchorPane pane, ImageView imageView){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(alive){
                    getDie_reason(row, col);
                    if(!isJump && die_reason == 1){
                        try{
                            String sqlQuery = "update zombie_data set die_reason = 0 where id = ?";
                            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
                            pstmt.setInt(1,id);
                            pstmt.executeUpdate();
                        }catch (SQLException e){
                            e.printStackTrace();
                        }
                    }
                    if(die_reason != 0 && isJump || die_reason == 2){
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
                    if(die_reason == 1 && isJump){
                        imageView.setImage(null);
                        alive = false;
                    }else if(die_reason == 2){
                        imageView.setImage(image_die);
                        alive = false;
                        try {
                            Thread.sleep(500);  // 暂停执行 5 秒钟（5000 毫秒）
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        imageView.setImage(null);
                    }
                }
            }
        };
        // 启动任务
        timer.schedule(task, 0, 10);

    }

    public boolean cheak_run(int row, double col){
        boolean isRun = true;
        double _min = 10000;
        try {
            String sqlQuery = "select mycol, id from plant_data where myrow = ?";
            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
            pstmt.setInt(1, row);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                double plant_col = rs.getDouble("mycol");
                int plant_id = rs.getInt("id");
                if(col - plant_col <= 25 && col - plant_col >= -100){
                    if(col - plant_col < _min){
                        _min = Math.abs(plant_col - col);
                        isRun = false;
                        plantBeJumped_id = plant_id;
                    }
                }
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isRun;
    }
    public boolean cheak_eat(int row, double col){
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
                if(plant_col - col < 115 && plant_col - col > 45){
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
