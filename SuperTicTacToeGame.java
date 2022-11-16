package Project2;

import java.util.*;

/**
 * class SuperTicTacToeGame contains all the required methods and variables to play a game of Super Tic Tac Toe.
 * Can play via pvp if ai is turned off or with O as AI if it is turned on.
 */
public class SuperTicTacToeGame {

    private Cell[][] board;
    private GameStatus status;
    private int dimension;
    private int winLength;
    private int turnNumber;

    private Stack<Cell[][]> gameMemory = new Stack<>();
    private Stack<int[]> turnMemory = new Stack<>();
    private boolean playerX;
    private boolean whoStarted;
    private boolean useAI;

    /**
     * constructor that takes in the game settings chosen by the user in SuperTicTacToe.main() and assigns them to this
     * instance's variable's
     * @param dimension - size of the board, always square.
     * @param winLength - Number of consecutive spots in a row required to win
     * @param whoStarted - who starts
     * @param useAI - whether AI is enabled or not
     */
    public SuperTicTacToeGame(int dimension, int winLength, boolean whoStarted, boolean useAI) {
        this.dimension = dimension;
        this.winLength = winLength;
        board = new Cell[dimension][dimension];
        this.whoStarted = whoStarted;
        this.useAI = useAI;
        this.reset();
    }

    /**
     * sets this.board to the input Cell[][] board
     * @param board
     */
    public void setBoard(Cell[][] board) {
        this.board = board;
    }
    /**
     * getter for Cell[][] board
     * @return board
     */
    public Cell[][] getBoard() {
        return board;
    }
    /**
     * getter for Cell[][] board
     * @return board
     */
    public void setDimension(int dim) {
        if (dim < 3 || dim > 15) {
            throw new IllegalArgumentException("Invalid Dimension entered");
        } else {
            this.dimension = dim;
        }
    }
    /**
     * getter for this.dimension
     * @return this.dimension
     */
    public int getDimension() {
        return this.dimension;
    }
    /**
     * sets this. status to the input GameStatus gs
     * @param gs
     */
    public void setStatus(GameStatus gs) {
        this.status = gs;
    }
    /**
     * getter for status
     * @return status
     */
    public GameStatus getGameStatus() {
        return status;
    }
    /**
     * sets this.winlength to the input int num
     * @param num
     */
    public void setWinLength(int num) {
        this.winLength = num;
    }
    /**
     * sets this.winlength to the input int num
     * @return int winlength
     */
    public int getWinLength() {
        return this.winLength;
    }
    /**
     * Sets PlayerX to the input boolean x
     * @param x
     */
    public void setPlayerX(boolean x) {
        playerX = x;
    }
    /**
     * Sets whoStarted to the argument boolean x
     * @param x
     */
    public void setWhoStarted(boolean x) {
        whoStarted = x;
    }
    /**
     * Sets useAI to the argument boolean x
     * @param x
     */
    public void setUseAI(boolean x) {
        useAI = x;
    }
    /**
     * Getter for playerX
     * @return playerX
     */
    public boolean getPlayerX() {
        return playerX;
    }
    /**
     * creates a copy of the argument Cell[][] board and returns the copy
     * @param board
     * @return result
     */
    public Cell[][] deepCopy(Cell[][] board){
        Cell[][] result = new Cell[board.length][];
        for (int i = 0; i < board.length; i++){
            result[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return result;
    }
    /**
     * select updates the status the Cell associated with the button selected based of what players turn it is. It then
     * updates tha game memory and will call ai_turn() if AI is enabled, and it is the AI's turn
     * @param row
     * @param col
     * @throws IllegalArgumentException
     */
    public void select(int row, int col) {
        if (board[row][col] != Cell.EMPTY){
            throw new IllegalArgumentException();
        }
        if (playerX) {
            board[row][col] = Cell.X;
        } else if (!playerX) {
            board[row][col] = Cell.O;
        }
        updateGameStatus(row, col);
        if(status == GameStatus.IN_PROGRESS) {
            int[] turn = {row, col};
            turnMemory.push(turn); //Tracking turns for AI to utilize
            Cell[][] currentBoard = deepCopy(board);
            gameMemory.push(currentBoard);
            playerX = !playerX;
            turnNumber++;

            if (useAI && !playerX) {
                ai_turn();
            }
        }
    }

    /**
     * reset clears game memory and restarts the Tic Tac Toe game, while keeping the users inputted game settings.
     */
    public void reset() {
        this.status = GameStatus.IN_PROGRESS;
        turnNumber = 0;
        gameMemory.clear();
        turnMemory.clear();
        playerX = whoStarted;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                board[row][col] = Cell.EMPTY;
            }
        }
        if (!whoStarted && useAI){
            ai_turn();
        }
    }
    /**
     * undo access the game memory to go backwards one move/ button selection then deletes that game memory. If AI is
     * enabled then reverts both the users and the AI's move and deletes both of these from the game memory
     */
    public void undo() {
        if(useAI){
            try {
                gameMemory.pop();
                gameMemory.pop();
                this.board = gameMemory.peek();
                turnMemory.pop();
                turnMemory.pop();
                turnNumber -= 2;
            }
            catch(EmptyStackException e){
                reset();
            }
        }else {
            try {
                gameMemory.pop();
                this.board = gameMemory.peek();
                turnMemory.pop();
                turnNumber--;
                playerX = !playerX;
            } catch (EmptyStackException e) {
                reset();
            }
        }
    }
    /**
     * randomTurnOrder pseudo-randomly assigns which player will start
     */
    public void randomTurnOrder() {
        Random random = new Random();
        int n = random.nextInt(2);
        if (n % 2 == 0) {
            whoStarted = true;
        } else {
            whoStarted = false;
        }
        reset();
    }

