
/**
 A class that represents a player.
 */
public class Player
{
    String name;
    char marker;

    /**
     Constructs a player with a name and X/O marker.
     @param aName the name of the player
     @param xo the marker of the player (X or O)
     */
    public Player(String aName, char xo)
    {
        name = aName;
        marker = xo;
    }

    public String getName()
    {
        return name;
    }
    public char getMarker(){
        return marker;
    }
}
