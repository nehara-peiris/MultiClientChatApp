package lk.ijse.multiclientchatapp.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.multiclientchatapp.EmojiPicker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormController extends Application {
    public AnchorPane root;
    public JFXTextArea chatArea;
    public TextField txtInput;
    public JFXButton btnSend;
    public ImageView imageView; // To display received image

    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private Thread listenerThread;

    public void initialize() {
        try {
            serverSocket = new ServerSocket(4000);
            socket = serverSocket.accept();
            System.out.println("Client accepted");

            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            listenerThread = new Thread(() -> {
                try {
                    String response;
                    while (true) {
                        // Check for message type (text or image)
                        response = dataInputStream.readUTF();
                        if (response.equals("TEXT")) {
                            String textMessage = dataInputStream.readUTF();
                            chatArea.appendText("Client: " + textMessage + "\n");
                        } else if (response.equals("IMAGE")) {
                            int imageLength = dataInputStream.readInt();
                            byte[] imageBytes = new byte[imageLength];
                            dataInputStream.readFully(imageBytes);

                            // Convert byte array to image and display
                            InputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                            Image image = new Image(byteArrayInputStream);
                            imageView.setImage(image);  // Display image on the server side
                            chatArea.appendText("Client sent an image.\n");
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

    public void btnEmojiOnAction(ActionEvent event) {
        EmojiPicker emojiPicker = new EmojiPicker();
        emojiPicker.showEmojiPicker(txtInput);
    }

    public void sendImage(byte[] imageBytes) {
        try {
            // Send image data
            dataOutputStream.writeUTF("IMAGE");
            dataOutputStream.writeInt(imageBytes.length);
            dataOutputStream.write(imageBytes);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/lk/ijse/multiclientchatapp/serverForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
