package de.jrk.nevosim;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import de.jrk.nevosim.Tile.TileType;
import de.jrk.nevosim.neuralnetwork.InputNeuron;
import de.jrk.nevosim.neuralnetwork.NeuralNetwork;
import de.jrk.nevosim.neuralnetwork.WorkingNeuron;

/**
 * The Creature living in the world and trying to survive.
 * Each Creature has his own neural network.
 * @author Jonas Keller
 *
 */
public class Creature {
	
	private double x;
	private double y;
	private double xFeelerRight;
	private double yFeelerRight;
	private double xFeelerLeft;
	private double yFeelerLeft;
	
	private int xTile;
	private int yTile;
	private int xTileFeelerRight;
	private int yTileFeelerRight;
	private int xTileFeelerLeft;
	private int yTileFeelerLeft;
	
	private float speed;
	private double feelerDistance;
	private double size;
	private float direction;
	
	private float energy;
	private float yearBorn;
	private float age;
	private double matureAge;
	private int generation;

	private boolean wantAttack;
	private boolean attack;
	private boolean isAttacked;
	private int splits;

	private Creature nearestCreature;
	private double nearestCreatureDistance;
	private float geneticDifference;
	
	private Color color;
	private BufferedImage image;
	
	private NeuralNetwork motherBrain;
	
	private boolean alive = true;
	private boolean selected;
	
	
	private static final double COST_MULTIPLIER = 0.05;
	private static final double ATTACK_VALUE = 100;
	private static final double BODY_SIZE = 0.1;
	private static final double FEELER_SIZE = 0.003;
	
	
	public static final String IN_ONLAND = "in_on-land";
	public static final String IN_RIGHTFEELERONLAND = "in_right-feeler-on-land";
	public static final String IN_LEFTFEELERONLAND = "in_left-feeler-on-land";
	public static final String IN_FOOD = "in_food";
	public static final String IN_ENERGY = "in_energy";
	public static final String IN_RIGHTFEELERFOOD = "in_right-feeler-food";
	public static final String IN_LEFTFEELERFOOD = "in_left-feeler-food";
	public static final String IN_AGE = "in_age";
	public static final String IN_GENETICDIFFERENCE = "in_genetic-difference";
	public static final String IN_ISATTACKED = "in_is-attacked";
	public static final String IN_MEMORY1 = "in_memory-1";
	public static final String IN_MEMORY2 = "in_memory-2";

	public static final String OUT_ROTATE = "out_rotate";
	public static final String OUT_EAT = "out_eat";
	public static final String OUT_MOVE = "out_move";
	public static final String OUT_SPLIT = "out_split";
	public static final String OUT_ATTACK = "out_attack";
	public static final String OUT_MEMORY1 = "out_memory-1";
	public static final String OUT_MEMORY2 = "out_memory-2";
	
	private InputNeuron inRightFeelerOnLand = new InputNeuron(IN_RIGHTFEELERONLAND);
	private InputNeuron inLeftFeelerOnLand = new InputNeuron(IN_LEFTFEELERONLAND);
	private InputNeuron inOnLand = new InputNeuron(IN_ONLAND);
	private InputNeuron inFood = new InputNeuron(IN_FOOD);
	private InputNeuron inEnergy = new InputNeuron(IN_ENERGY);
	private InputNeuron inRightFeelerFood = new InputNeuron(IN_RIGHTFEELERFOOD);
	private InputNeuron inLeftFeelerFood = new InputNeuron(IN_LEFTFEELERFOOD);
	private InputNeuron inAge = new InputNeuron(IN_AGE);
	private InputNeuron inGeneticDifference = new InputNeuron(IN_GENETICDIFFERENCE);
	private InputNeuron inIsAttacked = new InputNeuron(IN_ISATTACKED);
	private InputNeuron inMemory1 = new InputNeuron(IN_MEMORY1);
	private InputNeuron inMemory2 = new InputNeuron(IN_MEMORY2);
	
