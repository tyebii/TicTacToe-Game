import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.ListIterator;
import java.io.PrintWriter;

/**
 The logic behind the tic tac toe game.
 Handles displaying the board, registering and de-registering players,
 processing moves, and determining game state.
 */
public class TicTacToeGame
{
    private ArrayList<GameService> activeService;
    private int capacity = 2;
    private HashMap<String, Player> playerHash;
    private String gameID;
    private char[][] gameBoard;
    private char lastMove;
    private Player player1;
    private Player player2;

    public enum ReturnCode {
        NAME_TAKEN,
        GAME_FULL,
        SUCCESS
    }

    /**
     Constructs a tictactoe game instance.
     @param id used by players to connect to a specific game
     */
    public TicTacToeGame(String id)
    {
        playerHash = new HashMap<String, Player>(capacity);
        activeService = new ArrayList<GameService>(capacity);
        gameBoard = new char[3][3];
        gameID = id;
        // crude 50/50 to determine first move
        if ((int) (Math.random()*10+1) > 5) { lastMove = 'X'; }
        else { lastMove = 'O'; }
    }

    /**
     Resets the game board to empty (blank characters)
     */
    public void resetGame(){
        for (int i=0; i<gameBoard.length;i++) {
            for (int j=0; j<gameBoard[0].length;j++){
                gameBoard[i][j] = '\u0000';
            }
        }
        // crude 50/50 to determine first move
        if ((int) (Math.random()*10+1) > 5) { lastMove = 'X'; }
        else { lastMove = 'O'; }
    }



    /**
     Helper method to check if the player's name is valid
     @return codes for name taken or game full if not successful
     */
    public ReturnCode checkName(String aName) {
        if (player1 != null && aName.equalsIgnoreCase(player1.getName()) || player2 != null && aName.equalsIgnoreCase(player2.getName())) {
            return ReturnCode.NAME_TAKEN;
        }
        if (player1 != null && player2!=null) {
            return ReturnCode.GAME_FULL;
        }
        return ReturnCode.SUCCESS;
    }

    /**
     Method to register a player.
     @param aName the name of the player to register
     @return the player object containing their name and marker
     */
    public Player registerP(String aName){

        if (player1 == null) {
            player1 = new Player(aName, 'X');
            return player1;
        } else if (player2 == null) {
            player2 = new Player(aName, 'O');
            return player2;
        }
        return null;
    }

    /**
     De-registers a player. Method is called automatically upon both
     manual quit and force quit.
     @param aPlayer the player to de-register
     */
    public void leave(Player aPlayer)
    {
        if (aPlayer == player1) {
            player1 = null;
        } else if (aPlayer == player2) {
            player2 = null;
        }
    }

    public void add(GameService cs)
    {
        activeService.add(cs);
    }

    /**
     Helper method to broadcast a message to everyone in the game.
     @param msg the message to be broadcast
     */
    public void broadcast(Player requestor, String msg, GameService gameService)
    {
        for (GameService gs : activeService)
        {
            if (gs != gameService && gs.getPlayer() != null)
            {
                gs.putMessage(requestor.getName() + " " + msg);
            }
        }
    }

