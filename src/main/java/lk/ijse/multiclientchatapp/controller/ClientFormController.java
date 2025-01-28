package lk.ijse.multiclientchatapp.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.multiclientchatapp.EmojiPicker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class ClientFormController extends Application {
    public AnchorPane root;
    public JFXTextArea chatArea;
    public TextField txtInput;
    public JFXButton btnSend;
    public TextField txtUsername;

    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private Thread listenerThread;

    public void initialize() {
        try {
            Socket socket1 = new Socket("localhost", 4000);
            dataOutputStream = new DataOutputStream(socket1.getOutputStream());
            dataInputStream = new DataInputStream(socket1.getInputStream());

            Socket socket2 = new Socket("localhost", 4000);
            dataOutputStream = new DataOutputStream(socket2.getOutputStream());
            dataInputStream = new DataInputStream(socket2.getInputStream());

            // Listener thread to handle incoming messages and images
            listenerThread = new Thread(() -> {
                try {
                    while (true) {
                        String messageType = dataInputStream.readUTF();

                        if ("TEXT".equals(messageType)) {
                            // Handle text message
                            String message = dataInputStream.readUTF();
                            Platform.runLater(() -> chatArea.appendText("Server: "+ message + "\n"));

                        } else if ("IMAGE".equals(messageType)) {
                            // Handle image
                            int imageLength = dataInputStream.readInt();
                            byte[] imageData = new byte[imageLength];
                            dataInputStream.readFully(imageData);

                            // Convert byte array to image and display it in chat area
                            Image image = new Image(new ByteArrayInputStream(imageData));
                            ImageView imageView = new ImageView(image);
                            Platform.runLater(() -> {
                                chatArea.appendText("Server: [Image sent]\n");
                                VBox imageBox = new VBox(imageView);
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            listenerThread.start();

            txtInput.textProperty().addListener((observable, oldValue, newValue) -> {
                btnSend.setDisable(newValue.trim().isEmpty());
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnSendOnAction(ActionEvent event) {
        try {
            String msg = txtInput.getText();
            chatArea.appendText("Me: " + msg + "\n");

            // Send text message
            dataOutputStream.writeUTF("TEXT");
            dataOutputStream.writeUTF(msg);
            dataOutputStream.flush();

            txtInput.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sending image
    public void btnAttachOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                byte[] imageBytes = Files.readAllBytes(file.toPath());
                // Sending an identifier for image and the image data length before the image
                dataOutputStream.writeUTF("IMAGE");
                dataOutputStream.writeInt(imageBytes.length);
                dataOutputStream.write(imageBytes);
                dataOutputStream.flush();
                chatArea.appendText("Me: [Image sent]\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Emoji button functionality
    public void btnEmojiOnAction(ActionEvent event) {
        EmojiPicker emojiPicker = new EmojiPicker();
        emojiPicker.showEmojiPicker(txtInput);
    }

    public void setTxtUsername(TextField txtUsername) {
        this.txtUsername = txtUsername;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lk/ijse/multiclientchatapp/clientForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}



























