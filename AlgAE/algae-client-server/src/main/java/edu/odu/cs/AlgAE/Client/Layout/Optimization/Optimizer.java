package edu.odu.cs.AlgAE.Client.Layout.Optimization;

import java.util.ArrayList;
import java.util.Random;


public class Optimizer {
	
	private OptimizationProblem problem;

	public Optimizer(OptimizationProblem problem) {
		this.problem = problem;
	}

	
	private class OptimizationState {
		public double[] values;
		public double score;
		
		public OptimizationState (int dimension) {
			values = new double[dimension];
			score = 0.0;
		}

		public OptimizationState (OptimizationProblem p) {
			ArrayList<Variable> v = p.getVariables();
			values = new double[v.size()];
			for (int i = 0; i < values.length; ++i)
				values[i] = v.get(i).getValue();
			score = p.objectiveFunction();
		}
		
		public void restore (OptimizationProblem p) {
			ArrayList<Variable> v = p.getVariables();
			for (int i = 0; i < values.length; ++i)
				v.get(i).setValue(values[i]);			
		}
		
		public String toString() {
			StringBuffer result = new StringBuffer();
			result.append (score);
			result.append (":[");
			for (int i = 0; i < values.length; ++i) {
				if (i > 0)
					result.append(", ");
				result.append (values[i]);
			}
			result.append("]");
			return result.toString();
		}
		
	}
	
	private ArrayList<OptimizationState> candidates;
	private double stepMultiplier;
	
	public double solve(double initialStepSize, double finalStepSize, int iterationLimit) {
		int dimension = problem.getVariables().size();
		if (dimension > 0) {
			stepMultiplier = finalStepSize / initialStepSize;
			stepMultiplier = Math.exp(Math.log(stepMultiplier) / (double)(1 + iterationLimit/10));
			double[] scale = new double[dimension];
			for (int i = 0; i < dimension; ++i) {
				double s = problem.getVariables().get(i).getScale();
				if (s <= 0.0)
					s = 1.0;
				scale[i] = s;
			}
			OptimizationState initialState = new OptimizationState(problem);
			candidates = new ArrayList<OptimizationState>();
			candidates.add (initialState);
			double step = initialStepSize;
			int iterationCount = 0;
			while (step >= finalStepSize && iterationCount < iterationLimit) {
				OptimizationState base = selectCandidateState();
				OptimizationState permuted = perturbBaseState (base, step, scale);
				double stepTaken = Math.abs(solveAlongLine(base, permuted, dimension));
				if (stepTaken > 2.0)
					step = step / stepMultiplier;
				else if (stepTaken < 0.5)
					step = step * stepMultiplier;
				++iterationCount;
				trimCandidates(dimension);
			}
			OptimizationState best = candidates.get(0);
			best.restore(problem);
			return best.score;
		} else {
			return 0.0;
		}
	}

	static private Random random = new Random();

	
	private OptimizationState selectCandidateState() {
		int k = random.nextInt(candidates.size());
		return candidates.get(k);
	}

	private OptimizationState perturbBaseState(OptimizationState base,
			double step, double[] scale) {
		if (random.nextDouble() > 0.75)
			return randomlyPerturbBaseState(base, step, scale);
		else
			return gradientPerturbBaseState(base, step, scale);
	}

	private OptimizationState gradientPerturbBaseState(OptimizationState base,
			double step, double[] scale) {
		double[] unit = new double[scale.length];
		double sumsq = 0.0;

		OptimizationState perturbed = new OptimizationState(unit.length);
		for (int i = 0; i < unit.length; ++i) {
			perturbed.values[i] = base.values[i];
		}
		
		
		for (int i = 0; i < unit.length; ++i) {
			double save = perturbed.values[i];
			perturbed.values[i] += step * scale[i];
			perturbed.restore(problem);
			unit[i] = base.score - problem.objectiveFunction();
			sumsq += unit[i]*unit[i];
			perturbed.values[i] = save;
		}
		double norm = Math.sqrt(sumsq);
		if (norm == 0) {
			unit[random.nextInt(unit.length)] = 1.0;
			norm = 1.0;
		}

		for (int i = 0; i < unit.length; ++i) {
			perturbed.values[i] = base.values[i] + unit[i]/norm;
		}
		perturbed.restore(problem);
		perturbed.score = problem.objectiveFunction();
		return perturbed;
	}

	
	private OptimizationState randomlyPerturbBaseState(OptimizationState base,
			double step, double[] scale) {
		double[] unit = new double[scale.length];
		double sumsq = 0.0;
		for (int i = 0; i < unit.length; ++i) {
			double r = random.nextDouble();
			unit[i] = 2.0 * r - 1.0;;
			sumsq += r*r;
		}
		double norm = Math.sqrt(sumsq);
		if (norm == 0) {
			unit[random.nextInt(unit.length)] = 1.0;
			norm = 1.0;
		}
		OptimizationState perturbed = new OptimizationState(unit.length);
		for (int i = 0; i < unit.length; ++i) {
			perturbed.values[i] = base.values[i] + unit[i]*scale[i]*step/norm;
		}
		perturbed.restore(problem);
		perturbed.score = problem.objectiveFunction();
		return perturbed;
	}

	private double solveAlongLine(OptimizationState base,
			OptimizationState perturbed, int dimension) {
		
		double[] x = new double[4];
		double[] y = new double[4];
		OptimizationState[] states = new OptimizationState[4];
		
		x[0] = x[1] = 0.0;
		y[0] = y[1] = base.score;
		states[0] = states[1] = base;
		x[3] = 1.0;
		y[3] = perturbed.score;
		states[3] = perturbed;
		 
		x[2] = 0.5;
		OptimizationState mid = new OptimizationState(dimension);
		for (int i = 0; i < dimension; ++i) {
			mid.values[i] = (base.values[i] + perturbed.values[i]) / 2.0;
		}
		mid.restore(problem);
		mid.score = problem.objectiveFunction();
		y[2] = mid.score;
		states[2] = mid;
		
		double x2x1 = x[2] - x[1];
		double x2x3 = x[2] - x[3];
		double denom = x2x1 * (y[2] - y[3]) - x2x3 * (y[2] - y[1]);
		double xmin = x[1];
		if (Math.abs(denom) > 1.0E-10) {
			xmin = x[2] - 0.5 * (x2x1*x2x1*(y[2]-y[3]) - x2x3*x2x3*(y[2]-y[1])) / denom;
		}
		if (xmin != x[1]) {
			x[0] = xmin;
			OptimizationState opt = new OptimizationState(dimension);
			for (int i = 0; i < dimension; ++i) {
				opt.values[i] = base.values[i] + xmin * (perturbed.values[i] - base.values[i]);
			}
			opt.restore (problem);
			y[0] = opt.score = problem.objectiveFunction();
			states[0] = opt;
		}
		int jmin = 0;
		for (int i = 1; i < 4; ++i)
			if (y[i] < y[jmin])
				jmin = i;
		states[jmin].restore(problem);
		if (states[jmin] != base) {
			candidates.add (states[jmin]);
		}
		return x[jmin];
	}

	private void trimCandidates(int dimension) {
		// Sort into ascending order by score
		for (int i =1; i < candidates.size(); ++i) {
			OptimizationState p = candidates.get(i);
			int j = i-1;
			while (j >= 0 && candidates.get(j).score > candidates.get(j+1).score) {
				candidates.set(j+1, candidates.get(j));
				--j;
			}
			candidates.set(j+1, p);
		}
		// If necessary, trim the least successful candidates
		while (candidates.size() > dimension + 1) {
			candidates.remove(candidates.size()-1);
		}
	}


}
