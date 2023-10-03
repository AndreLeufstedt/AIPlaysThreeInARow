package org.src;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;

public class WriteToCSV {
    public synchronized static void save(String[] data, int data1, int data2) {
        String filePath = "DataSets1.csv";

        try {
            FileWriter fileWriter = new FileWriter(filePath, true);
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

            // Create a CSV record by joining the data array elements and adding the additional data1 and data2 columns
            StringBuilder csvRecord = new StringBuilder();
            for (String value : data) {
                csvRecord.append(value);
            }
            csvRecord.append(", ").append(data1).append(", ").append(data2);

            // Print the CSV record
            csvPrinter.printRecord(csvRecord.toString());

            // Close the FileWriter and CSVPrinter
            csvPrinter.close();
            fileWriter.close();

            System.out.println("Data written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
