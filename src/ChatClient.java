import Client.Chat;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.Scanner;

public class ChatClient extends Application {
    private static String serverIP; //defaults to localhost
    private static int port; //Port number of server
    private static String username; //Username in server

    public static void main(String[] args) {
        String[] getargs = new String[3]; //Passed to setArgs
        Scanner scan = new Scanner(System.in); //Reading missing arguments
        String[] ARGNAMES = {"IP Address (or localhost):","Port Number:","Username:"};
        for (int i = 0;i < 3;i++) {
            if (args.length > i) {getargs[i] = args[i];}
            else {
                System.out.println(ARGNAMES[i]); //Print appropriate prompt
                getargs[i] = scan.nextLine(); //Read user response
            }
        }
        System.out.println(getargs[0] + " " + getargs[1] + " " + getargs[2]);
        setArgs(getargs); //Sets static variables
        launch(getargs); //Jumps into JavaFX
    }

    //Sets static variables
    private static void setArgs(String[] strs) {
        serverIP = strs[0];
        port = Integer.parseInt(strs[1]);
        username = strs[2];
    }

    @Override
    public void stop() {
        //display.stop();
        System.exit(0);
    }

    //Makes a new Chat object
    @Override
    public void start(Stage primaryStage) throws Exception {
        new Chat(serverIP, port, username, 1000,true);
    }
}
