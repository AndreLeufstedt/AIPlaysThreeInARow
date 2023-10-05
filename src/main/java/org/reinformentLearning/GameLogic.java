package org.reinformentLearning;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

public class GameLogic {
    private static final int BOARD_SIZE = 12;
    private static char currentPlayer = 'X';
    private static JFrame frame;
    private static JButton[][] buttons = new JButton[BOARD_SIZE][BOARD_SIZE];

    private static JLabel[][] probabilityLabels = new JLabel[BOARD_SIZE][BOARD_SIZE];
    private static MultiLayerNetwork model;
    private static final double EPSILON = 0.2;  // Exploration rate


    static {
        try {
            File modelFile = new File("C:\\Users\\andre\\IdeaProjects\\AIPlaysThreeInARow\\TicTacToeModelReward.zip");
            model = ModelSerializer.restoreMultiLayerNetwork(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void start() {
        frame = new JFrame("Five-in-a-Row");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(BOARD_SIZE * 50, BOARD_SIZE * 50);
        frame.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        initializeButtons(frame);
        frame.setVisible(true);

        aiMove();
    }




    private static void initializeProbabilityLabels(JFrame probabilityFrame) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JLabel label = new JLabel("0.00");
                label.setFont(new Font("Arial", Font.PLAIN, 10));
                label.setHorizontalAlignment(JLabel.CENTER);
                probabilityLabels[i][j] = label;
                probabilityFrame.add(label);
            }
        }
    }

    private static void initializeButtons(JFrame frame) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton button = new JButton("");
                button.setFont(new Font("Arial", Font.PLAIN, 15));
                button.setFocusPainted(false);
                button.addActionListener(new ButtonClickListener(row, col));
                buttons[row][col] = button;
                frame.add(button);
            }
        }
    }

    private static void switchPlayer() {
        currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
    }

    private static boolean checkWin() {
        String currentPlayerStr = convertCurrentPlayer();
        // Check rows, columns and diagonals for 5 consecutive symbols
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (checkDirection(i, j, 0, 1, currentPlayerStr) ||  // horizontal
                        checkDirection(i, j, 1, 0, currentPlayerStr) ||  // vertical
                        checkDirection(i, j, 1, 1, currentPlayerStr) ||  // diagonal /
                        checkDirection(i, j, 1, -1, currentPlayerStr)) { // diagonal \
                    return true;
                }
            }
        }
        return false;
    }

    // Helper function for checkWin() to check in a specified direction
    private static boolean checkDirection(int row, int col, int dRow, int dCol, String player) {
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                return false; // Out of bounds
            }
            if (buttons[row][col].getText().equals(player)) {
                count++;
            } else {
                break;
            }
            row += dRow;
            col += dCol;
        }
        return count == 5;
    }

    private static String convertCurrentPlayer() {
        return String.valueOf(currentPlayer);
    }

    private static boolean checkDraw() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void showEndGameMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    private static void resetGame() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j].setText("");
            }
        }
        currentPlayer = 'X';
        aiMove();
    }

    private static int getBestMove(INDArray output) {
        // Flatten the output to a 1D array
        double[] probabilities = output.toDoubleVector();

        // Get the sorted indices based on the probabilities
        Integer[] indices = new Integer[probabilities.length];
        for (int i = 0; i < probabilities.length; i++) {
            indices[i] = i;
        }

        Arrays.sort(indices, (i1, i2) -> Double.compare(probabilities[i2], probabilities[i1]));

        // Iterate over the sorted indices and return the first index that corresponds to an unplayed move
        for (int index : indices) {
            int row = index / BOARD_SIZE;
            int col = index % BOARD_SIZE;
            if (buttons[row][col].getText().isEmpty()) {
                return index;
            }
        }

        // If all moves have been played (which shouldn't happen if the game is still ongoing), return -1
        return -1;
    }


    private static void aiMove() {
        // Get the current state of the board
        INDArray currentState = getCurrentState();

        // Predict the best move using the model
        INDArray output = model.output(currentState);
        int bestMove = getBestMove(output); // Implement this function to get the best move from the output

        // Convert the bestMove to row and col
        int row = bestMove / BOARD_SIZE;
        int col = bestMove % BOARD_SIZE;

        // Update the board
        buttons[row][col].setText(convertCurrentPlayer());

        // Check for win or draw
        if (checkWin()) {
            //showEndGameMessage(currentPlayer + " wins!");
            resetGame();
        } else if (checkDraw()) {
            //showEndGameMessage("It's a draw!");
            resetGame();
        } else {
            switchPlayer();
            aiMove(); // Recursive call for the next AI move
        }
    }

    private static INDArray getCurrentState() {
        // Convert the board to a 1D array representation
        INDArray state = Nd4j.zeros(1, BOARD_SIZE * BOARD_SIZE);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                String cellText = buttons[i][j].getText();
                char cell = cellText.isEmpty() ? ' ' : cellText.charAt(0);
                if (cell == 'X') {
                    state.putScalar(i * BOARD_SIZE + j, 1);
                } else if (cell == 'O') {
                    state.putScalar(i * BOARD_SIZE + j, -1);
                }
            }
        }
        return state;
    }

    private static class ButtonClickListener implements ActionListener {
        private final int row;
        private final int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }




        private static void aiMove_() {
            if (Math.random() < EPSILON) {
                // Exploration: choose a random valid move
                int row, col;
                do {
                    row = (int) (Math.random() * BOARD_SIZE);
                    col = (int) (Math.random() * BOARD_SIZE);
                } while (!buttons[row][col].getText().isEmpty());
                buttons[row][col].setText(convertCurrentPlayer());
            } else {
                // Exploitation: choose the move with the highest Q-value
                INDArray currentState = getCurrentState();
                INDArray qValues = model.output(currentState);
                int bestAction = Nd4j.argMax(qValues, 1).getInt(0);
                int row = bestAction / BOARD_SIZE;
                int col = bestAction % BOARD_SIZE;
                buttons[row][col].setText(convertCurrentPlayer());
            }
        }




        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().isEmpty() && currentPlayer == 'X') {
                buttons[row][col].setText(convertCurrentPlayer());
                if (checkWin()) {
                    showEndGameMessage("You win!");
                    resetGame();
                } else if (checkDraw()) {
                    showEndGameMessage("It's a draw!");
                    resetGame();
                } else {
                    switchPlayer();
                }
                if (currentPlayer == 'O') {
                    aiMove();
                    if (checkWin()) {
                        showEndGameMessage("AI wins!");
                        resetGame();
                    } else if (checkDraw()) {
                        showEndGameMessage("It's a draw!");
                        resetGame();
                    } else {
                        switchPlayer();
                    }
                }
            }
        }
    }
}