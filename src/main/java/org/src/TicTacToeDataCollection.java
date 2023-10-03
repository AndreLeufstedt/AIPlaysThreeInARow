package org.src;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.src.WriteToCSV;

import javax.swing.*;

public class TicTacToeDataCollection {

    public void saveMove(JButton[][] buttons, char currentPlayer, int currentState) {
        System.out.print("Saving move to file.\n");
        String[] state = new String[144];
        int i = 0;

        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 12; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    state[i] = "E";
                } else {
                    state[i] = buttons[row][col].getText();
                }
                i++;
            }
        }

        WriteToCSV.save(state, currentPlayer == 'X' ? 1 : -1, currentState);
    }
}


