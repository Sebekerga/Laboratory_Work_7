import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class Status{
    public boolean adding_is_queued = false;
    public int clients_number = 0;
}

public class Server {

    public static void main(String[] args) throws IOException {

        ArrayList<Person> collection = new ArrayList<>();

        System.out.println("Reading file . . .");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("resources/collection.xml"));
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();
            NodeList person_list = document.getElementsByTagName("person");
            for(int i = 0; i < person_list.getLength(); i++){
                Node node = person_list.item(i);
                Element element = (Element) node;
                Person person = new Person();

                // init ID
                person.id = Integer.parseInt(element.getAttribute("id"));

                // init name
                person.name = element.getElementsByTagName("name").item(0).getTextContent();

                // init coordinates
                person.coordinates = new Coordinates();
                person.coordinates.x = Float.parseFloat(((Element) element.getElementsByTagName("coordinates").item(0))
                        .getElementsByTagName("x").item(0).getTextContent());
                person.coordinates.x = Integer.parseInt(((Element) element.getElementsByTagName("coordinates").item(0))
                        .getElementsByTagName("y").item(0).getTextContent());

                // init creation_date
                person.creationDate = ZonedDateTime.parse(element.getElementsByTagName("creationDate").item(0).getTextContent(),
                        DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z"));

                // init height
                person.height = Integer.parseInt(element.getElementsByTagName("height").item(0).getTextContent());

                // init passportID
                try {
                    person.passportID = element.getElementsByTagName("passportID").item(0).getTextContent();
                } catch(NullPointerException e){
                    person.passportID = null;
                }

                // init eyeColor
                try {
                    person.eyeColor = Color.valueOf(element.getElementsByTagName("eyeColor").item(0).getTextContent());
                } catch(NullPointerException e){
                    person.eyeColor = null;
                }

                // init nationality
                try {
                    person.nationality = Country.valueOf(element.getElementsByTagName("nationality").item(0).getTextContent());
                } catch(NullPointerException e){
                    person.nationality = null;
                }

                // init location
                try {
                    person.location = new Location();
                    person.location.x = Integer.parseInt(((Element) element.getElementsByTagName("location").item(0))
                            .getElementsByTagName("x").item(0).getTextContent());
                    person.location.y = Integer.parseInt(((Element) element.getElementsByTagName("location").item(0))
                            .getElementsByTagName("y").item(0).getTextContent());
                    person.location.z = Double.parseDouble(((Element) element.getElementsByTagName("location").item(0))
                            .getElementsByTagName("z").item(0).getTextContent());
                    try {
                        person.location.name = element.getElementsByTagName("nationality").item(0).getTextContent();
                    } catch (NullPointerException e) {
                        person.location.name = null;
                    }
                } catch (NullPointerException e){
                    person.location = null;
                }

                collection.add(person);

            }
        } catch (ParserConfigurationException e) {
            System.out.println("Error reading file, starting with blank collection");
        } catch (SAXException e) {
            System.out.println("Error reading file, starting with blank collection");
        } catch (IOException e) {
            System.out.println("Error reading file, starting with blank collection");
        }

        Runtime.getRuntime().addShutdownHook(new OnExitSaving(collection));

        System.out.println("Server's up!");
        ServerSocket serverSocket = new ServerSocket(3333);
        Status server_status = new Status();

        Command[] commands = {
                new HelpCommand(),
                new InfoCommand(),
                new ShowCommand(),
                new AddElementCommand(),
                new UpdateElementOnIDCommand(),
                new RemoveByIdCommand(),
                new ClearCollectionCommand(),
//                new SaveCollectionCommand(),
//                new ExitCommand(),
                new InsertElementAtIndexCommand(),
                new AddElementIfMaxCommand(),
                new ShuffleCommand(),
                new MaxByHeight(),
                new PrintFieldAscendingHeightCommand(),
                new PrintFieldDescendingHeightCommand()
        };

        int ID_counter = 0;
        while(true) {
            ID_counter++;
            Socket socket = serverSocket.accept();
            System.out.println("Client #" + ID_counter + " connected");
            ServerClientThread client_thread = new ServerClientThread(socket, ID_counter, commands, collection, server_status);
            client_thread.start();
        }
    }
}
