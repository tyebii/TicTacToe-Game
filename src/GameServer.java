import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 A server that executes the Simple Chat Room Access Protocol.
 It can accept simultaneous connections from multiple clients.
 */
public class GameServer
{
    public static void main(String[] args) throws IOException
    {
        TicTacToeGame game = new TicTacToeGame();
        final int PORT = 8888;
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Waiting for players to connect...");
        Socket s;
        while (true)
        {
            s = server.accept();
            GameService service = new GameService(s, game);
            Thread t = new Thread(service);
            t.start();
        }
    }
}
