/**
 *  ChatMessageTools was created so that the server is able to properly search through
 *  its list of messages for the matching results client is requesting
 */
package Server;

import java.util.LinkedList;

public class ChatMessageTools {

    //Simply searches for is either user 1 or user 2 message either of the object in the list
    //of messages. Linear linked list. O(n) search time. Better implementations possible
    public LinkedList<ChatMessage> search (String user1, String user2, LinkedList<ChatMessage> messageList) {

        LinkedList<ChatMessage> returnList = new LinkedList<ChatMessage>();

        for (int i = 0; i < messageList.size(); i++) {

            ChatMessage temp = messageList.get(i);

            //checks if either slot has user desired
            if (user1.equals(temp.getUser1()) && user2.equals(temp.getUser2())) {
                returnList.add(temp);
            }

            else if (user2.equals(temp.getUser1()) && user1.equals(temp.getUser2())) {
                returnList.add(temp);
            }
        }
        return returnList;
    }
}
