package de.jrk.nevosim.neuralnetwork;

import java.util.ArrayList;

public class NeuralNetwork {
	
	private ArrayList<InputNeuron> inputNeurons = new ArrayList<InputNeuron>();
	private ArrayList<WorkingNeuron> hiddenNeurons = new ArrayList<WorkingNeuron>();
	private ArrayList<WorkingNeuron> outputNeurons = new ArrayList<WorkingNeuron>();
	
	public InputNeuron getInputNeuronFromName(String name) {
		for (InputNeuron in : inputNeurons) {
			if (in.name.equals(name)) {
				return in;
			}
		}
		return null;
	}
	
	public WorkingNeuron getOutputNeuronFromName(String name) {
		for (WorkingNeuron wn : outputNeurons) {
			if (wn.name.equals(name)) {
				return wn;
			}
		}
		return null;
	}
	
	public WorkingNeuron getHiddenNeuronFromName(String name) {
		for (WorkingNeuron hn : hiddenNeurons) {
			if (hn.name.equals(name)) {
				return hn;
			}
		}
		return null;
	}
	
	public void addInputNeuron(InputNeuron neuron) {
		inputNeurons.add(neuron);
	}
	
	public void addHiddenNeuron(WorkingNeuron neuron) {
		hiddenNeurons.add(neuron);
	}
	
	public void addOutputNeuron(WorkingNeuron neuron) {
		outputNeurons.add(neuron);
	}
	
	public void generateHiddenNeurons(int amount) {
		for (int i = 0; i < amount; i++) {
			hiddenNeurons.add(new WorkingNeuron("h#" + i, true));
		}
	}
	
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
	
	public void invalidate() {
		for (WorkingNeuron hn : hiddenNeurons) {
			hn.invalidate();
		}
		
		for (WorkingNeuron on : outputNeurons) {
			on.invalidate();
		}
	}
	
	public void randomizeAllWeights() {
		for (WorkingNeuron hn : hiddenNeurons) {
			hn.randomizeWeights();
		}
		for (WorkingNeuron on : outputNeurons) {
			on.randomizeWeights();
		}
	}
	
	public void mutate() {
		for (WorkingNeuron hn : hiddenNeurons) {
			hn.mutate();
		}
		for (WorkingNeuron on : outputNeurons) {
			on.mutate();
		}
	}
	
	public NeuralNetwork getClonedNetwork() {
		NeuralNetwork copy = new NeuralNetwork();
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

	public void load(String data) {
		String[] database = data.split("§");
		String dataHidden = database[0];
		String[] databaseHidden = dataHidden.split("\\?");
		String dataOutput = database[1];
		String[] databaseOutput = dataOutput.split("\\?");
		
		for (int i = 0; i < databaseHidden.length; i++) {
			String[] databaseN = databaseHidden[i].split(":");
			WorkingNeuron neuron = getHiddenNeuronFromName(databaseN[0]);
			if (neuron != null) {
				neuron.load(databaseN);
			}
		}
		
		for (int i = 0; i < databaseOutput.length; i++) {
			String[] databaseN = databaseOutput[i].split(":");
			WorkingNeuron neuron = getOutputNeuronFromName(databaseN[0]);
			if (neuron != null) neuron.load(databaseN);
		}
	}
}
