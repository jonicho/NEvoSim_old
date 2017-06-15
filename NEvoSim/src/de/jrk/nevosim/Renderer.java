package de.jrk.nevosim;

import java.awt.Color;
import java.awt.Graphics;

/**
 * NEvoSim simulates an evolution with creatures that have neural networks.
 * With each time a Creature splits, the weights of the neural network changes a bit.
 * In this way the creatures are getting more intelligent and can survive better.
 * @author Jonas Keller
 *
 */
public class Renderer {
	public static double x;
	public static double y;
	public static int width;
	public static int height;
	public static int size;
	public static boolean showAttackIndicator = true;
	
	public void render(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, width, height);
		size = width < height ? width : height;
		if (SimThread.isStarted) {
			// draw the world and all Creatures
			SimThread.world.draw(g);
			for (int i = 0; i < SimThread.creatures.size(); i++) {
				try {
					SimThread.creatures.get(i).draw(g);
				} catch (Exception e) { // catch exception. Sometimes the Creature dies while it is tried to draw it
					System.err.println("Draw skipped");
					e.printStackTrace();
				}
			}
		}
	}
}
