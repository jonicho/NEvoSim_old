package de.jrk.nevosim.neuralnetwork;

import java.util.ArrayList;

import de.jrk.nevosim.NEvoSim;

public class WorkingNeuron extends Neuron {
	
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private boolean hidden;
	
	public WorkingNeuron(String name, boolean hidden) {
		this.name = name;
		invalidate();
		this.hidden = hidden;
	}
	
	public WorkingNeuron(String[] database, boolean hidden) {
		this.hidden = hidden;
		load(database);
	}
	
	public void addConnectionNeuron(Neuron n, float weight) {
		connections.add(new Connection(n, weight));
	}
	
	private Connection getConnectionFromName(String name) {
		for (Connection c : connections) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	public void invalidate() {
		value = -2;
	}
	
	@Override
	public float getValue() {
		if (value == -2) {
			calculate();
		}
		return value;
	}

	private void calculate() {
		float x = 0;
		for (Connection c : connections) {
			x += c.getValue();
		}
		value = function(x);
	}
	
	public WorkingNeuron getNameCopy() {
		WorkingNeuron copy = new WorkingNeuron(getName(), hidden);
		return copy;
	}
	
	public ArrayList<Connection> getConnections() {
		return connections;
	}
	
	public void randomizeWeights() {
		for (Connection connection : connections) {
			connection.weight = (float)Math.random() * 2f - 1f;
		}
	}
	
	public void mutate() {
		for (Connection connection : connections) {
			if (true && Math.random() > 0.3) {
				if (NEvoSim.rand.nextBoolean()) connection.weight += 0.1f;
				else connection.weight -= 0.1f;
			}
		}
	}
	
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
	
	public void load(String[] database) {
		for (int i = 0; i < (database.length - 1) / 2; i++) {
			Connection connection = getConnectionFromName(database[i * 2 + 1]);
			if (connection != null) connection.weight = Float.parseFloat(database[i * 2 + 2]);
		}
	}
}
