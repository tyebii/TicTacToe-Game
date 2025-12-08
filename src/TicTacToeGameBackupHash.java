//import java.util.HashMap;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.ArrayList;
//import java.util.ListIterator;
//import java.io.PrintWriter;
///**
// A chat room consisting of multiple chatters.
// */
//public class TicTacToeGameBackupHash
//{
//    private ArrayList<GameService> activeService;
//    private int capacity = 2;
//    private HashMap<String, Player> playerHash;
//
//    private char[][] gameBoard;
//    private char lastMove;
//
//    /**
//     Constructs a tictactoe game.
//     */
//    public TicTacToeGame()
//    {
//        playerHash = new HashMap<String, Player>(capacity);
//        activeService = new ArrayList<GameService>(capacity);
//        gameBoard = new char[3][3];
//
//        // crude 50/50 to determine first move
//        if ((int) (Math.random()*10+1) > 5) { lastMove = 'X'; }
//        else { lastMove = 'O'; }
//    }
//    /**
//     Register a player.
//     @param aName the name to register
//     */
//    public boolean register(String aName)
//    {
//        if (playerHash.isEmpty()) {
//            playerHash.put(aName, new Player(aName, 'X'));
//
//        } else {
//            if (playerHash.containsKey(aName)) {
//                return false;
//            }
//            playerHash.put(aName, new Player(aName, 'O'));
//        }
//        return true;
//    }
//    /**
//     De-register a chatter to the room.
//     @param aName the name to de-register
//     */
//    public void leave(String aName)
//    {
//        playerHash.remove(aName);
//    }
//    public void add(GameService cs)
//    {
//        activeService.add(cs);
//    }
//    /**
//     Broadcast a message to everyone in the room.
//     @param msg the message to be broadcast
//     */
//    public void broadcast(String requestor, String msg, GameService chatService)
//    {
//        for (GameService cs : activeService)
//        {
//            if (cs != chatService && cs.getUserName() != null)
//            {
//                cs.putMessage(requestor + " " + msg);
//            }
//        }
//    }
//
//    /**
//     Displays the current state of the game.
//     Returns the current layout of the board, whether the game is in a final state or not,
//     and which player's turn it is.
//     */
//    public void display(GameService gameService) {
//        StringBuilder sb = new StringBuilder();
//        for (int row = 0; row < 3; row++) {
//            for (int col = 0; col < 3; col++) {
//                char v = gameBoard[row][col];
//                sb.append(v == '\u0000' ? ' ' : v);
//
//                if (col < 2) {
//                    sb.append(" | ");
//                }
//            }
//
//            if (row < 2) {
//                sb.append("\n---------\n");
//            }
//        }
//
//        // code here to display result?
//        char result = checkGameState();
//        if (result == 'X' || result == 'O') {
//
//        }
//        String TEMPSTRING = sb.toString();
//
//        for (GameService gs : activeService)
//        {
//            if (gs.getUserName() != null)
//            {
//                gs.putMessage(result);
//            }
//        }
//    }
//
//    public char checkGameState(){
//        for (int row = 0; row < 3; row++) {
//            if (gameBoard[row][0] != '\u0000' &&
//                    gameBoard[row][0] == gameBoard[row][1] &&
//                    gameBoard[row][1] == gameBoard[row][2]) {
//                return gameBoard[row][0];
//            }
//        }
//
//        for (int col = 0; col < 3; col++) {
//            if (gameBoard[0][col] != '\u0000' &&
//                    gameBoard[0][col] == gameBoard[1][col] &&
//                    gameBoard[2][col] == gameBoard[col][col]) {
//                return gameBoard[0][col];
//            }
//        }
//        // diags
//        if (gameBoard[0][0] != '\u0000' &&
//                gameBoard[0][0] == gameBoard[1][1] &&
//                gameBoard[1][1] == gameBoard[2][2]) {
//            return gameBoard[0][0];
//        }
//        if (gameBoard[0][2] != '\u0000' &&
//                gameBoard[0][2] == gameBoard[1][1] &&
//                gameBoard[1][1] == gameBoard[2][0]) {
//            return gameBoard[0][2];
//        }
//        return '\u0000';
//    }
//
//    public String fetchNameFromMarker (char m){
//
//    }
//
//    public boolean parseInput(String input, Player player){
//        int[] pos = mapToCoord(input);
//        if (pos == null) {
//            return false;
//        }
//        if (gameBoard[pos[0]][pos[1]] != '\u0000') {
//            return false;
//        }
//        gameBoard[pos[0]][pos[1]] = player.getMarker();
//        return true;
//    }
//
//    public int[] mapToCoord (String input){
//        return switch (input.toLowerCase()) {
//            case "top left", "1" -> new int[]{0, 0};
//            case "top middle", "2" -> new int[]{0, 1};
//            case "top right", "3" -> new int[]{0, 2};
//            case "left middle", "4" -> new int[]{1, 0};
//            case "center", "5" -> new int[]{1, 1};
//            case "right middle", "6" -> new int[]{1, 2};
//            case "bottom left", "7" -> new int[]{2, 0};
//            case "bottom middle", "8" -> new int[]{2, 1};
//            case "bottom right", "9" -> new int[]{2, 2};
//            default -> null;
//        };
//    }
//
//    public String handleMove(String input, String player, GameService gs){
//        Player currPlayer = playerHash.get(player);
//        //code here to check if in final state
//        if ((lastMove == currPlayer.getMarker())) {
//            return "It is not your turn.";
//        }
//        if(!parseInput(input, currPlayer)) {
//            return "Invalid input.";
//        }
//        display(gs);
//        lastMove = currPlayer.getMarker();
//        return "Marked " + currPlayer.getMarker() + ".";
//    }
//}
