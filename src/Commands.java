import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

interface Command{
    ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands) throws IOException;
    String inline_name();
}

class HelpCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        try {
            BufferedReader help_reader = new BufferedReader(new FileReader("resources/help.txt"));
            String line;
            while ((line = help_reader.readLine()) != null) {
                outputStream.writeUTF(line);
            }
        } catch (FileNotFoundException e){
            outputStream.writeUTF("Can't really help here :(");
        } catch (IOException e) {
            outputStream.writeUTF("Didn't read that line");
        }

        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "help";
    }
}

class InfoCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z");
        String init_time_string;
        try {
            ZonedDateTime init_time = list_to_modify.get(0).creationDate;
            for(Person person : list_to_modify){
                if(person.creationDate.toInstant().compareTo(init_time.toInstant()) < 0){
                    init_time = person.creationDate;
                }
            }
            init_time_string = init_time.format(formatter);
        }catch (IndexOutOfBoundsException e){
            init_time_string = "-";
        }


        outputStream.writeUTF("Collection type: Person \n" +
                "Creation date: " + init_time_string + "\n" +
                "Number of elements: " + list_to_modify.size() + "\n" +
                "Collection saving location: C:\\some\\directory\\Collection.txt");
        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "info";
    }
}

class ShowCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z");
        if(list_to_modify.size() == 0){
            outputStream.writeUTF("Collection is empty");
        }
        for(Person person: list_to_modify){
            outputStream.writeUTF(person.id + ": " + person.name + ", (" +
                    person.coordinates.x + ", " + person.coordinates.y + "), " +
                    person.creationDate.format(formatter) + ", " +
                    person.height);
        }
        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "show";
    }
}

class AddElementCommand implements Command{

    int int_scan_for_number(String first_message, String error_message, DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(first_message);
        while (true) {
            try {
                return Integer.parseInt(inputStream.readUTF());
            } catch (NumberFormatException e){
                outputStream.writeUTF(error_message);
            }
        }
    }

    double double_scan_for_number(String first_message, String error_message, DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(first_message);
        while (true) {
            try {
                return Double.parseDouble(inputStream.readUTF());
            } catch (NumberFormatException e){
                outputStream.writeUTF(error_message);
            }
        }
    }

