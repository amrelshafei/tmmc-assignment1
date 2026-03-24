package com.amrelshafei.tmmc.assignment1.cli.utils;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

import static com.amrelshafei.tmmc.assignment1.cli.utils.ImageColorUtil.areColorComponentsBelowThreshold;
import static com.amrelshafei.tmmc.assignment1.cli.utils.ImageColorUtil.kernelColorByAveragingComponents;

/**
 * Counts distinct vertical black lines in a white board while accounting for:
 *   - Kernel/stride usage for optimization
 *   - Noise in the middle row
 *   - Inconsistent height of a "vertical line"
 * 
 * Approach:
 * - Direction 1: Horizontal kernel scan along the middle row using a kernel size and a horizontal stride
 * - Direction 2: Upon detecting a black kernel (average color):
 *     - do an upward vertical traversal on the column above to probe height above kernel using a vertical stride (here use center of kernel to check color)
 *     - do a downward vertical traversal on the column below to probe height below kernel using a vertical stride (here use center of kernel to check color)
 * - Alogorithm:
 *     - Once you are inside a "vertical line", use a sliding window algorithm with a window size not exceeding WINDOW_SIZE 
 *     - Keep track of running average height along the current "vertical line"
 *     - The window "left" is always at or after the current "vertical line" left column
 *     - The window "right" is always at or before the current "vertical line" right column
 *     - Check if the sliding window has an average height that is close to the running average height of the "vertical line"
 *     - If not then this is more than enough noise to finalize the "vertical line"
 *     - Keep count of each finalized vertical line detection
 */
public class ImageLineCounter {

    // Kernel sizing and stride values
    private static final int KERNEL_SIZE = 5; // Odd size only
    private static final int HORIZONTAL_STRIDE = 2;
    private static final int VERTICAL_STRIDE = 3;

    // Detection threshold for RGBs near true black (0,0,0)
    private static final double BLACK_THRESHOLD = 20.0;

    // Sliding window parameters
    private static final int SLIDING_WINDOW_SIZE = 5;

    // Drift tolerance when comparing the average window height with the running average
    private static final double RUNNING_MEAN_DRIFT_RATIO = 0.25;
    private static final double HEIGHT_MEAN_MIN_DRIFT = 10.0; // Min height difference of a black column below or above the middle row from the the corresponding running mean

    public static int countVerticalLines(BufferedImage image) {
        int width  = image.getWidth();
        int height = image.getHeight();
        int cy = height / 2;

        int lineCount = 0;
        boolean inLine = false;

        // Sliding window
        Deque<Double> upwardsWindow = new ArrayDeque<>();
        double upwardsWindowSum = 0;
        double runningUpwardsMean = 0;
        Deque<Double> downwardsWindow = new ArrayDeque<>();
        double downwardsWindowSum = 0;
        double runningDownwardsMean = 0;

        int runningCount = 0;

        for (int x = 0; x < width; x += HORIZONTAL_STRIDE) {
            // Along the horizontal middle line check if the average color of the current moving kernel is black
            boolean isBlack = areColorComponentsBelowThreshold(kernelColorByAveragingComponents(image, x, cy, KERNEL_SIZE), BLACK_THRESHOLD);

            // If the kernel is not black AND the kernel was not inside a detected vertical line the last time
            // -> Bypass this whole iteration
            if (!isBlack && !inLine) {
                continue;
            }

            // If the kernel is not black AND the kernel was inside a detected vertical line the last time 
            // -> count as a new vertical line then exit iteration
            if (!isBlack) {
                lineCount++;
                inLine = false;
                upwardsWindow.clear();
                upwardsWindowSum = 0;
                runningUpwardsMean = 0;
                downwardsWindow.clear();
                downwardsWindowSum = 0;
                runningDownwardsMean = 0;
                runningCount = 0; // Global

                continue;
            }

            // Probe vertically to check height consistency of the current detected vertical line on every iteration (up + down == column height)
            int up = measureDirection(image, x, cy, -VERTICAL_STRIDE);
            int down = measureDirection(image, x, cy, VERTICAL_STRIDE);

            // If the kernel is black AND the kernel was not inside a detected vertical line the last time
            // -> Flag that the kernel is now inside a new detected vertical line
            if (!inLine) {
                inLine = true;
                upwardsWindow.clear();
                upwardsWindow.addLast((double) up);
                upwardsWindowSum = up;
                runningUpwardsMean = up;
                downwardsWindow.clear();
                downwardsWindow.addLast((double) down);
                downwardsWindowSum = down;
                runningDownwardsMean = down;
                runningCount = 1;

                continue;
            }

            // Use a sliding window to keep a sliding average of the height and check if the height is within the average height of the detected vertical line
            double upwardsWindowMean = upwardsWindowSum / upwardsWindow.size();
            double upwardThreshold = Math.max(HEIGHT_MEAN_MIN_DRIFT, RUNNING_MEAN_DRIFT_RATIO * runningUpwardsMean);
            boolean upwardsWithinGlobal = Math.abs(upwardsWindowMean - runningUpwardsMean) <= upwardThreshold;

            // Use a sliding window to keep a sliding average of the height and check if the height is within the average height of the detected vertical line
            double downwardsWindowMean = downwardsWindowSum / downwardsWindow.size();
            double downwardThreshold = Math.max(HEIGHT_MEAN_MIN_DRIFT, RUNNING_MEAN_DRIFT_RATIO * runningDownwardsMean);
            boolean downwardsWithinGlobal = Math.abs(downwardsWindowMean - runningDownwardsMean) <= downwardThreshold;

            if (!upwardsWithinGlobal || !downwardsWithinGlobal) {
                lineCount++;
                inLine = false;
                upwardsWindow.clear();
                upwardsWindowSum = 0;
                runningUpwardsMean = 0;
                downwardsWindow.clear();
                downwardsWindowSum = 0;
                runningDownwardsMean = 0;
                runningCount = 0;

                continue;
            }

            // Accept column as part of the vertical line and slide window
            runningCount++;
            if (upwardsWindow.size() >= SLIDING_WINDOW_SIZE) {
                upwardsWindowSum -= upwardsWindow.removeFirst();
            }
            upwardsWindow.addLast((double) up);
            upwardsWindowSum += up;
            runningUpwardsMean += (up - runningUpwardsMean) / runningCount;

            if (downwardsWindow.size() >= SLIDING_WINDOW_SIZE) {
                downwardsWindowSum -= downwardsWindow.removeFirst();
            }
            downwardsWindow.addLast((double) down);
            downwardsWindowSum += down;
            runningDownwardsMean += (down - runningDownwardsMean) / runningCount;
        }

        // Close trailing line
        if (inLine) {
            lineCount++;
        }

        return lineCount;
    }

    /**
     * Vertical height probe by analyzing an image to find the height of a black column below or above a cell using stride direction.
     * 
     * @param image The analyzed image
     * @param x Probed column
     * @param startY Starting row
     * @param strideY A directed stride where < 0 mean it measures height of black column below and above if > 0
     * @return
     */
    private static int measureDirection(BufferedImage image, int x, int startY, int strideY) {
        int distance = 0;
        int y = startY + strideY;

        while (y >= 0 && y < image.getHeight()) {
            if (areColorComponentsBelowThreshold(image.getRGB(x, y), BLACK_THRESHOLD)) break;

            distance += Math.abs(strideY);
            y += strideY;
        }
        return distance;
    }
}
