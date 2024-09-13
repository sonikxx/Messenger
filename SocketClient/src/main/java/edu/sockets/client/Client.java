package edu.sockets.client;

import edu.sockets.messageJSON.ConverterJSON;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    public Client(int port) {
        try {
            scanner = new Scanner(System.in);
            this.socket = new Socket("localhost", port);
            InputStream inputStream = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = socket.getOutputStream();
            out = new PrintWriter(outputStream, true); // auto flush()
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void start() {
        try {
            ThreadReader threadReader = new ThreadReader(in);
            ThreadWriter threadWriter = new ThreadWriter(out, scanner);
            threadReader.start();
            threadWriter.start();
            threadReader.join();
            threadWriter.interrupt(); // прерывание потока записи
        } catch (Exception e) {
            closeAll();
            System.err.println("Error: " + e.getMessage());
            System.exit(-1);
        }
    }

    private void closeAll() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private class ThreadReader extends Thread {
        private BufferedReader reader;

        public ThreadReader(BufferedReader reader) {
           this.reader = reader;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String input = reader.readLine();
                    String messageString = ConverterJSON.deserialisation(input).getText();
                    System.out.println(messageString);
                    if ("You have left the chat.".equals(messageString) || messageString.startsWith("Error:")) {
                        System.exit(-1);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(-1);
            }
        }
    }

    private class ThreadWriter extends Thread {
        private PrintWriter writer;
        private Scanner scanner;

        public ThreadWriter(PrintWriter writer, Scanner scanner) {
            this.writer = writer;
            this.scanner = scanner;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String input = scanner.nextLine();
                    writer.println(ConverterJSON.serialisation(input, 0L));
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(-1);
            }
        }
    }
}
