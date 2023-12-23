package com.example.plantvszombie;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.sql.*;

public abstract class Plant {
    //私有变量
    public int id;
    private int row;
    private double col;
    private AnchorPane parentpane;
    public Connection conn;

    public Plant(int row, double col, AnchorPane parentPane) throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/GameDB?useSSL=false","root","Zxx20040806*");

        //调用数据库存放row col 和 hp(以防为了存档的时候用)
        // 插入数据到数据库表 plant_data
        String sqlQuery = "INSERT INTO plant_data  VALUES (NULL,NULL,?, ?, ?)"; // 替换成正确的表名和列名
        PreparedStatement pstmt = conn.prepareStatement(sqlQuery,Statement.RETURN_GENERATED_KEYS);
        pstmt.setInt(1, row);
        pstmt.setDouble(2, col);
        pstmt.setInt(3, 300);

        this.row = row;
        this.col = col;
        this.parentpane = parentPane;

        // 执行插入操作
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Data inserted successfully.");

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } else {
            System.out.println("Failed to insert data.");
        }

        // 关闭连接
        pstmt.close();
    }

    //函数使用

    public int getRow(){
        return row;
    }

    public double getCol() {
        return col;
    }

    public int getHP() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT current_hp FROM plant_data WHERE id = ?");
        pstmt.setInt(1, id);
        ResultSet resultSet = pstmt.executeQuery();
        if(resultSet.next()){
            return resultSet.getInt("current_hp");
        }else{
            return 0;
        }
    }

    public abstract void cleanup() throws SQLException;

    public AnchorPane getParentpane() {
        return parentpane;
    }
}
