package org.rewardModel.DataCollection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;

public class WriteToCSV {
    public synchronized static void save(String[] data, int reward, int gameState) {
        String filePath = "src/main/java/org/rewardModel/DataCollection/RewardModelDataset1.csv";

        try {
            FileWriter fileWriter = new FileWriter(filePath, true);
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

            StringBuilder csvRecord = new StringBuilder();
            for (String value : data) {
                csvRecord.append(value);
            }
            csvRecord.append(",").append(reward).append(",").append(gameState);

            csvPrinter.printRecord(csvRecord.toString());

            csvPrinter.close();
            fileWriter.close();

            System.out.println("Data written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
