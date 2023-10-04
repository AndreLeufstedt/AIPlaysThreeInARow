package org.rewardModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class TicTacToeGUI {
    private static char currentPlayer = 'X';
    private static int count = 0;

    private static JFrame frame;
    private static final JButton[][] buttons = new JButton[12][12];

    private static TicTacToeDataCollection myDataCollection = new TicTacToeDataCollection();


    public static void loop() {
        int numIterations = 1000; // Number of iterations

        for (int i = 0; i < numIterations; i++) {
            // Call your program's logic here
            start();

            // Wait for your program to finish (assuming System.exit is called)
            try {
                // Sleep for a while (you can adjust the duration as needed)
                Thread.sleep(2000); // Sleep for 1 second
            } catch (InterruptedException e) {
                System.out.print(e);
            }
        }
    }

    public static void start() {
        count++;



        if (frame == null) {
            frame = new JFrame("Tic-Tac-Toe");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 900);
            frame.setLayout(new GridLayout(12, 12));
            initializeButtons(frame);
        } else {
            resetButtons();
        }

        frame.setVisible(true);

        if(count > 25000) {
            return;
        }

        for (int game = 0; game < 250; game++) {
            run();
        }

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
        for (int row = 0; row < 3; row++) {
            if (checkConsecutiveSymbols(currentPlayerStr, 0, row, 1, 0, 5)) {
                return true;
            }
        }

        // Check columns
        for (int col = 0; col < 3; col++) {
            if (checkConsecutiveSymbols(currentPlayerStr, col, 0, 0, 1, 5)) {
                return true;
            }
        }

        // Check diagonals
        if (checkConsecutiveSymbols(currentPlayerStr, 0, 0, 1, 1, 5) ||
                checkConsecutiveSymbols(currentPlayerStr, 0, 4, 1, -1, 5)) {
            return true;
        }

        return false;
    }

    private static boolean checkConsecutiveSymbols(String symbol, int startX, int startY, int deltaX, int deltaY, int count) {
        int consecutiveCount = 0;
        for (int i = 0; i < count; i++) {
            int row = startY + i * deltaY;
            int col = startX + i * deltaX;
            if (row >= 0 && row < 3 && col >= 0 && col < 3 && getValueOfButton(row, col).equals(symbol)) {
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
               // myDataCollection.saveMove(buttons, currentPlayer); // Save player's move

                if (checkWin() || checkDraw()) {
                    System.exit(0);
                }

                switchPlayer();
                aiMakeMove();
               // myDataCollection.saveMove(buttons, currentPlayer); // Save AI's move

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


    public static void run() {
        aiMakeMove();

        if (checkWin() || checkDraw()) {
            myDataCollection.saveMove(buttons, currentPlayer, checkWin() ? (currentPlayer == 'X' ? 1 : -1) : 0); // Save AI's move with win/lose/draw state

            start();
        }
        myDataCollection.saveMove(buttons, currentPlayer, -9);
        switchPlayer();

        aiMakeMove();
        if (checkWin() || checkDraw()) {
            myDataCollection.saveMove(buttons, currentPlayer, checkWin() ? (currentPlayer == 'X' ? 1 : -1) : 0); // Save AI's move with win/lose/draw state

            start();
        }
        myDataCollection.saveMove(buttons, currentPlayer, -9);

        switchPlayer();
        }
}
