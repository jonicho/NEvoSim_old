package de.jrk.nevosim.neuralnetwork;

/**
 * A neuron with a value.
 * Contains the function to calculate the output value.
 * @author jonas
 *
 */
abstract class Neuron {

	protected float value = -2;
	protected String name;
	
	public abstract float getValue();
	
	public String getName() {
		return name;
	}
	
	/**
	 * Calculates the function {@code f(x) = 1 / (1 + abs(x))}.
	 * @param x x
	 * @return The result of the function
	 */
	public static float function(float x) {
		return x / (1 + Math.abs(x));
	}

}