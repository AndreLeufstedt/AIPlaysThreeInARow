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

import static org.src.TicTacToeAITrain.outputSize;

public class TicTacToeDataPreparation {
    private static List<INDArray> inputs;
    private static List<INDArray> outputs;

    private static int skipped = 0;

    static {
        try {
            prepareData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void prepareData() throws Exception {
        // Load the dataset from a CSV file
        String csvFilePath = "Datasets1.csv"; // Replace with the actual file path
        InputSplit dataSplit = new FileSplit(new File(csvFilePath));
        try (RecordReader recordReader = new CSVRecordReader()) {
            recordReader.initialize(dataSplit);

            // Prepare lists for inputs (game states) and outputs (next moves)
            inputs = new ArrayList<>();
            outputs = new ArrayList<>();

            while (recordReader.hasNext()) {
                List<Writable> record = recordReader.next();
                if (record.size() >= 3) { // Ensure the record has at least 3 elements
                    try {
                        String gameState = record.get(0).toString();
                        int outcome = Integer.parseInt(record.get(1).toString().trim());
                        int currentState = Integer.parseInt(record.get(2).toString().trim()); // New input

                        // Convert gameState to input (one-hot encoding)
                        INDArray input = convertToInput(gameState);

                        // Convert outcome to output (one-hot encoding of next move)
                        INDArray output = convertToOutput(String.valueOf(outcome), currentState);

                        // Handle the currentState as needed
                        // For example, you can add it to the input or output, or use it in some other way.

                        inputs.add(input);
                        outputs.add(output);

                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing record: " + record);
                        e.printStackTrace();
                    }
                } else {
                    skipped++;
                    System.out.println(skipped + " Skipping record with insufficient elements: " + record);
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
        INDArray input = Nd4j.zeros(1, 432);

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

        // Reshape the input to have shape [1, 432]
        input = input.reshape(1, 432);

        return input;
    }


    // Helper method to convert outcome and currentState to output
    private static INDArray convertToOutput(String outcomeStr, int currentState) {
        int outcome = Integer.parseInt(outcomeStr);
        // Assuming 144 possible next moves in your classification problem
        int outputSize = 144;
        INDArray output = Nd4j.zeros(1, outputSize);

        // Set the appropriate position in the output array to 1
        output.putScalar(new int[] {0, outcome}, 1);

        // Append the currentState to the end
        output.putScalar(new int[] {0, outputSize - 1}, currentState);

        return output;
    }

}
