package lt.marius.pinchtoclose;

import lt.marius.pinchtoclose.algo.AreaAlgorithm;

public abstract class MultiFingerAreaListener implements MultiFingerGestureListener {
	
	private float currArea = -1;
	private int fingerCount = -1;
	
	private AreaAlgorithm algorithm;
	
	public MultiFingerAreaListener() {
		this(AreaAlgorithm.TRIANGLE);
	}
	
	public MultiFingerAreaListener(AreaAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	

	public void setAreaAlgorithm(AreaAlgorithm algorithm) {
		if (algorithm != null) {
			this.algorithm = algorithm;
		}
	}
	
	@Override
	public void onDeltaMove(float[] dx, float[] dy) {
		
	}
	
	@Override
	public void onMove(float[] startX, float[] startY, float[] endX,
			float[] endY) {
		if (startX.length >= 3) {
			
			
			float a = algorithm.area(endX, endY);
			if (currArea != -1) {
				onAreaChange(a - currArea);
			}
			currArea = a;
			onArea(currArea);
		}
		
	}
	
	@Override
	public void onDown(int fingerCount) {
//		if (fingerCount < 3 || fingerCount != this.fingerCount) {
			currArea = -1;
//			this.fingerCount = fingerCount;
//		}
	}
	
	@Override
	public void onUp(int fingerCount) {
		currArea = -1;
	}
	
	
	
	
	public abstract void onAreaChange(float deltaArea);
	public abstract void onArea(float newArea);

}
