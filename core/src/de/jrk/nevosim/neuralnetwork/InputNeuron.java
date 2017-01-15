package de.jrk.nevosim.neuralnetwork;

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
	
	public InputNeuron getNameCopy() {
		InputNeuron copy = new InputNeuron(getName());
		return copy;
	}
}
