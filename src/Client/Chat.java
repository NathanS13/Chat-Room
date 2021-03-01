/** Charles Ford, Michael Handrock, Nathan Schaefer, Rachel Young
 *  Chat Class
 *
 *  Chat is the class that controls Incoming, and Outgoing data, as well as mainly update the GUI.
 *  It's also responsible for giving the chat client user functionality (Buttons) that perforcs
 *  actions to the server.
 */
package Client;
import Misc.Command;
import Server.ChatMessage;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/** The chat class was made into a thread as it needs send constant messages to the server
 *  automatically. This also let's the chat client proxy update depending server responses.
 */
public class Chat extends Thread {
    private Chat_Client_Proxy client;
    private Client_Display display;
    private boolean run;
    public boolean acceptedUsername;
    public boolean usernameSet;
    private boolean stressTest;
    private int updateTimer;
    private String username;
    private String otherUsername;
    private LinkedList<ChatMessage> history;
    private LinkedList<String> usersSeen;

    /** The chat constructor is used to, upon its creation set a few different inputs based
     *  on the command line that prompts the user upon running/starting ChatClient. As well
     *  as initialize some variables needed for functionality
     * @param serverIP - Server IP the user input on CL
     * @param portNumber - Server Port the user input on CL
     * @param username - Users initial desired username. Can be rejected.
     * @param updateTimer - Default is 1 second (1000 milliseconds) for standard clients.
     *                      This is for ChatStressTest
     * @param startGUI - If normal chat client, runs a gui for user.
     */
    public Chat(String serverIP, int portNumber, String username, int updateTimer, boolean startGUI)
            throws IOException, InterruptedException {

        this.client = new Chat_Client_Proxy(this, serverIP, portNumber);
        this.run = true;
        this.acceptedUsername = false;
        this.usernameSet = false;
        this.username = username;
        this.otherUsername = "";
        this.history = new LinkedList<>();
        this.usersSeen = new LinkedList<>();
        this.updateTimer = updateTimer;
        this.stressTest = !startGUI;

        initializeConnection(); //Waits for a connection to be established.

        if (client.clientOutput != null) {
            System.out.println("Connection Accepted!");

            initializeUsername(); //Verifies input username, rejects if repeat.

            //Start GUI if standard chat client. False if stress test running.
            if (startGUI) {
                display = new Client_Display(this, this.username);
                display.drawGUI(new Stage());
            }

            run(); //start thread
        }

        else
            System.out.println("Connection Timed Out! Please Check IP or Port Number.");
    }

    /**
     * Connects our client proxy created in constructor to server proxy. Sets 10 second
     * time out for connection time. If connects, returns successful message, otherwise
     * a connection failed message.
     */
    private void initializeConnection() throws InterruptedException {
        Long tempTime = System.currentTimeMillis();
        Long timeOut = System.currentTimeMillis();

        while (client.clientOutput == null && (timeOut - tempTime) < 10000) {
            sleep(100);
            timeOut = System.currentTimeMillis(); //update timeout timer.
        }
    }

    /**
     * Checks if users' desired username is in use or not from the server. If server accepts
     * username, changed global username to desired, otherwise keeps prompting user for a new one.
     */
    private void initializeUsername() throws IOException, InterruptedException {
        synchronized (this) {
            while (!usernameSet) {
                client.clientOutput.writeObject(new Object[] {Command.checkUsername, this.username});
                wait(); //wait for server response
                if (acceptedUsername) {
                    usernameSet = true; //Username successfully set
                    System.out.println("Username set at: " + this.username);
                }
                else {
                    //prompt user for a new one until picks one available
                    System.out.println("Username in use. Please enter a new one: ");
                    Scanner scan = new Scanner(System.in);
                    this.username = scan.nextLine(); //User inputs another name
                }
            }
        }
    }

