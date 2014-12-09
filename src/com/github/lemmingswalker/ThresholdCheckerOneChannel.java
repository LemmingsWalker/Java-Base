package com.github.lemmingswalker;

/**
 * Created by doekewartena on 6/9/14.
 */
public class ThresholdCheckerOneChannel implements ThresholdChecker {

    int rightShiftOffset = 0;


    public float check (int color) {
        return (color >> rightShiftOffset) & 0xFF;
    }

    public ThresholdCheckerOneChannel checkRed() {
        rightShiftOffset = 16;
        return this;
    }

    public ThresholdCheckerOneChannel checkGreen() {
        rightShiftOffset = 8;
        return this;
    }

    public ThresholdCheckerOneChannel checkBlue() {
        rightShiftOffset = 0;
        return this;
    }

    public ThresholdCheckerOneChannel checkAlpha() {
        rightShiftOffset = 24;
        return this;
    }
}

