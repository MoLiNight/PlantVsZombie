package com.example.plantvszombie;
import javafx.scene.layout.AnchorPane;

import java.sql.*;



public class Zom {
    public static int Zombie_sum = 0;
    public int row;
    public int id;
    public double col;
    public int attack;
    public int speed;
    public boolean alive;
    public int plantBeAttacked_id;
    // die_reason 1 : be eaten / be crushed
    // die_reason 2 : be fried
    public int die_reason;
    //---------------------------------controller
    public AnchorPane anchorPane;
    //---------------------------------sql
    public static Connection conn;

    static {
        conn = null;
        try {
            if(conn == null || conn.isClosed()){
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/GameDB?useSSL=false","root","Zxx20040806*");
                System.out.println("数据库连接成功");
            }
        } catch (SQLException e) {
            System.out.println("数据库连接失败");
            e.printStackTrace(); // 打印异常信息
        }
    }

    public Zom(int r, double c, int a, int s, AnchorPane pane) {
        super();
        this.id = Zombie_sum;
        Zombie_sum += 1;
        this.row =r;
        this.col = c;
        this.attack = a;
        this.speed = s;
        this.alive = true;
        die_reason = 0;
        this.anchorPane = pane;

        try {
            String sqlQuery = "insert into zombie_data(id, myrow, mycol, die_reason) values(?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sqlQuery);

            // 设置参数
            pstmt.setInt(1, id);
            pstmt.setInt(2, row); // 设置myrow
            pstmt.setDouble(3, col); // 设置mycol
            pstmt.setInt(4, 0);
            // 执行更新操作
            pstmt.executeUpdate();

            // 关闭连接和Statement对象
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
