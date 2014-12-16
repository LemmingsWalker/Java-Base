package com.github.lemmingswalker;

/**
 * Created by doekewartena on 12/4/14.
 */
public interface ContourCreator {

    public void startOfScan(int[] pixels, int w, int h);

    public void startContour(int startIndex, int[] pixels, int imageWidth, int imageHeight);
    public void contourCreationFail(); // String reason?
    public void finishContour(int[] pixels, int imageWidth, int imageHeight);

    public void addCorner(int index, int x, int y);
    public void addEdge(int index, int x, int y);

    public void setMinAndMaxCornerValues(int minXIndex, int minX, int minYIndex, int minY, int maxXIndex, int maxX, int maxYIndex, int maxY);

    public void isOuterContour(boolean isOuterContour);

    public void finishOfScan();




}