    /**
     Displays the current state of the game to every connected player.
     Shows the current layout of the board, whether the game is in a final state or not,
     and which player's turn it is.
     @param gameService service corresponding to each client connection
     */
    public void display(GameService gameService) {
        // text art formatting for the board
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char v = gameBoard[row][col];
                sb.append(v == '\u0000' ? ' ' : v);
                if (col < 2) {
                    sb.append(" | ");
                }
            }
            if (row < 2) {
                sb.append("\n---------\n");
            }
        }

        // Check whose turn it is, and if board is in a final state
        String message = sb.toString();
        if (player1!=null && player2!=null){
            if (lastMove == player1.getMarker()) {
                message += "\nIt's " + player2.getName() + "'s turn.";
            } else {
                message += "\nIt's " + player1.getName() + "'s turn.";
            }
            char result = checkGameState();
            if (result == player1.getMarker()) {
                message += "\n" + player1.getName() + " wins.";
            } else if (player1!=null && result == player2.getMarker()) {
                message += "\n" + player2.getName() + " wins.";
            } else if (isBoardFull()){
                message += "\nIt's a tie.";
            }
        } else {
            message+="\nWaiting on an opponent...";
        }

        // Display the board to all users
        for (GameService gs : activeService)
        {
            if (gs.getPlayer() != null)
            {
                gs.putMessage(message);
            }
        }
    }

    /**
     Checks if the game is in a final state (win, loss, tie).
     @return X or O if the game is won by X or O, t if the game is tied, empty char otherwise
     */
    public char checkGameState(){
        for (int row = 0; row < 3; row++) {
            if (gameBoard[row][0] != '\u0000' &&
                    gameBoard[row][0] == gameBoard[row][1] &&
                    gameBoard[row][1] == gameBoard[row][2]) {
                return gameBoard[row][0];
            }
        }

        for (int col = 0; col < 3; col++) {
            if (gameBoard[0][col] != '\u0000' &&
                    gameBoard[0][col] == gameBoard[1][col] &&
                    gameBoard[2][col] == gameBoard[col][col]) {
                return gameBoard[0][col];
            }
        }
        // diags
        if (gameBoard[0][0] != '\u0000' &&
                gameBoard[0][0] == gameBoard[1][1] &&
                gameBoard[1][1] == gameBoard[2][2]) {
            return gameBoard[0][0];
        }
        if (gameBoard[0][2] != '\u0000' &&
                gameBoard[0][2] == gameBoard[1][1] &&
                gameBoard[1][1] == gameBoard[2][0]) {
            return gameBoard[0][2];
        }
        return '\u0000';
    }

    /**
     Checks if there are any remaining moves.
     @return false if there is an empty square left
     */
    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard[i][j] == '\u0000') { // If cell is empty
                    return false;
                }
            }
        }
        return true;
    }

    /**
     Marks a spot on the board.
     @param input the square to be marked
     @param player the player making the move
     */
    public boolean parseInput(String input, Player player){
        int[] pos = mapToCoord(input);
        if (pos == null) {
            return false;
        }
        if (gameBoard[pos[0]][pos[1]] != '\u0000') {
            return false;
        }
        gameBoard[pos[0]][pos[1]] = player.getMarker();
        return true;
    }

    /**
     Maps text input to board spaces.
     @param input specifies which space on the board to mark.
     */
    public int[] mapToCoord (String input){
        return switch (input.toLowerCase()) {
            case "top left", "1" -> new int[]{0, 0};
            case "top middle", "2" -> new int[]{0, 1};
            case "top right", "3" -> new int[]{0, 2};
            case "left middle", "4" -> new int[]{1, 0};
            case "center", "5" -> new int[]{1, 1};
            case "right middle", "6" -> new int[]{1, 2};
            case "bottom left", "7" -> new int[]{2, 0};
            case "bottom middle", "8" -> new int[]{2, 1};
            case "bottom right", "9" -> new int[]{2, 2};
            default -> null;
        };
    }

    /**
     Makes a move if possible and updates the board for all players.
     @param input the move to make
     @param player the player making the move
     */
    public String handleMove(String input, Player player, GameService gs){
        //Player currPlayer = player.equals(player1.getName()) ? player1 : player2;
        Player currPlayer = player;
        //code here to check if in final state
        if (isBoardFull()) return "";
        if ((lastMove == currPlayer.getMarker())) return "It is not your turn.";
        if(!parseInput(input, currPlayer)) return "Invalid input.";

        lastMove = currPlayer.getMarker();
        display(gs);
        return "Marked " + currPlayer.getMarker() + ".";
    }

    public String getGameID(){
        return gameID;
    }
}
