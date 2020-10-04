import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {

    static boolean sendData(String data, SocketChannel channel){
        ByteBuffer buf = ByteBuffer.allocate(48);
        buf.clear();
        buf.put(data.getBytes());

        buf.flip();

        while(buf.hasRemaining()) {
            try {
                channel.write(buf);
            } catch (IOException e) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args){
        try {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress("localhost", 1212));

            String newData = "Hello World!" + System.currentTimeMillis();
            sendData(newData, channel);
            ByteBuffer buf = ByteBuffer.allocate(48);
            int bytes_read = channel.read(buf);
            System.out.println(bytes_read);
            String line = "";
            for(int i = 0; i < bytes_read; i++){
                line += buf.get(i);
            }
            System.out.println(line);

//            channel.close();
        } catch (IOException e) {
            System.out.println("Unable to establish connection");
        }
    }
}
