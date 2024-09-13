package edu.sockets.server;

import edu.sockets.messageJSON.ConverterJSON;
import edu.sockets.models.Message;
import edu.sockets.models.Room;
import edu.sockets.models.User;
import edu.sockets.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import lombok.Getter;

@Component("Server")
public class Server {
    private UsersService usersService;
    private ServerSocket serverSocket;
    private List<ClientThread> clients = new ArrayList<>();

    @Autowired
    public Server(UsersService usersService) {
        this.usersService = usersService;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket client = serverSocket.accept();
                ClientThread clientThread = new ClientThread(usersService, client);
                clients.add(clientThread);
                clientThread.start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(-1);
        }
    }

    public class ClientThread extends Thread {
        private UsersService usersService;
        @Getter
        private Socket client;
        private BufferedReader in;
        @Getter
        private PrintWriter out;
        private boolean runClient;
        private User user;
        @Getter
        private Long roomId;

        public ClientThread(UsersService usersService, Socket client) {
            this.usersService = usersService;
            this.client = client;
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = client.getInputStream();
                in = new BufferedReader(new InputStreamReader(inputStream));
                OutputStream outputStream = client.getOutputStream();
                out = new PrintWriter(outputStream, true); // auto flush()
                out.println(ConverterJSON.serialisation("Hello from Server!", 0L));
                runClient = true;
                while (runClient) {
                    out.println(ConverterJSON.serialisation("1. SignIn\n" + "2. SignUp\n" + "3. Exit", 0L));
                    String clientCommand = ConverterJSON.deserialisation(in.readLine()).getText();
                    switch (clientCommand) {
                        case "1":
                            signIn();
                            break;
                        case "2":
                            signUp();
                            break;
                        case "3":
                            stopClient();
                            break;
                        default:
                            out.println(ConverterJSON.serialisation("Wrong :( try again", 0L));
                    }
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(-1);
            }
        }

        private void signUp() {
            try {
                out.println(ConverterJSON.serialisation("Enter username:", 0L));
                String userName = ConverterJSON.deserialisation(in.readLine()).getText();
                out.println(ConverterJSON.serialisation("Enter password:", 0L));
                String userPassword = ConverterJSON.deserialisation(in.readLine()).getText();
                usersService.signUp(userName, userPassword);
                out.println(ConverterJSON.serialisation("Successful signUp!", 0L));
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                out.println("Error: " + e.getMessage());
            }
        }

        private void signIn() {
            try {
                out.println(ConverterJSON.serialisation("Enter username:", 0L));
                String userName = ConverterJSON.deserialisation(in.readLine()).getText();
                out.println(ConverterJSON.serialisation("Enter password:", 0L));
                String userPassword = ConverterJSON.deserialisation(in.readLine()).getText();
                if (usersService.signIn(userName, userPassword) != null) {
                    out.println(ConverterJSON.serialisation("Successful signIn!", 0L));
                    user = usersService.getUser(userName).get();
                    printLastMessages(user);
                    roomMenu();
                } else {
                    out.println(ConverterJSON.serialisation("Invalid username or password :(", 0L));
                    stopClient();
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                out.println("Error: " + e.getMessage());
            }
        }

        private void printLastMessages(User user) {
            if (user.getLastRoomId() != -1L) {
                out.println(ConverterJSON.serialisation("Last messages:", 0L));
                List<Message> listMessages = usersService.getMessages(user.getLastRoomId());
                int endIndex = Math.min(30, listMessages.size());
                for (Message message : listMessages.subList(0, endIndex)) {
                    out.println(ConverterJSON.serialisation(message.toString(), 0L));
                }
            }
        }

        private void roomMenu() {
            while (runClient) {
                out.println(ConverterJSON.serialisation("1. Create room\n" + "2. Choose room\n" + "3. Exit", 0L));
                try {
                    String clientCommand = ConverterJSON.deserialisation(in.readLine()).getText();
                    switch (clientCommand) {
                        case "1":
                            createRoom();
                            break;
                        case "2":
                            chooseRoom();
                            break;
                        case "3":
                            stopClient();
                            break;
                        default:
                            out.println(ConverterJSON.serialisation("Wrong :( try again", 0L));
                    }
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                    out.println("Error: " + e.getMessage());
                }
            }
        }

        private void createRoom() throws IOException {
            out.println(ConverterJSON.serialisation("Enter room name:", 0L));
            try {
                String roomName = ConverterJSON.deserialisation(in.readLine()).getText();
                roomId = usersService.createRoom(user, roomName);
                out.println(ConverterJSON.serialisation("Successful create room!", 0L));
                usersService.updateRoom(user, roomId);
                sendMessages();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                out.println("Error: " + e.getMessage());
            }
        }

        private void chooseRoom() {
            List<Room> rooms = usersService.findAllRooms();
            try {
                while (runClient) {
                    for (int i = 1; i <= rooms.size(); ++i) {
                        out.println(ConverterJSON.serialisation(i + ". " + rooms.get(i - 1).getName(), 0L));
                    }
                    out.println(ConverterJSON.serialisation((rooms.size() + 1) + ". Exit", 0L));
                    String input = ConverterJSON.deserialisation(in.readLine()).getText();
                    Integer roomNumber = Integer.parseInt(input);
                    if (roomNumber == rooms.size() + 1) {
                        stopClient();
                        break;
                    }
                    if (roomNumber <= 0 || roomNumber > rooms.size() + 1)
                        out.println(ConverterJSON.serialisation("Incorrect number", 0L));
                    roomId = rooms.get(roomNumber - 1).getId();
                    usersService.updateRoom(user, roomId);
                    sendMessages();
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                out.println("Error: " + e.getMessage());
            }
        }

        private void sendMessages() {
            try {
                out.println(ConverterJSON.serialisation("Start messaging:", 0L));
                while (runClient) {
                    String text = ConverterJSON.deserialisation(in.readLine()).getText();
                    if (text.equals("Exit"))
                        stopClient();
                    else {
                        usersService.createMessage(user, text, roomId);
                        for (ClientThread otherClient: clients)
                            if (Objects.equals(roomId, otherClient.getRoomId()))
                                otherClient.getOut().println(ConverterJSON.serialisation(user.getEmail() + ": " + text, 0L));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                out.println("Error: " + e.getMessage());
            }
        }

        private void stopClient() {
            this.runClient = false;
            out.println(ConverterJSON.serialisation("You have left the chat.", 0L));
            synchronized (clients) {
                Iterator<ClientThread> iterator = clients.iterator();
                while (iterator.hasNext()) {
                    ClientThread otherClient = iterator.next();
                    if (client.equals(otherClient.getClient())) {
                        iterator.remove();
                    }
                }
            }
            try {
                client.close();
                in.close();
                out.close();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ClientThread that = (ClientThread) obj;
            return this.client.equals(that.client);
        }

        @Override
        public int hashCode() {
            return Objects.hash(client);
        }
    }

}