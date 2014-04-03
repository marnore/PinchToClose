package lt.marius.pinchtoclose.algo;

class TriangleArea implements AreaAlgorithm {

	@Override
	public float area(float x[], float y[]) {
		if (x.length <= 2 || y.length <= 2) {
			return 0;
		}
		
		float a = dist(x[0], y[0], x[1], y[1]);
		float b = dist(x[0], y[0], x[2], y[2]);
		float c = dist(x[1], y[1], x[2], y[2]);
		float p = (a + b + c) / 2;
		float area = (float) Math.sqrt(p * (p - a) * (p - b) * (p - c));
		System.out.println(area);
		return area;
	}

	private float dist(float x1, float y1, float x2, float y2) {
		return (float)Math.hypot((x2 - x1), (y2 - y1));
//		return (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

}
