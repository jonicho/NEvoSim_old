package de.jrk.nevosim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.jrk.nevosim.Tile.TileType;

/**
 * The World.
 * Contains a 100*100 Array of Tiles.
 * @author jonas
 */
public class World {
	
	public static Tile[][] world;
	private ValueNoise noise;
	private Pixmap pixmap;
	private Texture texture;
	
	public World() {
		noise = new ValueNoise();
		generateEmptyWorld();
	}
	
	/**
	 * Draws the World on the given SpriteBatch.
	 * @param batch SpriteBatch to draw the World on
	 */
	public void draw(SpriteBatch batch) {
		pixmap.dispose();
		texture.dispose();
		pixmap = new Pixmap(world.length, world[0].length, Format.RGBA8888);
		for (int x = 0; x < world.length; x++) {
			for (int y = 0; y < world[0].length; y++) {
				if (world[x][y].getType() == TileType.water) {
					pixmap.setColor(Color.BLUE);
				} else {
					pixmap.setColor(1 - (world[x][y].getFood()) / 101, 1, 0, 1);
				}
				pixmap.drawPixel(x, -y + 99);
			}
		}
		texture = new Texture(pixmap);
		batch.draw(texture, -500 + NEvoSim.x, -500 + NEvoSim.y, 1000, 1000);
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
					boolean canGrowFast = false;
					if (y - 1 >= 0) {
						if (world[x][y - 1].getType() == Tile.TileType.water) {
							canGrowFast = true;
						} else if (world[x][y - 1].getType() == Tile.TileType.land 
								&& world[x][y - 1].getFood() > 90) {
							canGrow = true;
						}
					}  else {
						canGrowFast = true;
					}
					
					if (x + 1 < world.length) {
						if (world[x + 1][y].getType() == Tile.TileType.water) {
							canGrowFast = true;
						} else if (world[x + 1][y].getType() == Tile.TileType.land 
								&& world[x + 1][y].getFood() > 90) {
							canGrow = true;
						}
					} else {
						canGrowFast = true;
					}
					
					if (y + 1 < world[0].length) {
						if (world[x][y + 1].getType() == Tile.TileType.water) {
							canGrowFast = true;
						} else if (world[x][y + 1].getType() == Tile.TileType.land 
								&& world[x][y + 1].getFood() > 90) {
							canGrow = true;
						}
					} else {
						canGrowFast = true;
					}
					
					if (x - 1 >= 0) {
						if (world[x - 1][y].getType() == Tile.TileType.water) {
							canGrowFast = true;
						} else if (world[x - 1][y].getType() == Tile.TileType.land 
								&& world[x - 1][y].getFood() > 90) {
							canGrow = true;
						}
					} else {
						canGrowFast = true;
					}
					
					
					if (canGrowFast) {
						world[x][y].grow((float) (Math.random() * 0.4f));
					} else
						if (canGrow) {
						world[x][y].grow((float) (Math.random() * 0.1f));
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
		pixmap = new Pixmap(world.length, world[0].length, Format.RGBA8888);
		texture = new Texture(pixmap);
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
