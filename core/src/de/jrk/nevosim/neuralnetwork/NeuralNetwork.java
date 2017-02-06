package de.jrk.nevosim.neuralnetwork;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;

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
	 * Draws the neural network on the given Pixmap.
	 * @param map the Pixmap
	 * @param x x
	 * @param y y
	 * @param width width
	 * @param height height
	 */
	public void draw(Pixmap map, int x, int y, int width, int height) {
		int inpDistance = height / inputNeurons.size();
		int hidDistance = height / hiddenNeurons.size();
		int outDistance = height / outputNeurons.size();
		int layDistance = width / 4;
		drawInputLayer(layDistance, y, inpDistance, map);
		drawWorkingLayer(hiddenNeurons, layDistance * 2, y, hidDistance, map);
		drawWorkingLayer(outputNeurons, layDistance * 3, y, outDistance, map);
	}
	
	
	private void drawInputLayer(int xLayer, int y, int distance, Pixmap map) {
		float highestValue = 0;
		for (InputNeuron in : inputNeurons) {
			float value = Math.abs(in.getValue());
			if (value > highestValue) highestValue = value;
		}
		
		for (int i = 0; i < inputNeurons.size(); i++) {
			int yI = (int) (distance * (i + 0.5f)) + y;
			float inpValue = inputNeurons.get(i).getValue();
			if (inpValue < 0) {
				map.setColor(1, 0, 0, 1);
			} else {
				map.setColor(0, 1, 0, 1);
			}
			inpValue = Math.abs(inpValue);
			map.fillCircle(xLayer, yI, (int) (20f * inpValue / (1 + inpValue)));
			inputNeurons.get(i).drawPos = new Vector2(xLayer, yI);
		}
	}

	
	private void drawWorkingLayer(ArrayList<WorkingNeuron> wns, int xLayer, int y, int distance, Pixmap map) {
		for (int i = 0; i < wns.size(); i++) {
			int yW = (int) (distance * (i + 0.5f)) + y;
			float inpValue = wns.get(i).getValue();
			if (inpValue < 0) {
				map.setColor(1, 0, 0, 1);
			} else {
				map.setColor(0, 1, 0, 1);
			}
			inpValue = Math.abs(inpValue);
			map.fillCircle(xLayer, yW, (int) (20f * inpValue / (1 + inpValue)));
			wns.get(i).drawPos = new Vector2(xLayer, yW);
		}
		drawConnections(wns, map);
	}

	
	private void drawConnections(ArrayList<WorkingNeuron> wns, Pixmap map) {
		float strongestConnection = getStrongestConnection(wns);
		for (WorkingNeuron wn : wns) {
			for (Connection c : wn.getConnections()) {
				float weight = c.weight;
				if (weight < 0) {
					map.setColor(new Color(0, 1, 0, Math.abs(c.weight) / strongestConnection).clamp());
				} else {
					map.setColor(new Color(1, 0, 0, Math.abs(c.weight) / strongestConnection).clamp());
				}
				map.drawLine((int) wn.drawPos.x, (int) wn.drawPos.y, (int) c.entryNeuron.drawPos.x, (int) c.entryNeuron.drawPos.y);
			}
		}
	}

	
	private float getStrongestConnection(ArrayList<WorkingNeuron> workingNeurons) {
		float result = 0;
		for (WorkingNeuron wn : workingNeurons) {
			for (Connection c : wn.getConnections()) {
				float weight = Math.abs(c.weight);
				if (weight > result) result = weight;
			}
		}
		return result;
	}
	
	/**
	 * Mutates the neural network.
	 * @param value the mutate value. Should be between {@code 0.0} and {@code 1.0}.
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
