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
import java.util.ArrayList;
import java.util.List;

public class ServerFormController extends Application {
    public AnchorPane root;
    public JFXTextArea chatArea;
    public TextField txtInput;
    public JFXButton btnSend;
    public ImageView imageView; // To display received image

    private ServerSocket serverSocket;
    private List<Socket> clientSockets = new ArrayList<>();
    private List<DataOutputStream> clientOutputStreams = new ArrayList<>();
    private Thread listenerThread;

    public void initialize() {
        try {
            serverSocket = new ServerSocket(4000);
            System.out.println("Server started, waiting for clients...");

            // Accept two clients
            for (int i = 0; i < 2; i++) {
                Socket socket = serverSocket.accept();
                clientSockets.add(socket);
                System.out.println("Client " + (i + 1) + " accepted");

                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                clientOutputStreams.add(dataOutputStream);

                // Start a new thread for each client
                new Thread(() -> listenToClient(socket)).start();
            }

            txtInput.textProperty().addListener((observable, oldValue, newValue) -> {
                btnSend.setDisable(newValue.trim().isEmpty());
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenToClient(Socket socket) {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String response;
            while (true) {
                // Check for message type (text or image)
                response = dataInputStream.readUTF();
                if (response.equals("TEXT")) {
                    String textMessage = dataInputStream.readUTF();
                    chatArea.appendText("Client: " + textMessage + "\n");
                    broadcastMessage("TEXT", textMessage);
                } else if (response.equals("IMAGE")) {
                    int imageLength = dataInputStream.readInt();
                    byte[] imageBytes = new byte[imageLength];
                    dataInputStream.readFully(imageBytes);

                    // Convert byte array to image and display
                    InputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                    Image image = new Image(byteArrayInputStream);
                    imageView.setImage(image);  // Display image on the server side
                    chatArea.appendText("Client sent an image.\n");
                    broadcastImage(imageBytes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String type, String message) {
        for (DataOutputStream dos : clientOutputStreams) {
            try {
                dos.writeUTF(type);
                dos.writeUTF(message);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastImage(byte[] imageBytes) {
        for (DataOutputStream dos : clientOutputStreams) {
            try {
                dos.writeUTF("IMAGE");
                dos.writeInt(imageBytes.length);
                dos.write(imageBytes);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnSendOnAction(ActionEvent event) {
        try {
            String msg = txtInput.getText();
            chatArea.appendText("Me: " + msg + "\n");

            // Send text message to all clients
            broadcastMessage("TEXT", msg);

            txtInput.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnEmojiOnAction(ActionEvent event) {
        EmojiPicker emojiPicker = new EmojiPicker();
        emojiPicker.showEmojiPicker(txtInput);
    }

    public void sendImage(byte[] imageBytes) {
        broadcastImage(imageBytes);
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