package org.src;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.Writable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeDataPreparation {
    private static List<INDArray> inputs;
    private static List<INDArray> outputs;

    static {
        try {
            prepareData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void prepareData() throws Exception {
        // Load the dataset from a CSV file
        String csvFilePath = "Dataset1.csv"; // Replace with the actual file path
        InputSplit dataSplit = new FileSplit(new File(csvFilePath));
        try (RecordReader recordReader = new CSVRecordReader()) {
            recordReader.initialize(dataSplit);

            // Prepare lists for inputs (game states) and outputs (next moves)
            inputs = new ArrayList<>();
            outputs = new ArrayList<>();

            while (recordReader.hasNext()) {
                List<Writable> record = recordReader.next();
                try {
                    String gameState = record.get(0).toString();
                    int outcome = Integer.parseInt(record.get(1).toString().trim());
                    // Convert gameState to input (one-hot encoding)
                    INDArray input = convertToInput(gameState);

                    // Convert outcome to output (one-hot encoding of next move)
                    INDArray output = convertToOutput(String.valueOf(outcome));

                    inputs.add(input);
                    outputs.add(output);

                } catch (NumberFormatException e) {
                    System.out.println("Error parsing record: " + record);
                    e.printStackTrace();
                }
            }


        }
    }

    public static List<INDArray> getInputs() {
        return inputs;
    }

    public static List<INDArray> getOutputs() {
        return outputs;
    }
    // Helper method to convert gameState to input (one-hot encoding)
    public static INDArray convertToInput(String gameState) {
        // This will result in an array of size 27 for 9 board positions
        INDArray input = Nd4j.zeros(1, 27);

        for (int i = 0; i < gameState.length(); i++) {
            char c = gameState.charAt(i);
            if (c == 'X') {
                input.putScalar(new int[] {0, i * 3}, 1);
            } else if (c == 'O') {
                input.putScalar(new int[] {0, i * 3 + 1}, 1);
            } else if (c == 'E') {
                input.putScalar(new int[] {0, i * 3 + 2}, 1);
            }
        }

        return input;
    }

    // Helper method to convert outcome to output (one-hot encoding of next move)
    private static INDArray convertToOutput(String gameState) {
        // The gameState for the output seems to be reused from the above. I assume this is a mistake.
        // Instead, you should have an 'int outcome' to tell which position is the next move.
        // Assuming that, the function should be like:

        int outcome = Integer.parseInt(gameState); // this line should be changed if the outcome is different
        INDArray output = Nd4j.zeros(1, 9);
        output.putScalar(new int[] {0, outcome}, 1);
        return output;
    }

}
