
/**
 A class represent a chat room user.
 */
public class Player
{
    String name;
    char marker;

    /**
     Constructs a chatter with a name.
     */
    public Player(String aName, char xo)
    {
        name = aName;
        marker = xo;
    }
    /**
     Returns the name
     @return the name
     */
    public String getName()
    {
        return name;
    }

    public char getMarker(){
        return marker;
    }
}
