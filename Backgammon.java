import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Backgammon {
    private int[] board; // Positive numbers - white pieces, negative - black pieces.
    private int[] eaten; // 2 cells - 1st cell for whites eaten pieces, 2nd for blacks.
    private int[] cubesUsages; // How many more usages do we have for each cube
    private boolean whitesTurn; // Is it whites turn?
    private Random rd = new Random(); // Random generator.
    private Scanner sc = new Scanner(System.in); // For getting the users position and cube to use.


    final int MAX_CUBE = 6;
    final int BOARD_LENGTH = 24;
    final int FIRST_POSITION = 0;
    final int FINAL_POSITION = BOARD_LENGTH - 1;
    final int START_QUARTER_2 = BOARD_LENGTH / 4;
    final int START_QUARTER_3 = BOARD_LENGTH / 2;
    final int START_QUARTER_4 = (3 * BOARD_LENGTH) / 4;
    final int BLACK_EATEN_POSITION = BOARD_LENGTH;
    final int WHITE_EATEN_POSITION = -1;


    Backgammon() {
        initBoard();
    }

    Backgammon(int boardSize) {
        initBoard(boardSize);
    }

    @SuppressWarnings("unused")
    public void initBoard() { // useful when playing more than 1 game.
        initBoard(BOARD_LENGTH);
    }

    public void initBoard(int boardSize) {
        if (boardSize != BOARD_LENGTH)
            //If the board size is incorrect, still initialize to 24.
            System.out.println("Invalid board size, The board will be initialized to 24.");
        this.board = new int[]{2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5, -5, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, -2};
        this.eaten = new int[2];// 2 cells - 1st cell for whites eaten pieces, 2nd for blacks.
        this.cubesUsages = new int[2];//How many more usages do we have for each cube
        this.whitesTurn = whiteStarts();// Is it whites turn?
    }

    private String reverseArray(int[] array) {
        //A method that accepts an array and returns an array in reverse order.
        String reverseArray = "[";
        for (int i = array.length - 1; i > 0; i--)
        {
            reverseArray += array[i] + ", ";
        }
        return reverseArray + (array[0] + "]");
    }

    public String toString() {
        //A method that returns a string that represents the contents of the board
        // (as it appears "in reality") and the number of stones eaten per player.
        int[] quarter1 = Arrays.copyOfRange(this.board, FIRST_POSITION, START_QUARTER_2);
        int[] quarter2 = Arrays.copyOfRange(this.board, START_QUARTER_2, START_QUARTER_3);
        int[] quarter3 = Arrays.copyOfRange(this.board, START_QUARTER_3, START_QUARTER_4);
        int[] quarter4 = Arrays.copyOfRange(this.board, START_QUARTER_4, BOARD_LENGTH);
        return (Arrays.toString(quarter1) + Arrays.toString(quarter2) + "\n" + reverseArray(quarter4)
                + reverseArray(quarter3) + "\nWhites eaten - " + this.eaten[0] + ", " + "blacks eaten - "
                + this.eaten[1]);
    }

    public int[] getBoard() {//A method that returns the attribute that represents the board.
        return this.board;
    }

    public boolean getWhitesTurn() {//A method that returns the whitesTurn attribute.
        return this.whitesTurn;
    }

    public void setBoard(int[] board) {
        this.board = board;
    }

    public boolean whiteStarts() {//A method that returns whether white starts or black.
        return this.whitesTurn = rd.nextBoolean();
    }

    public int[] roll2Cubes() {//A method that rolls two  game dice between 1 and 6.
        int[] cubes = new int[2];
        cubes[0] = (int) (Math.random() * MAX_CUBE) + 1;
        cubes[1] = (int) (Math.random() * MAX_CUBE) + 1;
        return cubes;
    }

    public boolean gameOver() {
        int countW = 0;
        int countB = 0;
        for (int i = 0; i < this.board.length; i++)
        {
            if (this.board[i] <= 0 && eaten[0] == 0)//The loop goes all over the board and checks for white players.
                countW++;//If there are no white players it counts the place.
            if (this.board[i] >= 0 && eaten[1] == 0)////The loop goes all over the board and checks for black players.
                countB++;////If there are no black players it counts the place.
        }//If in all places there are no white players or in all places there are no blacks
        // (including the place of the eaten) then the game is over.
        return countW == this.board.length || countB == this.board.length;
    }

    public boolean move(int position, int moveSize) {
        int direction = this.whitesTurn ? 1 : -1;
        int eatenPosition = this.whitesTurn ? 0 : 1;
        int otherPlayerEaten = this.whitesTurn ? 1 : 0;
        int savePosition = position + (moveSize * direction);
        if (legalMove(position, moveSize))//If the move is legal
        {
            if (position == BLACK_EATEN_POSITION || position == WHITE_EATEN_POSITION)//If eaten and want to get out
                eaten[eatenPosition] -= 1;//Lowers one eaten.
            else
                board[position] -= direction;//If not eaten then in each move takes off from the starting place.
            //(Includes the case where a player wants to get off the board)
            if (!outOfBoard(savePosition))//If the player does not leave the board.
            {
                if ((this.whitesTurn && (board[savePosition] < 0)) ||
                        (!this.whitesTurn && (board[savePosition] > 0)))
                {//If one player wants to eat the other player.
                    board[savePosition] = direction;//Replaces the player with the eaten player.
                    eaten[otherPlayerEaten] += 1;//Adds eaten.

                } else//If the player does not attack and does not leave the board
                    board[savePosition] += direction;//Moves the player.
            }
            return true;//The move is always legal so it returns true.
        }
        return false;//The move is invalid.
    }

    public boolean haveLegalMoves(int[] cubes) { // Any legal moves for the player
        for (int i = -1; i <= BOARD_LENGTH; i++)//A loop that goes through all the positions
        { // and checks for each cube if there is a valid move.
            boolean haveLegalMoves1 = legalMove(i, cubes[0]);
            boolean haveLegalMoves2 = legalMove(i, cubes[1]);
            if ((haveLegalMoves1 && this.cubesUsages[0] != 0) || (haveLegalMoves2 && this.cubesUsages[1] != 0))
                return true;// If one of the cube has not been used
            // and there is some legal move with the same cube then it returns true.
        }
        return false;//If there are no legal moves.
    }

    private boolean legalPosition(int position) {//Checks whether the position is valid for each player
        if ((this.whitesTurn && eaten[0] != 0 && position != WHITE_EATEN_POSITION) ||
                (!this.whitesTurn && eaten[1] != 0 && position != BLACK_EATEN_POSITION))
            // If there are eaten must start with them otherwise returns false.
            return false;
        if ((position == WHITE_EATEN_POSITION && this.whitesTurn) ||
                (!this.whitesTurn && position == BLACK_EATEN_POSITION))
            return true;//If starts from the eaten,returns true.
        return !outOfBoard(position) && ((this.whitesTurn && board[position] > 0)
                || (!this.whitesTurn && board[position] < 0));
        //Checks if the position is valid depending on the player.
    }

    private boolean legalMoveOutOfBoard(int position, int move) {//Checks the legality of the exit from the board.
        int whiteMove = BOARD_LENGTH - move;//The distance from the player to the end of the board.
        if (((position >= move - 1) && !this.whitesTurn) || position == farthestStone())
            return true;
        else return ((position <= whiteMove) && this.whitesTurn) || position == farthestStone();
// Tests that a player can only be taken out of the allowed position
// or that he can bring the players closer to getting off the board
// And if there are no players in the allowed position,
// can take out the player who is furthest from the end of the board.
    }

    public boolean legalMove(int startPosition, int moveSize) {//Checks if the move is valid.
        int direction = this.whitesTurn ? 1 : -1;
        if ((startPosition == WHITE_EATEN_POSITION && eaten[0] == 0) ||
                (startPosition == BLACK_EATEN_POSITION && eaten[1] == 0))
            return false;//If there are no eaten players can not be removed from the location of the eaten.
        else if (legalPosition(startPosition))//If the location is valid.
        {
            int savePosition = startPosition + (moveSize * direction);
            if (outOfBoard(savePosition))//If a player want to get out the board.
                return farthestStoneInLastQuadrant(myFarthestStone(direction)) &&
                        legalMoveOutOfBoard(startPosition, moveSize);
                //Checks that the farthest stone is in the house and also checks that the move is legal.
            else if ((this.whitesTurn && this.board[savePosition] < -1)
                    || (!this.whitesTurn && this.board[savePosition] > 1))
                return false;//The player cannot advance to a place that has more than one stone of the other player.
            return true;//In any other case can move forward.
        }
        return false;//If the location is invalid.
    }

    private int myFarthestStone(int blackOrWhite) {//Returns the stone furthest from the end of the board.
        if (blackOrWhite < 0 && eaten[1] != 0)//If black has eaten stones returns their position.
            return BLACK_EATEN_POSITION;
        else if (eaten[0] != 0 && blackOrWhite > 0)//If white has eaten stones returns their position.
            return WHITE_EATEN_POSITION;
        else
        {
            int positionOfFarWhite = FINAL_POSITION;//The least farthest place from the end of the board.
            int positionOfFarBlack = FIRST_POSITION;//The least farthest place from the end of the board.
            for (int i = 0; i < BOARD_LENGTH; i++)
            //The loop starts from the house of the blacks and ends at the house of the whites.
            {
                if (this.board[i] > 0 && positionOfFarWhite > i)
                    //If we found a place further away from the end of the board.
                    positionOfFarWhite = i;//Updating the farthest place.
                else if (this.board[i] < 0)
                    positionOfFarBlack = i;//Saves the last place that has a black stone.
            }
            if (blackOrWhite < 0)//If it's the black's turn.
                return positionOfFarBlack;
            return positionOfFarWhite;//If it's the white's turn.
        }
    }

    public int farthestStone() {//Returns the position of the stone furthest from the board.
        int direction = this.whitesTurn ? 1 : -1;
        return myFarthestStone(direction);
    }

    public boolean farthestStoneInLastQuadrant(int farthestStone) {
        if (outOfBoard(farthestStone) || farthestStone() != farthestStone)
            // If the position is off the board then certainly not in the last quarter.
            //Also checking that the input is really the farthest stone.
            return false;
        else if (board[farthestStone] < 0)
            return farthestStone < START_QUARTER_2;
            // If the farthest stone of the black in the house then returns true.
        else if (board[farthestStone] > 0)
            return START_QUARTER_4 <= farthestStone;
        //If the farthest stone of the white in the house then returns true.
        return false;
    }

    public boolean outOfBoard(int position) {//Checks if the position is out of the board.
        return position >= BOARD_LENGTH || position < FIRST_POSITION;
    }

    public void nextTurn() {//The method replaces the player's turn.
        this.whitesTurn = !this.whitesTurn;
    }

    public void runGame() {
        System.out.println(this);
        while (!this.gameOver())
        {
            int[] cubes = this.roll2Cubes();
            this.cubesUsages[1] = this.cubesUsages[0] = (cubes[0] == cubes[1]) ? 2 : 1;

            // Move the board using legal moves rolled by the cubes:
            while (this.cubesUsages[0] > 0 || this.cubesUsages[1] > 0)
            {
                if (!this.haveLegalMoves(cubes))
                    break;

                System.out.print(this.whitesTurn ? "Whites turn (⇄)" : "Blacks turn (⇆)");
                System.out.println(", Rolled " + cubes[0] + " " + cubes[1]);
                System.out.print("Insert position number: ");
                int choosenPosition = this.sc.nextInt();
                System.out.print("Insert cube number (0 or 1): ");
                int cubeToUse = this.sc.nextInt();
                if (cubeToUse < 0 || cubeToUse >= cubes.length)
                {
                    System.out.println("Please select a cube from the range of 0 to " + (cubes.length - 1));
                    continue;
                }
                if (this.cubesUsages[cubeToUse] <= 0)
                {
                    System.out.println("Can\'t use this cube again!");
                    continue;
                }
                int choosenMove = cubes[cubeToUse];
                boolean moved = this.move(choosenPosition, choosenMove);
                if (moved)
                {
                    this.cubesUsages[cubeToUse] -= 1;
                    System.out.println(this);
                } else
                    System.out.println("Illegal move!");
            }
            this.nextTurn();
        }
        System.out.println(this.whitesTurn ? "Black won!" : "White won!");
    }
}
