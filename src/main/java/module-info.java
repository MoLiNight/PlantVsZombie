module com.example.plantvszombie {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.plantvszombie.myController to javafx.fxml;
    exports com.example.plantvszombie;
}