    /**
     * The automated client check. After username and connection initialization, constructor
     * call this thread to run throughout the lifetime of the process. The client will send
     * "pings" to the server to check for any new users or messages that the client needs to
     * update to the GUI or the lists on the backend.
     */
    public void run() {
        new Thread(() -> {
            while (run) {
                try {
                    client.clientOutput.writeObject(new Object[]{Command.UpdateUsers}); //check new users
                    client.clientOutput.writeObject(new Object[]{Command.UpdateMessages, username, otherUsername}); //check new messages for selected user
                    sleep(updateTimer); //Update timer based on CL input. (1 second for normal clients)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * GUI calls this function. When the chat client user double clicks a name off of the
     * list, it calls this function to change the "otherUsername" to who they wish to chat with.
     * So now the run thread will fetch updated messages to display for chat client user based on who
     * they select.
     * @param otherUsername - User you're chatting with.
     */
    public void setOtherUsername(String otherUsername) {
        this.otherUsername = otherUsername;
    }

    /**
     * Used in display to update the current user we're chatting with. GUI Indicator.
     * @return
     */
    public String getOtherUsername() {
        return this.otherUsername;
    }

    /**
     * Used to get user's set username. Used for ChatStressTest so we can simulate
     * messaging between users.
     * @return - The current username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Function called within Chat_Client_Proxy.java . This is used to update the user list
     * within the Chat Client GUI. It simply compares its current user list with the
     * new user list returned. This is done to not constantly re-add previous work done to gui.
     * @param names - Client Proxy (server response) calls this with list of users that exist in server.
     */
    public void updateUserList(LinkedList<String> names) {
        int temp = usersSeen.size();
        for (int i = 0; i < temp; i++) {
            names.pop(); //keep popping until list potentially has new users
        }
        usersSeen.addAll(names); //update active user list

        if (!stressTest) {
            Platform.runLater(
                    () -> {
                        // Update UI here. If new users they are sent to display.
                        display.addUser(names);
                    }
            );
        }
    }

    /**
     * Function called within Chat_Client_Proxy.java . This is used to update the message list
     * within the Chat Client GUI. It simply compares its current message list with the
     * new message list returned. This is done to not constantly re-add previous work done to gui.
     * @param messages - Client Proxy (server response) calls this with list of messages that exist in server.
     */
    public void updateHistory(LinkedList<ChatMessage> messages) {
        int temp = history.size();
        for (int i = 0; i < temp; i++) {
            if (!messages.isEmpty())
                messages.pop(); //keep popping until list potentially has new messages
        }
        history.addAll(messages); //update current message list

        //This following block of code is used for ChatStressTest. We do this to measure
        //message delivery from sender to receiver.
        if (stressTest) {
            temp = messages.size();
            for (int i = 0; i < temp; i++) {
                ChatMessage tempMessage = messages.get(i);
                if (tempMessage.getUser2().equals(username))
                    System.out.println(System.currentTimeMillis() - tempMessage.getSenderTime());
            }
        }

        if (!stressTest) {
            Platform.runLater(
                    () -> {
                        // Update UI here. If new messages they are sent to display.
                        display.addMessages(messages);
                    }
            );
        }
    }

    /**
     * Similar to updateHistory, however this function is used in display when the user
     * decides to change chant windows. This means we have to get completely update the message
     * list for the user, that we want to chat with.
     * @return - List of messages to display in GUI.
     */
    public LinkedList<ChatMessage> getAllHistory() {
        //We wait for server to return our new message list that way
        //we don't display messages from wrong user.
        synchronized (this) {
            try {
                client.clientOutput.writeObject(new Object[]{Command.GetAllMessages, username, otherUsername});
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.history;
    }

    /**
     * Corresponds to to getAllHistory(). Chat_Client_Proxy.java uses this function to update
     * the message list we want with what the server response returns to us.
     * @param history - Message list of selected user.
     */
    public void setAllHistory(LinkedList<ChatMessage> history) {
        this.history = history;
    }

    /**
     * Sends String message to server for a new message to be created. Client_Display uses this
     * on button "Enter" or used directly in ChatStressTest.
     * @param toUser - Desired user to message.
     * @param message - Message entered in textfield.
     */
    public void sendMessage(String toUser, String message){

        try {
            client.clientOutput.writeObject(new Object[] {Command.SendMessage, username, toUser, message, System.currentTimeMillis()});
            client.clientOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used in ChatStressTest.java to force the clients to fetch message updates.
     * This is useful for when the stress test is trying to simulate 10,000 clients.
     * Normally the client would take ~300 seconds to update, but this method eliminates
     * this delay.
     * @throws IOException
     */
    public void forceUpdate() throws IOException {
        client.clientOutput.writeObject(new Object[]{Command.UpdateMessages, username, otherUsername});
    }
}
