package de.jrk.nevosim;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class Tile {
	
	private TileType type;
	private float food;
	public final static float EAT_VALUE = 1;
	
	public Tile(Tile.TileType type) {
		this(type, 0);
	}
	
	public Tile(TileType type, float food) {
		this.type = type;
		this.food = food;
	}
	
	public void setType(Tile.TileType type) {
		this.type = type;
	}
	
	public TileType getType() {
		return type;
	}
	
	public float getFood() {
		if (type == TileType.water) {
			return -1;
		}
		return food;
	}
	
	public float letEat() {
		if (type == TileType.land) {
			if (food >= EAT_VALUE) {
				food -= EAT_VALUE;
				return EAT_VALUE;
			} else {
				return 0;
			} 
		} else {
			throw new GdxRuntimeException("Tile is not land!");
		}
		
	}
	
	public void grow(float amount) {
		if (food <= 100) {
			food += amount;
		}
		if (food > 100) food = 100;
	}
	
	public enum TileType{
		water,
		land
	}
}
