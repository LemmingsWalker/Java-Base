package com.github.lemmingswalker;

/**
 * Created by doekewartena on 12/4/14.
 */
public interface ContourCreator {

    public void startOfScan(int[] pixels, int w, int h);

    public boolean checkForExistingBlob(int index, int x, int y);

    public void startContour(int startIndex);
    public void contourCreationFail();
    public void finishedContour();

    public void addToCornerIndexes(int index);
    public void addToEdgeIndexes(int index);

    public void finishOfScan();




}
