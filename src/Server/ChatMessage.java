/** Charles Ford, Michael Handrock, Nathan Schaefer, Rachel Young
 *  ChatMessage Class
 *
 *  This class will hold the two users associated with the message, the time sent,
 *  and the message itself. Serialized so we can send over sockets.
 */
package Server;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String user1;
    private String user2;
    private Long time;
    private Long senderTime;
    private String message;
    private static final long serialVersionUID = 1L;

    /**
     * Initialize Message Object
     * @param user1 - Message Sender
     * @param user2 - Message Receiver
     * @param time - Time message sent (based on server time)
     * @param message - Message sender sent.
     */
    public ChatMessage(String user1, String user2, Long time, String message, Long senderTime) {
        this.user1 = user1;
        this.user2 = user2;
        this.time = time;
        this.message = message;
        this.senderTime = senderTime;
    }

    /**
     * Gets sender
     * @return senders username
     */
    public String getUser1() {
        return user1;
    }

    /**
     * Gets receiver
     * @return receiver username
     */
    public String getUser2() {
        return user2;
    }

    /**
     * Time message was created. Based on server's time.
     * @return message creation time.
     */
    public Long getTime() {
        return time;
    }

    /**
     * Get the message sent
     * @return Message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Used for Testing sender to receiver times. Need Client A's real send time
     * to calculate received time properly.
     * @return senders time
     */
    public Long getSenderTime() {
        return senderTime;
    }

    /**
     * Used for debugging
     * @return - formatted print of the object.
     */
    @Override
    public String toString() {
        return String.format("user1: " + user1 + " user2: " + user2 + " time: " + time + " message: " + message + "\n");
    }
}
