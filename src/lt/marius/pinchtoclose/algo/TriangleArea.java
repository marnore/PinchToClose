package lt.marius.pinchtoclose.algo;

import android.graphics.Canvas;
import android.graphics.Paint;

class TriangleArea implements AreaAlgorithm {
	
	private float[] x, y;
	
	@Override
	public float area(float x[], float y[]) {
		if (x.length <= 2 || y.length <= 2) {
			return 0;
		}
		this.x = x;
		this.y = y;
		float a = dist(x[0], y[0], x[1], y[1]);
		float b = dist(x[0], y[0], x[2], y[2]);
		float c = dist(x[1], y[1], x[2], y[2]);
		float p = (a + b + c) / 2;
		float area = (float) Math.sqrt(p * (p - a) * (p - b) * (p - c));
		return area;
	}

	private float dist(float x1, float y1, float x2, float y2) {
		return (float)Math.hypot((x2 - x1), (y2 - y1));
	}

	@Override
	public void visualize(Canvas canvas, Paint paint) {
		if (x != null && x.length >= 3) {
			canvas.drawLine(x[0], y[0], x[1], y[1], paint);
			canvas.drawLine(x[1], y[1], x[2], y[2], paint);
			canvas.drawLine(x[2], y[2], x[0], y[0], paint);
		}
	}

}