	private WorkingNeuron outRotate = new WorkingNeuron(OUT_ROTATE, false);
	private WorkingNeuron outEat = new WorkingNeuron(OUT_EAT, false);
	private WorkingNeuron outMove = new WorkingNeuron(OUT_MOVE, false);
	private WorkingNeuron outSplit = new WorkingNeuron(OUT_SPLIT, false);
	private WorkingNeuron outAttack = new WorkingNeuron(OUT_ATTACK, false);
	private WorkingNeuron outMemory1 = new WorkingNeuron(OUT_MEMORY1, false);
	private WorkingNeuron outMemory2 = new WorkingNeuron(OUT_MEMORY2, false);
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getxFeelerRight() {
		return xFeelerRight;
	}

	public double getyFeelerRight() {
		return yFeelerRight;
	}

	public double getxFeelerLeft() {
		return xFeelerLeft;
	}

	public double getyFeelerLeft() {
		return yFeelerLeft;
	}

	public float getDirection() {
		return direction;
	}
	
	public Color getColor() {
		return color;
	}

	public Creature getNearestCreature() {
		return nearestCreature;
	}

	public void setNearestCreature(Creature nearestCreature) {
		this.nearestCreature = nearestCreature;
	}

	public double getNearestCreatureDistance() {
		return nearestCreatureDistance;
	}

	public void setNearestCreatureDistance(double nearestCreatureDistance) {
		this.nearestCreatureDistance = nearestCreatureDistance;
	}

