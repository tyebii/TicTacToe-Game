
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
/**
 Represents a client connecting to the game.
 */
public class GameClient
{
    public static void main(String[] args) throws IOException
    {
        final int PORT = 8888;
        final String HOST = "localhost";
        System.out.println("Welcome to TicTacToe!\n");
        Socket s = new Socket(HOST, PORT);
        InputStream inStream = s.getInputStream();
        OutputStream outStream = s.getOutputStream();
        final Scanner in = new Scanner(inStream);
        final PrintWriter out = new PrintWriter(outStream);
        final Scanner console = new Scanner(System.in);
        class InputRunnable implements Runnable
        {
            public void run()
            {
                while (!Thread.interrupted())
                {
                    String line = console.nextLine();
                    out.println(line);
                    out.flush();
                }
            }
        }
        InputRunnable runnable = new InputRunnable();
        Thread t = new Thread(runnable);
        t.start();
        while (in.hasNextLine())
        {
            String response = in.nextLine();
            System.out.println(response);
        }
        t.interrupt();
        System.out.println("Hit ENTER to exit.");
    }
}
