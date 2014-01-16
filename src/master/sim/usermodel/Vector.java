package master.sim.usermodel;
import org.eclipse.swt.graphics.Point;

public class Vector {

	public double x;
	public double y;

	public Vector(double x2, double y2) {
		x = x2;
		y = y2;
	}

	public Vector(Vector _position) {
		x = _position.x;
		y = _position.y;
	}

	public Vector normalize() {
		double magnitude = java.awt.Point.distance(x, y, 0, 0);
		if (magnitude != 0) {
			x /= magnitude;
			y /= magnitude;
		}
		return this;
	}

	public Vector limit(double max) {
		double magnitude = java.awt.Point.distance(x, y, 0, 0);
		if (magnitude > max) {
			normalize();
			x *= max;
			y *= max;
		}
		return this;
	}

	public Vector add(Vector v) {
		x += v.x;
		y += v.y;
		return this;
	}
	
	public Vector divide(double v){
		x /= v;
		y /= v;
		return this;
	}

	public double heading() {
		return -1 * Math.atan2(y, x);
	}

	public double distance(Vector v) {
		double dx = x - v.x;
		double dy = y - v.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public static Vector subtract(Vector v1, Vector v2) {
		return new Vector(v1.x - v2.x, v1.y - v2.y);
	}

	public Point toPoint() {
		return new Point((int) x, (int) y);
	}

	@Override
	public String toString() {
		return "Vector: x = " + x + ", y = " + y;
	}

	public double magnitude() {
		return x*x + y*y;
	}

	public Vector multiply(double d) {
		x *= d;
		y *= d;
		return this;
	}

}
