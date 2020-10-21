package Server;

public class Person {
    public int id; //must be grater then 0, generated
    public String name; //cannot be 0, string cannot be blank
    public Coordinates coordinates; //cannot be 0
    public java.time.ZonedDateTime creationDate; //cannot be 0, generated
    public int height; //must be grater then 0
    public String passportID; //can be null
    public Color eyeColor; //can be null
    public Country nationality; //can be null
    public Location location; //can be null
}

class Coordinates {
    public float x;
    public int y; // cannot be 0
}

class Location {
    public int x;
    public int y;
    public double z;
    public String name; // can be null
}

enum Color {
    BLACK,
    YELLOW,
    ORANGE,
    WHITE,
    BROWN
}

enum Country {
    RUSSIA,
    CHINA,
    ITALY,
    SOUTH_KOREA,
    NORTH_KOREA
}