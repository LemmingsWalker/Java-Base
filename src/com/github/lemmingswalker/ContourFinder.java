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

    // instead of scanning every row/column
    // we jump with increments
    private int jumpIncrement = 16;
    boolean scanOverX = true;

    ContourCreator contourCreator;

    // ------------ region of interest -----------------------------

    // a ROI will always be used, however if set to false
    // it will create a ROI as large as the image
    boolean useROI = false;

    Rectangle roi; // the one the user sets
    Rectangle wROI; // the one we work with
    // the class that backups, create and restores a roi.
    BorderBackupCreator borderBackupCreator;

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

    int scanId;

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

        borderBackupCreator = new BorderBackupCreator();

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
    public ContourFinder setJumpIncrement(int i) {
        jumpIncrement = i > 0 ? i : 1;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int getJumpIncrement() {
        return jumpIncrement;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public ContourFinder setScanOverX(boolean scanOverX) {
        this.scanOverX = scanOverX;
        return this;
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
    */
    public boolean isScanOverX() {
        return scanOverX;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public boolean isScanOverY() {
        return !isScanOverX();
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

    // what about an enable method?
    public ContourFinder disableROI() {
        useROI = false;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public Rectangle getRoi() {
        return roi;
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

    public void scan(int[] pixels, int imageWidth, int imageHeight) {
         scan(pixels, imageWidth, imageHeight, useROI ? roi : null, null, threshold, contourCreator);
    }

    /**
     *
     * @param pixels pixel array
     * @param imageWidth image getWidth
     * @return
     */
    // todo, methods that allow byte[] etc?
    public void scan(int[] pixels, int imageWidth, int imageHeight, Rectangle roi, ThresholdChecker thresholdChecker, float threshold, ContourCreator contourCreator) {

        if (!didInit) init();

        if (thresholdChecker == null) {
            thresholdChecker = contourWalker.getThresholdChecker();
        }

        if (contourCreator == null) {
            if (this.contourCreator == null)
            throw new NullPointerException("No ContourCreator has been set!");

            contourCreator = this.contourCreator;
        }

        borderBackupCreator.set(pixels, imageWidth, imageHeight);

        // get the region of interest right
        if (roi == null) roi = useROI ? this.roi : null;

        if (useROI) {
            // in case it exceeds the bottom or left/right edge
            Rectangle2D tmpROI = roi.createIntersection(new Rectangle(0, 0, imageWidth, imageHeight));
            wROI = new Rectangle((int) tmpROI.getX(), (int) tmpROI.getY(), (int) tmpROI.getWidth(), (int) tmpROI.getHeight());
        } else {
            wROI = new Rectangle(0, 0, imageWidth, imageHeight);
        }


        if (backupBorder) borderBackupCreator.backupBorder(wROI.x, wROI.y, wROI.width, wROI.height);
        borderBackupCreator.createBorder(wROI.x, wROI.y, wROI.width, wROI.height, borderColor);

        scanId++;

        // first scan with lines
        // if a pixel is blobColor
        // and the previous pixel
        // is black then we have a
        // start position to start scanning

        int index;

        int currentColor;

        float currentColorValue, lastColorValue;

        lastColorValue = thresholdChecker.check(borderColor);


        final boolean overX = isScanOverX();

        final int xIncrement = overX ? 1 : jumpIncrement;
        final int yIncrement = overX ? jumpIncrement : 1;
        final int startX = overX ? wROI.x : wROI.x + jumpIncrement;
        final int startY = overX ? wROI.y + jumpIncrement : wROI.y;
        final int maxX = wROI.x + wROI.width;
        final int maxY = wROI.y + wROI.height;

        contourCreator.startOfScan(pixels, imageWidth, imageHeight);

        for (int x = startX; x < maxX; x+= xIncrement) {
            for (int y = startY; y < maxY; y+= yIncrement) {

                index = y * imageWidth + x;

                currentColor = pixels[index];
                currentColorValue = thresholdChecker.check(currentColor);

                if (currentColorValue >= threshold && lastColorValue < threshold) { // edge / corner

                    contourWalker.scan(pixels, imageWidth, imageHeight, index, thresholdChecker, threshold, contourCreator, scanId);
                }

                lastColorValue = currentColorValue;
            }
        }


        if (backupBorder) borderBackupCreator.restoreBorder();

        contourCreator.finishOfScan();

    }



    // . . . . . . . . . . . . . . . . . . . . . . . .




}

