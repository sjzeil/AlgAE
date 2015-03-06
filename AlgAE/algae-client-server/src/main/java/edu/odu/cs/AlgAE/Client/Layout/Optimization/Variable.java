/**
 *
 */
package edu.odu.cs.AlgAE.Client.Layout.Optimization;

/**
 * @author zeil
 *
 */
public class Variable implements Cloneable {
	
	protected double value;
	private double min;
	private double max;
	private double scale;
	
	
	public Variable (double v, double min, double max, double scale)
	{
		value = v;
		this.min = min;
		this.max = max;
		this.scale = scale;
	}

	
	public Variable (double v, double min, double max)
	{
		value = v;
		this.min = min;
		this.max = max;
		double mmscale = Math.max(Math.abs(min), Math.abs(max));
		double vscale = Math.abs(v);
		this.scale = Math.max(Math.sqrt(mmscale*vscale), (mmscale+vscale)/2.0);
	}

	
	/**
	 * @param value the value to set
	 */
	public void setValue(double v) {
		this.value = Math.max(Math.min(v, max), min) ;
	}
	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param min minimum value that can be assigned to this variable
	 */
	public void setMin(double min) {
		this.min = min;
	}
	/**
	 * @return minimum value that can be assigned to this variable
	 */
	public double getMin() {
		return min;
	}
	
	/**
	 * @param max maximum value that can be assigned to this variable
	 */
	public void setMax(double max) {
		this.max = max;
	}
	/**
	 * @return maximum value that can be assigned to this variable
	 */
	public double getMax() {
		return max;
	}
	
	/**
	 * @param scale the scale to set
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}


	/**
	 * @return the scale
	 */
	public double getScale() {
		return scale;
	}


	public Object clone() {
		return new Variable (value, min, max, scale);
	}

	public String toString() {
		return "" + value;
	}
	
	public boolean equals (Object obj)
	{
		if (obj instanceof Variable) {
			Variable var = (Variable)obj;
			return value == var.value;
		} else
			return false;
	}
	
}