    /**
     * ai_turn will have the AI randomly select a button on it's first move. Then it will check if it can win on the next move or
     * stop the player from winning on their next move. If neither of those conditions are true it will try to make the optimal
     * move to get it closer to winning.
     */
    public void ai_turn() {
        //random turn to start with if its the first AI move of the game
        if (turnNumber == 0 || turnNumber == 1) {
            Random random = new Random();
            while(true){
                int n1 = random.nextInt(dimension);
                int n2 = random.nextInt(dimension);
                try {
                    select(n1, n2);
                    break;
                } catch (IllegalArgumentException e) {
                }
            }
        } else {
            int[] lastPlayerTurn = turnMemory.peek();
            int[] lastAITurn = turnMemory.elementAt(turnMemory.size()-2);


            int[] ai_AIWinCheck = ai_checkMove(lastAITurn);//checks for a winning move based on the move of last turn

            if (ai_AIWinCheck == null){ //if no winning move
                setPlayerX(true);
                int[] ai_PlayerWinCheck = ai_checkMove(lastPlayerTurn); //Checks for players last move to see if a win is possible now
                setPlayerX(false);
                if(ai_PlayerWinCheck == null){ //if no move to block
                    int[] ai_NormalMove = ai_NormalMove();
                    select(ai_NormalMove[0], ai_NormalMove[1]);
                }
                else{
                    select(ai_PlayerWinCheck[0], ai_PlayerWinCheck[1]);
                }
            }
            else{
                select(ai_AIWinCheck[0],ai_AIWinCheck[1]);
            }
        }
    }
    /**
     * AI checks winning conditions for horizontal, vertical, forward diagonal, and backwards diagonal, how close it is
     * to the winning conditions, its previous move, and will move/select a button based off of this.
     * @param lastTurn int[] with coordinates for the move to check.
     * @return result int[] with coordinates of winning/blocking move.
     * @return null if there is no winning/blocking move
     */
    private int[] ai_checkMove(int[] lastTurn){

        //Count the X/O of each possible direction for the coordinate parameter given
        int checkHorizontalTotal = countHorizontal(lastTurn[0]);
        int checkVerticalTotal = countVertical(lastTurn[1]);
        int checkFDiagTotal = countForwardDiagonal(lastTurn[0], lastTurn[1]);
        int checkRDiagTotal = countBackwardDiagonal(lastTurn[0], lastTurn[1]);

        int row = lastTurn[0];
        int col = lastTurn[1];
        int[] result;


        if(checkHorizontalTotal >= winLength-1){
            for(int i = 0 ; i < dimension ; i++){
                if(checkHorizontal(row, i) >= winLength && board[row][i] == Cell.EMPTY){//check each spot along that row for an empty spot that will win
                    result = new int[]{row, i};
                    return result;
                }
            }
        }
        else if(checkVerticalTotal >= winLength-1){
            for(int i = 0 ; i < dimension ; i++){
                if(checkVertical(i, col) >= winLength && board[i][col] == Cell.EMPTY){//check each spot along that column for an empty spot that will win
                    result = new int[]{i, col};
                    return result;
                }
            }
        }
        else if (checkFDiagTotal >= winLength-1){
            for(int i = 0; (row-i >= 0 && col+i < dimension); i++) {
                if (checkForwardDiagonal(row - i, col + i) >= winLength && board[row - i][col + i] == Cell.EMPTY) {//check each spot along that upRight diagonal
                    result = new int[]{row - i, col + i};
                    return result;
                }
            }
            for(int i = 0; (row+i < dimension && col-i >=0); i++){
                if(checkForwardDiagonal(row+i, col-i) >= winLength  && board[row+i][col-i] == Cell.EMPTY){//check each spot along the downleft diagonal
                    result = new int[]{row+i, col-i};
                    return result;
                }
            }
        }
        else if (checkRDiagTotal >= winLength-1){
            for(int i = 0; row-i >= 0 && col-i >=0; i++) {
                if (checkBackwardDiagonal(row - i, col - i) >= winLength && board[row - i][col - i] == Cell.EMPTY) {//check upleft diagonal
                    result = new int[]{row - i, col - i};
                    return result;
                }
            }
            for(int i = 0;row+i < dimension && col+i < dimension; i++ )
                if(checkBackwardDiagonal(row+i, col+i) >= winLength  && board[row+i][col+i] == Cell.EMPTY){//check downright diagonal
                    result = new int[]{row+i, col+i};
                    return result;
                }
            }

        return null; //returns null if no win/block
    }

