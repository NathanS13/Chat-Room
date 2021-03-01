/** Charles Ford, Michael Handrock, Nathan Schaefer, Rachel Young
 *  Chat_Client_Proxy Class
 *
 *  Client Proxy is responsible for receiving commands from Chat.java and
 *  sending the server socket the command. Responses that need action
 *  call functions within chat.java
 */
package Client;
import Misc.Command;
import Server.ChatMessage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class Chat_Client_Proxy extends Thread {
    private Socket clientSocket;
    public ObjectInputStream clientInput;
    public ObjectOutputStream clientOutput;
    private Chat chat;
    int portNumber;
    boolean run;
    private String serverIP;

    /**
     * Initialize client connection to proxy server
     * @param chat - Used so we're able to update client.
     * @param serverIP - Desired IP connection
     * @param portNumber - Desired Port Connection
     */
    public Chat_Client_Proxy(Chat chat, String serverIP, int portNumber) {
        this.chat = chat;
        this.portNumber = portNumber;
        this.run = true;
        this.serverIP = serverIP;
        start();
    }

    /**
     * The run method of the Chat Client Proxy thread is used to send a message
     * to its server proxy thread. Since messages are written
     * in arrays that have the first object as the enum Command, a switch is then
     * used on the first object to determine what commands to call on from the server response.
     * The other objects in the array are used for the paramaters of the methods that
     * will soon be called.
     */
    public void run() {
        try {
            clientSocket = new Socket(serverIP, portNumber);
            clientOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            synchronized (chat) {
                chat.notifyAll();
            }
            while (run && clientSocket.isConnected()) {
                if (clientInput == null) {
                    clientInput = new ObjectInputStream(clientSocket.getInputStream());
                }
                Object[] message = (Object[]) clientInput.readObject();
                Command command = (Command) message[0];
                switch (command) {

                    //Server Responses with action required in chat.java
                    case UserList: //Update Chat.java user list
                        chat.updateUserList((LinkedList<String>) message[1]);
                        break;

                    case ReceiveMessage: //update Chat.java message list
                        chat.updateHistory((LinkedList<ChatMessage>) message[1]);
                        break;

                    case GetAllMessages: //update Chat.java. See corresponding function setAllHistory()
                        synchronized (chat) {
                            chat.setAllHistory((LinkedList<ChatMessage>) message[1]);
                            chat.notifyAll();
                        }
                        break;

                    //Return response if users' desired name is available. Unlock the
                    //thread after response received
                    case checkUsername:
                        synchronized (chat) {
                            if ((boolean)message[1]) {
                                chat.acceptedUsername = true;
                                System.out.println("Name was Accepted!");
                            }
                            else {
                                System.out.println("Name was Rejected!");
                            }
                            chat.notifyAll(); //unlock thread
                        }
                        break;
                }
            }
            clientSocket.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }
}
