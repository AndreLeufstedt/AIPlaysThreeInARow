package org.rewardModel.DataCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class GUI {
    private static char currentPlayer = 'X';
    private static int count = 0;

    private static JFrame frame;
    private static final JButton[][] buttons = new JButton[12][12];

    private static DataCollection myDataCollection = new DataCollection();


    public static void main(String[] args) {
        if (frame == null) {
            frame = new JFrame("Tic-Tac-Toe");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 900);
            frame.setLayout(new GridLayout(12, 12));
            initializeButtons(frame);
            frame.setVisible(true);
        } else {
            resetButtons();
        }
        /*
        while (true) { // This will make the program run continuously
            boolean continueGame = run();
            if (!continueGame || checkDraw()) {
                resetButtons();
            }
            try {
                Thread.sleep(10); // Adding a 500ms delay between games
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

    }

    private static void initializeButtons(JFrame frame) {
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 12; col++) {
                JButton button = new JButton("");
                button.setFont(new Font("Arial", Font.PLAIN, 40));
                button.setFocusPainted(false);
                button.addActionListener(new ButtonClickListener(row, col));
                buttons[row][col] = button;
                frame.add(button);
            }
        }
    }

    private static void switchPlayer() {
        if (Objects.equals(currentPlayer, 'X')) {
            currentPlayer = 'O';
        } else {
            currentPlayer = 'X';
        }
    }

    private static boolean checkWin() {
        String currentPlayerStr = convertCurrentPlayer();

        // Check rows
        for (int row = 0; row < 12; row++) {
            if (checkConsecutiveSymbols(currentPlayerStr, 0, row, 1, 0, 5)) {
                return true;
            }
        }

        // Check columns
        for (int col = 0; col < 12; col++) {
            if (checkConsecutiveSymbols(currentPlayerStr, col, 0, 0, 1, 5)) {
                return true;
            }
        }

        // Check diagonals
        for (int i = 0; i < 8; i++) {
            if (checkConsecutiveSymbols(currentPlayerStr, i, 0, 1, 1, 5) ||
                    checkConsecutiveSymbols(currentPlayerStr, 0, i, 1, 1, 5) ||
                    checkConsecutiveSymbols(currentPlayerStr, i, 0, 1, -1, 5) ||
                    checkConsecutiveSymbols(currentPlayerStr, 0, i, 1, -1, 5)) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkConsecutiveSymbols(String symbol, int startX, int startY, int deltaX, int deltaY, int count) {
        int consecutiveCount = 0;
        for (int i = 0; i < count; i++) {
            int row = startY + i * deltaY;
            int col = startX + i * deltaX;
            if (row >= 0 && row < 12 && col >= 0 && col < 12 && getValueOfButton(row, col).equals(symbol)) {
                consecutiveCount++;
                if (consecutiveCount == 5) {
                    return true;
                }
            } else {
                consecutiveCount = 0;
            }
        }
        return false;
    }




    private static String convertCurrentPlayer() {
        return String.valueOf(currentPlayer);
    }

    private static String getValueOfButton(int row, int col) {
        return buttons[row][col].getText();
    }

    private static boolean checkDraw() {
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 12; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    return false; // If any button is empty, the game is not a draw
                }
            }
        }
        return true;
    }

    private static void aiMakeMove() {
        if (makeWinningMove()) {
            return; // AI wins, so it takes this move
        }

        if (makeBlockingMove()) {
            return; // Block the opponent's winning move
        }

        // If no winning or blocking move is possible, make a random move
        makeRandomMove();
    }

    private static boolean makeWinningMove() {
        // Check for a winning move
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 12; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    buttons[row][col].setText(convertCurrentPlayer());
                    if (checkWin()) {
                        buttons[row][col].setEnabled(false);
                        return true; // AI wins, so it takes this move
                    }
                    buttons[row][col].setText(""); // Reset the cell
                }
            }
        }
        return false; // No winning move found
    }

    private static boolean makeBlockingMove() {
        // Check for a blocking move
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 12; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    switchPlayer();
                    buttons[row][col].setText(convertCurrentPlayer()); // Pretend the opponent makes a move
                    switchPlayer();
                    if (checkWin()) {
                        buttons[row][col].setText(convertCurrentPlayer()); // Block the opponent's winning move
                        buttons[row][col].setEnabled(false);
                        return true;
                    }
                    buttons[row][col].setText(""); // Reset the cell
                }
            }
        }
        return false; // No blocking move found
    }

    private static void makeRandomMove() {
        // Make a random move if no winning or blocking move is found
        while (true) {
            int row = (int) (Math.random() * 12);
            int col = (int) (Math.random() * 12);
            if (buttons[row][col].getText().isEmpty()) {
                buttons[row][col].setText(convertCurrentPlayer());
                buttons[row][col].setEnabled(false);
                return;
            }
        }
    }



    private record ButtonClickListener(int row, int col) implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().isEmpty()) {
                buttons[row][col].setText(String.valueOf(currentPlayer));
                buttons[row][col].setEnabled(false);
                int gameState = checkGameState();
                myDataCollection.saveMove(buttons, currentPlayer, gameState);

                if (checkWin() || checkDraw()) {
                    System.exit(0);
                }

                switchPlayer();
                aiMakeMove();
                gameState = checkGameState();
                myDataCollection.saveMove(buttons, currentPlayer, gameState);

                if (checkWin() || checkDraw()) {
                    System.exit(0);
                }
                switchPlayer();
            }
        }
    }

    private static void resetButtons() {
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 12; col++) {
                buttons[row][col].setText("");
                buttons[row][col].setEnabled(true);
            }
        }
    }


    public static boolean run() {
        //aiMakeMove();
        int gameState = checkGameState();
        myDataCollection.saveMove(buttons, currentPlayer, gameState);
        if (gameState == 1 || gameState == -1 || gameState == 0) {
            return false; // End the game if there's a win, lose, or draw
        }

        switchPlayer();

        aiMakeMove();
        gameState = checkGameState();
        myDataCollection.saveMove(buttons, currentPlayer, gameState);
        if (gameState == 1 || gameState == -1 || gameState == 0) {
            return false; // End the game if there's a win, lose, or draw
        }

        switchPlayer();
        return true; // Continue the game
    }

    private static int checkGameState() {
        return checkWin() ? (currentPlayer == 'X' ? 1 : -1) : checkDraw() ? 0 : -9;
    }


}
