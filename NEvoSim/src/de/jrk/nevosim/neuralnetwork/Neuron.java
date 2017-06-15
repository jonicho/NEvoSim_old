package de.jrk.nevosim.neuralnetwork;

import de.jrk.nevosim.Vector;

/**
 * A neuron with a value.
 * Contains the function to calculate the output value.
 * @author Jonas Keller
 *
 */
abstract class Neuron {

	protected double value = -2;
	protected String name;
	public Vector drawPos;
	private boolean hovered;
	
	public boolean isHovered() {
		return hovered;
	}
	
	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}
	
	public abstract double getValue();
	
	public String getName() {
		return name;
	}
	
	/**
	 * Calculates the function {@code f(x) = 1 / (1 + abs(x))}.
	 * @param x x
	 * @return The result of the function
	 */
	public static double function(double x) {
		return x / (1 + Math.abs(x));
	}

}