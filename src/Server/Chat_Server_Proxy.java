/** Charles Ford, Michael Handrock, Nathan Schaefer, Rachel Young
 *  Chat_Client_Proxy Class
 *
 *  Chat_Server_Proxy is responsible for receiving commands from Chat_Client_Proxy and
 *  sending the Client socket the response from server.java. Responses that need action
 *  call commands within Chat_Client_Proxy or functions within Server.java .
 */
package Server;
import Misc.Command;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Chat_Server_Proxy extends Thread {
    private ServerSocket serverSocket;
    private Socket acceptSocket;
    private Server server;
    private int portNumber;
    private boolean run;
    private LinkedList<Socket> tempList = new LinkedList();

    /**
     * Initialize server connection for incoming proxy clients.
     * @param server - Referenced server.java file that server proxy references.
     * @param portNumber - Hosted port-
     */
    public Chat_Server_Proxy(Server server, int portNumber) {
        this.run = true;
        this.server = server;
        this.portNumber = portNumber;
        start();
        startChatServer();
    }

    /**
     * Start new thread when new client proxy connection is received.
     */
    public void startChatServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(portNumber);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    acceptSocket = serverSocket.accept();
                    startChatThread(acceptSocket);

                    //add unique socket to container.
                    //each socket has the outputstream; IE there connection
                    tempList.add(acceptSocket);
                    System.out.println(tempList);


                    //can select socket based on connection info from list, send a message in the stream
                    LinkedList<String> userList = server.getUserList();
                    new ObjectOutputStream(tempList.getFirst().getOutputStream()).writeObject(new Object[] {Command.UserList, userList});
                            //serverOutput.writeObject(new Object[] {Command.UserList, userList});

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Runs server thread while client connection exists. Able to write commands back to client
     * proxy with proper responses. Switch statement is used for incoming commands from client proxy's
     * output. Writes back with server output.
     * @param clientSocket
     */
    public void startChatThread(Socket clientSocket) {
        new Thread(() -> {
            try {
                ObjectOutputStream serverOutput = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream serverInput = new ObjectInputStream(clientSocket.getInputStream());

                while (run && clientSocket.isConnected()) {
                    Object[] message = (Object[]) serverInput.readObject();
                    Command command = (Command) message[0];
                    switch (command) {

                        //new message received
                        case SendMessage:
                            server.newMessage((String) message[1], (String) message[2], (String) message[3], (Long) message[4]);
                            break;

                        //fetch user list
                        case UpdateUsers:
                            LinkedList<String> userList = server.getUserList();
                            serverOutput.writeObject(new Object[] {Command.UserList, userList});
                            break;

                        //fetch new message list for given user
                        case UpdateMessages:
                            LinkedList<ChatMessage> search = server.getMessageHistory((String) message[1], (String) message[2]);
                            serverOutput.writeObject(new Object[] {Command.ReceiveMessage, search});
                            break;

                        //fetch all message list for given user
                        case GetAllMessages:
                            LinkedList<ChatMessage> search2 = server.getMessageHistory((String) message[1], (String) message[2]);
                            serverOutput.writeObject(new Object[] {Command.GetAllMessages, search2});
                            break;

                        //checks if received username is available and returns response
                        case checkUsername:
                            serverOutput.writeObject(new Object[] {Command.checkUsername,
                                    server.checkUsername(message[1].toString())});
                            break;
                    }
                    serverOutput.reset();
                }
                clientSocket.close();
            } catch (IOException e) {
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }


}
