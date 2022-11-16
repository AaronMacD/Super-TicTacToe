package Project2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EmptyStackException;

/**
 * class SuperTicTacToePanal contains the methods to create the GUI and get input from the TheTicTacToe Class
 * and TicTacToeGame Class
 */
public class SuperTicTacToePanel extends JPanel{


    private JButton[][] jButtonsBoard;
    private Cell[][] iBoard;
    private ImageIcon xIcon;
    private ImageIcon oIcon;
    private ImageIcon emptyIcon;
    private SuperTicTacToeGame game;

    private JPanel menu = new JPanel();
    private JPanel gameBoard = new JPanel();

    private JButton quitButton;
    private JButton undoButton;
    private JButton resetButton;

    private ButtonListener buttonListener = new ButtonListener();

    /**
     * constructor SuperTicTacTpePanel takes in a SuperTicTacToeGame object. It instantiates the ImageIcons, and adds
     * quit, reset, undo, and input grid to the Panel.
     * @param game
     */
    public SuperTicTacToePanel(SuperTicTacToeGame game){
        super();
        this.game = game;
        xIcon = new ImageIcon("X Icon.png");
        oIcon = new ImageIcon("O Icon.png");
        emptyIcon = new ImageIcon("Empty Icon.png");


        quitButton = new JButton("Quit");
        quitButton.addActionListener(buttonListener);
        menu.add(quitButton);

        undoButton = new JButton("Undo");
        undoButton.addActionListener(buttonListener);
        menu.add(undoButton);

        resetButton = new JButton("Reset");
        resetButton.addActionListener(buttonListener);
        menu.add(resetButton);

        gameBoard.setLayout(new GridLayout(game.getDimension(),game.getDimension()));
        jButtonsBoard = new JButton[game.getDimension()][game.getDimension()];
        instantiateButtons();
        displayBoard();

        this.add(menu);
        this.add(gameBoard);


    }

    /**
     * instantiateButtons instantates  n = dimensions^2 buttons with an empty icon and action listener.
     */
    private void instantiateButtons(){

        for (int row = 0; row < game.getDimension(); row++)
            for (int col = 0; col < game.getDimension(); col++) {
                jButtonsBoard[row][col] = new JButton("", emptyIcon);
                jButtonsBoard[row][col].addActionListener(buttonListener);
                jButtonsBoard[row][col].setPreferredSize(new Dimension(32,32));
                gameBoard.add(jButtonsBoard[row][col]);
            }
    }

    /**
     * displayBoard takes the Cell value of each button and updates the Icon to the correlating png
     */
    private void displayBoard(){
        iBoard = game.getBoard ();
        for (int row = 0; row < game.getDimension(); row++) {
            for (int col = 0; col < game.getDimension(); col++) {
                if (iBoard[row][col] == Cell.O) {
                    jButtonsBoard[row][col].setIcon(oIcon);
                }
                if (iBoard[row][col] == Cell.X) {
                    jButtonsBoard[row][col].setIcon(xIcon);
                }
                if (iBoard[row][col] == Cell.EMPTY){
                    jButtonsBoard[row][col].setIcon(emptyIcon);
                }
            }
        }
    }

    /**
     * inner class ButtonListener contains the methods to handle user's interactions with the GUI
     */
    private class ButtonListener implements ActionListener{
        @Override
        /**
         * actionPerformed first itterates through each button to determine what button was selected.
         * if no button was selected it checks the undo, reset, and quit buttons, and calls thier respective methods.
         * lastly it will check if the game status has changed.
         *
         * @param e
         */
        public void actionPerformed(ActionEvent e) {
            // Determine which button was selected.
            for (int row = 0; row < game.getDimension(); row++) {
                for (int col = 0; col < game.getDimension(); col++) {
                    if (jButtonsBoard[row][col] == e.getSource()) {
                        try{
                            // tell the game which button was selected.
                            game.select(row, col);
                            displayBoard();
                        }
                        catch(IllegalArgumentException selectE){
                            JOptionPane.showMessageDialog(null, "That isn't an empty space! Choose another.");
                        }

                    }
                }
            }
            //buttons for functions
            if(quitButton == e.getSource()){
                System.exit(0);
            }
            if (undoButton == e.getSource()){
                try{
                    game.undo();
                    displayBoard();
                }
                catch(EmptyStackException undoE){
                    JOptionPane.showMessageDialog(null, "No turns left to undo!");
                }

            }
            if(resetButton == e.getSource()){
                game.reset();
            }

            // Display the board using the private method describe above.
            displayBoard();


            // Determine if there is a winner by asking the game object. (see step 6)
            if (game.getGameStatus() == GameStatus.O_WON) {
                JOptionPane.showMessageDialog(null, "O won!\nThe game will reset");
                game.reset();
                displayBoard();
            } else if (game.getGameStatus() == GameStatus.X_WON) {
                JOptionPane.showMessageDialog(null, "X won!\nThe game will reset");
                game.reset();
                displayBoard();
            } else if (game.getGameStatus() == GameStatus.CATS) {
                JOptionPane.showMessageDialog(null, "Cats! No winner!!!!\nThe game will reset");
                game.reset();
                displayBoard();
            }


        }
    }
}

