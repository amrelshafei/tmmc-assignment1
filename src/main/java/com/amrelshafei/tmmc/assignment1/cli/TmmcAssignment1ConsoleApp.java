package com.amrelshafei.tmmc.assignment1.cli;

import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import static com.amrelshafei.tmmc.assignment1.cli.utils.ImageLineCounter.countVerticalLines;

public class TmmcAssignment1ConsoleApp {
    
    public static void main(String[] args) {
        // --- Requirement 1: Exactly 1 argument ---
        if (args.length != 1) {
            System.out.println("Error: Invalid number of arguments.");
            return;
        }

        try {
            String imagePath = args[0];
            File imageFile = new File(imagePath);

            if (!imageFile.exists()) {
                System.out.println("Error: File not found: " + imagePath);
                return;
            }

            if (!imageFile.isFile()) {
                System.out.println("Error: Path does not point to a file: " + imagePath);
                return;
            }

            BufferedImage image = ImageIO.read(imageFile);

            if (image == null) {
                System.out.println("Error: Could not read image file. Ensure it is a valid JPG: " + imagePath);
                return;
            }

            int lineCount = countVerticalLines(image);

            // --- Requirement 3: Output the number of vertical lines ---
            System.out.println(lineCount);

        } catch (Exception e) {
            // --- Requirement 1b: Any exception must be output to console ---
            System.out.println("Error: " + e.getMessage());
        }
    }

}
