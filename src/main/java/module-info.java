module lk.ijse.multiclientchatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires com.jfoenix;

    opens lk.ijse.multiclientchatapp to javafx.fxml;
    exports lk.ijse.multiclientchatapp;
    exports lk.ijse.multiclientchatapp.controller;
    opens lk.ijse.multiclientchatapp.controller to javafx.fxml;
}