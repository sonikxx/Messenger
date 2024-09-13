# Messenger
A multithreaded application that allows you to create chats and communicate with several people at the same time through the console. <br>
The user can register or log in, create their own chat or communicate in an already created one. All customer data, as well as chat history, is stored in the database. <br>
#### Technology Stack:
- Spring
- HikariCP
- JdbcTemplate
- PostgreSQL
- JSON

## Install
Clone a repository:
```
$ git clone git@github.com:sonikxx/Messenger.git
```
Build server:
```
$ cd Messenger/SocketServer
$ mvn clean install
```
The server JAR file is started as follows:
```
$ java -jar target/socket-server.jar --port=8081
```
Build client:
```
$ cd Messenger/SocketClient
$ mvn clean install
```
The client is run separately:
```
$ java -jar target/socket-client.jar --server-port=8081
```

## Features
The user connects to the running server on the port. Then he can sign up or log in to an existing account. After authorization, the user can create a new chat or enter to an already created one, inside the chat, users begin to correspond or can leave it. When the user re-enters the application, the last 30 messages are displayed in the room the user previously visited.
<br> <br>
<img src="https://github.com/user-attachments/assets/1d8455e3-5fac-433d-b933-c92c6e42a979" width="700"/>
<br> <br>
JSON format is used for data exchange between the server and the client.

## Data storage
The database has the following structure: <br>

<img src="https://github.com/user-attachments/assets/e4c41c7f-e037-45bf-be02-e71aec6adb9b" width="550"/>

<br> <br>
When registering a user, his password is stored in the database in encrypted form using technology **Spring Security**. When you log in, it's the encrypted versions of the passwords that are compared, not the passwords themselves:
<br> <br>
<img src="https://github.com/user-attachments/assets/518cf50d-e868-475c-bfa9-27b4bd8c1585" width="600"/>


