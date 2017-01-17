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
	
	public void setValue(float value) {
		this.value = value;
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
