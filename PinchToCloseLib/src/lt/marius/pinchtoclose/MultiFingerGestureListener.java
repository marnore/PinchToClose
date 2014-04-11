package lt.marius.pinchtoclose;
/**
 * 
 * @author Marius Noreikis
 * Created: Mar 28, 2014
 */
public interface MultiFingerGestureListener {
	/**
	 * reports relative movement of the pointers
	 * @param dx array of X coordinates, dx[0] represents X coordinate
	 *  of the first pointer, dx[1] of the second and so on.
	 * @param dy array of Y coordinates, dy[0] represents Y coordinate
	 *  of the first pointer, dy[1] of the second and so on.
	 */
	public void onDeltaMove(float[] dx, float[] dy);
	/**
	 * reports movement of the pointers
	 * @param startX array of X coordinates representing a point where
	 * the movement started; startX[0] represents X coordinate
	 *  of the first pointer, startX[1] of the second and so on.
	 * @param startY same as startX just for Y coordinate
	 * @param endX array of X coordinates representing the point where
	 * the pointer currently is; endX[0] represents X coordinate
	 *  of the first pointer, endX[1] of the second and so on.
	 * @param endY same as endX just for Y coordinate
	 */
	public void onMove(float[] startX, float[] startY, float[] endX, float endY[]);
	/**
	 * 
	 * @param fingerCount pointers on the screen after down event
	 */
	public void onDown(int fingerCount);
	/**
	 * Current number of fingers touching the screen
	 * @param fingerCount pointers on the screen after the pointer was removed
	 */
	public void onUp(int fingerCount);
}
