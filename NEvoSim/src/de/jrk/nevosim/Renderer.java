package de.jrk.nevosim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * NEvoSim simulates an evolution with creatures that have neural networks.
 * With each time a Creature splits, the weights of the neural network changes a bit.
 * In this way the creatures are getting more intelligent and can survive better.
 * @author Jonas Keller
 *
 */
public class Renderer {
	public static float x;
	public static float y;
	public static int width;
	public static int height;
	public static int size;
	public static boolean showAttackIndicator = true;
	public static BufferedImage attackIndicator;
	
	public Renderer() {
		attackIndicator = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		attackIndicator.getGraphics().fillOval(0, 0, 100, 100);
	}
	
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
	
	
//	/**
//	 * Calculates witch creature was clicked
//	 */
//	private void calculateClickedCreature() {
//		if (pause || true) {
//			double mouseX = 0;
//			double mouseY = 0;
//			mouseX = (input.mouseX - 500) * zoom + 500 - x;
//			mouseY = (input.mouseY - 500) * zoom + 500 - y;
//			if (siteDifference < 0) {
//				mouseY -= Math.abs(siteDifference/2) * zoom;
//			} else {
//				mouseX -= Math.abs(siteDifference/2) * zoom;
//			}
//			Creature c = null;
//			float distance = 12;
//			for (int i = 0; i < SimThread.creatures.size(); i++) {
//				Creature cr = SimThread.creatures.get(i);
//				double disX = mouseX - cr.getX();
//				double disY = mouseY - cr.getY();
//				double dis = (float) Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2));
//				if (dis < distance) {
//					c = cr;
//					distance = dis;
//				}
//			}
//			if (c != null) selectedCreature = c;
//		}
//	}
}
