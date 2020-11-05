import java.io.*;
import java.net.BindException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerClientThread extends Thread{
    Socket clientSocket;
    int clientID;
    Command[] commands;
    ArrayList<Person> list_reference;
    Status server_status;

    ServerClientThread(Socket socket, int clientID, Command[] commands, ArrayList<Person> list, Status server_status){
        clientSocket = socket;
        this.clientID = clientID;
        this.commands = commands;
        this.list_reference = list;
        this.server_status = server_status;
    }

    public void run(){
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            Command[] commands_on_collection = {
                    new AddElementCommand(),
                    new UpdateElementOnIDCommand(),
                    new InsertElementAtIndexCommand(),
                    new AddElementIfMaxCommand()
            };

            ArrayList<Person> list = list_reference;
            String input_line = "";
            String reply_line;
            while(!input_line.equals("exit")){
                dataOutputStream.writeUTF("> ");
                String command_name = dataInputStream.readUTF()  + " oopses_cather";
                boolean correct_command = false;
                for (Command command_iterator : commands) {
                    if (command_iterator.inline_name().equals(command_name.split("\\s+")[0])) {
                        boolean command_on_collection = false;
                        for(Command command_on_collection_iterator : commands_on_collection)
                            if (command_on_collection_iterator.inline_name().equals(command_name.split("\\s+")[0])){
                                command_on_collection = true;
                                break;
                            }
                        if(command_on_collection) {
                            ArrayList<Person> buffer_list = command_iterator.execute(new ArrayList<Person>(), dataInputStream, dataOutputStream, command_name.split("\\s+")[1]);
                            Person new_person = buffer_list.get(0);
                            new_person.id = list.size() + 1;
                            list.add(new_person);
                        }
                        else
                            list = command_iterator.execute(list, dataInputStream, dataOutputStream, command_name.split("\\s+")[1]);

                        correct_command = true;
                        break;
                    }
                }

                if(!correct_command){
                    dataOutputStream.writeUTF("invalid command");
                }
            }
            dataInputStream.close();
            clientSocket.close();
            System.out.println("Closed connection with client #" + clientID);
        } catch (BindException e) {
//                System.out.print(".");
        } catch (SocketException e) {
            System.out.println("Lost connection with client #" + clientID);
        } catch (EOFException e) {
            System.out.println("Closed connection with client #" + clientID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