    /**
     * Method for finding a standard move for the AI. Checks each column/row in each possible direction and puts the one
     * with the highest number of consecutive O's into an array for each direction, then sorts it from lowest to highest
     * Then, based on whatever the last element in the array is, makes that move along that row/column/diagonal in a
     * random empty spot.
     * @return int[] coordinates for the move.
     */
    private int[] ai_NormalMove() {
        int vertChoice = 0, horizChoice = 0, diagFChoice = 0, diagRChoice = 0;
        int vertMax = 0, horizMax = 0, diagFMax = 0, diagRMax = 0;
        int vertAICount, vertPlayerCount;
        int horizAICount, horizPlayerCount;
        int diagFAICount, diagFPlayerCount;
        int diagRAICount, diagRPlayerCount;


        //Vertical Counting
        for (int i = 0; i < dimension; i++) {
            vertAICount = countVertical(i);
            playerX = !playerX; //switch to player in order to count them
            vertPlayerCount = countVertical(i);
            playerX = !playerX; //switch back

            int emptySpaces = countVerticalEmpty(i);

            if (vertAICount > vertMax && (vertAICount + emptySpaces) >= winLength && emptySpaces > 0) {
                vertMax = vertAICount;
                vertChoice = i;
            }
        }
        //Horizontal Counting
        for (int i = 0; i < dimension; i++) {
            horizAICount = countHorizontal(i);
            playerX = !playerX; //switch to player in order to count them
            horizPlayerCount = countHorizontal(i);
            playerX = !playerX; //switch back

            int emptySpaces = countHorizontalEmpty(i);

            if (horizAICount > horizMax && (horizAICount + emptySpaces) >= winLength && emptySpaces > 0) {
                horizMax = horizAICount;
                horizChoice = i;
            }
        }
        //Diagonal Forward
        for (int i = 0; i < (dimension); i++) {
            //check along rows
            diagFAICount = countForwardDiagonal(i, 0);
            playerX = !playerX; //switch to player in order to count them
            diagFPlayerCount = countForwardDiagonal(i, 0);
            playerX = !playerX; //switch back

            int emptySpaces = countForwardDiagonalEmpty(i,0);

            if (diagFAICount > diagFMax && (diagFAICount + emptySpaces) >= winLength && emptySpaces > 0) {
                diagFMax = diagFAICount;
                diagFChoice = i;
            }
            //check along columns from bottom
            diagFAICount = countForwardDiagonal(dimension - 1, i);
            playerX = !playerX; //switch to player in order to count them
            diagFPlayerCount = countForwardDiagonal(dimension - 1, i);
            playerX = !playerX; //switch back

            emptySpaces = countForwardDiagonalEmpty(dimension - 1, i);

            if (diagFAICount > diagFMax && (diagFAICount + emptySpaces) >= winLength && emptySpaces > 0) {
                diagFMax = diagFAICount;
                diagFChoice = i + dimension;
            }

        }
        //Diagonal Reverse
        for (int i = 0; i < dimension; i++) {
            //check along rows
            diagRAICount = countBackwardDiagonal(i, 0);
            playerX = !playerX; //switch to player in order to count them
            diagRPlayerCount = countBackwardDiagonal(i, 0);
            playerX = !playerX; //switch back

            int emptySpaces = countBackwardDiagonalEmpty(i, 0);

            if (diagRAICount > diagRMax && (diagRAICount + emptySpaces) >= winLength && emptySpaces > 0) {
                diagRMax = diagRAICount;
                diagRChoice = i;
            }
            //check along columns from top
            diagRAICount = countBackwardDiagonal(0, i);
            playerX = !playerX; //switch to player in order to count them
            diagRPlayerCount = countBackwardDiagonal(0, i);
            playerX = !playerX; //switch back

            emptySpaces = countBackwardDiagonalEmpty(0, i);

            if (diagRAICount > diagRMax && (diagRAICount + emptySpaces) >= winLength && emptySpaces > 0) {
                diagRMax = diagRAICount;
                diagRChoice = i + dimension;
            }
        }

        int[][] array = {{vertMax, vertChoice, 1},
                {horizMax, horizChoice, 2},
                {diagFMax, diagFChoice, 3},
                {diagRMax, diagRChoice, 4},
                {0, 0, 5}};
        Arrays.sort(array, (a, b) -> Integer.compare(a[0], b[0])); //sorts based on first column of array in ascending order, lowest being first and highest being last.

        int bestDirection = array[4][2];
        int directionChoice = array[4][1];
        Random random = new Random();


        //commented out lines below are nonrandom versions of coordinate selection. Not as elegant as random.
        switch (bestDirection) {
            case 1: //vertical
                while(true){
                    int i = random.nextInt(dimension);
                    if (board[i][directionChoice] == Cell.EMPTY) {
                        int[] result = {i, directionChoice};
                        return result;
                    }
                }
//                for (int i = 0; i < dimension; i++) {
//                    if (board[i][directionChoice] == Cell.EMPTY) {
//                        int[] result = {i, directionChoice};
//                        return result;
//                    }
//                }
            case 2: //horizontal
                while(true){
                    int i = random.nextInt(dimension);
                    if (board[directionChoice][i] == Cell.EMPTY) {
                        int[] result = {directionChoice, i};
                        return result;
                    }
                }
//                for (int i = 0; i < dimension; i++) {
//                    if (board[directionChoice][i] == Cell.EMPTY) {
//                        int[] result = {directionChoice, i};
//                        return result;
//                    }
//                }
            case 3: //fdiag
                if (directionChoice > dimension - 1) { //
                    directionChoice -= dimension;

                    while(true) {
                        int i = random.nextInt(dimension - directionChoice);
                        if (board[dimension - 1 - i][directionChoice + i] == Cell.EMPTY) {
                            int[] result = {dimension - 1 - i, directionChoice + i};
                            return result;
                        }
                    }

//                    for (int i = 0; i < dimension - directionChoice; i++) {
//                        if (board[dimension - 1 - i][directionChoice + i] == Cell.EMPTY) {
//                            int[] result = {dimension - 1 - i, directionChoice + i};
//                            return result;
//                        }
//                    }

                } else {

                    while(true) {
                        int i = random.nextInt(dimension -(dimension-directionChoice) + 1);
                        if (board[directionChoice - i][i] == Cell.EMPTY) {
                            int[] result = {directionChoice - i, i};
                            return result;
                        }
                    }

//                    for (int i = 0; i < dimension - (dimension - directionChoice) + 1; i++) {
//                        if (board[directionChoice - i][i] == Cell.EMPTY) {
//                            int[] result = {directionChoice - i, i};
//                            return result;
//                        }
//                    }
                }

            case 4: //rdiag
                if (directionChoice > dimension - 1) {
                    directionChoice -= dimension;

                    while(true) {
                        int i = random.nextInt(dimension - directionChoice);
                        if (board[i][directionChoice + i] == Cell.EMPTY) {
                            int[] result = {i, directionChoice + i};
                            return result;
                        }
                    }

//                    for (int i = 0; i < dimension - directionChoice; i++) {
//                        if (board[i][directionChoice + i] == Cell.EMPTY) {
//                            int[] result = {i, directionChoice + i};
//                            return result;
//                        }
//                    }

                } else {

                    while(true) {
                        int i = random.nextInt(dimension - directionChoice);
                        if (board[directionChoice + i][i] == Cell.EMPTY) {
                            int[] result = {directionChoice + i, i};
                            return result;
                        }
                    }

//                    for (int i = 0; i < dimension - directionChoice; i++) {
//                        if (board[directionChoice + i][i] == Cell.EMPTY) {
//                            int[] result = {directionChoice + i, i};
//                            return result;
//                        }
//                    }

                }
            case 5:
                break;

        }
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] == Cell.EMPTY) {
                    int[] result = {i, j};
                    return result;
                }
            }
        }
        return null;
    }
    /**
     * updateGameStatus checks the Vertical, Horizontal, Forward Diagonal and Backwards diagonal to see if
     * winning conditions / cats have been met, and will set the game status to the appropriate status.
     * @param row
     * @param col
     */
    private void updateGameStatus(int row, int col) {
        int horiz = checkHorizontal(row, col);
        int vert = checkVertical(row, col);
        int fDiag = checkForwardDiagonal(row, col);
        int rDiag = checkBackwardDiagonal(row, col);
        boolean cats = checkCats();
        if (playerX && (horiz >= winLength || vert >= winLength || fDiag >= winLength || rDiag >= winLength)) {
            this.status = GameStatus.X_WON;
        } else if (!playerX && (horiz >= winLength || vert >= winLength || fDiag >= winLength || rDiag >= winLength)) {
            this.status = GameStatus.O_WON;
        } else if (cats) {
            this.status = GameStatus.CATS;
        } else {
            this.status = GameStatus.IN_PROGRESS;
        }
    }
    /**
     * Getter for whose turn it currently is
     *
     * @return activePlayer
     */
    private Cell getActivePlayer(){
        Cell activePlayer;
        if(playerX) {
            activePlayer = Cell.X;
        }
        else{
            activePlayer = Cell.O;
        }
        return activePlayer;
    }
    /**
     * countHorizontal returns the number of cells occupied by the active player in the row
     * @param row from which to count each column for the active player
     * @return int counter
     */
    private int countHorizontal(int row) {
        int counter = 0;
        Cell activePlayer = getActivePlayer();
        for (int i = 0; i < dimension; i++) {
            if (board[row][i] == activePlayer) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * countHorizontalEmpty returns the number of cells that are empty in the specified row.
     * @param row from which to count each column for empty spots
     * @return int counter
     */
    private int countHorizontalEmpty(int row) {
        int counter = 0;
        for (int i = 0; i < dimension; i++) {
            if (board[row][i] == Cell.EMPTY ) {
                counter++;
            }
        }
        return counter;
    }
    /**
     * returns the total number of Cells occupied by the active player that are next to each other in the row
     * @param row coordinate in the first dimension
     * @param col coordinate in the second dimension
     * @return int counter of consecutive X or O in the row
     */
    private int checkHorizontal(int row, int col) {
        int counterRight = 0;
        int counterLeft = 0;
        Cell activePlayer = getActivePlayer();
            //first is left
        for (int i = 1; (col + i < dimension) && board[row][col + i] == activePlayer; i++) {
            counterRight++;
        }
            //next is right
        for (int i = 1; (col - i >= 0) && board[row][col - i] == activePlayer; i++) {
            counterLeft++;
        }
        int total = counterLeft + counterRight+1;
        return total;
    }
    /**
     * countVertical returns the number of cells occupied by the active player in the column
     * @param col from which to count each row for the current player
     * @return counter
     */
    private int countVertical(int col) {
        int counter = 0;
        Cell activePlayer = getActivePlayer();
        for (int i = 0; i < dimension; i++) {
            if (board[i][col] == activePlayer) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * countHorizontalEmpty returns the number of cells that are empty in the specified column.
     * @param col from which to count each row for empty spots
     * @return int counter
     */
    private int countVerticalEmpty(int col) {
        int counter = 0;
        for (int i = 0; i < dimension; i++) {
            if (board[i][col] == Cell.EMPTY) {
                counter++;
            }
        }
        return counter;
    }
    /**
     * returns the total number of Cells occupied by the active player that are next to each other in the column
     * @param row coordinate in the first dimension
     * @param col coordinate in the second dimension
     * @return int counter of consecutive X or O in the row
     */
    private int checkVertical(int row, int col) {
        int counterUp = 0;
        int counterDown = 0;
        Cell activePlayer = getActivePlayer();
        //first is Up
        for (int i = 1; (row + i < dimension) && board[row + i][col] == activePlayer; i++) {
            counterDown++;
        }
        //next is Down
        for (int i = 1; (row - i >= 0) && board[row - i][col] == activePlayer; i++) {
            counterUp++;
        }
        int total = counterUp + counterDown + 1;
        return total;
    }
    /**
     * returns the total number of Cells occupied by the active player that are next to each other in the forward diagonal direction
     * @param row coordinate in the first dimension
     * @param col coordinate in the second dimension
     * @return int counter of consecutive X or O in the forward diagonal direction /
     */
    private int checkForwardDiagonal(int row, int col) {
        //breaking diagonals into 4 parts : upRight and downLeft, upLeft and downRight.
        int counterUpRight = 0;
        int counterDownLeft = 0;
        Cell activePlayer = getActivePlayer();
        //First is upRight. For loop scans through by increasing the board[x][y] by 1 each scan until it hits the
        //edge ****OR**** it finds a different Cell enum.
        for (int i = 1; (row - i >= 0 && col + i < dimension) && board[row - i][col + i] == activePlayer; i++) {
            counterUpRight++;
        }
        //Second is DownLeft. For loop scans though by decreasing the board[x][y] by 1 each scan until it hits the
        //edge ****OR**** it finds a different Cell enum
        for (int i = 1; (row + i <dimension && col - i >= 0) && board[row + i][col - i] == activePlayer; i++) {
            counterDownLeft++;
        }
        int total = counterDownLeft + counterUpRight + 1;
        return total;
    }
    /**
     * countForwardDiagonal returns the number of cells occupied by the active player in the forward diagonal line
     * cell is in
     * @param row coordinate in the first dimension
     * @param col coordinate in the second dimension
     * @return int counter of total X or O in the forward diagonal direction /
     */
    private int countForwardDiagonal(int row, int col) {
        int counterUpRight = 0;
        int counterDownLeft = 0;
        Cell activePlayer = getActivePlayer();
        for (int i = 0; (row - i >= 0 && col + i < dimension); i++) {
            if(board[row - i][col + i] == activePlayer){
                counterUpRight++;
            }
        }
        for (int i = 1; (row + i < dimension && col - i >= 0); i++) {
            if(board[row + i][col - i] == activePlayer)
                counterDownLeft++;
        }
        int total = counterDownLeft + counterUpRight;
        return total;
    }

    /**
     * countForwardDiagonal returns the number of cells that are empty in the forward diagonal direction based on parameter coordinates
     * @param row coordinate in the first dimension
     * @param col coordinate in the second dimension
     * @return int counter of empty spaces in the forward diagonal direction /
     */
    private int countForwardDiagonalEmpty(int row, int col) {
        int counterUpRight = 0;
        int counterDownLeft = 0;
        for (int i = 0; (row - i >= 0 && col + i < dimension); i++){
            if(board[row - i][col + i] == Cell.EMPTY){
                counterUpRight++;
            }
        }
        for (int i = 1; (row + i < dimension && col - i >= 0); i++) {
            if(board[row + i][col - i] == Cell.EMPTY)
                counterDownLeft++;
        }
        int total = counterDownLeft + counterUpRight;
        return total;
    }

    /**
     * returns the total number of Cells occupied by the active player that are next to each other in the  backwards
     * diagonal line the cell is in.
     * @param row coordinate in the first dimension
     * @param col coordinate in the second dimension
     * @return int counter of consecutive X or O in the backwards diagonal direction \
     */
    private int checkBackwardDiagonal(int row, int col) {
        //breaking diagonals into 4 parts : upRight and downLeft, upLeft and downRight.
        int counterUpLeft = 0;
        int counterDownRight = 0;
        Cell activePlayer = getActivePlayer();
        //First is upRight. For loop scans through by increasing the board[x][y] by 1 each scan until it hits the
        //edge ****OR**** it finds a different Cell enum.
        for (int i = 1; (row - i >= 0 && col - i >= 0) && board[row - i][col - i] == activePlayer; i++) {
            counterUpLeft++;
        }
        //Second is DownLeft. For loop scans though by decreasing the board[x][y] by 1 each scan until it hits the
        //edge ****OR**** it finds a different Cell enum
        for (int i = 1; (row + i < dimension && col + i < dimension) && board[row + i][col + i] == activePlayer; i++) {
            counterDownRight++;
        }
        int total = counterUpLeft + counterDownRight+1;
        return total;
    }
    /**
     * returns the total number of Cells occupied by the active player in the backwards diagonal line the cell is in.
     * @param row coordinate in the first dimension
     * @param col coordinate in the second dimension
     * @return int counter of total X or O in the backwards diagonal direction \
     */
    private int countBackwardDiagonal(int row, int col) {
        int counterUpLeft = 0;
        int counterDownRight = 0;
        Cell activePlayer = getActivePlayer();
        for (int i = 0; (row - i >= 0 && col - i >= 0); i++) {
            if(board[row - i][col - i] == activePlayer){
                counterUpLeft++;
            }
        }
        for (int i = 1; (row + i < dimension && col + i < dimension); i++) {
            if (board[row + i][col + i] == activePlayer){
                counterDownRight++;
            }
        }
        int total = counterUpLeft + counterDownRight;
        return total;
    }
    /**
     * returns the total number of empty Cells in the backwards diagonal line the input cell is in.
     * @param row coordinate in the first dimension
     * @param col coordinate in the second dimension
     * @return int counter of total empty spaces in the backwards diagonal direction \
     */
    private int countBackwardDiagonalEmpty(int row, int col) {
        int counterUpLeft = 0;
        int counterDownRight = 0;
        for (int i = 0; (row - i >= 0 && col - i >= 0); i++) {
            if(board[row - i][col - i] == Cell.EMPTY){
                counterUpLeft++;
            }

        }
        for (int i = 1; (row + i < dimension && col + i < dimension); i++) {
            if (board[row + i][col + i] == Cell.EMPTY){
                counterDownRight++;
            }

        }
        int total = counterUpLeft + counterDownRight;
        return total;
    }

    /**
     * returns false if there are any empty square remaining, otherwise returns true;
     *
     * @return false
     * @return true
     */
    private boolean checkCats() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] == Cell.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Method for generating a random set of coordinates.
     * @return int[] random coordinates
     */
    private int[] randomSelection(){
        Random random = new Random();
        while(true) {
            int n1 = random.nextInt(dimension);
            int n2 = random.nextInt(dimension);
            if (board[n1][n2] == Cell.EMPTY) {
                int[] result = {n1, n2};
                return result;
            }
        }
    }

    /**
     * A method to add a delay in ms. Not implemented.
     * @param ms int time in milliseconds that is the delay
     */
    public static void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }
}


