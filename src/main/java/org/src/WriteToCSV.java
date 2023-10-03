package org.src;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class WriteToCSV {
    public static void save(String Data[], int data1 ) {
        String filePath = "DataSet1.csv";

        try {
            FileWriter fileWriter = new FileWriter(filePath, true);
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);



            csvPrinter.printRecord(String.join("", Data[0], Data[1], Data[2], Data[3], Data[4], Data[5], Data[6], Data[7], Data[8]) + ", " + data1);

            // Close the FileWriter and CSVPrinter
            csvPrinter.close();
            fileWriter.close();

            System.out.println("Data written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
