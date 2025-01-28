package lk.ijse.multiclientchatapp.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UsernameFormController extends Application {
    public TextField txtUName;
    public JFXButton btnSave;


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lk/ijse/multiclientchatapp/usernameForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("User");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void btnSaveOnAction(ActionEvent event) throws IOException {
        String username = txtUName.getText();


    }
}
