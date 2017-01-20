package de.jrk.nevosim.neuralnetwork;

import java.util.ArrayList;

/**
 * A neural network with one input layer, one hidden layer and one output layer.
 * @author Jonas Keller
 *
 */
public class NeuralNetwork {
	
	private ArrayList<InputNeuron> inputNeurons = new ArrayList<InputNeuron>();
	private ArrayList<WorkingNeuron> hiddenNeurons = new ArrayList<WorkingNeuron>();
	private ArrayList<WorkingNeuron> outputNeurons = new ArrayList<WorkingNeuron>();
	
	/**
	 * Returns the input neuron with the given name.
	 * @param name The name of the neuron
	 * @return the input neuron
	 */
	public InputNeuron getInputNeuron(String name) {
		for (InputNeuron in : inputNeurons) {
			if (in.name.equals(name)) {
				return in;
			}
		}
		return null;
	}

	/**
	 * Returns the output neuron with the given name.
	 * @param name The name of the neuron
	 * @return the output neuron
	 */
	public WorkingNeuron getOutputNeuron(String name) {
		for (WorkingNeuron wn : outputNeurons) {
			if (wn.name.equals(name)) {
				return wn;
			}
		}
		return null;
	}

	/**
	 * Returns the hidden neuron with the given name.
	 * @param name The name of the neuron
	 * @return the hidden neuron
	 */
	public WorkingNeuron getHiddenNeuron(String name) {
		for (WorkingNeuron hn : hiddenNeurons) {
			if (hn.name.equals(name)) {
				return hn;
			}
		}
		return null;
	}
	
	/**
	 * Adds the given input neuron.
	 * @param neuron the input neuron to add
	 */
	public void addInputNeuron(InputNeuron neuron) {
		inputNeurons.add(neuron);
	}

	/**
	 * Adds the given hidden neuron.
	 * @param neuron the hidden neuron to add
	 */
	public void addHiddenNeuron(WorkingNeuron neuron) {
		hiddenNeurons.add(neuron);
	}

	/**
	 * Adds the given output neuron.
	 * @param neuron the output neuron to add
	 */
	public void addOutputNeuron(WorkingNeuron neuron) {
		outputNeurons.add(neuron);
	}
	
	/**
	 * Generates {@code amount} hidden neurons.
	 * @param amount the amount of hidden neurons
	 */
	public void generateHiddenNeurons(int amount) {
		for (int i = 0; i < amount; i++) {
			hiddenNeurons.add(new WorkingNeuron("h#" + i, true));
		}
	}
	
	/**
	 * Connects the complete output layer with the complete hidden layer 
	 * and the complete hidden layer with the complete input layer.
	 */
	public void generateFullMesh() {
		for (WorkingNeuron hn : hiddenNeurons) {
			for (InputNeuron in : inputNeurons) {
				hn.addConnectionNeuron(in, 1);
			}
		}
		
		for (WorkingNeuron on : outputNeurons) {
			for (WorkingNeuron hn : hiddenNeurons) {
				on.addConnectionNeuron(hn, 1);
			}
		}
	}
	
	/**
	 * Invalidates all neurons.
	 */
	public void invalidate() {
		for (WorkingNeuron hn : hiddenNeurons) {
			hn.invalidate();
		}
		
		for (WorkingNeuron on : outputNeurons) {
			on.invalidate();
		}
	}
	
	/**
	 * Randomizes all weights.
	 */
	public void randomizeAllWeights() {
		for (WorkingNeuron hn : hiddenNeurons) {
			hn.randomizeWeights();
		}
		for (WorkingNeuron on : outputNeurons) {
			on.randomizeWeights();
		}
	}
	
	/**
	 * Mutates the neural network.
	 */
	public void mutate() {
		for (WorkingNeuron hn : hiddenNeurons) {
			hn.mutate();
		}
		for (WorkingNeuron on : outputNeurons) {
			on.mutate();
		}
	}
	
	/**
	 * Clones a neural network.
	 * @return the cloned neural network
	 */
	public NeuralNetwork getClonedNetwork() {
		NeuralNetwork copy = new NeuralNetwork();
		// adding all neurons that are in this neural network to the cloned neural network
		for (InputNeuron in : inputNeurons) {
			copy.addInputNeuron(in.getNameCopy());
		}
		for (WorkingNeuron hn : hiddenNeurons) {
			copy.addHiddenNeuron(hn.getNameCopy());
		}
		for (WorkingNeuron on : outputNeurons) {
			copy.addOutputNeuron(on.getNameCopy());
		}
		
		copy.generateFullMesh();
		
		// copying all weights
		for (int i = 0; i < hiddenNeurons.size(); i++) {
			for (int j = 0; j < hiddenNeurons.get(i).getConnections().size(); j++) {
				copy.hiddenNeurons.get(i).getConnections().get(j).weight = hiddenNeurons.get(i).getConnections().get(j).weight;
			}
		}
		
		for (int i = 0; i < outputNeurons.size(); i++) {
			for (int j = 0; j < outputNeurons.get(i).getConnections().size(); j++) {
				copy.outputNeurons.get(i).getConnections().get(j).weight = outputNeurons.get(i).getConnections().get(j).weight;
			}
		}
		
		return copy;
	}
	
	/**
	 * Saves the complete neural network.
	 * @return the save data
	 */
	public String save() {
		String data = "";
		
		for (int i = 0; i < hiddenNeurons.size(); i++) {
			data += hiddenNeurons.get(i).save();
			
			if (i + 1 != hiddenNeurons.size()) {
				data += "?";
			}
		}
		
		data += "§";
		
		for (int i = 0; i < outputNeurons.size(); i++) {
			data += outputNeurons.get(i).save();
			if (i + 1 != hiddenNeurons.size()) {
				data += "?";
			}
		}
		return data;
	}

	/**
	 * Loads the neural network with the given data.
	 * @param data the data to load the neural network
	 */
	public void load(String data) {
		String[] database = data.split("§");
		String dataHidden = database[0];
		String[] databaseHidden = dataHidden.split("\\?");
		String dataOutput = database[1];
		String[] databaseOutput = dataOutput.split("\\?");
		
		for (int i = 0; i < databaseHidden.length; i++) {
			String[] databaseN = databaseHidden[i].split(":");
			WorkingNeuron neuron = getHiddenNeuron(databaseN[0]);
			if (neuron != null) {
				neuron.load(databaseN);
			}
		}
		
		for (int i = 0; i < databaseOutput.length; i++) {
			String[] databaseN = databaseOutput[i].split(":");
			WorkingNeuron neuron = getOutputNeuron(databaseN[0]);
			if (neuron != null) neuron.load(databaseN);
		}
	}
}
