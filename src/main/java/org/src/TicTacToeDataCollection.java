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
        System.out.print("Saving move to file. \n");
        String[] _State = new String[9];
        int i = 0;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    _State[i] = "E";
                } else {
                    _State[i] = buttons[row][col].getText();
                }
                i++;
            }
        }

        WriteToCSV.save(_State, currentPlayer == 'X' ? 1 : -1, currentState);
    }
}

