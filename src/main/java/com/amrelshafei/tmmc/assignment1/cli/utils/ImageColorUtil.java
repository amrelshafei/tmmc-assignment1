package com.amrelshafei.tmmc.assignment1.cli.utils;

import java.awt.image.BufferedImage;

public class ImageColorUtil {
    
    /**
     * Checks if each color component is below a given threshold.
     * 
     * @param argb ARGB color int
     * @param threshold Max threshold for a color component from ARGB
     * @return A flag denoting if each color component is below a given threshold
     */
    static boolean areColorComponentsBelowThreshold(int argb, double threshold) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >>  8) & 0xFF;
        int b =  argb        & 0xFF;
        return r <= threshold && g <= threshold && b <= threshold;
    }

    /**
     * Finds the average of each color component from all the pixels inside a kernel with center (cx, cy). Must use an odd size only.
     * 
     * @param image A given image
     * @param cx Center of kernel in column coordinate
     * @param cy Center of kernel in row coordinate
     * @param size Odd kernel size
     * @return The average of each color component from all the pixels inside a kernel with center (cx, cy)
     */
    static int kernelColorByAveragingComponents(BufferedImage image, int cx, int cy, int size) {
        int x0 = Math.max(0, cx - size / 2);
        int x1 = Math.min(image.getWidth() - 1, cx + size / 2);
        int y0 = Math.max(0, cy - size / 2);
        int y1 = Math.min(image.getHeight() - 1, cy + size / 2);

        long sumA = 0;
        long sumR = 0;
        long sumG = 0;
        long sumB = 0;
        int count = 0;

        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                int argb = image.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >>  8) & 0xFF;
                int b =  argb & 0xFF;

                sumA += a;
                sumR += r;
                sumG += g;
                sumB += b;
                count++;
            }
        }

        if (count == 0) return 0xFFFFFFFF; // default white

        int avgA = (int) (sumA / count);
        int avgR = (int) (sumR / count);
        int avgG = (int) (sumG / count);
        int avgB = (int) (sumB / count);
        return (avgA << 24) | (avgR << 16) | (avgG << 8) | avgB;
    }
}
