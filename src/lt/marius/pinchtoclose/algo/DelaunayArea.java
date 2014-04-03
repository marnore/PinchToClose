package lt.marius.pinchtoclose.algo;

import java.util.Arrays;

class DelaunayArea implements AreaAlgorithm {

	//O(n^4) complexity at worst case but since n <= 10 should be OK
	private float getAreaSum(int n, float[] x, float[] y, float[] z)
	{
		float result = 0;
		System.out.println("== == ==");
		System.out.println(Arrays.toString(x) + " " + Arrays.toString(y));
		for (int i = 0; i < n - 2; i++) {
			for (int j = i + 1; j < n; j++) {
				for (int k = i + 1; k < n; k++)	
				{
					if (j == k) {
						continue;
					}
					double xn = (y[j] - y[i]) * (z[k] - z[i]) - (y[k] - y[i]) * (z[j] - z[i]);

					double yn = (x[k] - x[i]) * (z[j] - z[i]) - (x[j] - x[i]) * (z[k] - z[i]);

					double zn = (x[j] - x[i]) * (y[k] - y[i]) - (x[k] - x[i]) * (y[j] - y[i]);
					boolean flag;
					if (flag = (zn < 0 ? 1 : 0) != 0) {
						for (int m = 0; m < n; m++) {
							flag = (flag) && ((x[m] - x[i]) * xn + (y[m] - y[i]) * yn + (z[m] - z[i]) * zn <= 0);
						}

					}

					if (!flag)
					{
						continue;
					}

					result += area(x[i], y[i], x[j], y[j], x[k], y[k]);
				}

			}

		}
		if (result == 0) {
		}
		System.out.println(result);
		return result;
	}

	private float area(float x1, float y1, float x2, float y2, float x3, float y3) {
		float a = dist(x1, y1, x2, y2);
		float b = dist(x1, y1, x3, y3);
		float c = dist(x2, y2, x3, y3);
		float p = (a + b + c) / 2;
		float area = (float) Math.sqrt(p * (p - a) * (p - b) * (p - c));
		System.out.println("area? " + area);
		return area;
	}

	private float dist(float x1, float y1, float x2, float y2) {
		return (float) Math.hypot((x2 - x1), (y2 - y1));
	}

	@Override
	public float area(float[] x, float[] y) {
		if (x.length <= 2 || y.length <= 2) {
			System.out.println("heh");
			return 0;
		}
		float z[] = new float[x.length];
		for (int i = 0; i < x.length; i++) {
			x[i] /= 1e4;
			y[i] /= 1e4;
			z[i] = (x[i] * x[i] + y[i] * y[i]);
		}
		float area = getAreaSum(x.length, x, y, z);
		return area;
	}

}
