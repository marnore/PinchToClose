package lt.marius.pinchtoclose;

import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;

public class MultiFingerGestureDetector {
	
	private static float POINTER_ERROR_THRESHOLD = 10.f;	//10 pixels here and there
	private int fingerCount;
	private float[] posX, posY;
	private float[] startX, startY;
	private float[] dx, dy;
	private boolean moving;
	private int[] mappings;
	private float lastArea = -1;
	
	private MultiFingerGestureListener listener;
	
	/**
	 * 
	 * @param fingerCount maximum number of fingers to register
	 */
	public MultiFingerGestureDetector(int fingerCount, MultiFingerGestureListener l) {
		this.listener = l;
		this.fingerCount = fingerCount;
		posX = new float[fingerCount];
		posY = new float[fingerCount];
		dx = new float[fingerCount];
		dy = new float[fingerCount];
		startX = new float[fingerCount];
		startY = new float[fingerCount];
		moving = false;
	}
	
	private int pointerCount;
	
	public boolean onTouchEvent(MotionEvent event) {
		pointerCount = Math.min(event.getPointerCount(), fingerCount);
//		if (pointerCount == fingerCount) {
			
			int index = event.getActionIndex();
			//for secondary pointer events
//			switch (event.getActionMasked()) {
//			case MotionEvent.ACTION_POINTER_DOWN:
//				break;
//			case MotionEvent.ACTION_POINTER_UP:
//				break;
//			default:
//				break;
//			}
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
					listener.onDown(pointerCount);
				break;
			case MotionEvent.ACTION_UP:
					listener.onUp(pointerCount);
				break;
			case MotionEvent.ACTION_CANCEL:
				moving = false;
				break;
			case MotionEvent.ACTION_MOVE:
				posX = new float[fingerCount];
				posY = new float[fingerCount];
				gatherXY(event, posX, posY, pointerCount);
				dx = substract(posX, startX, pointerCount);
				dy = substract(posY, startY, pointerCount);
				listener.onDeltaMove(dx, dy);
				listener.onMove(copy(startX, pointerCount), copy(startY, pointerCount),
						copy(posX, pointerCount), copy(posY, pointerCount));
				copyValues(posX, startX);
				copyValues(posY, startY);
				
				break;
			}
//		}

		return true;
	}
	
	
	
	private float[] copy(float[] array) {
		return copy(array, array.length);
	}
	
	private float[] copy(float[] array, int length) {
		float[] dst = new float[length];
		System.arraycopy(array, 0, dst, 0, length);
		return dst;
	}
	
	private void copyValues(float[] src, float[] dst) {
		int n = Math.min(src.length, dst.length);
		for (int i = 0; i < n; i++) {
			dst[i] = src[i];
		}
	}
	
	private float[] substract(float[] a, float[] b) {
		return substract(a, b, a.length);
	}
	
	private float[] substract(float a[], float b[], int length) {
		float[] c = new float[length];
		for (int i = 0; i < length; i++) {
			c[i] = a[i] - b[i];
		}
		return c;
	}
	
	private void gatherXY(MotionEvent event, float x[], float y[], int pointerCount) {
		for (int pointerIndex = 0; pointerIndex < pointerCount; pointerIndex++ ) {
			int pointerId = event.getPointerId(pointerIndex);
			if (pointerId >= 0) {
				x[pointerIndex] = event.getX(pointerIndex);
				y[pointerIndex] = event.getY(pointerIndex);
			}
		}
	}
	
	private void gatherXY_old(MotionEvent event, float x[], float y[], int pointerCount) {
		String log = "";
		for (int pointerIndex = 0; pointerIndex < pointerCount; pointerIndex++ ) {

			int pointerId = event.getPointerId(pointerIndex);
			log += pointerId;
			if (pointerId >= 0) {
//				Log.d("temp", "Pointer id is " + pointerId + " index is " + pointerIndex + " x len " + x.length + " " + y.length);
				x[pointerId] = event.getX(pointerIndex);
				y[pointerId] = event.getY(pointerIndex);
			}
		}
		Log.d("temp", log);
	}
	
}
