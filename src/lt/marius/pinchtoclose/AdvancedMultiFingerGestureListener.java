package lt.marius.pinchtoclose;

public abstract class AdvancedMultiFingerGestureListener implements MultiFingerGestureListener {
	
	private float currArea = -1;
	private float currRotation = 0;
	private boolean rotationSet = false;
	
	@Override
	public void onDeltaMove(float[] dx, float[] dy) {
		
	}
	
	@Override
	public void onMove(float[] startX, float[] startY, float[] endX,
			float[] endY) {
		if (startX.length >= 3) {
			float a = triangleAreaSize(endX, endY);
			if (currArea != -1) {
				onAreaChange(a - currArea);
			}
			currArea = a;
			onArea(currArea);
		}
		
	}
	
	@Override
	public void onDown(int fingerCount) {
		if (fingerCount < 3) {
			currArea = -1;
		}
	}
	
	@Override
	public void onUp(int fingerCount) {
		if (fingerCount < 3) {
			currArea = -1;
		}
	}
	
	private float dist(float x1, float y1, float x2, float y2) {
		return (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	private float triangleAreaSize(float posX[], float posY[]) {
		float a = dist(posX[0], posY[0], posX[1], posY[1]);
		float b = dist(posX[0], posY[0], posX[2], posY[2]);
		float c = dist(posX[1], posY[1], posX[2], posY[2]);
		float p = (a + b + c) / 2;
		return (float) Math.sqrt(p * (p - a) * (p - b) * (p - c));
	}
	
	//super helper functions for building cool stuff on top
	public abstract void onRotateDelta(float delatAngle);
	public abstract void onRotate(float angle);
	
	public abstract void onAreaChange(float deltaArea);
	public abstract void onArea(float newArea);
	
	public abstract void onScale(float scaleFactor);
}
