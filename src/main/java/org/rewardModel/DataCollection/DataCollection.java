package org.rewardModel.DataCollection;

import org.rewardModel.DataCollection.WriteToCSV;

import javax.swing.*;

public class DataCollection {

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

        int reward = (currentPlayer == 'X' && currentState == 1) || (currentPlayer == 'O' && currentState == -1) ? 1 :
                (currentPlayer == 'X' && currentState == -1) || (currentPlayer == 'O' && currentState == 1) ? -1 : 0;

        WriteToCSV.save(state, reward, currentState);
    }
}
