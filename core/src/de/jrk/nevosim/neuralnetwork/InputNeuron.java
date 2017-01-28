package de.jrk.nevosim.neuralnetwork;

/**
 * An Input for a neural network.
 * @author Jonas Keller
 *
 */
public class InputNeuron extends Neuron {
	
	public InputNeuron(String name) {
		this.name = name;
		value = 0;
	}

	@Override
	public float getValue() {
		return value;
	}
	
	/**
	 * Sets the value of the neuron.
	 * @param value the value
	 */
	public void setValue(float value) {
		this.value = value;
	}
	
	/**
	 * Sets the value of the neuron with a boolean.
	 * If {@code value} is {@code true} the value is set to {@code 1}.
	 * If {@code value} is {@code false} the value is set to {@code -1}
	 * @param value the value
	 */
	public void setValue(boolean value) {
		if (value) this.value = 1;
		else this.value = -1;
	}
	
	/**
	 * Returns a InputNeuron with the same name.
	 * @return InputNeuron with the same name
	 */
	public InputNeuron getNameCopy() {
		InputNeuron copy = new InputNeuron(getName());
		return copy;
	}
}
