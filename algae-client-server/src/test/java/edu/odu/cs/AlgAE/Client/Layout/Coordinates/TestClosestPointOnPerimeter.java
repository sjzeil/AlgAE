/**
 * 
 */
package edu.odu.cs.AlgAE.Client.Layout.Coordinates;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import edu.odu.cs.AlgAE.Client.Layout.Optimization.Variable;

/**
 * @author zeil
 *
 */
public class TestClosestPointOnPerimeter extends TestCase {

    public TestClosestPointOnPerimeter(String testName) {
        super(testName);
    }

    public static void main( String[] args ) throws Exception {
        TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite(TestClosestPointOnPerimeter.class);
    }
    
    
    private static class BoundedBox implements BoundedRegion {

    	private Rectangle2D.Double bb;
    	
    	public BoundedBox (double x0, double y0, double x1, double y1) {
    		bb = new Rectangle2D.Double (x0, y0, x1-x0, y1 - y0);
    	}
    	
		@Override
		public Rectangle2D getBBox() {
			return bb;
		}

		@Override
		public boolean isFixed(
				IdentityHashMap<FreeOrFixed, Boolean> alreadyChecked) {
			return true;
		}
    	
    }
    
    
    
    
	public static void test_multi() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 2.0, 2.0);
		double mina = 0.0;
		double maxa = 360.0;
		double[] tests = {0.0, 22.5, 45.0, 67.5, 
				90.0, 112.5, 135.0, 157.5, 
				180.0, 202.5, 225.0, 247.5, 
				270.0, 292.5, 315.0, 337.5, 360.0 
				};
		for (int i = 0; i < tests.length; ++i) {
			double angle = tests[i];
			Point2D p0 = new Point2D.Double(1.5, 1.5);
			PerimeterPoint pp1 = new PerimeterPoint(angle, box);
			Point2D p1 = pp1.getCoordinates();
			Point2D p2 = new Point2D.Double(p1.getX() + (p1.getX() - p0.getX()),
					p1.getY() + (p1.getY() - p0.getY()));
			ClosestPointOnPerimeter pp3 = new ClosestPointOnPerimeter(box, new Point(p2.getX(), p2.getY()), mina, maxa);
			Point2D p3 = pp3.getCoordinates();
			
			double angle2 = angle;
			if (angle2 < mina || angle2 > maxa) {
				double d1 = Math.abs(angle2 - mina);
				double d2 = Math.abs(angle2 - maxa);
				angle2 = (d1 < d2) ? mina : maxa;
			}
			PerimeterPoint pp4 = new PerimeterPoint(angle2, box);
			Point2D p4 = pp4.getCoordinates();
			assertEquals ((int)(100.0*p4.getX()), (int)(100*p3.getX()));
			assertEquals ((int)(100.0*p4.getY()), (int)(100*p3.getY()));
		}		
	}

	public static void test_multi_bounded() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 2.0, 2.0);
		double mina = 45.0;
		double maxa = 135.0;
		double[] tests = {0.0, 22.5, 45.0, 67.5, 
				90.0, 112.5, 135.0, 157.5, 
				180.0, 202.5, 225.0, 247.5, 
				270.0, 292.5, 315.0, 337.5, 359 
				};
		for (int i = 0; i < tests.length; ++i) {
			double angle = tests[i];
			Point2D p0 = new Point2D.Double(1.5, 1.5);
			PerimeterPoint pp1 = new PerimeterPoint(angle, box);
			Point2D p1 = pp1.getCoordinates();
			Point2D p2 = new Point2D.Double(p1.getX() + (p1.getX() - p0.getX()),
					p1.getY() + (p1.getY() - p0.getY()));
			ClosestPointOnPerimeter pp3 = new ClosestPointOnPerimeter(box, new Point(p2.getX(), p2.getY()), mina, maxa);
			Point2D p3 = pp3.getCoordinates();
			
			double angle2 = angle;
			if (angle2 < mina || angle2 > maxa) {
				double d1 = Math.abs(angle2 - mina);
				double d2 = Math.abs(angle2 - maxa);
				angle2 = (d1 < d2) ? mina : maxa;
			}
			Math.min(Math.max(angle, mina), maxa);
			PerimeterPoint pp4 = new PerimeterPoint(angle2, box);
			Point2D p4 = pp4.getCoordinates();
			assertEquals ((int)(100.0*p4.getX()), (int)(100*p3.getX()));
			assertEquals ((int)(100.0*p4.getY()), (int)(100*p3.getY()));
		}		
	}
    
    
	public static void test_0() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		ClosestPointOnPerimeter p1 = new ClosestPointOnPerimeter(box, new Point(2.0, 0.0), 0.0, 360.0);
		Point2D pc = p1.getCoordinates();
		assertEquals (20, (int)(10*pc.getX()));
		assertEquals (10, (int)(10*pc.getY()));
		
		assertEquals (p1, new Point(2.0, 1.0));
		assertEquals (new Point(2.0, 1.0), p1);
		
		Variable[] var = p1.getVariables();
		assertEquals (0, var.length);
	}

	public static void test_45() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		ClosestPointOnPerimeter p1 = new ClosestPointOnPerimeter(box, new Point(3.2, 0.9), 0.0, 360.0);
		Point2D pc = p1.getCoordinates();
		assertEquals (30, (int)(10*pc.getX()));
		assertEquals (10, (int)(10*pc.getY()));
		
	}

	public static void test_90() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		ClosestPointOnPerimeter p1 = new ClosestPointOnPerimeter(box, new Point(5.0, 1.5), 0.0, 360.0);
		Point2D pc = p1.getCoordinates();
		assertEquals (30, (int)(10*pc.getX()));
		assertEquals (15, (int)(10*pc.getY()));
		
		assertEquals (p1, new Point(3.0, 1.5));
		assertEquals (new Point(3.0, 1.5), p1);
		
		Variable[] var = p1.getVariables();
		assertEquals (0, var.length);
	}

	public static void test_180() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		ClosestPointOnPerimeter p1 = new ClosestPointOnPerimeter(box, new Point(2.0, 5.0), 0.0, 360.0);
		Point2D pc = p1.getCoordinates();
		assertEquals (20, (int)(10*pc.getX()));
		assertEquals (20, (int)(10*pc.getY()));
		
		assertEquals (p1, new Point(2.0, 2.0));
		assertEquals (new Point(2.0, 2.0), p1);
		
		Variable[] var = p1.getVariables();
		assertEquals (0, var.length);
	}
	
	
	public static void test_45a() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		ClosestPointOnPerimeter p1 = new ClosestPointOnPerimeter(box, new Point(3.2, 1.5), 0.0, 45.0);
		Point2D pc = p1.getCoordinates();
		assertEquals (30, (int)(10*pc.getX()));
		assertEquals (10, (int)(10*pc.getY()));
		
	}

	public static void test_45b() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		ClosestPointOnPerimeter p1 = new ClosestPointOnPerimeter(box, new Point(5.0, 0.0), -45.0, 45.0);
		Point2D pc = p1.getCoordinates();
		assertEquals (30, (int)(10*pc.getX()));
		assertEquals (10, (int)(10*pc.getY()));
	}
	
	public static void test_45c() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		ClosestPointOnPerimeter p1 = new ClosestPointOnPerimeter(box, new Point(-5.0, 0.0), -45.0, 45.0);
		Point2D pc = p1.getCoordinates();
		assertEquals (10, (int)(10*pc.getX()));
		assertEquals (10, (int)(10*pc.getY()));
	}
}
