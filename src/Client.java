import java.io.*;
import java.net.*;
import java.util.Scanner;

enum INPUT_REQUEST_TYPE{
    COMMAND,
    ADDITIONAL,
    NONE
}

public class Client {

    public static void main(String[] args){
        try {
            Socket socket = new Socket("localhost",3333);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            String input_line = "";
            String reply_line = "";

            Command[] commands = {
                    new HelpCommand(),
                    new InfoCommand(),
                    new ShowCommand(),
                    new AddElementCommand(),
                    new UpdateElementOnIDCommand(),
                    new RemoveByIdCommand(),
                    new ClearCollectionCommand(),
                    new SaveCollectionCommand(),
//                new ExitCommand(),
                    new InsertElementAtIndexCommand(),
                    new AddElementIfMaxCommand(),
                    new ShuffleCommand(),
                    new MaxByHeight(),
                    new PrintFieldAscendingHeightCommand(),
                    new PrintFieldDescendingHeightCommand()
            };

            while(!input_line.equals("exit")){
                reply_line = inputStream.readUTF();
                System.out.print(reply_line);

                INPUT_REQUEST_TYPE input_request_type = INPUT_REQUEST_TYPE.NONE;
                if(reply_line.charAt(0) == '>')
                    input_request_type = INPUT_REQUEST_TYPE.COMMAND;
                for(int i = reply_line.length() - 1; i > 0; i--){
                    if(reply_line.charAt(i) == ':') {
                        input_request_type = INPUT_REQUEST_TYPE.ADDITIONAL;
                        break;
                    }
                    else if(reply_line.charAt(i) != ' ') {
                        break;
                    }
                }
//                switch (input_request_type){
//                    case COMMAND:
//
//                        break;
//                    case ADDITIONAL:
//
//                        break;
//                    case NONE:
//
//                        break;
//                }
                if(input_request_type != INPUT_REQUEST_TYPE.NONE) {
                    input_line = bufferedReader.readLine();

                    boolean command_available = false;
                    for(Command command : commands)
                        if(command.inline_name().equals(input_line)){
                            command_available = true;
                            break;
                        }
                    if(command_available || input_request_type != INPUT_REQUEST_TYPE.COMMAND) {
                        outputStream.writeUTF(input_line);
                        outputStream.flush();
                    }else
                        System.out.println("invalid command");
                }else
                    System.out.print("\n");
            }

            outputStream.close();
            socket.close();

        } catch (IOException e) {
            System.out.println("Unable to establish connection");
        }
    }
}
