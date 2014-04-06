package lt.marius.pinchtoclose.algo;

import static java.lang.Math.asin;
import static java.lang.Math.hypot;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Class for calculating an area of any shape. Firstly the shape
 * is triangulated using Delaunay triangulation, then areas of
 * each triangle are summed
 * @author Marius Noreikis
 * Created: Apr 6, 2014
 */
class DelaunayArea implements AreaAlgorithm {

	private Tri omega;
	private List<Tri> triang = new ArrayList<Tri>();
	
	
	@Override
	public void visualize(Canvas canvas, Paint p) {
		canvas.save();
		for (Tri t : triang) {
			drawLine(t.p1, t.p2, canvas, p);
			drawLine(t.p2, t.p3, canvas, p);
			drawLine(t.p3, t.p1, canvas, p);
			if (t.p1 != omega.p1 && t.p1 != omega.p2 && t.p1 != omega.p3 &&
					t.p2 != omega.p1 && t.p2 != omega.p2 && t.p2 != omega.p3 &&
					t.p3 != omega.p1 && t.p3 != omega.p2 && t.p3 != omega.p3 
					){
				int col = p.getColor();
				p.setColor(col & 0x70ffffff);
				Path path = new Path();
				path.moveTo(t.p1.x, t.p1.y);
				path.lineTo(t.p2.x, t.p2.y);
				path.lineTo(t.p3.x, t.p3.y);
				path.close();
				canvas.drawPath(path, p);
				p.setColor(col);
			}
		}
		canvas.restore();
	}
	
	private void drawLine(Point p1, Point p2, Canvas canvas, Paint paint) {
//		canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
		float len = (float) hypot(p2.x - p1.x, p2.y - p1.y);
		float rotation = (float) toDegrees(asin( (p2.y - p1.y) / (len) ));
		if (p2.x < p1.x) {
			rotation = 180 - rotation;
		}
		canvas.save();
		canvas.translate(p1.x, p1.y);
		canvas.rotate(rotation);
		canvas.drawLine(0, 0, len, 0, paint);
		
		canvas.restore();
	}

