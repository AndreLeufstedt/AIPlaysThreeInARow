package org.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class TicTacToeGUI {
    private static char currentPlayer = 'X';
    private static int count = 0;

    private static JFrame frame;
    private static final JButton[][] buttons = new JButton[3][3];

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
        frame = new JFrame("Tic-Tac-Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(new GridLayout(3, 3));

        initializeButtons(frame);

        frame.setVisible(true);

        if(count > 2500) {
            System.exit(0);
        }
/*
        while (true) {
            run();
        }
*/
    }

    private static void initializeButtons(JFrame frame) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
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
            if (Objects.equals(getValueOfButton(row, 0), currentPlayerStr) &&
                    Objects.equals(getValueOfButton(row, 1), currentPlayerStr) &&
                    Objects.equals(getValueOfButton(row, 2), currentPlayerStr)) {
                return true;
            }
        }

        // Check columns
        for (int col = 0; col < 3; col++) {
            if (Objects.equals(getValueOfButton(0, col), currentPlayerStr) &&
                    Objects.equals(getValueOfButton(1, col), currentPlayerStr) &&
                    Objects.equals(getValueOfButton(2, col), currentPlayerStr)) {
                return true;
            }
        }

        // Check diagonals
        if (Objects.equals(getValueOfButton(0, 0), currentPlayerStr) &&
                Objects.equals(getValueOfButton(1, 1), currentPlayerStr) &&
                Objects.equals(getValueOfButton(2, 2), currentPlayerStr)) {
            return true;
        }
        if (Objects.equals(getValueOfButton(0, 2), currentPlayerStr) &&
                Objects.equals(getValueOfButton(1, 1), currentPlayerStr) &&
                Objects.equals(getValueOfButton(2, 0), currentPlayerStr)) {
            return true;
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
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    return false; // If any button is empty, the game is not a draw
                }
            }
        }
        return true;
    }

    private static void aiMakeMove() {
        // Check for a winning move
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    buttons[row][col].setText(convertCurrentPlayer());
                    if (checkWin()) {
                        buttons[row][col].setEnabled(false);
                        return; // AI wins, so it takes this move
                    }
                    buttons[row][col].setText(""); // Reset the cell
                }
            }
        }

        // Check for a blocking move
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    switchPlayer();
                    buttons[row][col].setText(convertCurrentPlayer()); // Pretend the opponent makes a move
                    switchPlayer();
                    if (checkWin()) {
                        buttons[row][col].setText(convertCurrentPlayer()); // Block the opponent's winning move
                        buttons[row][col].setEnabled(false);
                        return;
                    }
                    buttons[row][col].setText(""); // Reset the cell
                }
            }
        }

        // If no winning or blocking move, place 'O' randomly (you can implement more advanced strategies here)
        int randomRow, randomCol;
        do {
            randomRow = (int) (Math.random() * 3);
            randomCol = (int) (Math.random() * 3);
        } while (!buttons[randomRow][randomCol].getText().isEmpty());

        buttons[randomRow][randomCol].setText(convertCurrentPlayer());
        buttons[randomRow][randomCol].setEnabled(false);
    }


    private record ButtonClickListener(int row, int col) implements ActionListener {

        @Override
            public void actionPerformed(ActionEvent e) {
                if (buttons[row][col].getText().isEmpty()) {
                    buttons[row][col].setText(String.valueOf(currentPlayer));
                    buttons[row][col].setEnabled(false);

                    if (checkWin()) {
                        myDataCollection.start(buttons, 1, currentPlayer);
                        //JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins!");
                        System.exit(0);
                    } else if (checkDraw()) {
                        myDataCollection.start(buttons, 1, currentPlayer);
                        //JOptionPane.showMessageDialog(null, "It's a draw!");
                        System.exit(0);
                    }

                    switchPlayer();
                    aiMakeMove();
                    if (checkWin()) {
                        myDataCollection.start(buttons, 1, currentPlayer);
                        //JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins!");
                        System.exit(0);
                    } else if (checkDraw()) {
                        myDataCollection.start(buttons, 0, currentPlayer);
                        //JOptionPane.showMessageDialog(null, "It's a draw!");
                        System.exit(0);
                    }
                    switchPlayer();

                    // Runs the data collection

                }
            }
        }

        public static void run() {
            aiMakeMove();

            if (checkWin()) {
                myDataCollection.start(buttons, 1, currentPlayer);
                //JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins!");
                //System.exit(0);
                frame.dispose();
                start();
            } else if (checkDraw()) {
                myDataCollection.start(buttons, 0, currentPlayer);
                System.out.print("Draw");
                //JOptionPane.showMessageDialog(null, "It's a draw!");
                //System.exit(0);
                frame.dispose();
                start();
            }

            switchPlayer();
            aiMakeMove();
            if (checkWin()) {
                myDataCollection.start(buttons, 1, currentPlayer);
                //JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins!");
                //System.exit(0);
                frame.dispose();
                start();
            } else if (checkDraw()) {
                myDataCollection.start(buttons, 0, currentPlayer);
                System.out.print("Draw");
                //JOptionPane.showMessageDialog(null, "It's a draw!");
                //System.exit(0);
                frame.dispose();
                start();
            }
            switchPlayer();

        }
}
