package de.jrk.nevosim.neuralnetwork;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import de.jrk.nevosim.CreatureInfo;
import de.jrk.nevosim.Vector;

/**
 * A neural network with one input layer, one hidden layer and one output layer.
 * 
 * @author Jonas Keller
 *
 */
public class NeuralNetwork {

	private ArrayList<InputNeuron> inputNeurons = new ArrayList<InputNeuron>();
	private ArrayList<WorkingNeuron> hiddenNeurons = new ArrayList<WorkingNeuron>();
	private ArrayList<WorkingNeuron> outputNeurons = new ArrayList<WorkingNeuron>();
	private boolean hovered = false;

	/**
	 * Returns the input neuron with the given name.
	 * 
	 * @param name
	 *            The name of the neuron
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
	 * 
	 * @param name
	 *            The name of the neuron
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
	 * 
	 * @param name
	 *            The name of the neuron
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
	 * 
	 * @param neuron
	 *            the input neuron to add
	 */
	public void addInputNeuron(InputNeuron neuron) {
		inputNeurons.add(neuron);
	}

	/**
	 * Adds the given hidden neuron.
	 * 
	 * @param neuron
	 *            the hidden neuron to add
	 */
	public void addHiddenNeuron(WorkingNeuron neuron) {
		hiddenNeurons.add(neuron);
	}

	/**
	 * Adds the given output neuron.
	 * 
	 * @param neuron
	 *            the output neuron to add
	 */
	public void addOutputNeuron(WorkingNeuron neuron) {
		outputNeurons.add(neuron);
	}

	/**
	 * Generates {@code amount} hidden neurons.
	 * 
	 * @param amount
	 *            the amount of hidden neurons
	 */
	public void generateHiddenNeurons(int amount) {
		for (int i = 0; i < amount; i++) {
			hiddenNeurons.add(new WorkingNeuron("h#" + i, true));
		}
	}

	/**
	 * Connects the complete output layer with the complete hidden layer and the
	 * complete hidden layer with the complete input layer.
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

	public void calculateNetwork() {
		for (WorkingNeuron wn : outputNeurons) {
			wn.getValue();
		}
	}

	/**
	 * Draws the neural network on the given Graphics.
	 * 
	 * @param g
	 *            the Graphics
	 */
	public void draw(Graphics g) {
		int inpDistance = CreatureInfo.height / inputNeurons.size();
		int hidDistance = CreatureInfo.height / hiddenNeurons.size();
		int outDistance = CreatureInfo.height / outputNeurons.size();
		int layDistance = CreatureInfo.width / 4;
		int size = CreatureInfo.height / 300;
		hovered = false;
		drawInputLayer(layDistance, inpDistance, g, size);
		drawWorkingLayer(hiddenNeurons, layDistance * 2, hidDistance, g, size);
		drawWorkingLayer(outputNeurons, (int) (layDistance * 3), outDistance, g, size);
		drawConnections(hiddenNeurons, g);
		drawConnections(outputNeurons, g);
	}

	private void drawInputLayer(int xLayer, int distance, Graphics g, int size) {
		for (int i = 0; i < inputNeurons.size(); i++) {
			int yI = (int) (distance * (i + 0.5f));
			double inpValue = inputNeurons.get(i).getValue();
			if (inpValue < 0) {
				g.setColor(new Color(255, 0, 0));
			} else {
				g.setColor(new Color(0, 255, 0));
			}
			inpValue = Math.abs(inpValue);
			int radius = (int) (10f * inpValue / (1 + inpValue) + 5) * size;
			g.fillOval(xLayer - radius, yI - radius, radius * 2, radius * 2);
			g.drawString(inputNeurons.get(i).getName(), 0, yI);
			Vector drawPos = new Vector(xLayer, yI);
			inputNeurons.get(i).drawPos = drawPos;
			if (Vector.getDistance(drawPos, CreatureInfo.mousePos) <= distance / 2) {
				inputNeurons.get(i).setHovered(true);
				hovered = true;
			} else {
				inputNeurons.get(i).setHovered(false);
			}
		}
	}

	private void drawWorkingLayer(ArrayList<WorkingNeuron> wns, int xLayer, int distance, Graphics g, int size) {
		for (int i = 0; i < wns.size(); i++) {
			int yW = (int) (distance * (i + 0.5f));
			double inpValue = wns.get(i).getValue();
			if (inpValue < 0) {
				g.setColor(new Color(255, 0, 0));
			} else {
				g.setColor(new Color(0, 255, 0));
			}
			inpValue = Math.abs(inpValue);
			int radius = (int) (10f * inpValue / (1 + inpValue) + 5) * size;
			g.fillOval(xLayer - radius, yW - radius, radius * 2, radius * 2);
			g.drawString(wns.get(i).getName(), xLayer + radius, yW);
			Vector drawPos = new Vector(xLayer, yW);
			wns.get(i).drawPos = drawPos;
			if (Vector.getDistance(drawPos, CreatureInfo.mousePos) <= distance / 2) {
				wns.get(i).setHovered(true);
				hovered = true;
			} else {
				wns.get(i).setHovered(false);
			}
		}
	}

	private void drawConnections(ArrayList<WorkingNeuron> wns, Graphics g) {
		double strongestConnection = getStrongestConnection(wns);
		for (WorkingNeuron wn : wns) {
			for (Connection c : wn.getConnections()) {
				if (!hovered || (wn.isHovered() || c.getEntryNeuron().isHovered())) {
					double weight = c.weight;
					if (weight < 0) {
						g.setColor(new Color(0f, 1f, 0f, (float) (Math.abs(c.weight) / strongestConnection)));
					} else {
						g.setColor(new Color(1f, 0f, 0f, (float) (Math.abs(c.weight) / strongestConnection)));
					}
					g.drawLine((int) wn.drawPos.getX(), (int) wn.drawPos.getY(), (int) c.entryNeuron.drawPos.getX(),
							(int) c.entryNeuron.drawPos.getY());
				}
			}
		}
	}

	private double getStrongestConnection(ArrayList<WorkingNeuron> workingNeurons) {
		double result = 0;
		for (WorkingNeuron wn : workingNeurons) {
			for (Connection c : wn.getConnections()) {
				double weight = Math.abs(c.weight);
				if (weight > result)
					result = weight;
			}
		}
		return result;
	}

	/**
	 * Mutates the neural network.
	 * 
	 * @param value
	 *            the mutate value. Should be between {@code 0.0} and
	 *            {@code 1.0}.
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
	 * 
	 * @return the cloned neural network
	 */
	public NeuralNetwork getClonedNetwork() {
		NeuralNetwork copy = new NeuralNetwork();
		// adding all neurons that are in this neural network to the cloned
		// neural network
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
				copy.hiddenNeurons.get(i).getConnections().get(j).weight = hiddenNeurons.get(i).getConnections()
						.get(j).weight;
			}
		}

		for (int i = 0; i < outputNeurons.size(); i++) {
			for (int j = 0; j < outputNeurons.get(i).getConnections().size(); j++) {
				copy.outputNeurons.get(i).getConnections().get(j).weight = outputNeurons.get(i).getConnections()
						.get(j).weight;
			}
		}

		return copy;
	}

	/**
	 * Saves the complete neural network.
	 * 
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
	 * 
	 * @param data
	 *            the data to load the neural network
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
			if (neuron != null)
				neuron.load(databaseN);
		}
	}
}
