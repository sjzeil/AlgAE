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
public class TestPerimeterPoint extends TestCase {

    public TestPerimeterPoint(String testName) {
        super(testName);
    }

    public static void main( String[] args ) throws Exception {
        TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite(TestPerimeterPoint.class);
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

    
    public static class A {
    	public double angle;
    	public double x;
    	public double y;
    	
    	A (double a, double xx, double yy) {
    		angle = a;
    		x = xx;
    		y = yy;
    	}
    }
    
	public static void test_multi() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 2.0, 2.0);
		
		A[] tests = {
				new A(0.0, 1.5, 1.0),
				new A(22.5, 1.75, 1.0),
				new A(45.0, 2.0, 1.0), 
				new A(67.5, 2.0, 1.25), 
				new A(90.0, 2.0, 1.5), 
				new A(112.5, 2.0, 1.75), 
				new A(135.0, 2.0, 2.0), 
				new A(157.5, 1.75, 2.0), 
				new A(180.0, 1.5, 2.0), 
				new A(202.5, 1.25, 2.0), 
				new A(225.0, 1.0, 2.0), 
				new A(247.5, 1.0, 1.75), 
				new A(270.0, 1.0, 1.5), 
				new A(292.5, 1.0, 1.25), 
				new A(315.0, 1.0, 1.0), 
				new A(337.5, 1.25, 1.0), 
				new A(360.0, 1.5, 1.0) 
				};
		for (int i = 0; i < tests.length; ++i) {
			A test = tests[i];
			PerimeterPoint p1 = new PerimeterPoint(test.angle, box);
			Point2D pc = p1.getCoordinates();
			assertEquals ((int)(10.0*test.x), (int)(10*pc.getX()));
			assertEquals ((int)(10.0*test.y), (int)(10*pc.getY()));
		}		
	}

    
    
    
	public static void test_0() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		PerimeterPoint p1 = new PerimeterPoint(0.0, box);
		Point2D pc = p1.getCoordinates();
		assertEquals (20, (int)(10*pc.getX()));
		assertEquals (10, (int)(10*pc.getY()));
		
		assertEquals (p1, new Point(2.0, 1.0));
		assertEquals (new Point(2.0, 1.0), p1);
		
		Variable[] var = p1.getVariables();
		assertEquals (1, var.length);
		assertEquals (0.0, var[0].getValue());
	}

	public static void test_45() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		PerimeterPoint p1 = new PerimeterPoint(45.0, box);
		Point2D pc = p1.getCoordinates();
		assertEquals (30, (int)(10*pc.getX()));
		assertEquals (10, (int)(10*pc.getY()));
		
		assertEquals (p1, new Point(3.0, 1.0));
		assertEquals (new Point(3.0, 1.0), p1);
		
		Variable[] var = p1.getVariables();
		assertEquals (1, var.length);
		assertEquals (45.0, var[0].getValue());
	}

	public static void test_90() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		PerimeterPoint p1 = new PerimeterPoint(90.0, box);
		Point2D pc = p1.getCoordinates();
		assertEquals (30, (int)(10*pc.getX()));
		assertEquals (15, (int)(10*pc.getY()));
		
		assertEquals (p1, new Point(3.0, 1.5));
		assertEquals (new Point(3.0, 1.5), p1);
		
		Variable[] var = p1.getVariables();
		assertEquals (1, var.length);
		assertEquals (90.0, var[0].getValue());
	}

	public static void test_180() {
		BoundedBox box = new BoundedBox(1.0, 1.0, 3.0, 2.0);
		PerimeterPoint p1 = new PerimeterPoint(180.0, box);
		Point2D pc = p1.getCoordinates();
		assertEquals (20, (int)(10*pc.getX()));
		assertEquals (20, (int)(10*pc.getY()));
		
		assertEquals (p1, new Point(2.0, 2.0));
		assertEquals (new Point(2.0, 2.0), p1);
		
		Variable[] var = p1.getVariables();
		assertEquals (1, var.length);
		assertEquals (180.0, var[0].getValue());
	}
}
