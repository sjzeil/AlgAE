/**
 * 
 */
package edu.odu.cs.AlgAE.Client.Layout.Optimization;

import java.util.ArrayList;

import junit.framework.*;
import junit.textui.TestRunner;

/**
 * @author zeil
 *
 */
public class TestOptimizer extends TestCase {

    public TestOptimizer(String testName) {
        super(testName);
    }

    public static void main( String[] args ) throws Exception {
        TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite(TestOptimizer.class);
    }
    
    public static class Parabolic implements OptimizationProblem {
    	private ArrayList<Variable> v;
    	
    	public Parabolic (int dimension, double startValue) {
    		v = new ArrayList<Variable>();
    		for (int i = 0; i < dimension; ++i) {
    			v.add (new Variable(startValue, -10.0, 10.0, 1.0));
    		}
    	}

		@Override
		public ArrayList<Variable> getVariables() {
			return v;
		}

		@Override
		public double objectiveFunction() {
			double sum = 0.0;
			for (int i = 0; i < v.size(); ++i) {
				double x = v.get(i).getValue();
				sum += (x - 2.5)*(x-2.5);
			}
			return sum+1.0;
		}
    	
    }

    
    public static class OptProblem1 implements OptimizationProblem {
    	private ArrayList<Variable> v;
    	
    	public OptProblem1 (int dimension, double startValue) {
    		v = new ArrayList<Variable>();
    		for (int i = 0; i < dimension; ++i) {
    			v.add (new Variable(startValue, 0.0, 100*Math.PI, 1.0));
    		}
    	}

		@Override
		public ArrayList<Variable> getVariables() {
			return v;
		}

		@Override
		public double objectiveFunction() {
			double sum = 0.0;
			for (int i = 0; i < v.size(); ++i) {
				double x = v.get(i).getValue();
				sum += x/(2*Math.PI) + Math.cos(x);
			}
			return sum;
		}
    	
    }
    
    public static double globalOpt = 2.9;
    
    
	public static void testOneStep1() {
		Parabolic problem = new Parabolic (1, 4.0);
		Optimizer opt = new Optimizer (problem);
		opt.solve(2.0, 0.01, 1);
		assertEquals(2.5, problem.getVariables().get(0).getValue(), 0.05);
	}

	public static void testOneStep2() {
		Parabolic problem = new Parabolic (1, 4.0);
		Optimizer opt = new Optimizer (problem);
		opt.solve(0.25, 0.01, 1);
		assertEquals(2.5, problem.getVariables().get(0).getValue(), 0.05);
	}

	public static void testOneStep3() {
		Parabolic problem = new Parabolic (1, 1.0);
		Optimizer opt = new Optimizer (problem);
		opt.solve(0.5, 0.01, 1);
		assertEquals (2.5, problem.getVariables().get(0).getValue(), 0.05);
	}

	public static void test1DLocal() {
		OptProblem1 problem = new OptProblem1 (1, 4.0);
		Optimizer opt = new Optimizer (problem);
		opt.solve(0.5, 0.01, 10000);
		for (int i = 0; i < problem.getVariables().size(); ++i) {
			double x = problem.getVariables().get(i).getValue();
			assertEquals ((int)(10*globalOpt), (int)(10*x));
		}
	}

	public static void test2DLocal() {
		OptProblem1 problem = new OptProblem1 (2, 4.0);
		Optimizer opt = new Optimizer (problem);
		opt.solve(0.5, 0.01, 10000);
		for (int i = 0; i < problem.getVariables().size(); ++i) {
			double x = problem.getVariables().get(i).getValue();
			assertEquals ((int)(10*globalOpt), (int)(10*x));
		}
	}

}
