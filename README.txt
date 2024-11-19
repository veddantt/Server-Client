Address Book Application
This project is a client-server address book application implemented in Java. It allows users to manage an address book remotely through a command-line interface, with functionality for multiple clients to connect concurrently.

Authors
Vedant Patel

Overview
The application consists of two main components:

Server (MultiThreadServer.java): Manages the address book and handles requests from multiple clients concurrently.
Client (Client.java): Connects to the server and allows users to send commands to interact with the address book.
Features
Login/Logout: User authentication for secure access to address book management commands.
Add Records: Create new entries in the address book.
Delete Records: Remove entries by record ID.
Update Records: Modify existing entries in the address book.
List Records: View all records in the address book.
Search Records: Look up entries by keywords.
View Active Users: Display a list of users currently connected to the server.
Shutdown Server: Shut down the server (available to the root user only).
Quit Client: Disconnect the client from the server.
Server (MultiThreadServer.java)
The server listens on port 4652 and manages incoming client connections in separate threads. Supported commands include:

LOGIN [UserID] [Password]: Authenticates the user.
LOGOUT: Logs the current user out.
ADD [firstName] [lastName] [phoneNumber]: Adds a new record with the provided information.
DELETE [recordID]: Deletes the record with the specified ID.
UPDATE [recordID] [newValue]: Updates a record with a new value.
LOOK [keyword]: Searches for records containing the keyword.
LIST: Displays all records in the address book.
WHO: Shows a list of active users.
SHUTDOWN: Shuts down the server (root access only).
QUIT: Disconnects the client from the server.
Client (Client.java)
The client connects to the server, allowing users to send commands through a command-line interface. It displays server responses for each command sent and automatically reconnects if disconnected.

How to Run
Compile the Java files:

On the server side: javac MultiThreadServer.java ChildThread.java
                    java MultiThreadServer

On the client side: javac Client.java
                    java Client

Use localhost if running on the same machine. Replace <server_host> with the hostname or IP address of the server.

Usage
Once connected, enter commands at the client prompt. The server will respond with results for each command.

Example Session
plaintext
Copy code
Enter command: LOGIN john john01
Server: 200 OK

Enter command: ADD Vedant Patel 586-883-1317
Server: 200 OK
The new Record ID is 1001

Enter command: LIST
Server: 200 OK
The list of records in the address book:
1001 Vedant Patel 586-883-1317

Enter command: QUIT
Server: 200 OK

Responsibility
Vedant Patel: Implemented LOGIN, ADD, LIST, WHO, and general server setup. ALso Implemented DELETE, UPDATE, SHUTDOWN, LOGOUT, and client-server communication.

