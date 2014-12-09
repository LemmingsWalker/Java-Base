/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package com.github.lemmingswalker;


import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 *
 * @example Hello
 *
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

public class ContourFinder {

    public final static String VERSION = "##library.prettyVersion##";

    private static boolean didWelcome;

    private boolean didInit;

    public ContourWalker contourWalker;

    // instead of scanning every row
    // we jump with increments
    private int scanIncrementX = 32;
    private int scanIncrementY = 32;

    ContourCreator contourCreator;

    // ------------ region of interest -----------------------------

    // a ROI will always be used, however if set to false
    // it will create a ROI as large as the image
    boolean useROI = false;

    Rectangle roi; // the one the user sets
    Rectangle wROI; // the one we work with
    // the class that backups, create and restores a roi.
    ROICreator roiCreator;

    // there can be a slight speed
    // increase if not backing up the
    // border, however, this will affect
    // the image pixels in the end
    public boolean backupBorder = true;

    // default is pure black
    // but the user can change it in case he's not using the default
    // threshold checker.
    protected int borderColor = -16777216;

    float threshold = 128;


    /**
     *
     */
    public ContourFinder() {

        contourWalker = new ContourWalker();

        welcome();
    }




    private void welcome() {
        if (!didWelcome) {
            System.out.println("##library.name## ##library.prettyVersion## by ##author##");
            didWelcome = true;
        }
    }

    /**
     * return the version of the library.
     *
     * @return String
     */
    public static String version() {
        return VERSION;
    }


    // -----------------
    // S E T T I N G S
    // -----------------



    // . . . . . . . . . . . . . . . . . . . . . . . .


    /**
     * Using init is optional, it will check every call to scan if it did init.
     */
    public void init() {


        if (contourWalker.getThresholdChecker() == null) {
            contourWalker.setThresholdTracker(new ThresholdCheckerOneChannel());
        }

        roiCreator = new ROICreator();

        if (roi == null) {
            roi = new Rectangle();
        }

        didInit = true;

    }


    // . . . . . . . . . . . . . . . . . . . . . . . .



    /**
     * Set a manual value for how much it must jump when scanning the
     * pixels for blobs.
     *
     * @param i
     * @return
     */
    public ContourFinder setScanIncrementX(int i) {
        scanIncrementX = Common.max(i, 1);
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     * Set a manual value for how much it must jump when scanning the
     * pixels for blobs.
     *
     * @param i
     * @return
     */
    public ContourFinder setScanIncrementY(int i) {
        scanIncrementY = Common.max(i, 1);
        return this;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int getScanIncrementX() {
        return scanIncrementX;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int getScanIncrementY() {
        return scanIncrementY;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public ContourFinder setContourCreator(ContourCreator contourCreator) {
        this.contourCreator = contourCreator;
        return this;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    // -------------------
    // U S E
    // -------------------


    // . . . . . . . . . . . . . . . . . . . . . . . .

    /**
    Returns true if it will scan over the x axis.
    It will scan over whichever increment is the biggest.
    */
    public boolean overX() {
        return scanIncrementX > scanIncrementY;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public boolean overY() {
        return !overX();
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     *
     * Set the Region of Interest.
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public ContourFinder setROI(int x, int y, int w, int h) {

        if (roi == null) {
            roi = new Rectangle();
        }
        // we are not interested in the border
        // so we adjust the values slightly
        roi.setRect(x-1, y-1, w+2, h+2);

        useROI = true;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     * Set the Region of Interest.
     *
     * @param roi
     * @return
     */
    public ContourFinder setROI(Rectangle roi) {

        this.roi = roi;
        useROI = true;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public ContourFinder disableROI() {
        useROI = false;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public Rectangle getRoi() {
        return roi;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     * Convenient way to set the thresholdChecker in contourWalker.
     *
     * @param thresholdChecker
     * @return
     */
    public ContourFinder setThresholdChecker(ThresholdChecker thresholdChecker) {
        contourWalker.setThresholdTracker(thresholdChecker);
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     * Convenient way to get the thresholdChecker of the contourWalker.
     *
     * @return
     */
    public ThresholdChecker getThresholdChecker() {
        return contourWalker.thresholdChecker;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public ContourFinder setThreshold(float threshold) {
        this.threshold = threshold;
        return this;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public float getThreshold() {
        return threshold;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public ContourFinder setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int getBorderColor() {
        return borderColor;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .



    /**
     *
     * @param pixels pixel array
     * @param imageWidth image getWidth
     * @return
     */
    // todo, methods that allow byte[] etc?
    public void scan(int[] pixels, int imageWidth, int imageHeight) {

        if (!didInit) init();

        // todo, check if contourCreator is set
        // rename pre and post?
        contourCreator.startOfScan();
        //contourCreator.setPixels(pixels);
        //contourCreator.setImageSize(imageWidth, imageHeight);

        // thresholdChecker can't be null since init takes care of that
        ThresholdChecker thresholdChecker = contourWalker.thresholdChecker;


        roiCreator.set(pixels, imageWidth);

        wROI = createWorkROI(imageWidth, imageHeight);
        if (backupBorder) roiCreator.backupBorder(wROI.x, wROI.y, wROI.width, wROI.height);
        roiCreator.createBorder(wROI.x, wROI.y, wROI.width, wROI.height, borderColor);


        // first scan with lines
        // if a pixel is blobColor
        // and the previous pixel
        // is black then we have a
        // start position to start scanning

        int index;

        int currentColor;

        float currentColorValue, lastColorValue;
        final float t = threshold;

        lastColorValue = thresholdChecker.check(borderColor);


        final boolean overX = overX();

        final int xIncrement = overX ? 1 : scanIncrementX;
        final int yIncrement = overX ? scanIncrementY : 1;
        final int startX = overX ? wROI.x : wROI.x + scanIncrementX;
        final int startY = overX ? wROI.y + scanIncrementY : wROI.y;
        final int maxX = wROI.x + wROI.width;
        final int maxY = wROI.y + wROI.height;


        for (int x = startX; x < maxX; x+= xIncrement) {
            for (int y = startY; y < maxY; y+= yIncrement) {

                index = y * imageWidth + x;

                currentColor = pixels[index];
                currentColorValue = thresholdChecker.check(currentColor);

                if (currentColorValue >= t && lastColorValue < t) { // edge

                    if (!contourCreator.checkForExistingBlob(index, x, y)) {

                        contourWalker.scan(pixels, imageWidth, imageHeight, index, threshold, contourCreator);

                    }
                }

                lastColorValue = currentColorValue;
            }
        }


        if (backupBorder) roiCreator.restoreBorder();

        contourCreator.finishOfScan();

    }



    // . . . . . . . . . . . . . . . . . . . . . . . .


    /**
     *
     * This takes care of a ROI exceeding the bounds of the image.
     *
     * @param imageWidth
     * @param imageHeight
     * @return
     */
    private Rectangle createWorkROI(int imageWidth, int imageHeight) {

        Rectangle wROI;

        if (useROI) {
            // in case it exceeds the bottom or left/right edge
            Rectangle2D tmpROI = roi.createIntersection(new Rectangle(0, 0, imageWidth, imageHeight));
            wROI = new Rectangle((int)tmpROI.getX(), (int)tmpROI.getY(), (int)tmpROI.getWidth(), (int)tmpROI.getHeight());
        }
        else {
            wROI = new Rectangle(0, 0, imageWidth, imageHeight);
        }

        return wROI;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .




}

