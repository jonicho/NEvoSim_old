package de.jrk.nevosim.neuralnetwork;

import java.util.ArrayList;

import de.jrk.nevosim.NEvoSim;

/**
 * A neuron that processes the values of all connections.
 * @author Jonas Keller
 *
 */
public class WorkingNeuron extends Neuron {
	
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private boolean hidden;
	
	/**
	 * @param name The name of the working neuron
	 * @param hidden Whether the working neuron is hidden
	 */
	public WorkingNeuron(String name, boolean hidden) {
		this.name = name;
		invalidate();
		this.hidden = hidden;
	}
	
	/**
	 * Generates a new working neuron with the given database.
	 * @param database The database to load
	 * @param hidden Whether the working neuron is hidden
	 */
	public WorkingNeuron(String[] database, boolean hidden) {
		this.hidden = hidden;
		load(database);
	}
	
	/**
	 * Adds a connection with the given neuron and weight.
	 * @param n The neuron to be connected
	 * @param weight The weight of the connection
	 */
	public void addConnectionNeuron(Neuron n, float weight) {
		connections.add(new Connection(n, weight));
	}
	
	/**
	 * Returns the connection with the given name.
	 * Returns {@code null} if there is no connection with the given name.
	 * @param name The name of the neuron of the connection
	 * @return The connection
	 */
	private Connection getConnectionFromName(String name) {
		for (Connection c : connections) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Sets the value to {@code -2} to invalidate it.
	 */
	public void invalidate() {
		value = -2;
	}
	
	@Override
	public float getValue() {
		if (value == -2) {
			calculate(); // re-calculate the value if it is invalid
		}
		return value;
	}

	/**
	 * Calculates the value with the values of all connections.
	 */
	private void calculate() {
		float x = 0;
		for (Connection c : connections) {
			x += c.getValue();
		}
		value = function(x);
	}
	
	/**
	 * Returns a WorkingNeuron with the same name.
	 * @return WorkingNeuron with the same name
	 */
	public WorkingNeuron getNameCopy() {
		WorkingNeuron copy = new WorkingNeuron(getName(), hidden);
		return copy;
	}
	
	/**
	 * @return A list with all connections
	 */
	public ArrayList<Connection> getConnections() {
		return connections;
	}
	
	/**
	 * Randomizes all weights.
	 */
	public void randomizeWeights() {
		for (Connection connection : connections) {
			connection.weight = (float)Math.random() * 2f - 1f;
		}
	}
	
	/**
	 * Changes the value of the connections with the given value.
	 * @param value the mutate value. Should be between {@code 0.0} and {@code 1.0}.
	 */
	public void mutate(float value) {
		for (Connection connection : connections) {
			if (Math.random() > value) {
				if (NEvoSim.rand.nextBoolean()) connection.weight += value;
				else connection.weight -= value;
			}
		}
	}
	
	/**
	 * Saves all connections.
	 * @return The save data
	 */
	public String save() {
		String data = "";
		data += name + ":";
		for (int i = 0; i < connections.size(); i++) {
			data += connections.get(i).entryNeuron.getName() + ":" + connections.get(i).weight;
			if (i + 1 != connections.size()) {
				data += ":";
			}
		}
		return data;
	}
	
	/**
	 * Loads connections with the given data.
	 * If a connection is not in the data it is not changed.
	 * @param database Data to load the connections
	 */
	public void load(String[] database) {
		for (int i = 0; i < (database.length - 1) / 2; i++) {
			Connection connection = getConnectionFromName(database[i * 2 + 1]);
			if (connection != null) connection.weight = Float.parseFloat(database[i * 2 + 2]);
		}
	}
}
