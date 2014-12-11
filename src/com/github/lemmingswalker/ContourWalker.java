package com.github.lemmingswalker;


/**
 * Created by doekewartena on 4/30/14.
 */
public class ContourWalker {

    int moveDirection, checkDirection;


    final static  int LEFT = -1;
    final static  int RIGHT = 1;
    // will be set in scan according to
    // the width of the image
    int UP, DOWN;

    boolean downIsFree, rightIsFree, upIsFree, leftIsFree;

    ThresholdChecker thresholdChecker;



    public ContourWalker() {
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public ContourWalker setThresholdTracker(ThresholdChecker thresholdChecker) {
        this.thresholdChecker = thresholdChecker;
        return this;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public ThresholdChecker getThresholdChecker() {
        return thresholdChecker;
    }



    public void scan(int[] pixels, int imageWidth, int imageHeight, int startX, int startY, float threshold, ContourCreator blobCreator) {

        int start = startY*imageWidth+startX;

        scan(pixels, imageWidth, imageHeight, start, threshold, blobCreator);
    }


    /**
     *
     * @param pixels
     * @param imageWidth
     * @param startIndex
     * @param threshold
     * @return
     */
    public void scan(int[] pixels, int imageWidth, int imageHeight, int startIndex, float threshold, ContourCreator blobCreator) {

        blobCreator.startContour(startIndex, pixels, imageWidth, imageHeight);

        if (thresholdChecker == null) {
            // make one channel checker the default since it's the fastest
            thresholdChecker = new ThresholdCheckerOneChannel();
        }

        final float t = threshold;
        float valueDown, valueUp, valueLeft, valueRight;

        UP = -imageWidth;
        DOWN = imageWidth;


        int current = startIndex;

        valueDown = thresholdChecker.check(pixels[current+DOWN]);
        valueRight = thresholdChecker.check(pixels[current+RIGHT]);
        valueUp = thresholdChecker.check(pixels[current+UP]);
        valueLeft = thresholdChecker.check(pixels[current+LEFT]);


        // or we don't add it!
        downIsFree = valueDown >= t;
        rightIsFree = valueRight >= t;
        upIsFree =  valueUp >= t;
        leftIsFree = valueLeft >= t;

        // todo: 1 line starting point blob
        // if we would check all surrounding pixels
        // from the start point then we could
        // figure out if the start is like a single line
        // if that is true then we have to scan in the other direction as well
        // maybe set a boolean that the first cornerPixel is not a cornerPixel
        float valueDownLeft = thresholdChecker.check(pixels[current+DOWN+LEFT]);
        float valueDownRight = thresholdChecker.check(pixels[current+DOWN+RIGHT]);
        float valueUpLeft = thresholdChecker.check(pixels[current+UP+LEFT]);
        float valueUpRight = thresholdChecker.check(pixels[current+UP+RIGHT]);

        // todo: onLine check, this should work, 1st = horizontal line 2nd = vertical line
        // onLine = upIsFree && downIsFree ? true : leftIsFree && rightIsFree ? true : false;

        // false on isolated pixel
        if(!setStartDirection()) {
            // we can't get a blob out of this
            // we can't reject the blob since rejected blobs
            // are valid!
            // but nOfBlobs is now incorrect!
            blobCreator.contourCreationFail();
            return;
        }



        int c = 0;
        if (!leftIsFree) c++;
        if (!rightIsFree) c++;
        if (!upIsFree) c++;
        if (!downIsFree) c++;

        if (c >= 2) {  // it's a corner // todo, this is not correct?, could also be a single line
            blobCreator.addToCornerIndexes(current);
        }

        float valueMoveDirection, valueCurrentDirection;

        while(true) {

            valueMoveDirection =  thresholdChecker.check(pixels[current + moveDirection]);

            if (valueMoveDirection >= t) {

                blobCreator.addToEdgeIndexes(current);

                current += moveDirection;

                if (current == startIndex) {
                    break;
                }

                valueCurrentDirection =  thresholdChecker.check(pixels[current + checkDirection]);

                if (valueCurrentDirection >= t) {

                    blobCreator.addToCornerIndexes(current);

                    current += checkDirection;

                    if (current == startIndex) {
                        break;
                    }

                    setDirection(checkDirection);
                }


            }
            else {
                // we hit a wall
                // so go right since we want to go clock wise
                blobCreator.addToCornerIndexes(current);
                turnRight();

            }

        }

        blobCreator.finishContour(pixels, imageWidth, imageHeight);

    }

    // . . . . . . . . . . . . . . . . . . . . . . .


    /*
   Make sure downIsFree, rightIsFree, upIsFree and leftIsFree is set
   */
    protected boolean setStartDirection() {
        if (downIsFree && !rightIsFree) { // DOWN free
            moveDirection = DOWN;
            checkDirection = RIGHT;
        }
        else if (rightIsFree && !upIsFree) { // RIGHT free
            moveDirection = RIGHT;
            checkDirection = UP;
        }
        else if (upIsFree && !leftIsFree ) { // UP free
            moveDirection = UP;
            checkDirection = LEFT;
        }

        else if (leftIsFree && !downIsFree) { // LEFT free
            moveDirection = LEFT;
            checkDirection = DOWN;
        }
        else {
            // we have hit a isolated pixel
            /*
            PApplet.println("pixel index: "+current);

            String l =  leftIsFree ? " " : "X";
            String r =  rightIsFree ? " " : "X";
            String u =  upIsFree ? " " : "X";
            String d =  downIsFree ? " " : "X";

            PApplet.println("[ ][" + u + "][ ]");
            PApplet.println("["+l+"][C]["+r+"]");
            PApplet.println("[ ]["+d+"][ ]");
            */

            return false;
        }
        return true;

    }

    // . . . . . . . . . . . . . . . . . . . . . . .

    // . . . . . . . . . . . . . . . . . . . . . . .

    protected void setDirection(int dir) {
        if (dir == RIGHT) {
            moveDirection = RIGHT;
            checkDirection = UP;
        }
        else if (dir == DOWN) {
            moveDirection = DOWN;
            checkDirection = RIGHT;
        }
        else if (dir == LEFT) {
            moveDirection = LEFT;
            checkDirection = DOWN;
        }
        else if (dir == UP) {
            moveDirection = UP;
            checkDirection = LEFT;
        }
    }


    // . . . . . . . . . . . . . . . . . . . . . . .


    /*
    We want to check in a clockwise motion to get the contour.
    We could make a call to setDirection but this is faster.
     */
    protected void turnRight() {

        if (moveDirection == UP) {
            moveDirection = RIGHT;
            checkDirection = UP;
        }
        else if (moveDirection == RIGHT) {
            moveDirection = DOWN;
            checkDirection = RIGHT;
        }
        else if (moveDirection == DOWN) {
            moveDirection = LEFT;
            checkDirection = DOWN;
        }
        else if (moveDirection == LEFT) {
            moveDirection = UP;
            checkDirection = LEFT;
        }

        //PApplet.println("turnRight");
        //printDirection();


    }

    // . . . . . . . . . . . . . . . . . . . . . . .




}
