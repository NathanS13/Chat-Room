/** Charles Ford, Michael Handrock, Nathan Schaefer, Rachel Young
 *  Server Class
 *
 *  This class will hold the two users associated with the message, the time sent,
 *  and the message itself. Serialized so we can send over sockets.
 */
package Server;
import java.util.LinkedList;

public class Server {
    public Chat_Server_Proxy server;
    private LinkedList<String> usernames;
    private LinkedList<ChatMessage> messageData;

    /**
     * Constructor for server. Used to store messages and users. Also performs searches
     * when client requests data.
     * @param portNumber - Port number instructed to host server on.
     */
    public Server(int portNumber) {
        this.server = new Chat_Server_Proxy(this, portNumber);
        this.usernames = new LinkedList<>();
        this.messageData = new LinkedList<ChatMessage>();
        System.out.println("Server started on port: " + portNumber);
    }

    /**
     * Check if username given exists within user list. Gives a response to server proxy
     * which then gives response to the client.
     * @param name - User name to check if exists
     * @return true if available, false otherwise.
     */
    public boolean checkUsername(String name) {
        if (!usernames.contains(name)) {
            usernames.add(name);
            return true;
        }
        else
            return false;
    }

    /**
     * Get user list for client (Server Proxy Requests). Used for client GUI.
     * @return - All users in user list.
     */
    public LinkedList<String> getUserList() {
        return usernames;
    }

    /**
     * Get message history for the two provided usernames. Server Proxy requests, and
     * list is returned to client proxy.
     * @param user1 - First user to search for (To)
     * @param user2 - Second user to search for (From)
     * @return Message list for the corresponding users.
     */
    public LinkedList<ChatMessage> getMessageHistory(String user1, String user2) {
        ChatMessageTools tools = new ChatMessageTools();
        LinkedList<ChatMessage> tempMessages = tools.search(user1, user2, messageData);
        return tempMessages;
    }

    /**
     * Creates a new message when called from Server.java . Server Proxy calls this function
     * and then returns its response to Client Proxy.
     * @param user1 - User 1 sending message (To)
     * @param user2 - User 2 receiving message (From)
     * @param message
     */
    public void newMessage(String user1, String user2, String message, Long senderTime) {
        messageData.add(new ChatMessage(user1, user2, System.currentTimeMillis(), message, senderTime));
        System.out.println(messageData);
    }
}
