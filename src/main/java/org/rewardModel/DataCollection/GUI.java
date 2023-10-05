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

        while (true) { // This will make the program run continuously
            boolean continueGame = run();
            if (!continueGame || checkDraw()) {
                resetButtons();
            }
            try {
                Thread.sleep(1); // Adding a 500ms delay between games
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 8; col++) { // Check possible 5-cell sequences in the row.
                if (checkConsecutiveSymbols(currentPlayerStr, col, row, 1, 0, 5)) {
                    return true;
                }
            }
        }

        // Check columns
        for (int col = 0; col < 12; col++) {
            for (int row = 0; row < 8; row++) { // Check possible 5-cell sequences in the column.
                if (checkConsecutiveSymbols(currentPlayerStr, col, row, 0, 1, 5)) {
                    return true;
                }
            }
        }

        // Check diagonals
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 12; col++) {
                // Downward diagonals (top-left to bottom-right)
                if (checkConsecutiveSymbols(currentPlayerStr, col, row, 1, 1, 5)) {
                    return true;
                }

                // Upward diagonals (bottom-left to top-right)
                if (row > 3) { // only start from row 4 onwards as we are moving upwards
                    if (checkConsecutiveSymbols(currentPlayerStr, col, row, 1, -1, 5)) {
                        return true;
                    }
                }
            }
        }

        return false;
        // Unoptimized win checks, kept if optimized code fails.
        /*
        // Check rows
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 8; col++) { // This ensures you check all possible 5-cell sequences in the row.
                if (checkConsecutiveSymbols(currentPlayerStr, col, row, 1, 0, 5)) {
                    return true;
                }
            }
        }


        // Check columns
        for (int col = 0; col < 12; col++) {
            for (int row = 0; row < 8; row++) { // up to row 7 since we want to check sequences of 5
                if (checkConsecutiveSymbols(currentPlayerStr, col, row, 0, 1, 5)) {
                    return true;
                }
            }
        }


        // Check downward diagonals
        for (int i = 0; i < 8; i++) { // From the left border
            if (checkConsecutiveSymbols(currentPlayerStr, i, 0, 1, 1, 5)) {
                return true;
            }
        }
        for (int i = 0; i < 8; i++) { // From the top border
            if (checkConsecutiveSymbols(currentPlayerStr, 0, i, 1, 1, 5)) {
                return true;
            }
        }

        // Check upward diagonals
        for (int i = 4; i < 12; i++) { // From the left border
            if (checkConsecutiveSymbols(currentPlayerStr, i, 11, 1, -1, 5)) {
                return true;
            }
        }
        for (int i = 0; i < 8; i++) { // From the bottom border
            if (checkConsecutiveSymbols(currentPlayerStr, 0, i, 1, -1, 5)) {
                return true;
            }
        }

        // Check downward diagonals starting from the left border
        for (int col = 0; col < 8; col++) {
            for (int len = 12 - col; len >= 5; len--) {
                if (checkConsecutiveSymbols(currentPlayerStr, col, 0, 1, 1, len)) {
                    return true;
                }
            }
        }

        // Check downward diagonals starting from the top border
        for (int row = 0; row < 8; row++) {
            for (int len = 12 - row; len >= 5; len--) {
                if (checkConsecutiveSymbols(currentPlayerStr, 0, row, 1, 1, len)) {
                    return true;
                }
            }
        }

        // Check downward diagonals that are entirely inside the board
        for (int col = 1; col < 7; col++) { // columns 1 to 6
            for (int row = 0; row < 8; row++) { // rows 0 to 7
                if (checkConsecutiveSymbols(currentPlayerStr, col, row, 1, 1, 5)) {
                    return true;
                }
            }
        }

        // Check upward diagonals starting from the left border
        for (int col = 0; col < 8; col++) {
            if (checkConsecutiveSymbols(currentPlayerStr, col, 11, 1, -1, 5)) {
                return true;
            }
        }

        // Check upward diagonals starting from the bottom border
        for (int row = 4; row < 12; row++) {
            if (checkConsecutiveSymbols(currentPlayerStr, 0, row, 1, -1, 5)) {
                return true;
            }
        }

        // Check upward diagonals that are entirely inside the board
        for (int col = 1; col < 7; col++) {
            for (int row = 4; row < 12; row++) {
                if (checkConsecutiveSymbols(currentPlayerStr, col, row, 1, -1, 5)) {
                    return true;
                }
            }
        }





        return false;

         */
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

        // If no winning or blocking move is possible, make the best possible move available
        makeBestMove();
    }

    private static boolean makeWinningMove() {
        // Check for a winning move
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 12; col++) {
                if (buttons[row][col].getText().isEmpty() && canAIWin(row, col, 5)) {
                    buttons[row][col].setText(convertCurrentPlayer());
                    buttons[row][col].setEnabled(false);
                    return true; // AI wins, so it takes this move
                }
            }
        }
        return false; // No winning move found
    }


    private static boolean canAIWin(int row, int col, int threatLength) {
        String aiPlayerStr = convertCurrentPlayer();
        int[][] directions = {{0,1}, {1,0}, {1,1}, {1,-1}}; // horizontal, vertical, diagonal

        for (int[] direction : directions) {
            for (int offset = 0; offset < threatLength; offset++) {
                int consecutiveCount = 0;

                for (int i = 0; i < threatLength; i++) {
                    int newRow = row + (i - offset) * direction[0];
                    int newCol = col + (i - offset) * direction[1];

                    if (newRow >= 0 && newRow < 12 && newCol >= 0 && newCol < 12) {
                        if (buttons[newRow][newCol].getText().equals(aiPlayerStr)) {
                            consecutiveCount++;
                        } else if (!buttons[newRow][newCol].getText().isEmpty()) {
                            break; // This spot is occupied by the opponent, so it's not a valid winning move
                        }
                    } else {
                        break; // Out of bounds
                    }
                }

                if (consecutiveCount == threatLength - 1) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean makeBlockingMove() {
        for (int threatLength = 4; threatLength >= 3; threatLength--) {
            for (int row = 0; row < 12; row++) {
                for (int col = 0; col < 12; col++) {
                    if (buttons[row][col].getText().isEmpty()) {
                        switchPlayer();  // Pretend the opponent makes a move
                        buttons[row][col].setText(convertCurrentPlayer());

                        if (hasPotentialWin(row, col, threatLength)) {
                            switchPlayer();  // Switch back to AI
                            buttons[row][col].setText(convertCurrentPlayer()); // Block the potential win
                            buttons[row][col].setEnabled(false);
                            return true;
                        }
                        buttons[row][col].setText("");  // Reset the cell
                    }
                }
            }
        }
        return false;  // No blocking move found
    }

    private static boolean hasPotentialWin(int row, int col, int threatLength) {
        String currentPlayerStr = convertCurrentPlayer();
        int[][] directions = {{0,1}, {1,0}, {1,1}, {1,-1}}; // horizontal, vertical, diagonal

        for (int[] direction : directions) {
            for (int offset = 0; offset < threatLength; offset++) {
                int consecutiveCount = 0;

                for (int i = 0; i < threatLength; i++) {
                    int newRow = row + (i - offset) * direction[0];
                    int newCol = col + (i - offset) * direction[1];

                    if (newRow >= 0 && newRow < 12 && newCol >= 0 && newCol < 12 && buttons[newRow][newCol].getText().equals(currentPlayerStr)) {
                        consecutiveCount++;
                    } else {
                        break;
                    }
                }

                if (consecutiveCount == threatLength) {
                    return true;
                }
            }
        }

        return false;
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

    private static int countAdjacentMatches(int row, int col) {
        int count = 0;
        // Check all eight directions: left, right, up, down, four diagonals
        int[][] dirs = {{0,1}, {0,-1}, {1,0}, {-1,0}, {1,1}, {-1,-1}, {1,-1}, {-1,1}};

        for (int[] dir : dirs) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < 12 && newCol >= 0 && newCol < 12 && buttons[newRow][newCol].getText().equals(convertCurrentPlayer())) {
                count++;
            }
        }

        return count;
    }

    private static void makeBestMove() {
        int maxCount = 0;
        int bestRow = -1, bestCol = -1;

        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 12; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    int count = countAdjacentMatches(row, col);
                    if (count > maxCount) {
                        maxCount = count;
                        bestRow = row;
                        bestCol = col;
                    }
                }
            }
        }

        if (bestRow != -1 && bestCol != -1) {
            buttons[bestRow][bestCol].setText(convertCurrentPlayer());
            buttons[bestRow][bestCol].setEnabled(false);
        } else {
            makeRandomMove();
        }
    }




    private record ButtonClickListener(int row, int col) implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().isEmpty()) {
                buttons[row][col].setText(String.valueOf(currentPlayer));
                buttons[row][col].setEnabled(false);
                System.out.println("Player's move processed.");

                int gameState = checkGameState();


                if (checkWin() || checkDraw()) {
                    myDataCollection.saveMove(buttons, currentPlayer, gameState);
                    System.exit(0);
                }

                switchPlayer();
                aiMakeMove();
                System.out.println("AI's move processed.");
                gameState = checkGameState();


                if (checkWin() || checkDraw()) {
                    myDataCollection.saveMove(buttons, currentPlayer, gameState);
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

    // For automatic usage
    public static boolean run() {
        //aiMakeMove();
        int gameState = checkGameState();
        myDataCollection.saveMove(buttons, 'O', gameState);
        if (gameState == 1 || gameState == -1 || gameState == 0) {

            return false; // End the game if there's a win, lose, or draw
        }

        switchPlayer();

        aiMakeMove();
        gameState = checkGameState();
        myDataCollection.saveMove(buttons, 'X', gameState);
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
