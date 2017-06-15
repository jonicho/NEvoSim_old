package de.jrk.nevosim.neuralnetwork;

/**
 * A connection to connect two neurons with a weight.
 * @author Jonas Keller
 *
 */
public class Connection {
	
	public double weight = 0;
	public Neuron entryNeuron;

	public Connection(Neuron entryNeuron, double weight) {
		this.weight = weight;
		this.entryNeuron = entryNeuron;
	}
	
	public Neuron getEntryNeuron() {
		return entryNeuron;
	}
	
	/**
	 * Returns the value of the entry neuron multiplied by the weight.
	 * @return value
	 */
	public double getValue() {
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
