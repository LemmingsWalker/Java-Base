package com.github.lemmingswalker;

/**
 * Created by doekewartena on 12/4/14.
 */
public interface ContourCreator {

    public void startOfScan();

    public void setPixels(int[] pixels);
    public void setImageSize(int w, int h);

    public boolean checkForExistingBlob(int index, int x, int y);

    public void startContour();
    public void contourCreationFail();
    public void finishedContour();

    public void setContourScanStartIndex(int index);
    public void addToCornerIndexes(int index);
    public void addToEdgeIndexes(int index);

    public void finishOfScan();




}
