/**
 *
 */
package edu.odu.cs.AlgAE.Client.Layout.Optimization;

import java.util.ArrayList;

/**
 * An optimization (minimization) problem.
 *
 * @author zeil
 *
 */
public interface OptimizationProblem {
    
    /**
     * A list of all variables to be manipulated by the optimizer
     * @return list of variables, set to a current/initial state
     */
    public ArrayList<Variable> getVariables();
    
    /**
     * Evaluate the objective function. An optimizer will seek a set of assignments
     * to the variables that minimizes this value.
     *
     * @return value of the objective for the optimization
     */
    public double objectiveFunction();

}
