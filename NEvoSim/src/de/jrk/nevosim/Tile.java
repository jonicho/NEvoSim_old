package de.jrk.nevosim;

/**
 * A World Tile.
 * @author Jonas Keller
 *
 */
public class Tile {
	
	private TileType type;
	private double food;
	public final static double EAT_VALUE = 4f;
	
	/**
	 * Generates a new Tile with the given type.
	 * @param type Whether the Tile is sea (water) or land
	 */
	public Tile(Tile.TileType type) {
		this(type, 0);
	}
	
	/**
	 * Generates a new Tile with the given type and food value.
	 * @param type Whether the Tile is sea (water) or land
	 * @param food How much food the Tile have
	 */
	public Tile(TileType type, double food) {
		this.type = type;
		this.food = food;
	}
	
	public void setType(Tile.TileType type) {
		this.type = type;
	}
	
	public TileType getType() {
		return type;
	}
	
	/**
	 * Returns the food value, if the tile is land.
	 * Returns {@code -1.0} if the tile is water.
	 * @return The food value
	 */
	public double getFood() {
		if (type == TileType.water) {
			return -1;
		}
		return food;
	}
	
	/**
	 * Reduces the food value by {@code EAT_VALUE} and returns {@code EAT_VALUE}.
	 * Returns {@code 0} if the food value is fewer than {@code EAT_VALUE}.
	 * @return The eat value
	 */
	public double letEat() {
		if (type == TileType.land) {
			if (food >= EAT_VALUE) {
				food -= EAT_VALUE;
				return EAT_VALUE;
			}
		}
		return 0;
		
	}
	
	/**
	 * Increases the food value by the given amount.
	 * If the food value is more than {@code 100} it sets the food value to {@code 100}.
	 * @param amount Amount to increase the food value
	 */
	public void grow(double amount) {
		if (food <= 100) {
			food += amount;
		}
		if (food > 100) food = 100;
	}
	
	/**
	 * The type of a tile.
	 * @author Jonas Keller
	 *
	 */
	public enum TileType{
		water,
		land
	}
}
