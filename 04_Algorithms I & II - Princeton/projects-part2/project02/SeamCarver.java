package week2;
import edu.princeton.cs.algs4.Picture;

/**
 * A vertical seam in an image is a path of pixels connected 
 * from the top to the bottom with one pixel in each row; 
 * a horizontal seam is a path of pixels connected from the 
 * left to the right with one pixel in each column.
 * @author anderspedersen
 */

public class SeamCarver {
    private Picture currentPic;
    private int width;
    private int height;

    /**
     * create a seam carver object based on the given picture
     * Do not mutate the Picture argument to the constructor.
     * @param picture
     */
    public SeamCarver(Picture picture) {
	if (picture == null) throw new IllegalArgumentException("picture is null.");
	currentPic = new Picture(picture);
	width = currentPic.width();
	height = currentPic.height();
    }

    /**
     * current picture
     * @return
     */
    public Picture picture() {
	return currentPic;
    }

    /**
     * width of current picture
     * O(1)
     * @return
     */
    public int width() {
	width = currentPic.width();
	return width;
    }

    /**
     * height of current picture
     * O(1)
     * @return
     */
    public int height() {
	height = currentPic.height();
	return height;
    }

    /**
     * energy of pixel at column x and row y
     * O(1)
     * @param x
     * @param y
     * @return
     */
    public double energy(int x, int y) {
	if (x < 0 || x > width() - 1 || y < 0 || y > height() - 1) throw new IllegalArgumentException("x or y out of range");
	//if edge, then 1000
	if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
	    return 1000;
	} else {
	    return Math.sqrt(xGradientSquared(x, y) + yGradientSquared(x, y));
	}
    }

    /** 
     * Get squared value of right-left elements (x axis)
     * @param col
     * @param row
     * @return
     */
    private double xGradientSquared(int x, int y) {
	double Rx = Math.pow(Math.abs(currentPic.get(x+1, y).getRed() - currentPic.get(x-1, y).getRed()),2);
	double Gx = Math.pow(Math.abs(currentPic.get(x+1, y).getGreen() - currentPic.get(x-1, y).getGreen()),2);
	double Bx = Math.pow(Math.abs(currentPic.get(x+1, y).getBlue() - currentPic.get(x-1, y).getBlue()),2);
	return (Rx + Gx + Bx);
    }

    /**
     * get squared value of bottom-top elements (y axis)
     * @param col
     * @param row
     * @return
     */
    private double yGradientSquared(int x, int y) {
	double Ry = Math.pow(Math.abs(currentPic.get(x, y+1).getRed() - currentPic.get(x, y-1).getRed()),2);
	double Gy = Math.pow(Math.abs(currentPic.get(x, y+1).getGreen() - currentPic.get(x, y-1).getGreen()),2);
	double By = Math.pow(Math.abs(currentPic.get(x, y+1).getBlue() - currentPic.get(x, y-1).getBlue()),2);
	return (Ry + Gy + By);
    }

    /**
     * sequence of indices for horizontal seam
     * transpose image and use vertical to find seam
     * O(w*h)
     * @return
     */
    public int[] findHorizontalSeam() {
	Picture originalPicture = currentPic;
	transposePicture();
	int[] seam = findVerticalSeam();
	currentPic = originalPicture;
	return seam;
    }

    /**
     * sequence of indices for vertical seam
     * The reason we don't have to worry about sorting the picture 
     * is that the natural ordering of the picture is such that 
     * the topological sort is to just start from the top row 
     * and work your way down (since when finding a vertical seam, 
     * all rows only point to pixels beneath them).
     * O(w*h)
     * @return
     */ 
    public int[] findVerticalSeam() { 
	double[] distTo = new double[width()]; //accumulates all minimums for each row, i.e. 10 minimums if 10 positions
	double[] min = new double[width()]; //min for each element in row of the 2 or 3 possibilities 
	int[][] edgeTo = new int[height()][width()]; //edge from last vertex to the min of the 2 or 3 elements

	//initialize top row to weights
	for (int j = 0; j < width(); ++j)
	    distTo[j] = (int) energy(j, 0);

	//starting at second row from top 
	for (int i = 1; i < height(); ++i) {
	    for (int j = 0; j < width(); ++j) {
		min[j] = Integer.MAX_VALUE;

		//for each entry in row, check the distance to pixels
		//down-left, down, and down-right. Of those, save the min
		//of the 3 possibilities to min[] end edgeTo[].
		for (int col = j-1; col <= j+1; ++col) {
		    if (col < 0 || col > width() - 1) 
			continue;

		    double distance = distTo[col] + energy(j, i);

		    if (distance < min[j]) {
			min[j] = distance;
			edgeTo[i][j] = col;
		    }
		}

	    }
	    //update accumulated distance with min of the element 
	    System.arraycopy(min, 0, distTo, 0, width()); 
	}

	//find index of the min position, i.e. the
	//minimum accumulated in the bottom row
	int index = findMinInArr(distTo);
	int[] seams = new int[height()];

	//starting at last position, go through each row
	//and select the minimum, to find x vertices
	for (int i = height() - 1; i >= 0; --i) {
	    seams[i] = index; 
	    index = edgeTo[i][index]; //set index to the vertex pointing to min
	}

	return seams;

    }

    /**
     * helper to find min in distTo (accumulated mins for each row)
     * @param arr
     * @return
     */
    private int findMinInArr(double[] arr) {
	int index = -1;
	double min = Double.MAX_VALUE;

	for (int i = 0; i < arr.length; ++i) {
	    if (arr[i] < min) {
		index = i;
		min = arr[i];
	    }
	}
	return index;
    }

    /**
     * remove horizontal seam from current picture
     * transpose image, remove seam with vertical and transpose back
     * O(w*h)
     * @param seam
     */
    public void removeHorizontalSeam(int[] seam) {
	checkValuesOfSeam(seam, true);
	transposePicture();
	removeVerticalSeam(seam);
	transposePicture();
    }

    /**
     * remove vertical seam from current picture
     * O(w*h)
     * @param seam
     */
    public void removeVerticalSeam (int[] seam) {	
	checkValuesOfSeam(seam, false);
	Picture p = new Picture(width() - 1, height());
        for (int y = 0; y < height(); y++) {
            int k = 0;
            for (int x = 0; x < width(); x++) {
                if (x != seam[y]) {
                    p.set(k, y, currentPic.get(x, y));
                    k++;
                }
            }
        }
        currentPic = p;
    }

    private void checkValuesOfSeam(int[] seam, boolean horizontal) {
	if (horizontal) {
	    if (seam == null || seam.length != width() || width() <= 1) throw new IllegalArgumentException();
	    for (int i = 1; i < seam.length; i++) {
		if (i < 0 || i > width() || Math.abs(seam[i]-seam[i-1]) > 1) 
		    throw new IllegalArgumentException();
	    }
	} else {
	    if (seam == null || seam.length != height() || height() <= 1) throw new IllegalArgumentException();
	    for (int i = 1; i < seam.length; i++) {
		if (i < 0 || i > height() || Math.abs(seam[i]-seam[i-1]) > 1 ) 
		    throw new IllegalArgumentException(); 
	    }
	}	
    }

    /**
     * transposes picture
     */
    private void transposePicture() {
	Picture transpose = new Picture(height(), width());
	for (int i = 0; i < transpose.width(); i++) {
	    for (int j = 0; j < transpose.height(); j++) {
		transpose.set(i, j, currentPic.get(j, i)); 	    }
	}
	currentPic = transpose;
    }

    /**
     * unit testing (optional)
     * @param args
     */
    public static void main(String[] args) {
    }

}