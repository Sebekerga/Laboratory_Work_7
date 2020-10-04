import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {

    public static void main(String[] args){
        while(true) {
            try {
                ServerSocket serverSocket = new ServerSocket(1212);
                while (true) {
                    Socket socket = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    String line = reader.readLine();
                    System.out.println("Received: " + line);
                    outputStream.writeBytes("You said:" + line + "\n");
                }
            } catch (SocketException e) {
                System.out.println("Lost connection with client");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
