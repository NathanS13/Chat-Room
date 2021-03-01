/** Charles Ford, Michael Handrock, Nathan Schaefer, Rachel Young
 *  Client_Display Class
 *
 *  Responsible for handling user input and receiving updates from Chat.java
 *  GUI
 */

package Client;
import Server.ChatMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * GUI
 */
public class Client_Display {
    private Pane root = new Pane();
    private ObservableList allUserList = FXCollections.observableArrayList();
    private ListView listView = new ListView(allUserList);
    private ObservableList messageList = FXCollections.observableArrayList();
    private ListView messageView = new ListView(messageList);
    private Button sendButton = new Button();
    private int boardHeight = 450;
    private int boardWidth= 800;
    private TextField messageBox = new TextField();
    private Label allMessages = new Label();
    private Label currentUser = new Label();
    private Label genericLabel = new Label();

    //Chat.java items
    private Chat chat;
    private String username;

    /**
     * Constructor
     * @param chat - given chat
     * @param username - given username
     */
    Client_Display(Chat chat, String username) {
        this.chat = chat;
        this.username = username;
    }

    public void drawGUI(Stage stage) {
        stage.setTitle("Data Breach");
        root.setBackground(new Background(new BackgroundFill(Color.rgb(54, 57, 63), CornerRadii.EMPTY, Insets.EMPTY)));
        Rectangle grayBar = new Rectangle(0, 0, 800, 30);
        grayBar.setFill(Color.GRAY);

        //Send Message Button Bottom-Right
        sendButton.setText("Send");
        sendButton.setMinWidth(75);
        sendButton.setMinHeight(25);
        sendButton.relocate(725, 425);

        //Mesasge Box you can type in, Bottom
        messageBox.setMinHeight(25);
        messageBox.setMinWidth(575);
        messageBox.setStyle("-fx-border-color: black");
        messageBox.relocate(155, 424);

        //The chat box of all the messages users entered
        allMessages.setMinWidth(0);
        allMessages.setMinHeight(0);
        allMessages.setMaxWidth(630);
        allMessages.setMaxHeight(375);
        allMessages.setWrapText(true);
        allMessages.relocate(165, 35);
        allMessages.setStyle("-fx-font-size: 16; -fx-text-fill: white");

        //Current user label, at top Gray Bar
        currentUser.relocate(160, 4);
        currentUser.setStyle("-fx-font-size: 16; -fx-text-fill: white; -fx-font-weight: bold;");
        currentUser.setText("Current User: " + username);

        //generic lab for user list
        genericLabel.relocate(4, 4);
        genericLabel.setStyle("-fx-font-size: 16; -fx-text-fill: white; -fx-font-weight: bold;");
        genericLabel.setText("User List");

        //Add all FX to the pane
        root.getChildren().addAll(grayBar, currentUser, listView, messageView);
        root.getChildren().addAll(sendButton, genericLabel);
        root.getChildren().addAll(messageBox, allMessages);

        //Initialize the pane, and show
        stage.setScene(new Scene(root, this.boardWidth, this.boardHeight));
        stage.show();

        //Initialize User list, Message Box.
        resetList();
        emptyList();
        resetMessageList();
        emptyMessageList();

        //Button Action
        sendButton.setOnAction(e -> {
            chat.sendMessage(chat.getOtherUsername(), messageBox.getText());
            messageBox.setText("");
            messageView.scrollTo(messageList.size());
        });

        //When user double clicks user off user list
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    StringBuilder temp = new StringBuilder(listView.getSelectionModel().getSelectedItems().toString());
                    temp.deleteCharAt(temp.length() - 1);
                    temp.deleteCharAt(0);
                    chat.setOtherUsername(temp.toString());
                    resetMessageList();
                    emptyMessageList();
                    getAllMessages();
                }
            }
        });
    }

    /**
     * Adds user list to gui
     * @param messages
     */
    public void addUser(LinkedList<String> messages) {
        String temp;
        for (int i = 0; i < messages.size(); i++) {
            temp = messages.get(i);
            allUserList.add(temp);
        }
    }

    /**
     * Clear user list
     */
    public void emptyList() {
        root.getChildren().remove(listView);
        listView = new ListView(allUserList);
        root.getChildren().add(listView);
        listView.setPrefHeight(420);
        listView.setPrefWidth(155);
        listView.relocate(0, 30);
    }

    /**
     * Resets user list (Initialization)
     */
    public void resetList() {
        allUserList = null;
        allUserList = FXCollections.observableArrayList();
    }

    /**
     * Empties user list. Called when clicking on a new user. Makes needed calls
     * to chat.java for message list we need.
     */
    public void emptyMessageList() {
        messageList.add("You are talking with: " + chat.getOtherUsername());
        root.getChildren().remove(messageView);
        messageView = new ListView(messageList);
        root.getChildren().add(messageView);
        messageView.setPrefHeight(385);
        messageView.setPrefWidth(635);
        messageView.relocate(160, 35);
    }

    /**
     * Display new message for chat client
     * @param messages - Message to display
     */
    public void addMessages(LinkedList<ChatMessage> messages) {
        ChatMessage temp;
        String tempTime;
        for (int i = 0; i < messages.size(); i++) {
            temp = messages.get(i);
            tempTime = formatTime(temp.getTime());
            if (temp.getUser1().equals(username)) {
                messageList.add("<"+ tempTime +">" + " To " + temp.getUser2() + ": " + temp.getMessage());
            }

            else if (temp.getUser2().equals(username)) {
                messageList.add("<"+ tempTime +">" + " From " + temp.getUser1() + ": " + temp.getMessage());
            }
        }
    }

    /**
     * Fetches all messages for selected user. (After double clicking a user)
     */
    private void getAllMessages() {
        LinkedList<ChatMessage> history = chat.getAllHistory();
        ChatMessage temp;
        String tempTime;
        for (int i = 0; i < history.size(); i++) {
            temp = history.get(i);
            tempTime = formatTime(temp.getTime());
            if (temp.getUser1().equals(username)) {
                messageList.add("<"+ tempTime +">" + " To " + temp.getUser2() + ": " + temp.getMessage());
            }

            else if (temp.getUser2().equals(username)) {
                messageList.add("<"+ tempTime  +">" + " From " + temp.getUser1() + ": " + temp.getMessage());
            }
        }
    }

    /**
     * Clear message list. Used when clicking on a new user to chat with. Resets UI.
     */
    public void resetMessageList() {
        messageList = null;
        messageList = FXCollections.observableArrayList();
    }

    /**
     * Formats time for user display.
     * @param time - Message time
     */
    private String formatTime(Long time) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");
        Date date = new Date(time);
        return format.format((date));
    }

}


