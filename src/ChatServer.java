import Server.Server;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Scanner;


public class ChatServer extends Application {
    private Server server;
    private static int port; //Port number of server, usually 12345

    public static void main(String[] args) {
        if (args.length >= 1) {setPort(args[0]);}
        else {
            Scanner scan = new Scanner(System.in);
            System.out.println("Port Number:"); //Print appropriate prompt
            setPort(scan.nextLine()); //Read user response
        }
        launch(String.valueOf(port)); //Enters JavaFX
    }

    //Sets static port number
    private static void setPort(String p) {
        port = Integer.parseInt(p);
    }

    @Override
    public void stop() {
        //display.stop();
        System.exit(0);
    }

    //Makes a new Server object
    @Override
    public void start(Stage primaryStage) throws Exception {
        server = new Server(port);
    }
}
