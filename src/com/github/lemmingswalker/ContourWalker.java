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


    // we store here for every edge index the contourExistCheckValue
    // this we we can check fast if a blob exists or not
    int[] contourExistCheckData;

    public ContourWalker() {
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public ContourWalker setThresholdTracker(ThresholdChecker thresholdChecker) {
        this.thresholdChecker = thresholdChecker;
        return this;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public ThresholdChecker getThresholdChecker() {
        if (thresholdChecker == null) {
            // make one channel checker the default since it's the fastest
            thresholdChecker = new ThresholdCheckerOneChannel();
        }
        return thresholdChecker;
    }



    public void scan(int[] pixels, int imageWidth, int imageHeight, int startX, int startY, float threshold, ContourCreator blobCreator, int scanID) {

        int start = startY*imageWidth+startX;

        // avoid it being null
        thresholdChecker = getThresholdChecker();

        scan(pixels, imageWidth, imageHeight, start, thresholdChecker, threshold, blobCreator, scanID);
    }


    /**
     *
     * @param pixels
     * @param imageWidth
     * @param startIndex
     * @param threshold
     * @return
     */
    public void scan(int[] pixels, int imageWidth, int imageHeight, int startIndex, ThresholdChecker thresholdChecker, float threshold, ContourCreator blobCreator, int scanID) {

        if (contourExistCheckData == null || contourExistCheckData.length < pixels.length) {
            contourExistCheckData = new int[pixels.length];
        }

        // we already have the contour, or a new image is scanned without a new scanId
        if (contourExistCheckData[startIndex] == scanID) return;

        blobCreator.startContour(startIndex, pixels, imageWidth, imageHeight);

        float valueDown, valueUp, valueLeft, valueRight;

        UP = -imageWidth;
        DOWN = imageWidth;


        int current = startIndex;
        int x = current % imageWidth;
        int y = (current - x) / imageWidth;


        int minX, minY, maxX, maxY;
        minX = minY = Integer.MAX_VALUE;
        maxX = maxY = Integer.MIN_VALUE;
        int minXCornerIndex, maxXCornerIndex, minYCornerIndex, maxYCornerIndex;
        minXCornerIndex = maxXCornerIndex = minYCornerIndex = maxYCornerIndex = -1;

        valueDown = thresholdChecker.check(pixels[current + DOWN]);
        valueRight = thresholdChecker.check(pixels[current + RIGHT]);
        valueUp = thresholdChecker.check(pixels[current + UP]);
        valueLeft = thresholdChecker.check(pixels[current + LEFT]);


        // or we don't add it!
        downIsFree = valueDown >= threshold;
        rightIsFree = valueRight >= threshold;
        upIsFree = valueUp >= threshold;
        leftIsFree = valueLeft >= threshold;

        // todo: 1 line starting point blob
        // if we would check all surrounding pixels
        // from the start point then we could
        // figure out if the start is like a single line
        // if that is true then we have to scan in the other direction as well
        // maybe set a boolean that the first cornerPixel is not a cornerPixel
        float valueDownLeft = thresholdChecker.check(pixels[current + DOWN + LEFT]);
        float valueDownRight = thresholdChecker.check(pixels[current + DOWN + RIGHT]);
        float valueUpLeft = thresholdChecker.check(pixels[current + UP + LEFT]);
        float valueUpRight = thresholdChecker.check(pixels[current + UP + RIGHT]);

        // todo: onLine check, this should work, 1st = horizontal line 2nd = vertical line
        // onLine = upIsFree && downIsFree ? true : leftIsFree && rightIsFree ? true : false;

        // false on isolated pixel
        if (!setStartDirection()) {
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

        if (c >= 2) {  // it's a corner // todo, could also be a single line
            blobCreator.addCorner(current, x, y);
            if (x < minX) { minX = x; minXCornerIndex = current;}
            if (x > maxX) { maxX = x; maxXCornerIndex = current;}
            if (y < minY) { minY = y; minYCornerIndex = current;}
            if (y > maxY) { maxY = y; maxYCornerIndex = current;}
        }

        float valueMoveDirection, valueCurrentDirection;

        while (true) {

            valueMoveDirection = thresholdChecker.check(pixels[current + moveDirection]);

            if (valueMoveDirection >= threshold) {

                blobCreator.addEdge(current, x, y);
                contourExistCheckData[current] = scanID;

                current += moveDirection;
                x = current % imageWidth;
                y = (current - x) / imageWidth;

                if (current == startIndex) break;

                valueCurrentDirection = thresholdChecker.check(pixels[current + checkDirection]);

                if (valueCurrentDirection >= threshold) {

                    blobCreator.addCorner(current, x, y);
                    if (x < minX) { minX = x; minXCornerIndex = current;}
                    if (x > maxX) { maxX = x; maxXCornerIndex = current;}
                    if (y < minY) { minY = y; minYCornerIndex = current;}
                    if (y > maxY) { maxY = y; maxYCornerIndex = current;}

                    current += checkDirection;
                    x = current % imageWidth;
                    y = (current - x) / imageWidth;

                    if (current == startIndex) break;

                    setDirection(checkDirection);
                }


            } else {
                // we hit a wall
                // so go right since we want to go clock wise
                blobCreator.addCorner(current, x, y);
                if (x < minX) { minX = x; minXCornerIndex = current;}
                if (x > maxX) { maxX = x; maxXCornerIndex = current;}
                if (y < minY) { minY = y; minYCornerIndex = current;}
                if (y > maxY) { maxY = y; maxYCornerIndex = current;}
                turnRight();

            }

        }

        blobCreator.setMinAndMaxCornerValues(minXCornerIndex, minX, minYCornerIndex, minY, maxXCornerIndex, maxX, maxYCornerIndex, maxY);

        // check if it's an outerContour or not
        // we do this by checking pixels outside the contour
        // from the most left corner

        valueDown = thresholdChecker.check(pixels[minXCornerIndex+DOWN]);
        valueUp = thresholdChecker.check(pixels[minXCornerIndex+UP]);
        valueLeft = thresholdChecker.check(pixels[minXCornerIndex+LEFT]);
        // we don't have to check for right

        boolean downIsFree = valueDown < threshold;
        boolean upIsFree =  valueUp < threshold;
        boolean leftIsFree = valueLeft < threshold;

        boolean isOuterContour = upIsFree || leftIsFree || downIsFree;

        blobCreator.isOuterContour(isOuterContour);

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
