package Server;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3333);
        System.out.println("Server's up!");

        int ID_counter = 0;
        while(true) {
            ID_counter++;
            Socket socket = serverSocket.accept();
            System.out.println("Client #" + ID_counter + " connected");
            ServerClientThread client_thread = new ServerClientThread(socket, ID_counter);
            client_thread.start();
        }
    }

}
