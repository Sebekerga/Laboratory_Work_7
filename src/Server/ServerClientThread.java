package Server;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerClientThread extends Thread{
    Socket clientSocket;
    int clientID;
    ServerClientThread(Socket socket, int clientID){
        clientSocket = socket;
        this.clientID = clientID;
    }

    public void run(){
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            String input_line = "";
            String reply_line;
            while(!input_line.equals("stop")){
                input_line = dataInputStream.readUTF();
                System.out.println("client #" + clientID + " says: " + input_line);
                dataOutputStream.writeUTF("Your ID is " + clientID);
                dataOutputStream.flush();
            }
            dataInputStream.close();
            clientSocket.close();
            System.out.println("Closed connection with client #" + clientID);
        } catch (BindException e) {
//                System.out.print(".");
        } catch (SocketException e) {
            System.out.println("Lost connection with client #" + clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
