## How to run the program
#### Starting the Server: (Windows) [Server Must Be Run First!]

     Navigate to directory where project was saved IE "~/lab1/" with your command prompt (CMD):
     Example: "cd C:\Users\<SystemName>\Desktop\lab1"
     
     Next Type The Command:
     java -jar ChatServer.jar <port number>  OR
     java -jar ChatServer.jar
         and a prompt will come up on the console asking for your desired port number

Alternatively you can load the project in intellij and launch within the program after setting up the environment.

#### Starting Chat Clients: (Windows) [Server Must Be Run First!]

      Open another command prompt and once again navigate to the directory with CMD. (Same command above)
      Example: "cd C:\Users\<SystemName>\Desktop\lab1"

      Next Type The Command:
      java ChatClient.jar <IP Address (or localhost)> <Port Number> <User Name>  OR
      java ChatClient.jar
         and a prompt will come up on the console asking for your desired IP address,
         port number and username

Alternatively you can load the project in intellij and launch within the program after setting up the environment.

#### Chat Stress Test: (Windows) [Server Must Be Run First!]
      
      If you wish to run ChatStressTest, Start a server first (similarly above) and do the following:

      Open another command prompt and once again navigate to the directory with CMD. (Same command above)
      Example: "cd C:\Users\<SystemName>\Desktop\lab1"

      Next Type The Command:
      java ChatStressTest.jar <IP Address (or localhost)> <Port Number> <Number of Connections>  OR
      java ChatStressTest.jar
         and a prompt will come up on the console asking for your desired IP address,
         port number and number of connections

Alternatively you can load the project in intellij and launch within the program after setting up the environment.


NOTE: You must have a folder named "doc" in the same directory as the jar file. As in a folder containing the project should have "ChatStressTest.jar" and "doc" (folder) on the same level to produce results.

## Restrictions
JDK or Java 10 was used to create this program, and also required to run it. Due to Java FX issues in java 11 and later, there is unknown and erroneous behavior.

JDK 10.0.2 Download: [https://www.oracle.com/java/technologies/java-archive-javase10-downloads.html](https://www.oracle.com/java/technologies/java-archive-javase10-downloads.html)

Select relevant operating system under "Java SE Development Kit 10.0.2" table.

## How to use the server
The server should be started before any clients are run. After which, the server will simply run once a port number is selected, and itself has no interactivity.

## How to use the client
The client should have the same port input that the server was started with. The IP used will be dependant on the setup desired, and is based on the machine that the server is started on. If testing on same machine, use "localhost" for IP. Lastly just simply enter a desired username with no spaces.

Once inputs are taken, the client will attempt to connect to the specified port and IP address for 10 seconds, and will report an error if it cannot find the server. Please refer to "Tips & Guidelines Responses" tab on the wiki for setting up multiple clients on different computers.

The username selected will be displayed at the top of the GUI as "Current User: <entered name>". 

The list of users created will appears on the left column under "User List". You can select which user to talk with by double clicking their name. Once selected at the top of the message box (right under "Current User:" label) It was have a response of who you are talking with labeled as "You are talking with: <user selected>" Default client start <user selected> is blank, and you are also able to talk to your self if you desire.

Creating a message is done by typing what you wish to send in the box to the left of the "Send" button. Simply left click the box and begin typing. After you finish typing your message left click the "Send" button at the bottom right.

Please note pressing the "Enter" key doesn't send a message with the current implementation.

__________________________________________________________________________
# Classes Breakdown
Please refer to the PDF within the project directory "~\lab1\doc\Structure of Program.pdf" For a visual diagram of the client & server program flow.

# Client Side

## ChatClient
ChatClient is the main 'run' for the chat client. There are a few ways to run this (refer to 'Using The Software' on the wiki or read above). If the user doesn't provide all three requested arguments, this program will ask for any missing ones (IP address, server's port number, username). After which, ChatClient it will create an instance of the Chat object. Read more about the Chat object below.

## Chat
This is responsible for the internal logic of the chat window and initial setup, and is where all user functionality exists besides server sided actions. The constructor makes a Chat_Client_Proxy to set up the connection, checks username uniqueness from the server (Client will ask for another username if the current one is taken until a unique username is entered. Each name check is requested to the server.) and finally starts the GUI in Client_Display.

## Chat_Client_Proxy
This maintains communication between Chat and the server, it sends commands to the server and also handles commands the server sends back. It can respond to those commands by updating users and messages, or notifying the user if their name is already taken.

## Client_Display
This handles the display itself: showing received messages and allowing users to select who to chat with and send messages to. This is also where the format for messages and times are established.

## ChatStressTest
This is another way of starting clients that doesn't use the GUI, instead running a large number of clients all at once. Instead of a username, it asks for a number of connections, then starts that number of new Chat objects, which then places them in a map for testing access. Upon creating each chat object, it undergoes the same process any normal client would on start.


# Server Side

## ChatServer
ChatServer is the main 'run' for the chat server. There are a few ways to run this (refer to 'Using The Software' on the wiki or read above). If the user doesn't provide the requested argument of a port number when starting the server, this program will ask for a port number. After which, ChatServer will create an instance of Server object. Read more about the Server object below.

## Server
The server has a few key roles in the runtime of the program. It's main functionality is its Chat_Server_Proxy object (read more below), and its ability to maintain the user lists in addition to all messages created. On top of this, the server will also perform searches that the client might request, and return the requested information through the Chat_Server_Proxy.

Upon creation of this object, it will create a List of Usernames, and a list of ChatMessages. Chat_Server_Proxy is also created which has the ability to handle chat/server socket inputs and outputs. As the server will perform searches on these lists for clients requesting information, the search times are poor due to the linear linked list container with no form of indexing. This could be improved, as it's O(N) search time on both user lists and messages.

## Chat_Server_Proxy
Upon the Chat_Server_Proxy's creation, it will constantly listen for any possible new incoming client socket connections. When a new connection is received a new thread is started to handle that client socket connection for the life of that client connection.

Once the sockets between the Chat_Server_Proxy and Chat_Client_Proxy have a connection established, the thread associated with the client connection will then be able to receive and send commands needed between the chat and server relationship. For example, the client might send the command to check their requested username, in which the server will check the list of users and send a response (command) back to the client if the username was available or not. 

## ChatMessage
ChatMessage will manage the two users associated with the message, time sent, and the message itself. The object is serialized so we can send information over sockets correctly. These are stored within the servers message list.

## ChatMessageTools
ChatMessageTools was created so that the server is able to properly search through its list of messages for the matching results a client is requesting.
