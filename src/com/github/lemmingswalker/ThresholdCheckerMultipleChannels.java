package com.github.lemmingswalker;

/**
 * Created by doekewartena on 6/9/14.
 */
public class ThresholdCheckerMultipleChannels implements ThresholdChecker {

    boolean checkRed = false;
    boolean checkGreen = false;
    boolean checkBlue = true;
    boolean checkAlpha = false;

    float sum;
    float div;

    public float check (int color) {
        sum = 0;
        div = 0;

        if (checkRed) {
            div++;
            sum += (color >> 16) & 0xFF;
        }
        if (checkGreen) {
            div++;
            sum += (color >> 8) & 0xFF;
        }
        if (checkBlue) {
            div++;
            sum += color & 0xFF;
        }
        if (checkAlpha) {
            div++;
            sum += (color >> 24) & 0xFF;
        }

        return (sum/div);

    }

    public ThresholdCheckerMultipleChannels checkRed(boolean b) {
        checkRed = b;
        return this;
    }

    public ThresholdCheckerMultipleChannels checkGreen(boolean b) {
        checkGreen = b;
        return this;
    }

    public ThresholdCheckerMultipleChannels checkBlue(boolean b) {
       checkBlue = b;
       return this;
    }

    public ThresholdCheckerMultipleChannels checkAlpha(boolean b) {
       checkAlpha = b;
       return this;
    }

}
