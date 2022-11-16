package Project2;

import javax.swing.*;

/**
 * class SuperTicTacToe contains the main method to play a game of Super Tic Tac Toe
 */
public class SuperTicTacToe {

    /**
     * main method creates an instance GUI, the proceeds to ask the User for their preferred game settings.
     * Then main will create an instnance of a SuperTicTacToeGame with the aforementioned game settings.
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame gui = new JFrame("Super Tic-Tac-Toe");
        int dimension = 3;
        int winCondition = 3;

        //Entry for Dimension of board
        boolean whoStarts = true;
        boolean randomStart = false;

        boolean quit = false;
        boolean validDimEntry = false;
        while (!validDimEntry && !quit) {
            String dimensionSelection = JOptionPane.showInputDialog(null,
                    "Enter in the size of the board (2-15): ");

            if (dimensionSelection == null) {
                gui.dispose();
                quit = true;
                break;
            }

            try {
                dimension = Integer.parseInt(dimensionSelection);
                if (dimension > 2 && dimension < 15) {
                    validDimEntry = true;
                } else {
                    validDimEntry = false;
                }
            } catch (Exception e) {
                validDimEntry = false;
            }

            if (validDimEntry == false) {
                JOptionPane.showMessageDialog(null, "Invalid Entry! Must be between 2 and 15.", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
            }
        }

        //Entry for Win Condition
        boolean validWinEntry = false;
        while (!validWinEntry && !quit) {
            String winSelection = JOptionPane.showInputDialog(null,
                    "Enter win condition (Can't exceed dimension): ");

            if (winSelection == null) {
                gui.dispose();
                quit = true;
                break;
            }

            try {
                winCondition = Integer.parseInt(winSelection);
                if (winCondition < 3 || winCondition > dimension) {
                    validWinEntry = false;
                } else {
                    validWinEntry = true;
                }
            } catch (Exception e) {
                validWinEntry = false;
            }

            if (validWinEntry == false) {
                JOptionPane.showMessageDialog(null, "Invalid Entry! Must be 3 or greater, but can't be greater than the board dimension.", "Invalid Entry", JOptionPane.ERROR_MESSAGE);
            }
        }

        int ai_Selection = 1;
        boolean useAI = false;
        if (!quit) {


            ai_Selection = JOptionPane.showConfirmDialog(null,
                    "Do you want O to be an AI Opponent?", "AI Opponent",
                    JOptionPane.YES_NO_CANCEL_OPTION);


            if (ai_Selection == JOptionPane.CANCEL_OPTION) {
                gui.dispose();
                quit = true;
            }
        }

        //Entry for who goes first
        String turnSelection = null;
        if (!quit) {
            turnSelection = JOptionPane.showInputDialog(null,
                    "Enter who goes first, or enter R for random");
        }

        //Popup alerting user that entry was invalid
        if (!turnSelection.equals("x") && !turnSelection.equals("X") && !turnSelection.equals("o") && !turnSelection.equals("O") && !turnSelection.equals("r") && !turnSelection.equals("R")) {
            JOptionPane.showMessageDialog(null, "your entry was invalid; X hs been selected for you by default");

        }

        if (turnSelection.equals("o") || turnSelection.equals("O")) {
            whoStarts = false;
        }
        if (turnSelection.equals("r") || turnSelection.equals("R")) {
            randomStart = true;
        }
        if (turnSelection == null) {
            gui.dispose();
            quit = true;
        }


        //setting up parameters for the game and starting the game panel
        if (!quit) {

            if (ai_Selection == JOptionPane.YES_OPTION) {
                useAI = true;
            }

            SuperTicTacToeGame game = new SuperTicTacToeGame(dimension, winCondition, whoStarts, useAI);
            SuperTicTacToePanel panel = new SuperTicTacToePanel(game);

            if (randomStart) {
                game.randomTurnOrder();
            }
            gui.add(panel);
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gui.pack();
            gui.setVisible(true);


        }
    }
}
