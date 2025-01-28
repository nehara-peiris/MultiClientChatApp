package lk.ijse.multiclientchatapp;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class EmojiPicker {

    private final String[] emojis = {
            "😀", "😂", "😍", "😎", "😢", "😡", "👍", "👎", "🎉", "❤️"
    };

    public void showEmojiPicker(TextField txtInput) {
        Stage emojiStage = new Stage();
        GridPane gridPane = new GridPane();

        int row = 0;
        int col = 0;

        for (String emoji : emojis) {
            Button emojiButton = new Button(emoji);
            emojiButton.setOnAction(event -> {
                txtInput.appendText(emoji); // Append emoji to the text input
                emojiStage.close(); // Close the emoji picker after selection
            });
            gridPane.add(emojiButton, col, row);
            col++;
            if (col > 5) {
                col = 0;
                row++;
            }
        }

        Scene scene = new Scene(gridPane, 200, 200);
        emojiStage.setScene(scene);
        emojiStage.setTitle("Select Emoji");
        emojiStage.show();
    }
}