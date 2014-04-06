package lt.marius.pinchtoclose.algo;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Interface to draw an object to Canvas
 * @author Marius Noreikis
 * Created: Apr 6, 2014
 */
public interface Visualizable {
	/**
	 * object should draw itself to the specified canvas with the specified paint
	 * It is up to object whether to use this paint object or not
	 * If an object cannot draw itself this method does nothing
	 * @param canvas canvas where to paint itself
	 * @param paint suggested paint for draw operations
	 */
	public void visualize(Canvas canvas, Paint paint);
}