	/**
	 * Lightweight 2D Point implementation
	 * @author Marius Noreikis
	 * Created: Apr 6, 2014
	 */
	static class Point {
		float x, y;

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}
		@Override
		public String toString() {
			return String.format("(%.4f %.4f)", x, y);
		}
	}
	
	/**
	 * Lightweight 2D vector implementation for certain calculations
	 * @author Marius Noreikis
	 * Created: Apr 6, 2014
	 */
	static class Vector extends Point {

		public Vector(float x, float y) {
			super(x, y);
		}
		
		public float dot(Vector v2) {
			return x * v2.x + y * v2.y;
		}

		public float cross(Vector v) {
			return x * v.y - v.x * y;
		}
	}
	
	/**
	 * Lightweight triangle implementation
	 * @author Marius Noreikis
	 * Created: Apr 6, 2014
	 */
	static class Tri {
		Point p1, p2, p3;
		
		public Tri(){}
		public Tri(Point p1, Point p2, Point p3) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		}
		//special thanks to http://www.blackpawn.com/texts/pointinpoly/
		public boolean contains(Point p) {
			// Compute vectors        
			Vector v0 = new Vector(p3.x - p1.x, p3.y - p1.y);
			Vector v1 = new Vector(p2.x - p1.x, p2.y - p1.y);
			Vector v2 = new Vector(p.x - p1.x, p.y - p1.y);

			// Compute dot products
			float dot00 = v0.dot(v0);
			float dot01 = v0.dot(v1);
			float dot02 = v0.dot(v2);
			float dot11 = v1.dot(v1);
			float dot12 = v1.dot(v2);

			// Compute barycentric coordinates
			float invDenom = 1f / (dot00 * dot11 - dot01 * dot01);
			float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
			float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

			// Check if point is in triangle
			return (u >= 0) && (v >= 0) && (u + v < 1);
		}
		
		@Override
		public String toString() {
			return p1 + " " + p2 + " " + p3;
		}
		public void ensureCCW() {
			Vector v12 = new Vector(p3.x - p1.x, p3.y - p1.y);
			Vector v23 = new Vector(p2.x - p1.x, p2.y - p1.y);
			if (v12.cross(v23) > 0) {
				Point p = p2;	//make ccw
				p2 = p3;
				p3 = p;
			}
		}
		
		public float area() {
			float a2 = (p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y);
			float b2 = (p3.x - p2.x) * (p3.x - p2.x) + (p3.y - p2.y) * (p3.y - p2.y);
			float c2 = (p3.x - p1.x) * (p3.x - p1.x) + (p3.y - p1.y) * (p3.y - p1.y);
			return (float) (0.25 * sqrt( 4 * a2 * b2 - ((a2 + b2 - c2) * (a2 + b2 - c2)) ));
		}
	}

	
	
	@Override
	public float area(float[] x, float[] y) {
		int n = x.length;
		if (n < 3) return 0;
		triang = new ArrayList<Tri>();
				
		Tri omegaFace = genOmega(3000, 3000);	//should get real device screen size instead
		this.omega = omegaFace;
		triang.add(omegaFace);
		for (int i = 0; i < n; i++) {	//take all points and triangulate into omega
			Point p = new Point(x[i], y[i]);
			//find which triangle the point is in
			Tri inside;
			int ind;
			if (triang.size() == 1) {
				ind = 0;
			} else {
				ind = findTri(triang, p);
			}
			inside = triang.get(ind);
			//split the triangle
			triang.remove(ind);
			//ccw
			Tri t1 = new Tri(p, inside.p1, inside.p2);
			Tri t2 = new Tri(p, inside.p2, inside.p3);
			Tri t3 = new Tri(p, inside.p3, inside.p1);
			t1.ensureCCW();
			t2.ensureCCW();
			t3.ensureCCW();
			triang.add(t1);
			//check for edge flip
			checkEdgeFlip(triang, t1);
			triang.add(t2);
			checkEdgeFlip(triang, t2);
			triang.add(t3);
			checkEdgeFlip(triang, t3);
		}
		float area = 0;
		for (Tri t : triang) {
			if (t.p1 != omegaFace.p1 && t.p1 != omegaFace.p2 && t.p1 != omegaFace.p3 &&
				t.p2 != omegaFace.p1 && t.p2 != omegaFace.p2 && t.p2 != omegaFace.p3 &&
				t.p3 != omegaFace.p1 && t.p3 != omegaFace.p2 && t.p3 != omegaFace.p3 
				){
				area += t.area();
			}
		}
		
		return area;
	}
	
	private void checkEdgeFlip(List<Tri> triangs, Tri added) {
		int ind = findNeighbor(triangs, added);
		if (ind == -1) return; //nothing to change
		Tri neigh = triangs.get(ind);
		Point pt;
		if (!neigh.p1.equals(added.p2) && !neigh.p1.equals(added.p3)) pt = neigh.p1;
		else if (!neigh.p2.equals(added.p2) && !neigh.p2.equals(added.p3)) pt = neigh.p2;
		else pt = neigh.p3;
		float d = determinant(added.p1, added.p2, added.p3, pt);
		if (d > 0){ //pt is inside need to flip
			Tri t1 = new Tri(added.p1, added.p2, pt);	//ccw?
			Tri t2 = new Tri(pt, added.p3, added.p1);	//ccw?
			t1.ensureCCW();
			t2.ensureCCW();
			triangs.remove(ind);
			triangs.remove(added);
			triangs.add(t1);
			//checkEdgeFlip(triangs, t1);	//recursive check to see if new triangs did not introduce
			triangs.add(t2);			//invalid faces
			//checkEdgeFlip(triangs, t2);
		}
	}

	//from http://en.wikipedia.org/wiki/Delaunay_triangulation
	private float determinant(Point a, Point b, Point c, Point d) {
		return (a.x - d.x) * (b.y - d.y) * (c.x*c.x - d.x*d.x + c.y*c.y - d.y*d.y) +
			   (a.y - d.y) * (b.x*b.x - d.x*d.x + b.y*b.y - d.y*d.y) * (c.x - d.x) +
			   (b.x - d.x) * (c.y - d.y) * (a.x*a.x - d.x*d.x + a.y*a.y - d.y*d.y) -
			   
			   (a.x*a.x - d.x*d.x + a.y*a.y - d.y*d.y) * (b.y - d.y) * (c.x - d.x) -
			   (a.y - d.y) * (b.x - d.x) * (c.x*c.x - d.x*d.x + c.y*c.y - d.y*d.y) -
			   (a.x - d.x) * (b.x*b.x - d.x*d.x + b.y*b.y - d.y*d.y) * (c.y - d.y);
	}

	private int findNeighbor(List<Tri> triangs, Tri added) {
		for (int i = 0; i < triangs.size(); i++) {
			Tri t = triangs.get(i);
			if (t.equals(added)) {
				continue;	//self
			}
			if ((added.p2.equals(t.p1) || added.p2.equals(t.p2) || added.p2.equals(t.p3)) &&
				(added.p3.equals(t.p1) || added.p3.equals(t.p2) || added.p3.equals(t.p3))) {
				return i;
			}
		}
		return -1;
	}

	private int findTri(List<Tri> triang, Point p) {
		int ind = 0;
		for (Tri t : triang) {
			if (t.contains(p)) {
				return ind;
			}
			ind++;
		}
		return 0;
	}

	private Tri genOmega(float w, float h) {
		System.out.println(w + " " + h);
		float size = max(w, h);
		return new Tri(	//ccw
				new Point(w / 2f - 1.5f * size, h / 2f + size / 2f),
				new Point(w / 2f, h / 2f - size),
				new Point(w / 2f + 1.5f * size, h / 2f + size / 2f)
				);
	}

}