	public NeuralNetwork getBrain() {
		return motherBrain;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public double getMatureAge() {
		return matureAge;
	}
	
	public int getGeneration() {
		return generation;
	}

	/**
	 * Generates a new Creature with the given Color and x- and y-position.
	 * @param color the color
	 * @param x the y-position
	 * @param y the x-position
	 * @param generateBrain whether a brain (neural network) is to be generated
	 */
	public Creature(Color color, double x, double y, boolean generateBrain) {
		yearBorn = Main.year;
		if (color != null) this.color = varyColor(color);
		this.x = x;
		this.y = y;
		feelerDistance = 0.01;
		energy = 150;
		age = 0;
		direction = (float) (Math.random() * 360);
		size = ((energy - 80) / (1 + Math.abs(energy / 100))) / 1000 * Renderer.size;
		calculateFeelerPos();
		
		motherBrain = new NeuralNetwork();
		if (generateBrain) generateBrain(true);
		
		matureAge = Math.random() + 0.2f;
		createImage();
	}
	
	/**
	 * Generates a new creature that inherits from the given mother.
	 * The complete neural network will be copied and then mutated.
	 * @param motherCreature the mother of the new creature
	 * @param energy
	 */
	public Creature(Creature motherCreature, float energy) {
		this(motherCreature.getColor(),  motherCreature.getX(), motherCreature.getY(), false);
		
		matureAge = motherCreature.getMatureAge() + (float)Math.random() * 0.1f - 0.05f;
		if (matureAge < 0.2f) {
			matureAge = 0.2f;
		}
		generation = motherCreature.getGeneration() + 1;
		motherBrain = motherCreature.getBrain().getClonedNetwork();
		this.energy = energy;
		
		inRightFeelerOnLand = motherBrain.getInputNeuron(IN_RIGHTFEELERONLAND);
		inLeftFeelerOnLand = motherBrain.getInputNeuron(IN_LEFTFEELERONLAND);
		inOnLand = motherBrain.getInputNeuron(IN_ONLAND);
		inFood = motherBrain.getInputNeuron(IN_FOOD);
		inEnergy = motherBrain.getInputNeuron(IN_ENERGY);
		inRightFeelerFood = motherBrain.getInputNeuron(IN_RIGHTFEELERFOOD);
		inLeftFeelerFood = motherBrain.getInputNeuron(IN_LEFTFEELERFOOD);
		inAge = motherBrain.getInputNeuron(IN_AGE);
		inGeneticDifference = motherBrain.getInputNeuron(IN_GENETICDIFFERENCE);
		inIsAttacked = motherBrain.getInputNeuron(IN_ISATTACKED);
		inMemory1 = motherBrain.getInputNeuron(IN_MEMORY1);
		inMemory2 = motherBrain.getInputNeuron(IN_MEMORY2);
		
		outRotate = motherBrain.getOutputNeuron(OUT_ROTATE);
		outEat = motherBrain.getOutputNeuron(OUT_EAT);
		outMove = motherBrain.getOutputNeuron(OUT_MOVE);
		outSplit = motherBrain.getOutputNeuron(OUT_SPLIT);
		outAttack = motherBrain.getOutputNeuron(OUT_ATTACK);
		outMemory1 = motherBrain.getOutputNeuron(OUT_MEMORY1);
		outMemory2 = motherBrain.getOutputNeuron(OUT_MEMORY2);
	}
	
	/**
	 * Loads a creature from the given data.
	 * @param data
	 */
	public Creature(String data) {
		this(null, 0, 0, false);
		generateBrain(false);
		try {
			load(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		calculateFeelerPos();
		createImage();
	}
	
	private Color varyColor(Color c) {
		int r = (int) (c.getRed() + Math.random() * 20 - 10);
		int g = (int) (c.getGreen() + Math.random() * 20 - 10);
		int b = (int) (c.getBlue() + Math.random() * 20 - 10);
		r = r < 0 ? 0 : r;
		r = r > 255 ? 255 : r;
		g = g < 0 ? 0 : g;
		g = g > 255 ? 255 : g;
		b = b < 0 ? 0 : b;
		b = b > 255 ? 255 : b;
		return new Color(r, g, b);
	}
	
	/**
	 * Draws the creature on the given SpriteBatch.
	 * @param batch the batch on witch is to be drawn
	 */
	public void draw(Graphics g) {
		selected = this == Main.selectedCreature;
		
		double s = 0.01;
		
		size = (((energy - 100) / (1 + Math.abs((energy - 100) * s))) * s + 0.4) * 0.1 * Renderer.size;
		
		if (selected) {
			double indicatorSize = Renderer.size / 30;
			g.setColor(new Color(255, 0, 0, 150));
			g.fillOval((int) (x * Renderer.size - indicatorSize / 2), (int) (y * Renderer.size - indicatorSize / 2),
					(int)indicatorSize, (int)indicatorSize);
		}
		
		g.drawImage(image, (int)(x * Renderer.size - BODY_SIZE * size/2), 
				   (int)(y * Renderer.size - BODY_SIZE * size/2), 
				   (int)(BODY_SIZE * size), (int)(BODY_SIZE * size), null);

		g.drawImage(image, (int)(xFeelerLeft * Renderer.size - FEELER_SIZE * Renderer.size/2), 
				   (int)(yFeelerLeft * Renderer.size - FEELER_SIZE * Renderer.size/2), 
				   (int)(FEELER_SIZE * Renderer.size), (int)(FEELER_SIZE * Renderer.size), null);
		
		g.drawImage(image, (int)(xFeelerRight * Renderer.size - FEELER_SIZE * Renderer.size/2), 
				   (int)(yFeelerRight * Renderer.size - FEELER_SIZE * Renderer.size/2), 
				   (int)(FEELER_SIZE * Renderer.size), (int)(FEELER_SIZE * Renderer.size), null);
		
		// TODO draw attack indicator
		if (Renderer.showAttackIndicator) {
			if (isAttacked) {
			} else if (attack) {
			} else if (wantAttack) {
			} else {
			} 
		}
	}
	
	private void createImage() {
		image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		g.setColor(Color.BLACK);
		g.fillOval(0, 0, 100, 100);
		g.setColor(color);
		g.fillOval(10, 10, 80, 80);
	}
	
	
	/**
	 * Draws the neural network of the creature in the given Graphics;
	 * @param g the Graphics on witch is to be drawn
	 */
	public void drawBrain(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, CreatureInfo.width, CreatureInfo.height);
		motherBrain.draw(g);
	}
	
	public String createInfoText() {
		String infoText = "<html><body>";
		infoText += " State: ";
		if (alive) infoText += "alive<br>";
		else infoText += "died<br>";
		infoText += "Energy: " + energy + "<br>";
		infoText += "Generation: " + generation + "<br>";
		infoText += "Age: " + age + "<br>";
		infoText += "Splits: " + splits + "<br>";
		infoText += "Speed: " + speed * 10000 + "<br>";
		infoText += "Direction: " + direction + "<br>";
		infoText += "</body></html>";
		return infoText;
	}
	
	/**
	 * Updates the creature.
	 */
	public void update() {
		if (outAttack.getValueBool()) {
			wantAttack = true;
		} else {
			wantAttack = false;
		}
		if (wantAttack) {
			if (nearestCreature != null) {
				nearestCreature.energy -= ATTACK_VALUE;
				if (geneticDifference >= 0.01f) {
					energy += ATTACK_VALUE / 2;
				} else {
					energy -= ATTACK_VALUE;
				}
				nearestCreature.isAttacked = true;
				attack = true;
			} else {
				attack = false;
			}
		} else {
			attack = false;
		}
		speed = outMove.getValue() * outMove.getValue() / 100;
		direction += outRotate.getValue();
		if (direction > 360)
			direction -= 360;
		if (direction < 0)
			direction += 360;
		if (outEat.getValueBool()) {
			if (xTile >= 0 && xTile < 100 && yTile >= 0 && yTile < 100
					&& World.world[xTile][yTile].getType() == TileType.land) {
				energy += World.world[xTile][yTile].letEat();
			}
		}
		if (outSplit.getValueBool() && energy > 300 && age >= matureAge) {
			split();
		}
		
		x += Math.sin(Math.toRadians(direction)) * speed;
		y += Math.cos(Math.toRadians(direction)) * speed;
		calculateFeelerPos();
		xTile = (int) (x * 100);
		yTile = (int) (y * 100);
		xTileFeelerRight = (int) (xFeelerRight * 100);
		yTileFeelerRight = (int) (yFeelerRight * 100);
		xTileFeelerLeft = (int) (xFeelerLeft * 100);
		yTileFeelerLeft = (int) (yFeelerLeft * 100);
		
		calculateCosts();
		
		if (energy < 100) {
			alive = false;
			SimThread.creatures.remove(this);
		}
		
		age = Main.year - yearBorn;
		
		isAttacked = false;
		
		updateInputs();
		motherBrain.invalidate();
	}
	
	/**
	 * Updates the inputs of the neural network.
	 */
	public void updateInputs() {
		boolean onLand = xTile >= 0 && xTile < 100 && yTile >= 0 && yTile < 100 
				&& World.world[xTile][yTile].getType() == TileType.land;
		inOnLand.setValue(onLand);
		if (onLand) {
			inFood.setValue(World.world[xTile][yTile].getFood());
		} else {
			inFood.setValue(0);
		}
		
		boolean rightFeelerOnLand = xTileFeelerRight >= 0 && xTileFeelerRight < 100 && yTileFeelerRight >= 0 && yTileFeelerRight < 100 
				&& World.world[xTileFeelerRight][yTileFeelerRight].getType() == TileType.land;
		inRightFeelerOnLand.setValue(rightFeelerOnLand);
		if (rightFeelerOnLand) {
			inRightFeelerFood.setValue(World.world[xTileFeelerRight][yTileFeelerRight].getFood());
		} else {
			inRightFeelerFood.setValue(0);
		}
		
		boolean leftFeelerOnLand = xTileFeelerLeft >= 0 && xTileFeelerLeft < 100 && yTileFeelerLeft >= 0 && yTileFeelerLeft < 100 
				&& World.world[xTileFeelerLeft][yTileFeelerLeft].getType() == TileType.land;
		inLeftFeelerOnLand.setValue(leftFeelerOnLand);
		if (leftFeelerOnLand) {
			inLeftFeelerFood.setValue(World.world[xTileFeelerLeft][yTileFeelerLeft].getFood());
		} else {
			inLeftFeelerFood.setValue(0);
		}
		
		inIsAttacked.setValue(isAttacked);
		
		inEnergy.setValue((energy-100) / 200);
		inAge.setValue(age);
		inMemory1.setValue(outMemory1.getValue());
		inMemory2.setValue(outMemory2.getValue());
		calculateGeneticDifference();
		inGeneticDifference.setValue(geneticDifference);
	}
	
	/**
	 * Calculates the costs and subtracts is from the energy.
	 */
	private void calculateCosts() {
		energy -= speed * speed * COST_MULTIPLIER;
		energy -= Math.pow(outRotate.getValue(), 2) * COST_MULTIPLIER * 100;
		energy -= outEat.getValue() * outEat.getValue() * COST_MULTIPLIER;
		if (wantAttack)
			energy -= 1 * COST_MULTIPLIER;
		energy -= age / 20;
		if (!(xTile >= 0 && xTile < World.world.length && yTile >= 0 && yTile < World.world[0].length && World.world[xTile][yTile].getType() == TileType.land)) {
			energy -= 4;
		}
	}
	
	/**
	 * Creates a new creature with {@code this} as mother and adds it to the list of creatures.
	 * The neural network is mutated with {@code (1 / (generation + 5)) * 5 + 0.1f}.
	 */
	private void split() {
		Creature childCreature = new Creature(this, 150);
		energy -= 150;
		childCreature.getBrain().mutate();
		SimThread.creatures.add(childCreature);
		childCreature = null;
		splits++;
	}
	
	/**
	 * Adds all neurons to the neural network and connects them.
	 * @param randomizeWeights whether the weights are randomized
	 */
	private void generateBrain(boolean randomizeWeights) {
		motherBrain.addInputNeuron(inEnergy);
		motherBrain.addInputNeuron(inFood);
		motherBrain.addInputNeuron(inOnLand);
		motherBrain.addInputNeuron(inRightFeelerOnLand);
		motherBrain.addInputNeuron(inLeftFeelerOnLand);
		motherBrain.addInputNeuron(inRightFeelerFood);
		motherBrain.addInputNeuron(inLeftFeelerFood);
		motherBrain.addInputNeuron(inAge);
		motherBrain.addInputNeuron(inGeneticDifference);
		motherBrain.addInputNeuron(inIsAttacked);
		motherBrain.addInputNeuron(inMemory1);
		motherBrain.addInputNeuron(inMemory2);
		
		motherBrain.generateHiddenNeurons(10);
		
		motherBrain.addOutputNeuron(outEat);
		motherBrain.addOutputNeuron(outMove);
		motherBrain.addOutputNeuron(outRotate);
		motherBrain.addOutputNeuron(outSplit);
		motherBrain.addOutputNeuron(outAttack);
		motherBrain.addOutputNeuron(outMemory1);
		motherBrain.addOutputNeuron(outMemory2);
		
		motherBrain.generateFullMesh();
		
		if (randomizeWeights) motherBrain.randomizeAllWeights();
	}
	
	/**
	 * Calculates the position of the feelers.
	 */
	private void calculateFeelerPos() {
		xFeelerRight = x + Math.sin(Math.toRadians(direction + 15)) * feelerDistance;
		yFeelerRight = y + Math.cos(Math.toRadians(direction + 15)) * feelerDistance;
		xFeelerLeft = x + Math.sin(Math.toRadians(direction - 15)) * feelerDistance;
		yFeelerLeft = y + Math.cos(Math.toRadians(direction - 15)) * feelerDistance;
	}
	
	/**
	 * Saves this creature.
	 * @return the save data
	 */
	public String save() {
		String data = "";
		data += x + "," + y + "," + direction + "," + energy + "," + yearBorn + "," + generation + ","
		+ color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + matureAge + "," + motherBrain.save();
		return data;
	}
	
	/**
	 * Loads a creature with the given data.
	 * @param data the data to load the creature
	 * @throws Exception if the file is invalid
	 */
	public void load(String data) throws Exception {
		String[] database = data.split(",");
		if (database.length != 11) {
			throw new Exception("The save file is invalid!");
		}
		x = Float.parseFloat(database[0]);
		y = Float.parseFloat(database[1]);
		direction = Float.parseFloat(database[2]);
		energy = Float.parseFloat(database[3]);
		yearBorn = Float.parseFloat(database[4]);
		generation = Integer.parseInt(database[5]);
		color = new Color(Integer.parseInt(database[6]), Integer.parseInt(database[7]), Integer.parseInt(database[8]));
		matureAge = Float.parseFloat(database[9]);
		motherBrain.load(database[10]);
	}
	
	/**
	 * Calculates the genetic difference of {@code this} and the nearest creature by
	 * calculate the distance between the colors.
	 */
	private void calculateGeneticDifference() {
		if (nearestCreature != null) {
			float rDifference = nearestCreature.getColor().getRed() - color.getRed();
			if (rDifference < 0)
				rDifference = -rDifference;
			float gDifference = nearestCreature.getColor().getGreen() - color.getGreen();
			if (gDifference < 0)
				gDifference = -gDifference;
			float bDifference = nearestCreature.getColor().getBlue() - color.getBlue();
			if (bDifference < 0)
				bDifference = -bDifference;
			geneticDifference = (rDifference + gDifference + bDifference) / 3;
		} else {
			geneticDifference = -1;
		}
	}
}
