package de.jrk.nevosim;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Select;

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
public class Creature implements Disposable {
	
	private float x;
	private float y;
	private float xFeelerRight;
	private float yFeelerRight;
	private float xFeelerLeft;
	private float yFeelerLeft;
	
	private int xTile;
	private int yTile;
	private int xTileFeelerRight;
	private int yTileFeelerRight;
	private int xTileFeelerLeft;
	private int yTileFeelerLeft;
	
	private float speed;
	private float feelerDistance;
	private float size;
	private float direction;
	
	private float energy;
	private float yearBorn;
	private float age;
	private float matureAge;
	private int generation;

	private boolean wantAttack;
	private boolean attack;
	private boolean isAttacked;
	private int splits;

	private Creature nearestCreature;
	private float nearestCreatureDistance;
	private float geneticDifference;
	
	private Color color;
	private Texture texture;
	private Texture wantAttackTexture;
	private Texture attackTexture;
	private Texture attackedTexture;
	
	private NeuralNetwork brain;
	
	private boolean textureCreated = false;
	private boolean canDispose = false;
	private boolean alive = true;
	private boolean selected;
	
	
	private static final float COST_MULTIPLIER = 0.05f;
	private static final float ATTACK_VALUE = 100;
	private static final float BODY_SIZE = 6;
	private static final float FEELER_SIZE = 2;
	
	
	public static final String IN_ONLAND = "in_on-land";
	public static final String IN_RIGHTFEELERONLAND = "in_right-feeler-on-land";
	public static final String IN_LEFTFEELERONLAND = "in_left-feeler-on-land";
	public static final String IN_FOOD = "in_food";
	public static final String IN_ENERGY = "in_energy";
	public static final String IN_RIGHTFEELERFOOD = "in_right-feeler-food";
	public static final String IN_LEFTFEELERFOOD = "in_left-feeler-food";
	public static final String IN_AGE = "in_age";
	public static final String IN_MEMORY1 = "in_memory-1";
	public static final String IN_MEMORY2 = "in_memory-2";
	public static final String IN_OSCILLATION = "in_oscillation";
	public static final String IN_GENETICDIFFERENCE = "in_geneticdifference";
	public static final String IN_ISATTACKED = "in_is-attacked";

	public static final String OUT_ROTATE = "out_rotate";
	public static final String OUT_EAT = "out_eat";
	public static final String OUT_MOVE = "out_speed";
	public static final String OUT_SPLIT = "out_split";
	public static final String OUT_MEMORY1 = "out_memory-1";
	public static final String OUT_MEMORY2 = "out_memory-2";
	public static final String OUT_OSCILLATION = "out_oscillation";
	public static final String OUT_ATTACK = "out_attack";
	
	private InputNeuron inRightFeelerOnLand = new InputNeuron(IN_RIGHTFEELERONLAND);
	private InputNeuron inLeftFeelerOnLand = new InputNeuron(IN_LEFTFEELERONLAND);
	private InputNeuron inOnLand = new InputNeuron(IN_ONLAND);
	private InputNeuron inFood = new InputNeuron(IN_FOOD);
	private InputNeuron inEnergy = new InputNeuron(IN_ENERGY);
	private InputNeuron inRightFeelerFood = new InputNeuron(IN_RIGHTFEELERFOOD);
	private InputNeuron inLeftFeelerFood = new InputNeuron(IN_LEFTFEELERFOOD);
	private InputNeuron inAge = new InputNeuron(IN_AGE);
	private InputNeuron inMemory1 = new InputNeuron(IN_MEMORY1);
	private InputNeuron inMemory2 = new InputNeuron(IN_MEMORY2);
	private InputNeuron inOscillation = new InputNeuron(IN_OSCILLATION);
	private InputNeuron inGeneticDifference = new InputNeuron(IN_GENETICDIFFERENCE);
	private InputNeuron inIsAttacked = new InputNeuron(IN_ISATTACKED);
	
