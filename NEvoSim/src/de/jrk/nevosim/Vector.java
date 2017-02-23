package de.jrk.nevosim;

public class Vector {
	private int x;
	private int y;
	
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public static int getDistance(Vector vec1, Vector vec2) {
		return (int) Math.round(Math.sqrt(Math.pow((double)vec1.getX() - (double)vec2.getX(), 2) + Math.pow((double)vec1.getY() - (double)vec2.getY(), 2)));
	}
	
	@Override
	public String toString() {
		return x + "; " + y;
	}
}
