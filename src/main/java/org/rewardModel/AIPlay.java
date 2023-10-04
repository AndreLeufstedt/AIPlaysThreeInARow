package org.rewardModel;

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

public class AIPlay {
    private static char currentPlayer = 'X';
    private static JFrame frame;
    private static final int BOARD_SIZE = 12;
    private static final JButton[][] buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
    private static MultiLayerNetwork trainedModel;
    private static JFrame probabilityFrame;

    private static final JLabel[][] probabilityLabels = new JLabel[BOARD_SIZE][BOARD_SIZE];

    public static void start() {
        trainedModel = loadModel("C:\\Users\\andre.leufstedt\\IdeaProjects\\AThirdAiPlaysTicTacToe\\TicTacToeModel.zip");
        frame = new JFrame("Five-in-a-Row");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(BOARD_SIZE * 50, BOARD_SIZE * 50);
        frame.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        initializeButtons(frame);
        frame.setVisible(true);

        probabilityFrame = new JFrame("Probabilities");
        probabilityFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        probabilityFrame.setSize(BOARD_SIZE * 100, BOARD_SIZE * 100);
        probabilityFrame.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeProbabilityLabels(probabilityFrame);


        while (true) {
            if (currentPlayer == 'O') {
                aiMakeMove();
                switchPlayer();
            }
        }
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

    public static MultiLayerNetwork loadModel(String modelFilePath) {
        MultiLayerNetwork model = null;
        try {
            model = ModelSerializer.restoreMultiLayerNetwork(new File(modelFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    private static void aiMakeMove() {
        // This part will need significant adjustment due to board size change.
        // This is a rough adaptation and likely won't be optimal.
        INDArray input = convertBoardToINDArray();
        INDArray output = trainedModel.output(input);

        displayProbabilities(output);

        int[] moves = new int[BOARD_SIZE * BOARD_SIZE];
        double[] probabilities = new double[BOARD_SIZE * BOARD_SIZE];

        for (int move = 0; move < BOARD_SIZE * BOARD_SIZE; move++) {
            int row = move / BOARD_SIZE;
            int col = move % BOARD_SIZE;

            if (buttons[row][col].getText().isEmpty()) {

                moves[move] = move;
                probabilities[move] = output.getDouble(move);
            } else {
                probabilities[move] = -1; // Invalid move; set probability to negative
            }
        }

        int bestMove = getMaxIndex(probabilities);
        int row = bestMove / BOARD_SIZE;
        int col = bestMove % BOARD_SIZE;

        buttons[row][col].setText(convertCurrentPlayer());
        if (checkWin()) {
            showEndGameMessage("AI wins!");
            resetGame();
        } else if (checkDraw()) {
            showEndGameMessage("It's a draw!");
            resetGame();
        }
    }

    private static int getMaxIndex(double[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private static INDArray convertBoardToINDArray() {
        INDArray boardArray = Nd4j.zeros(1, BOARD_SIZE * BOARD_SIZE * 3); // Multiplied by 3 for 3 possible states
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                String text = buttons[i][j].getText();
                if (text.equals("X")) {
                    boardArray.putScalar(new int[] {0, (i * BOARD_SIZE + j) * 3}, 1);
                } else if (text.equals("O")) {
                    boardArray.putScalar(new int[] {0, (i * BOARD_SIZE + j) * 3 + 1}, 1);
                } else {
                    boardArray.putScalar(new int[] {0, (i * BOARD_SIZE + j) * 3 + 2}, 1);
                }
            }
        }
        return boardArray;
    }


    private static void displayProbabilities(INDArray output) {
        probabilityFrame.dispose();
        probabilityFrame = new JFrame("Probabilities");
        probabilityFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        probabilityFrame.setSize(BOARD_SIZE * 100, BOARD_SIZE * 100);
        probabilityFrame.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeProbabilityLabels(probabilityFrame);
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                double probability = output.getDouble(i * BOARD_SIZE + j);
                JButton probabilityButton = new JButton(new DecimalFormat("0.00").format(probability));
                probabilityButton.setEnabled(false);
                probabilityFrame.add(probabilityButton);
            }
        }
        probabilityFrame.setVisible(true);
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
    }

    public static void main(String[] args) {
        start();
    }

    private static class ButtonClickListener implements ActionListener {
        private final int row;
        private final int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
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
                    aiMakeMove();
                    switchPlayer();
                }
            }
        }
    }
}