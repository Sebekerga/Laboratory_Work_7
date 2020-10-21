import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args){
        try {
            Socket socket = new Socket("localhost",3333);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            String input_line = "";
            String reply_line = "";
            while(!input_line.equals("stop")){
                input_line = bufferedReader.readLine();
                outputStream.writeUTF(input_line);
                outputStream.flush();
                reply_line = inputStream.readUTF();
                System.out.println("Server says: " + reply_line);
            }

            outputStream.close();
            socket.close();

        } catch (IOException e) {
            System.out.println("Unable to establish connection");
        }
    }

//    static boolean sendMessage(String message, DataOutputStream outputStream){
//        try {
//            outputStream.writeUTF(message);
//            outputStream.flush();
//        } catch (IOException e) {
//            System.out.println("Unable to send message!");
//            return false;
//        }
//        return true;
//    }
//
//    static String readMessage(DataInputStream inputStream){
//        try {
//            return inputStream.readUTF();
//        } catch (IOException e) {
//            return "";
//        }
//    }
}
