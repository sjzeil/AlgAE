/**
 * 
 */
package edu.odu.cs.AlgAE.Client.Layout.Optimization;


/**
 * @author zeil
 *
 */
public class Angle extends Variable {

	/**
	 * @param a angle
	 */
	public Angle(double a) {
		super(0, -3600.0, 3600.0, 360.0);
		setValue (a);
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double v) {
		value = v;
		while (value > 360.0)
			value -= 360.0;
		while (value < 0.0)
			value += 360.0;
		if (getMin() < 0) {
			if (value > getMax() && value < getMin() + 360.0) {
				if (Math.abs(value - getMax()) < Math.abs(getMin() + 360 - value))
					value = getMax();
				else
					value = getMin() + 360.0;
			}
		} else {
			if (value > getMax() || value < getMin() ) {
				if (Math.abs(value - getMax()) < Math.abs(getMin() - value))
					value = getMax();
				else
					value = getMin();
			}
		}
	}
	
	public Object clone() {
		return new Angle (value);
	}

	public String toString() {
		return "@" + value;
	}


}
