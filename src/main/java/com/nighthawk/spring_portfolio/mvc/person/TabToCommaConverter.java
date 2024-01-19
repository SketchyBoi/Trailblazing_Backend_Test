package com.nighthawk.spring_portfolio.mvc.person;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TabToCommaConverter {
    public static void main(String[] args) {
        // Input and output file paths
        String inputFile = "src/main/java/com/nighthawk/spring_portfolio/mvc/person/input.txt";
        String outputFile = "src/main/java/com/nighthawk/spring_portfolio/mvc/person/output.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             FileWriter writer = new FileWriter(outputFile)) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Replace tabs with commas and spaces
                String modifiedLine = line.replaceAll("\t", ", ");

                // Write the modified line to the output file
                writer.write(modifiedLine + "\n");
            }

            System.out.println("Conversion complete. Output written to " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
