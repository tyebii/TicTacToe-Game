import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
/**
 Executes Simple Chat Room Access Protocol commands
 from a socket.
 */
public class GameService implements Runnable
{
    private String userName;
    private Player player;
    private Socket s;
    private TicTacToeGame gameInstance;
    private PrintWriter out;
    private Scanner in;
    private boolean loggedIn;
    private boolean loggedOut;

    private static final String HELP_MSG = "List of commands:" +
            "\n register {username} - Registers current player as {username}. Name must be no longer than 15 characters." +
            "\n refresh - Displays the current state of the game." +
            "\n restart - Restarts the game." +
            "\n quit - Disconnects user from the game." +
            "\n move {input} - Places an X or O at the location of {input}. format for {input} is as follows:" +
            "\n \"top left\", \"1\" - places a marker in the top left square." +
            "\n \"top middle\", \"2\" - places a marker in the top middle square." +
            "\n \"top right\", \"3\" - places a marker in the top right square." +
            "\n \"left middle\", \"4\" - places a marker in the left middle square." +
            "\n \"center\", \"5\" - places a marker in the center square." +
            "\n \"right middle\", \"6\" - places a marker in the right middle square." +
            "\n \"bottom left\", \"7\" - places a marker in the bottom left square." +
            "\n \"bottom middle\", \"8\" - places a marker in the bottom middle square." +
            "\n \"bottom right\", \"9\" - places a marker in the bottom right square.";

    /**
     Constructs a service object that processes commands
     from a socket for a chat room.
     @param aSocket the socket
     @param game the board
     */
    public GameService(Socket aSocket, TicTacToeGame game)
    {
        s = aSocket;
        gameInstance = game;
        gameInstance.add(this);
        loggedIn = false;
        loggedOut = false;
    }
    /**
     Executes all commands until the LOGOUT command or the
     end of input.
     Upon join, lists the ID of the game client is currently connected to.
     */
    public void run()
    {
        try
        {
            try
            {
                InputStream inStream = s.getInputStream();
                OutputStream outStream = s.getOutputStream();
                in = new Scanner(inStream);
                out = new PrintWriter(outStream);
                out.println("Joined '" + gameInstance.getGameID() + "'");
                out.flush();
                while (!loggedOut && in.hasNext())
                {
                    String command = in.next();
                    String response = executeCommand(command);
                    putMessage(response);
                }
            } finally
            {
                if (gameInstance != null) {
                    gameInstance.leave(player);
                }
                s.close();
            }
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    /**
     Send the message through the socket.
     @param msg the message needs to be send
     */
    public void putMessage(String msg)
    {
        if (out != null)
        {
            out.println(msg);
            out.flush();
        }
    }
    /**
     Executes a single command.
     List of available commands: register, move, display, restart, quit
     @param command the command to be executed.
     @return the reply to send to the client console.
     */
    public String executeCommand(String command)
    {
        if (command.equalsIgnoreCase("help"))
        {
            return HELP_MSG;
        }
        else if (command.equalsIgnoreCase("register"))
        {
            if (loggedIn) {
                if (player != null) { // edge case
                    return "SYSTEM: You are already logged in as " + player.getName();
                }
            }
            String temp_name = in.next();
//            switch(gameInstance.register(temp_name)){
//                case NAME_TAKEN:
//                    return "SYSTEM: Username " + temp_name + " has already been taken.";
//                case GAME_FULL:
//                    return "SYSTEM: Game is currently full.";
//            }
//            userName = temp_name;
            switch(gameInstance.checkName(temp_name)){
                case NAME_TAKEN:
                    return "SYSTEM: Username " + temp_name + " has already been taken.";
                case GAME_FULL:
                    return "SYSTEM: Game is currently full.";
            }
            player = gameInstance.registerP(temp_name);
            gameInstance.broadcast(player, "joined the game.\n", this);
            loggedIn = true;
            gameInstance.display(this); // Display game automatically
            return "SYSTEM: " + player.getName() + " joined the game.";
        }
        else if (!loggedIn)
        {
            in.nextLine(); // ignore the rest of the command
            return "SYSTEM: You must register before playing.";
        }
        else if (command.equalsIgnoreCase("move"))
        {
            if (gameInstance.isBoardFull()) {
                return "SYSTEM: Game is over. Type 'restart' to start a new game.";
            }
            String input = in.nextLine().trim();
            return "SYSTEM: " + gameInstance.handleMove(input, player, this);
        }
        else if (command.equalsIgnoreCase("display"))
        {
            gameInstance.display(this);
            return "";
        }
        else if (command.equalsIgnoreCase("restart"))
        {
            gameInstance.resetGame();
            gameInstance.display(this);
            return "";
        }
        else if (command.equalsIgnoreCase("quit"))
        {
            gameInstance.broadcast(player, "left the game.", this);
            gameInstance.leave(player);
            loggedOut = true;
            return player.getName() + " left the game.";
        }
        in.nextLine(); // ignore the rest of the command
        return "SYSTEM: Invalid command";
    }
    /**
     Returns the user name of this service.
     @return the user name of this service
     */
    public Player getPlayer()
    {
        return player;
        //return player.getName();
    }
}
