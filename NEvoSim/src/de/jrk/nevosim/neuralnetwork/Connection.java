package de.jrk.nevosim.neuralnetwork;

/**
 * A connection to connect two neurons with a weight.
 * @author Jonas Keller
 *
 */
public class Connection {
	
	public float weight = 0;
	public Neuron entryNeuron;

	public Connection(Neuron entryNeuron, float weight) {
		this.weight = weight;
		this.entryNeuron = entryNeuron;
	}
	
	/**
	 * Returns the value of the entry neuron multiplied by the weight.
	 * @return value
	 */
	public float getValue() {
		return weight * entryNeuron.getValue();
	}
	
	/**
	 * Returns the name of the entry neuron.
	 * @return name of the entry neuron
	 */
	public String getName() {
		return entryNeuron.getName();
	}
}
