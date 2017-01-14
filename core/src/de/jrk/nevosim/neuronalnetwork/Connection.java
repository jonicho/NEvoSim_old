package de.jrk.nevosim.neuronalnetwork;

public class Connection {
	
	public float weight = 0;
	public Neuron entryNeuron;

	public Connection(Neuron n, float weight) {
		this.weight = weight;
		this.entryNeuron = n;
	}
	
	public float getValue() {
		return weight * entryNeuron.getValue();
	}
	
	public String getName() {
		return entryNeuron.getName();
	}
}
