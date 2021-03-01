import Client.Chat;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Scanner;
import static java.lang.Thread.sleep;

public class ChatStressTest extends Application {

    HashMap<Integer, Chat> clientMap = new HashMap<Integer, Chat>();
    private static String serverIP; //defaults to localhost
    private static int port; //Port number of server
    private static int numberOfConnections; //Username in server

    public static void main(String[] args) {
        String[] getargs = new String[3]; //Passed to setArgs
        Scanner scan = new Scanner(System.in); //Reading missing arguments
        String[] ARGNAMES = {"IP Address:","Port Number:","Number of Connections:"};
        for (int i = 0;i < 3;i++) {
            if (args.length > i) {getargs[i] = args[i];}
            else {
                System.out.println(ARGNAMES[i]); //Print appropriate prompt
                getargs[i] = scan.nextLine(); //Read user response
            }
        }
        System.out.println(getargs[0] + " " + getargs[1] + " " + getargs[2]);
        setArgs(getargs);
        launch(getargs);
    }

    // Sets static variables
    private static void setArgs(String[] strs) {
        serverIP = strs[0];
        port = Integer.parseInt(strs[1]);
        numberOfConnections = Integer.parseInt(strs[2]);
    }

    @Override
    public void stop() {
        //display.stop();
        System.exit(0);
    }

    //Makes a new Chat object
    @Override
    public void start(Stage primaryStage) throws Exception {
        int tempTime;

        //Increase update timer based on how many connections. This is done to ensure
        //when testing high volumes, we can limit resources that our chat system uses to
        //update.
        if (numberOfConnections < 1000)
            tempTime = 1000;
        else if (numberOfConnections < 10000)
            tempTime = 10000;
        else
            tempTime = 120000;

        //Start n amount of connections based on user input. Should be in intervals
        //of 10, 100, 10 000, 100 000 connections. Puts in a map so that we can
        //access the client easily for testing.
        for (int i = 0; i < numberOfConnections; i++)
            clientMap.put(i, new Chat(serverIP, port, String.valueOf(i), tempTime, false));

        System.out.println("Finished starting " + numberOfConnections + " connections!" +
                " Beginning message time analysis, please wait.");

        //create a file in lab1/doc/. Will be labeled as numberOfConnections.txt.
        //Delete file if exists already.
        String fileName = "doc/" + String.valueOf(numberOfConnections) + ".txt";
        File tempFile = new File(fileName);
        if (tempFile.exists())
            tempFile.delete();

        //Takes the console print from all clients and puts it in a text file. This
        //make it much faster and efficient to get all client responses, as they're
        //not guaranteed to respond with message times in order. File is based on name
        //given above.
        FileOutputStream tempStream = new FileOutputStream( tempFile, true);
        PrintStream out = new PrintStream(tempStream, true);
        System.setOut(out);

        //Please refer to /doc/StressTestLoop.pdf for visual explanation.
        //Splits half of total number of connections in half. The first half is
        //designated as senders. The other half are the receivers.
        //The loop will take sender 1 by 1, and send 5 messages, which is based off
        //of the halfMark offset + the senders index. If the i + halfMark set is near the end
        //of the senders limit (ie we have 50 connections, senders 19-24 will create out of
        //bounds errors) we do some other math to loop back to halfMark offset for
        //selected receiver. In doing this weird looping, we ensure all receivers get 5 messages.
        Chat user1, user2;
        int halfMark = numberOfConnections/2;
        for (int i = 0; i < halfMark; i++) {
            user1 = clientMap.get(i);
            for (int g = i; g < (i + 5); g++) {
                int receiverOffset = g + halfMark;

                if (receiverOffset >= numberOfConnections && (g < numberOfConnections)){
                    receiverOffset = g;
                }

                else if (receiverOffset >= numberOfConnections && g >= numberOfConnections) {
                    receiverOffset = g - halfMark;
                }

                //update user 1, user 2, other user and current users.
                //this is done due to the way messages are received.
                user2 = clientMap.get(receiverOffset);
                user1.setOtherUsername(user2.getUsername());
                user2.setOtherUsername(user1.getUsername());

                //Resets current message viewing. Again, due to the way messages
                //are updated.
                user1.getAllHistory();
                user2.getAllHistory();

                //Sends arbitrary message to receiver
                user1.sendMessage(user1.getOtherUsername(), "Test message!");

                //Forces the receiver to update their messages so we can get
                //consistent results across all tiers of clients. This is done because
                //update timer increases with client tier stress test so that the testers
                //computer doesn't lock up completely. Realistically we would have servers
                //to handle this large client size, and also be able to directly tell the
                //receivers client to update with the message.
                //user2.forceUpdate();
                sleep(100);
            }
            //sleep a whole update timer to ensure receivers got messages before
            //the loop iteration. This could cause erroneous results without it.
            sleep(tempTime + 100);
        }

        //Computing message times complete! Reset default output stream to console
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.out.println("Finished Sending all Messages. Now will Calculate " +
                "Average Message time for " + numberOfConnections + " clients!");

        //File IO reading reference code: https://www.journaldev.com/709/java-read-file-line-by-line
        //Following block simply just goes line by line adding each number to the sum, and
        //incrementing a counter.
        BufferedReader read;
        double messageTimeSum = 0;
        int i = 0;
        try {
            read = new BufferedReader(new FileReader(fileName));
            String line = read.readLine();
            while (line != null) {
                messageTimeSum += Integer.parseInt(line);
                i++;
                line = read.readLine();
            }
            read.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Calculate average time
        DecimalFormat df2 = new DecimalFormat("#.##");
        double avgTime = (messageTimeSum / i) / 1000;

        //Console print of results
        System.out.println("Total Messages sent: " + i);
        System.out.println("Sum of Message Time: " + messageTimeSum);
        System.out.println("Average Message Receive time: " + df2.format(avgTime) + " seconds");

        //Append results to the txt file created.
        System.setOut(out);
        System.out.println("Total Messages sent: " + i);
        System.out.println("Sum of Message Time: " + messageTimeSum);
        System.out.println("Average Message Receive time: " + df2.format(avgTime) + " seconds");

    }
}