	private WorkingNeuron outRotate = new WorkingNeuron(OUT_ROTATE, false);
	private WorkingNeuron outEat = new WorkingNeuron(OUT_EAT, false);
	private WorkingNeuron outMove = new WorkingNeuron(OUT_MOVE, false);
	private WorkingNeuron outSplit = new WorkingNeuron(OUT_SPLIT, false);
	private WorkingNeuron outMemory1 = new WorkingNeuron(OUT_MEMORY1, false);
	private WorkingNeuron outMemory2 = new WorkingNeuron(OUT_MEMORY2, false);
	private WorkingNeuron outOscillation = new WorkingNeuron(IN_OSCILLATION, false);
	private WorkingNeuron outAttack = new WorkingNeuron(OUT_ATTACK, false);
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getxFeelerRight() {
		return xFeelerRight;
	}

	public float getyFeelerRight() {
		return yFeelerRight;
	}

	public float getxFeelerLeft() {
		return xFeelerLeft;
	}

	public float getyFeelerLeft() {
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

	public float getNearestCreatureDistance() {
		return nearestCreatureDistance;
	}

	public void setNearestCreatureDistance(float nearestCreatureDistance) {
		this.nearestCreatureDistance = nearestCreatureDistance;
	}

	public NeuralNetwork getBrain() {
		return brain;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public float getMatureAge() {
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
	public Creature(Color color, float x, float y, boolean generateBrain) {
		yearBorn = NEvoSim.year;
		this.color = color;
		this.x = x;
		this.y = y;
		feelerDistance = 10;
		energy = 150;
		age = 0;
		direction = (float) (Math.random() * 360);
		calculateFeelerPos();
		
		brain = new NeuralNetwork();
		if (generateBrain) generateBrain(true);
		
		matureAge = (float)Math.random() * 1 + 0.2f;
	}
	
	/**
	 * Generates a new creature that inherits from the given mother.
	 * The complete neural network will be copied and then mutated.
	 * @param motherCreature the mother of the new creature
	 * @param energy
	 */
	public Creature(Creature motherCreature, float energy) {
		this(new Color(motherCreature.getColor().r + (float)(Math.random() - 0.5) / 10, 
					   motherCreature.getColor().g + (float)(Math.random() - 0.5) / 10, 
					   motherCreature.getColor().b + (float)(Math.random() - 0.5) / 10, 1), 
					   motherCreature.getX(), motherCreature.getY(), false);
		
		matureAge = motherCreature.getMatureAge() + (float)Math.random() * 0.1f - 0.05f;
		if (matureAge < 0.2f) {
			matureAge = 0.2f;
		}
		generation = motherCreature.getGeneration() + 1;
		brain = motherCreature.getBrain().getClonedNetwork();
		this.energy = energy;
		
		inRightFeelerOnLand = brain.getInputNeuron(IN_RIGHTFEELERONLAND);
		inLeftFeelerOnLand = brain.getInputNeuron(IN_LEFTFEELERONLAND);
		inOnLand = brain.getInputNeuron(IN_ONLAND);
		inFood = brain.getInputNeuron(IN_FOOD);
		inEnergy = brain.getInputNeuron(IN_ENERGY);
		inRightFeelerFood = brain.getInputNeuron(IN_RIGHTFEELERFOOD);
		inLeftFeelerFood = brain.getInputNeuron(IN_LEFTFEELERFOOD);
		inAge = brain.getInputNeuron(IN_AGE);
		inMemory1 = brain.getInputNeuron(IN_MEMORY1);
		inMemory2 = brain.getInputNeuron(IN_MEMORY2);
		inGeneticDifference = brain.getInputNeuron(IN_GENETICDIFFERENCE);
		inIsAttacked = brain.getInputNeuron(IN_ISATTACKED);
		
		outRotate = brain.getOutputNeuron(OUT_ROTATE);
		outEat = brain.getOutputNeuron(OUT_EAT);
		outMove = brain.getOutputNeuron(OUT_MOVE);
		outSplit = brain.getOutputNeuron(OUT_SPLIT);
		outMemory1 = brain.getOutputNeuron(OUT_MEMORY1);
		outMemory2 = brain.getOutputNeuron(OUT_MEMORY2);
		outAttack = brain.getOutputNeuron(OUT_ATTACK);
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
	}
	
	/**
	 * Draws the creature on the given SpriteBatch.
	 * @param batch the batch on witch is to be drawn
	 */
	public void draw(SpriteBatch batch) {
		selected = this.equals(NEvoSim.selectedCreature);
		
		size = ((energy - 80) / (1 + Math.abs(energy / 100))) / 50;
		
		if (!textureCreated) {
			createTextures();
		}
		
		if (NEvoSim.showAttackIndicator) {
			if (isAttacked) {
				draw(attackedTexture, batch);
			} else if (attack) {
				draw(attackTexture, batch);
			} else if (wantAttack) {
				draw(wantAttackTexture, batch);
			} else {
				draw(texture, batch);
			} 
		} else {
			draw(texture, batch);
		}
	}
	
	/**
	 * Draws the creature on the given SpriteBatch with the given texture.
	 * @param texture the texture
	 * @param batch the batch on witch is to be drawn
	 */
	private void draw(Texture texture, SpriteBatch batch) {
		batch.draw(texture, x - 500f - BODY_SIZE * size/2 + NEvoSim.x, 
							y - 500f - BODY_SIZE * size/2 + NEvoSim.y, 
							BODY_SIZE * size, BODY_SIZE * size);
		
		batch.draw(texture, xFeelerRight - 500f - FEELER_SIZE/2f + NEvoSim.x, 
							yFeelerRight - 500f - FEELER_SIZE/2f + NEvoSim.y, 
							FEELER_SIZE, FEELER_SIZE);
		
		batch.draw(texture, xFeelerLeft - 500f - FEELER_SIZE/2f + NEvoSim.x, 
							yFeelerLeft - 500f - FEELER_SIZE/2f + NEvoSim.y, 
							FEELER_SIZE, FEELER_SIZE);
	}
	
	
	/**
	 * Draws the information of the creature in the given Pixmap;
	 * @param map the Pixmap on witch is to be drawn
	 * @param infoNetworkHeight the height of the neural network
	 * @return the info String
	 */
	public String drawInfo(Pixmap map, int infoNetworkHeight) {
		infoNetworkHeight = (int) (map.getHeight() * 0.8);
		brain.draw(map, 0, 0, map.getWidth(), infoNetworkHeight);
		return createInfoText();
	}
	
	private String createInfoText() {
		String infoText = "";
		infoText += " State: ";
		if (alive) infoText += "alive\n";
		else infoText += "died\n";
		infoText += " Energy: " + energy + "\n";
		infoText += " Generation: " + generation + "\n";
		infoText += " Age: " + age + "\n";
		infoText += " Speed: " + speed + "\n";
		infoText += " Direction: " + direction + "\n";
		return infoText;
	}
	
	/**
	 * Creates the four textures.
	 */
	private void createTextures() {
		Pixmap pixmap;
		pixmap = new Pixmap(220, 220, Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		pixmap.fillCircle(110, 110, 110);
		pixmap.setColor(color);
		pixmap.fillCircle(110, 110, 100);
		texture = new Texture(pixmap);
		pixmap.dispose();

		pixmap = new Pixmap(220, 220, Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		pixmap.fillCircle(110, 110, 110);
		pixmap.setColor(color);
		pixmap.fillCircle(110, 110, 100);
		pixmap.setColor(Color.RED);
		pixmap.fillCircle(110, 110, 30);
		wantAttackTexture = new Texture(pixmap);
		pixmap.dispose();
		
		pixmap = new Pixmap(220, 220, Format.RGBA8888);
		pixmap.setColor(Color.BLACK);
		pixmap.fillCircle(110, 110, 110);
		pixmap.setColor(color);
		pixmap.fillCircle(110, 110, 100);
		pixmap.setColor(Color.RED);
		pixmap.fillCircle(110, 110, 80);
		attackTexture = new Texture(pixmap);
		pixmap.dispose();
		
		pixmap = new Pixmap(220, 220, Format.RGBA8888);
		pixmap.setColor(Color.RED);
		pixmap.fillCircle(110, 110, 110);
		attackedTexture = new Texture(pixmap);
		pixmap.dispose();
		
		textureCreated = true;
		canDispose = true;
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
		speed = outMove.getValue() * outMove.getValue();
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
		xTile = (int) (x / 10);
		yTile = (int) (y / 10);
		xTileFeelerRight = (int) (xFeelerRight / 10);
		yTileFeelerRight = (int) (yFeelerRight / 10);
		xTileFeelerLeft = (int) (xFeelerLeft / 10);
		yTileFeelerLeft = (int) (yFeelerLeft / 10);
		
		calculateCosts();
		
		if (energy < 100) {
			alive = false;
			NEvoSim.deadCreatures.add(this);
			SimThread.creatures.remove(this);
		}
		
		age = NEvoSim.year - yearBorn;
		
		isAttacked = false;
		
		updateInputs();
		brain.invalidate();
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
		
		inEnergy.setValue(energy/10);
		inAge.setValue(age);
		inMemory1.setValue(outMemory1.getValue());
		inMemory2.setValue(outMemory2.getValue());
		inOscillation.setValue((float) Math.sin(age * outOscillation.getValue() * 100) * 40);
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
		energy -= age / 10;
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
		brain.addInputNeuron(inEnergy);
		brain.addInputNeuron(inFood);
		brain.addInputNeuron(inOnLand);
		brain.addInputNeuron(inRightFeelerOnLand);
		brain.addInputNeuron(inLeftFeelerOnLand);
		brain.addInputNeuron(inRightFeelerFood);
		brain.addInputNeuron(inLeftFeelerFood);
		brain.addInputNeuron(inAge);
		brain.addInputNeuron(inMemory1);
		brain.addInputNeuron(inMemory2);
		brain.addInputNeuron(inOscillation);
		brain.addInputNeuron(inGeneticDifference);
		brain.addInputNeuron(inIsAttacked);
		
		brain.generateHiddenNeurons(10);
		
		brain.addOutputNeuron(outEat);
		brain.addOutputNeuron(outMove);
		brain.addOutputNeuron(outRotate);
		brain.addOutputNeuron(outSplit);
		brain.addOutputNeuron(outMemory1);
		brain.addOutputNeuron(outMemory2);
		brain.addOutputNeuron(outOscillation);
		brain.addOutputNeuron(outAttack);
		
		brain.generateFullMesh();
		
		if (randomizeWeights) brain.randomizeAllWeights();
	}
	
	/**
	 * Calculates the position of the feelers.
	 */
	private void calculateFeelerPos() {
		xFeelerRight = (float) (x + Math.sin(Math.toRadians(direction + 15)) * feelerDistance);
		yFeelerRight = (float) (y + Math.cos(Math.toRadians(direction + 15)) * feelerDistance);
		xFeelerLeft = (float) (x + Math.sin(Math.toRadians(direction - 15)) * feelerDistance);
		yFeelerLeft = (float) (y + Math.cos(Math.toRadians(direction - 15)) * feelerDistance);
	}
	
	public void dispose() {
		if (canDispose) {
			texture.dispose();
			wantAttackTexture.dispose();
			attackTexture.dispose();
			attackedTexture.dispose();
		}
	}
	
	/**
	 * Saves this creature.
	 * @return the save data
	 */
	public String save() {
		String data = "";
		data += x + "," + y + "," + direction + "," + energy + "," + yearBorn + "," 
		+ color.r + "," + color.g + "," + color.b + "," + matureAge + "," + brain.save();
		return data;
	}
	
	/**
	 * Loads a creature with the given data.
	 * @param data the data to load the creature
	 * @throws Exception if the file is invalid
	 */
	public void load(String data) throws Exception {
		String[] database = data.split(",");
		if (database.length != 10) {
			throw new Exception("The save file is invalid!");
		}
		x = Float.parseFloat(database[0]);
		y = Float.parseFloat(database[1]);
		direction = Float.parseFloat(database[2]);
		energy = Float.parseFloat(database[3]);
		yearBorn = Float.parseFloat(database[4]);
		color = new Color(Float.parseFloat(database[5]), Float.parseFloat(database[6]), Float.parseFloat(database[7]), 1);
		matureAge = Float.parseFloat(database[8]);
		brain.load(database[9]);
	}
	
	/**
	 * Calculates the genetic difference of {@code this} and the nearest creature by
	 * calculate the distance between the colors.
	 */
	private void calculateGeneticDifference() {
		if (nearestCreature != null) {
			float rDifference = nearestCreature.getColor().r - color.r;
			if (rDifference < 0)
				rDifference = -rDifference;
			float gDifference = nearestCreature.getColor().g - color.g;
			if (gDifference < 0)
				gDifference = -gDifference;
			float bDifference = nearestCreature.getColor().b - color.b;
			if (bDifference < 0)
				bDifference = -bDifference;
			geneticDifference = (rDifference + gDifference + bDifference) / 3;
		} else {
			geneticDifference = -1;
		}
	}
}