    float float_scan_for_number(String first_message, String error_message, DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(first_message);
        while (true) {
            try {
                return Float.parseFloat(inputStream.readUTF());
            } catch (NumberFormatException e){
                outputStream.writeUTF(error_message);
            }
        }
    }

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{

        Person new_person = new Person();


        // name initialization
        outputStream.writeUTF("Person's name: ");
        new_person.name = inputStream.readUTF();


        // time initialization
        new_person.creationDate = ZonedDateTime.now();


        // height initialization
        int height_buffer = int_scan_for_number(new_person.name + "'s height: ",
                "Please, enter whole number: ",
                inputStream, outputStream);
        while(true){
            if(height_buffer <= 0){
                height_buffer = int_scan_for_number("Please, enter correct value: ",
                        "Please, enter whole number: ",
                        inputStream, outputStream);
            }
            else {
                new_person.height = height_buffer;
                break;
            }
        }


        // coordinates initialization
        Coordinates coordinates_buffer = new Coordinates();
        coordinates_buffer.x = float_scan_for_number("Enter coordinates X value: ",
                "Please, enter correct X value: ",
                inputStream, outputStream);
        int y_buffer = int_scan_for_number("Enter coordinates Y value: ",
                "Please, enter whole number: ",
                inputStream, outputStream);
        while(true){
            if(y_buffer == 0){
                y_buffer = int_scan_for_number("Please, enter correct value: ",
                        "Please, enter whole number: ",
                        inputStream, outputStream);
            }
            else {
                coordinates_buffer.y = y_buffer;
                break;
            }
        }
        new_person.coordinates = coordinates_buffer;


        // passportID initialization
        outputStream.writeUTF(new_person.name + "'s passport ID or leave empty: ");
        new_person.passportID = inputStream.readUTF();
        if(new_person.passportID.length() == 0)
            new_person.passportID = null;


        // eyeColor initialization
        outputStream.writeUTF("Choose " + new_person.name + "'s eye color from this");
        for(Color color : Color.values())
            outputStream.writeUTF("\n" + color);
        outputStream.writeUTF(" colors or leave empty: ");
        while (true){
            try {
                String color = inputStream.readUTF().toUpperCase();
                if(color.length() == 0)
                    break;
                new_person.eyeColor = Color.valueOf(color);
                break;
            }catch (IllegalArgumentException e){
                outputStream.writeUTF("Please enter correct value for color: ");
            }
        }


        // nationality initialization
        outputStream.writeUTF("Choose " + new_person.name + "'s nationality from this");
        for(Country country : Country.values())
            outputStream.writeUTF("\n" + country);
        outputStream.writeUTF(" countries or leave empty: ");
        while (true){
            try {
                String country = inputStream.readUTF().toUpperCase();
                if(country.length() == 0)
                    break;
                new_person.nationality = Country.valueOf(country);
                break;
            }catch (IllegalArgumentException e){
                outputStream.writeUTF("Please enter correct value for country: ");
            }
        }


        // location initialization
        Location location_buffer = new Location();
        // TODO: add check for empty line
        outputStream.writeUTF("Do you want to add " + new_person.name + "'s location? (YES/anything else): ");
        if(inputStream.readUTF().equals("YES")) {
            location_buffer.x = int_scan_for_number("Enter person's location X value: ",
                    "Please, enter correct X value: ",
                    inputStream, outputStream);
            location_buffer.y = int_scan_for_number("Enter person's location Y value: ",
                    "Please, enter correct Y value: ",
                    inputStream, outputStream);
            location_buffer.z = double_scan_for_number("Enter person's location Z value: ",
                    "Please, enter correct Z value: ",
                    inputStream, outputStream);
            outputStream.writeUTF("Enter location name or leave empty: ");
            location_buffer.name = inputStream.readUTF();
            if (location_buffer.name.length() == 0)
                location_buffer.name = null;
            new_person.location = location_buffer;
        }
        else
            new_person.location = null;

        // pushing person object to the end of list
        new_person.id = list_to_modify.size() + 1;
        list_to_modify.add(new_person);
        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "add";
    }
}

class UpdateElementOnIDCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        int index = 0;
        try {
            index = Integer.parseInt(secondary_commands);
        } catch (NumberFormatException e){
            outputStream.writeUTF("Invalid index value");
        }

        Person person_buffer = null;
        for(Person person : list_to_modify){
            if(person.id == index){
                person_buffer = person;
                break;
            }
        }
        if(person_buffer == null){
            outputStream.writeUTF("No person with such index found");
            return list_to_modify;
        }

        outputStream.writeUTF("Current Person's data " +
                "\nIndex: " + person_buffer.id +
                "\nElement creation date : " +
                person_buffer.creationDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z")) +
                "\nName: " + person_buffer.name +
                "\nHeight: " + person_buffer.height +
                "\nPassportID : " + ((person_buffer.passportID == null) ? "-" : person_buffer.passportID) +
                "\nEye color: " + ((person_buffer.eyeColor == null) ? "-" : person_buffer.eyeColor.toString().toLowerCase()) +
                "\nNationality: " + ((person_buffer.nationality == null) ? "-" : person_buffer.nationality.toString().toLowerCase()) +
                "\nLocation: " + "-"); //TODO: add location parser

        AddElementCommand addElementCommand = new AddElementCommand();
        ArrayList<Person> new_person_buffer = new ArrayList<>();
        addElementCommand.execute(new_person_buffer, inputStream, outputStream, "");

        new_person_buffer.get(0).id = list_to_modify.size() + 1;
        list_to_modify.add(new_person_buffer.get(0));

        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "update";
    }
}

class RemoveByIdCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        int id = 0;
        try {
            id = Integer.parseInt(secondary_commands);
        } catch (NumberFormatException e){
            outputStream.writeUTF("Invalid ID value");
        }
        for(int i = 0; i < list_to_modify.size(); i++){
            if(list_to_modify.get(i).id == id){
                list_to_modify.remove(i);
                for(Person person : list_to_modify)
                    if(person.id > id)
                        person.id -= 1;
                return list_to_modify;
            }
        }

        outputStream.writeUTF("No person with such ID found");
        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "remove_by_id";
    }
}

class ClearCollectionCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        return new ArrayList<>();
    }

    @Override
    public String inline_name() {
        return "clear";
    }
}

class SaveCollectionCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        if(secondary_commands.equals("oopses_cather"))
            secondary_commands = "resources/collection.xml";
        try{
            Writer file_writer = new FileWriter(secondary_commands);
            BufferedWriter buffered_file_writer = new BufferedWriter(file_writer);

            String tab = "  ";

            outputStream.writeUTF("Writing into file...");
            buffered_file_writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                    "<collection>\n");
            for(Person person : list_to_modify){
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
            outputStream.writeUTF("DONE!");

            buffered_file_writer.flush();

        } catch (IOException e) {
            outputStream.writeUTF("Unable to open file for saving");
            return list_to_modify;
        }


        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "save";
    }
}

class ExitCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        System.exit(0);

        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "exit";
    }
}

class InsertElementAtIndexCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        int index = 0;
        try {
            index = Integer.parseInt(secondary_commands);
        } catch (NumberFormatException e) {
            outputStream.writeUTF("Invalid index value");
        }

        AddElementCommand addElementCommand = new AddElementCommand();
        ArrayList<Person> new_person_buffer = new ArrayList<>();
        addElementCommand.execute(new_person_buffer, inputStream, outputStream, "");

        new_person_buffer.get(0).id = index;
        for (Person person : list_to_modify)
            if (person.id > index)
                person.id += 1;

        list_to_modify.add(new_person_buffer.get(0));
        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "insert_at";
    }
}

class AddElementIfMaxCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        ArrayList<Person> new_person_buffer = new ArrayList<>();
        AddElementCommand addElementCommand = new AddElementCommand();
        addElementCommand.execute(new_person_buffer, inputStream, outputStream, "");

        boolean adding_condition = true;
        for(Person person : list_to_modify)
            if(person.height >= new_person_buffer.get(0).height) {
                adding_condition = false;
                break;
            }
        if(adding_condition){
            new_person_buffer.get(0).id = list_to_modify.size() + 1;
            list_to_modify.add(new_person_buffer.get(0));
        }

        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "add_if_max";
    }
}

class ShuffleCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        Collections.shuffle(list_to_modify);
        for(int i = 1; i < list_to_modify.size() + 1; i++)
            list_to_modify.get(i - 1).id = i;
        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "shuffle";
    }
}

class MaxByHeight implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        int max_height = 1;
        for(Person person : list_to_modify)
            if(person.height > max_height)
                max_height = person.height;
        for(Person person : list_to_modify)
            if(person.height == max_height){
                outputStream.writeUTF(person.id + ": " + person.name + ", (" +
                    person.coordinates.x + ", " + person.coordinates.y + "), " +
                    person.creationDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z")) + ", " +
                    person.height + ", ");

                return list_to_modify;
            }
        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "max_by_height";
    }
}

class PrintFieldAscendingHeightCommand implements Command{

//    class SortByHeightAscending

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        Integer[] heights = new Integer[list_to_modify.size()];
        for(int i = 0; i < list_to_modify.size(); i++)
            heights[i] = list_to_modify.get(i).height;
        for(int i = 0; i < list_to_modify.size(); i++)
            outputStream.writeUTF(heights[i].toString());
        Arrays.sort(heights, Comparator.naturalOrder());
        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "print_field_ascending_height";
    }
}

class PrintFieldDescendingHeightCommand implements Command{

    @Override
    public ArrayList<Person> execute(ArrayList<Person> list_to_modify, DataInputStream inputStream, DataOutputStream outputStream, String secondary_commands)  throws IOException{
        Integer[] heights = new Integer[list_to_modify.size()];
        for(int i = 0; i < list_to_modify.size(); i++)
            heights[i] = list_to_modify.get(i).height;
        Arrays.sort(heights, Comparator.reverseOrder());
        for(int i = 0; i < list_to_modify.size(); i++)
            outputStream.writeUTF(heights[i].toString());
        return list_to_modify;
    }

    @Override
    public String inline_name() {
        return "print_field_descending_height";
    }
}