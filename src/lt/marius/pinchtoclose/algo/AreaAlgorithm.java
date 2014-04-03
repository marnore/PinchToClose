package lt.marius.pinchtoclose.algo;

public interface AreaAlgorithm {

	public static final AreaAlgorithm TRIANGLE = new TriangleArea();
	public static final AreaAlgorithm DELAUNAY = new DelaunayArea();
	
	/**
	 * Computes the 2D area of specified points
	 * @param x array of the x coords of the points
	 * @param y array of the y coords of the points
	 * @return the size of the area
	 */
	public float area(float[] x, float []y);
	
}
