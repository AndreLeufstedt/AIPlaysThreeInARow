package org.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Objects;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import java.io.File;
import java.io.IOException;

import static org.nd4j.linalg.factory.Nd4j.*;

public class TicTacToeAIPlay {
    private static char currentPlayer = 'X';
    private static JFrame frame;
    private static final JButton[][] buttons = new JButton[3][3];

    // You should initialize this with your trained model
    private static MultiLayerNetwork trainedModel;

    private static JFrame probabilityFrame;
    public static void start() {
        trainedModel = loadModel("C:\\Users\\andre.leufstedt\\IdeaProjects\\AThirdAiPlaysTicTacToe\\TicTacToeModel.zip");
        frame = new JFrame("Tic-Tac-Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(new GridLayout(3, 3));

        initializeButtons(frame);
        frame.setVisible(true);

        // Create the probability frame
        probabilityFrame = new JFrame("Probabilities");
        probabilityFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        probabilityFrame.setSize(300, 300);
        probabilityFrame.setLayout(new GridLayout(3, 3));
        initializeProbabilityLabels(probabilityFrame);

        while (true) {
            if(currentPlayer == 'O') {
                aiMakeMove();
                switchPlayer();
            }
        }
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
        currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
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
        INDArray input = convertBoardToINDArray();
        INDArray output = trainedModel.output(input);

        // Create an array to store the moves and their probabilities
        int[] moves = new int[9];
        double[] probabilities = new double[9];

        // Populate the moves and probabilities arrays
        for (int move = 0; move < 9; move++) {
            int row = move / 3;
            int col = move % 3;
            if (buttons[row][col].getText().isEmpty()) {
                moves[move] = move;
                probabilities[move] = output.getDouble(0, move);
            } else {
                moves[move] = -1; // Mark unavailable moves
                probabilities[move] = Double.NEGATIVE_INFINITY; // Set negative infinity for unavailable moves
            }
        }

        // Sort the moves by probabilities in descending order
        for (int i = 0; i < 9; i++) {
            for (int j = i + 1; j < 9; j++) {
                if (probabilities[i] < probabilities[j]) {
                    // Swap moves and probabilities
                    int tempMove = moves[i];
                    double tempProb = probabilities[i];
                    moves[i] = moves[j];
                    probabilities[i] = probabilities[j];
                    moves[j] = tempMove;
                    probabilities[j] = tempProb;
                }
            }
        }

        // Try each move until an available one is found
        for (int move : moves) {
            if (move != -1) {
                int row = move / 3;
                int col = move % 3;
                buttons[row][col].setText(convertCurrentPlayer());
                buttons[row][col].setEnabled(false);
                displayProbabilities(output); // Update probabilities
                return; // Exit the loop if a valid move is made
            }
        }
    }


    private static void initializeProbabilityLabels(JFrame frame) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JLabel label = new JLabel("");
                label.setFont(new Font("Arial", Font.PLAIN, 20));
                label.setHorizontalAlignment(JLabel.CENTER);
                frame.add(label);
            }
        }
    }

    private static INDArray convertBoardToINDArray() {
        char[] boardArray = new char[9];
        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {

                if (buttons[row][col].getText().equals("X")) {
                    boardArray[index] = (char) 1.0;
                } else if (buttons[row][col].getText().equals("O")) {
                    boardArray[index] = (char) -1.0;
                } else {
                    boardArray[index] = (char) 0.0;
                }
                index++;
            }
        }

        // Convert the board to a string representation
        String boardString = new String(boardArray);
        System.out.print(boardString);

        // Use the TicTacToeDataPreparation class to convert it to one-hot encoded INDArray
        INDArray oneHotEncodedInput = TicTacToeDataPreparation.convertToInput(boardString);

        return oneHotEncodedInput;
    }

    private static void displayProbabilities(INDArray probabilities) {
        DecimalFormat df = new DecimalFormat("#.####");
        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JLabel label = (JLabel) probabilityFrame.getContentPane().getComponent(index);
                label.setText("P(" + row + "," + col + "): " + df.format(probabilities.getDouble(index)));
                index++;
            }
        }
        probabilityFrame.pack();
        probabilityFrame.setVisible(true);
    }

    private static class ButtonClickListener implements ActionListener {
        int row, col;

        ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
                    if (buttons[row][col].getText().isEmpty()) {
                        buttons[row][col].setText(String.valueOf(currentPlayer));
                        buttons[row][col].setEnabled(false);

                        if (checkWin()) {

                            JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins!");
                            System.exit(0);
                        } else if (checkDraw()) {

                            JOptionPane.showMessageDialog(null, "It's a draw!");
                            System.exit(0);
                        }

                        switchPlayer();
                        aiMakeMove();
                        if (checkWin()) {

                            JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins!");
                            System.exit(0);
                        } else if (checkDraw()) {

                            JOptionPane.showMessageDialog(null, "It's a draw!");
                            System.exit(0);
                        }
                        switchPlayer();

                        // Runs the data collection

                    }
                }
            }



    public static void main(String[] args) {
        start();
    }
}
