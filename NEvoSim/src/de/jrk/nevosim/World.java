package de.jrk.nevosim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import de.jrk.nevosim.Tile.TileType;

/**
 * The World.
 * Contains a 100*100 Array of Tiles.
 * @author Jonas Keller
 */
public class World {
	
	public static Tile[][] world;
	private ValueNoise noise;
	
	public World() {
		noise = new ValueNoise();
		generateEmptyWorld();
	}
	
	/**
	 * Draws the World on the given SpriteBatch.
	 * @param batch SpriteBatch to draw the World on
	 */
	public void draw(Graphics gr) {
		BufferedImage img = new BufferedImage(world.length, world[0].length, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.createGraphics();
		for (int x = 0; x < world.length; x++) {
			for (int y = 0; y < world[0].length; y++) {
				if (world[x][y].getType() == TileType.water) {
					g.setColor(Color.BLUE);
				} else {
					g.setColor(new Color(1 - (world[x][y].getFood()) / 101, 1, 0));
				}
				g.drawRect(x, y, 1, 1);
			}
		}
		gr.drawImage(img, 0, 0, (int)Renderer.size, (int)Renderer.size, null);
	}
	
	/**
	 * Updates the World.
	 */
	public void update() {
		grow();
	}
	
	/**
	 * Lets grow grass.
	 */
	private void grow() {
		for (int x = 0; x < world.length; x++) {
			for (int y = 0; y < world[0].length; y++) {
				if (world[x][y].getType() == Tile.TileType.land && world[x][y].getFood() <= 100) {
					boolean canGrow = false;
					if (y - 1 >= 0) {
						if (world[x][y - 1].getType() == TileType.water 
								|| world[x][y - 1].getFood() > 90) {
							canGrow = true;
						}
					}
					
					if (!canGrow && x + 1 < world.length) {
						if (world[x + 1][y].getType() == TileType.water
								|| world[x + 1][y].getFood() > 90) {
							canGrow = true;
						}
					}
					
					if (!canGrow && y + 1 < world[0].length) {
						if (world[x][y + 1].getType() == TileType.water 
								|| world[x][y + 1].getFood() > 90) {
							canGrow = true;
						}
					}
					
					if (!canGrow && x - 1 >= 0) {
						if (world[x - 1][y].getType() == TileType.water 
								|| world[x - 1][y].getFood() > 90) {
							canGrow = true;
						}
					}
					
					
					if (canGrow) {
						world[x][y].grow((float) (Math.random() * 0.2f));
					}
				}
			}
		}
	}
	
	/**
	 * Generates a random World.
	 */
	public void generateRandomWorld() {
		noise.generateNoise();
		world = new Tile[100][100];
		for (int x = 0; x < world.length; x++) {
			for (int y = 0; y < world[0].length; y++) {
				if (x == 0 || y == 0 || x == world.length - 1 || y == world[0].length - 1) {
					world[x][y] = new Tile(TileType.water);
				} else if (noise.world[x][y] < 0) {
					world[x][y] = new Tile(Tile.TileType.land);
				} else {
					world[x][y] = new Tile(Tile.TileType.water);
				}
			}
		}
	}
	
	/**
	 * Generates an empty World.
	 */
	public void generateEmptyWorld() {
		world = new Tile[100][100];
		for (int x = 0; x < world.length; x++) {
			for (int y = 0; y < world[0].length; y++) {
				world[x][y] = new Tile(TileType.water);
			}
		}
	}
	
	/**
	 * Saves the World.
	 * @return The save data
	 */
	public String save() {
		String data = "";
		for (int x = 0; x < world.length; x++) {
			for (int y = 0; y < world[0].length; y++) {
				data = data + world[x][y].getFood() + ",";
			}
			data += "\n";
		}
		return data;
	}
	
	/**
	 * Loads the World with the given data.
	 * @param data The data to load the World
	 */
	public void load(String data) {
		String[] database = data.split(",");
		int i = 0;
		for (int x = 0; x < world.length; x++) {
			for (int y = 0; y < world[0].length; y++) {
				float food = Float.parseFloat(database[i]);
				if (food == -1.0) {
					world[x][y] = new Tile(TileType.water);
				} else {
					world[x][y] = new Tile(TileType.land, food);
				}
				i++;
			}
		}
	}
}
