import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 A server that handles connecting clients to game instances.
 It can host multiple games, and accept simultaneous connections for up to two clients per game.
 */
public class GameServer {
    private static final int PORT = 8888;
    private static final Map<String, TicTacToeGame> gamesList = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Waiting for players to connect...");
        while (true) {
            Socket s = server.accept();
            // Handle each client in a separate thread
            new Thread(() -> handleClient(s)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Ask client for game instance id
            out.println("Input a game ID join an existing game or to create a new game:");
            String gameID = in.readLine();

            // Get existing room or create new one
            TicTacToeGame game;
            synchronized (gamesList) {
                game = gamesList.computeIfAbsent(gameID, k -> new TicTacToeGame(gameID));
            }

            // Start service for this client in that room
            GameService service = new GameService(socket, game);
            service.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}