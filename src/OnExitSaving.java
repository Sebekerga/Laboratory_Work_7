import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class OnExitSaving extends Thread {
    ArrayList<Person> collection;

    OnExitSaving(ArrayList<Person> collection){
        this.collection = collection;
    }

    public void run(){
        try{
            Writer file_writer = new FileWriter("resources/collection.xml");
            BufferedWriter buffered_file_writer = new BufferedWriter(file_writer);

            String tab = "  ";

            System.out.print("Writing into file...");
            buffered_file_writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                    "<collection>\n");
            for(Person person : collection){
                buffered_file_writer.write(tab + "<person id = \"" + person.id + "\">\n" +
                        tab+tab + "<name>" + person.name + "</name>\n" +
                        tab+tab + "<coordinates>" + "\n" +
                        tab+tab+tab + "<x>" + person.coordinates.x + "</x>\n" +
                        tab+tab+tab + "<y>" + person.coordinates.y + "</y>\n" +
                        tab+tab + "</coordinates>\n" +
                        tab+tab + "<creationDate>" + person.creationDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z")) + "</creationDate>\n" +
                        tab+tab + "<height>" + person.height + "</height>\n" +
                        (person.passportID == null ? "": (tab+tab + "<passportID>" + person.passportID + "</passportID>\n")) +
                        (person.eyeColor == null ? "": (tab+tab + "<eyeColor>" + person.eyeColor + "</eyeColor>\n")) +
                        (person.nationality == null ? "": (tab+tab + "<nationality>" + person.nationality + "</nationality>\n")) +
                        (person.location == null ? "":
                                (tab+tab + "<location>\n" +
                                        tab+tab+tab + "<x>" + person.location.x + "</x>\n" +
                                        tab+tab+tab + "<y>" + person.location.y + "</y>\n" +
                                        tab+tab+tab + "<z>" + person.location.z + "</z>\n" +
                                        (person.location.name == null ? "" : (tab+tab+tab + "<name>" + person.location.name + "</name>\n")) +
                                        tab+tab + "</location>\n")) +
                        tab + "</person>\n");

            }
            buffered_file_writer.write("</collection>");
            System.out.println("DONE!");

            buffered_file_writer.flush();
        } catch (IOException e) {
            System.out.println("Unable to open file for saving");
        }
    }
}