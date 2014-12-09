package com.github.lemmingswalker;

/**
 * Created by doekewartena on 6/9/14.
 */
public class ThresholdCheckerMultipleChannelsInverse extends ThresholdCheckerMultipleChannels {

    public float check (int color) {
        return 255 - super.check(color);
    }

}
