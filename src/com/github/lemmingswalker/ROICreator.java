package com.github.lemmingswalker;


/**
 * Created by doekewartena on 6/8/14.
 */
public class ROICreator {

    // pixels to operate on
    int[] pixels;

    // getWidth of the image to operate on
    int width;


    // used to create a backup
    // to revert changes at the end
    int[] pixelsTop, pixelsBottom, pixelsLeft, pixelsRight;

    // the values used to backUp the border
    int x, y, w, h;

    // . . . . . . . . . . . . . . . . . . . . . . . .


    public void set(int[] pixels, int width) {
        this.pixels = pixels;
        this.width = width;
    }


    public void backupBorder() {
        if (pixels == null) {
            // todo throw nullPointerException?
            System.err.println("ERROR in ROICreator: set has to be called before backupBorder");
        }
        int height = pixels.length/width;
        backupBorder(0, 0, width, height);
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public void backupBorder(int x, int y, int w, int h) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        // we always keep the largest array
        // this to avoid recreating the array
        // when calls for different size is made
        if (pixelsTop == null || w > pixelsTop.length) {
            pixelsTop = new int[w];
            pixelsBottom = new int[w];
        }
        if (pixelsLeft == null || h > pixelsLeft.length) {
            pixelsLeft = new int[h];
            pixelsRight = new int[h];
        }

        int c; // count;

        // top edge
        c = 0; // count
        for (int cx = x; cx < x + w; cx++ ) {
            pixelsTop[c++] = pixels[cx+(y*width)];
        }

        // bottom edge
        c = 0;
        for (int cx = x; cx < x + w; cx++ ) {
            pixelsBottom[c++] = pixels[cx+((y+h-1)*width)];
        }

        // left edge
        c = 0;
        for (int cy = y; cy < y+h; cy++ ) {
            pixelsLeft[c++] = pixels[x+(cy*width)];
        }

        // right edge
        c = 0;
        for (int cy = y; cy < y+h; cy++ ) {
            pixelsRight[c++] = pixels[x+(w-1)+(cy*width)];
        }





    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public void createBorder(int color) {
        int height = pixels.length/width;
        createBorder(0, 0, width, height, color);
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public void createBorder(int x, int y, int w, int h, int color) {

        // top edge
        for (int cx = x; cx < x + w; cx++ ) {
            pixels[cx+(y*width)] = color;
        }

        // bottom edge
        for (int cx = x; cx < x + w; cx++ ) {
            pixels[cx+((y+h-1)*width)] = color;
        }

        // left edge
        for (int cy = y; cy < y+h; cy++ ) {
            pixels[x+(cy*width)] = color;
        }

        // right edge
        for (int cy = y; cy < y+h; cy++ ) {
            pixels[x+(w-1)+(cy*width)] = color;
        }

    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public void restoreBorder() {

        int c; // count;

        // top edge
        c = 0; // count
        for (int cx = x; cx < x + w; cx++ ) {
            pixels[cx+(y*width)] = pixelsTop[c++];
        }

        // bottom edge
        c = 0;
        for (int cx = x; cx < x + w; cx++ ) {
            pixels[cx+((y+h-1)*width)] = pixelsBottom[c++];
        }

        // left edge
        c = 0;
        for (int cy = y; cy < y+h; cy++ ) {
            pixels[x+(cy*width)] = pixelsLeft[c++];
        }

        // right edge
        c = 0;
        for (int cy = y; cy < y+h; cy++ ) {
            pixels[x+(w-1)+(cy*width)] = pixelsRight[c++];
        }


    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

}