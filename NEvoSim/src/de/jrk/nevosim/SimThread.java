package de.jrk.nevosim;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Thread to simulate the creatures and the world.
 * @author Jonas Keller
 *
 */
public class SimThread extends Thread {

	public static ArrayList<Creature> creatures = new ArrayList<Creature>();
	public static World world = new World();
	public boolean stop = false;
	public static boolean save = false;
	private SaveLoad saveLoad = new SaveLoad();
	public static boolean isStarted = false;
	public static long nanoFrameDuration;
	public static int stepsPerSecond;
	private ArrayList<Float> spss = new ArrayList<Float>();
	private long nanoTimeBegin;
	private long nanoTimeEnd;
	
	public SimThread() {
		super("SimThread");
	}
	
	@Override
	public void run() {
		saveLoad.load();
		isStarted = true;
		while (!stop) {
			long timeBegin = System.currentTimeMillis();
			nanoTimeBegin = System.nanoTime();
			
			if (!Main.pause) {
				world.update();
				if (creatures.size() > 0) {
					calculateNearestCreatures();
					for (int i = 0; i < creatures.size(); i++) {
						try {
							creatures.get(i).update();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				if (creatures.size() < 10 || Math.random() < 0.01) {
					creatures.add(new Creature(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1), 
							(float) Math.random(), (float) Math.random(), true));
				}

				Main.year += 0.0005f;
			}
			
			long timeEnd = System.currentTimeMillis();
			if ((!Main.fastForward || Main.pause) && Renderer.targetFrameDuration - (timeEnd - timeBegin) > 0) {
				try {
					Thread.sleep(Renderer.targetFrameDuration - (timeEnd - timeBegin));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			nanoTimeEnd = System.nanoTime();
			
			calculateSps();
			
			if (Main.save) {
				saveLoad.save(false);
				Main.save = !Main.save;
			}
			if (save) {
				saveLoad.save(true);
				stop = true;
			}
		}
	}
	
	/**
	 * Calculate the nearest creature of each creature and gives it to the creature.
	 */
	private void calculateNearestCreatures() {
		for (int i = 0; i < creatures.size(); i++) {
			creatures.get(i).setNearestCreature(null);
			creatures.get(i).setNearestCreatureDistance(Float.MAX_VALUE);
		}
		
		for (int i = 0; i < creatures.size(); i++) {
			for (int j = 0; j < creatures.size(); j++) {
				
				if (i != j) {
					Creature creatureA = creatures.get(i);
					Creature creatureB = creatures.get(j);
					
					double disX = creatureA.getX() - creatureB.getX();
					double disY = creatureA.getY() - creatureB.getY();
					
					if (disX < 30 && disY < 30) {
						double rightFeelerDisX = creatureA.getxFeelerRight() - creatureB.getX();
						double rightFeelerDisY = creatureA.getyFeelerRight() - creatureB.getY();
						double rightFeelerDis = (float) Math.sqrt(Math.pow(rightFeelerDisX, 2) + Math.pow(rightFeelerDisY, 2));

						double leftFeelerDisX = creatureA.getxFeelerLeft() - creatureB.getX();
						double leftFeelerDisY = creatureA.getyFeelerLeft() - creatureB.getY();
						double leftFeelerDis = (float) Math.sqrt(Math.pow(leftFeelerDisX, 2) + Math.pow(leftFeelerDisY, 2));

						if (rightFeelerDis < 4 && creatureA.getNearestCreatureDistance() > rightFeelerDis) {
							creatureA.setNearestCreature(creatureB);
							creatureA.setNearestCreatureDistance(rightFeelerDis);
						}

						if (leftFeelerDis < 4 && creatureA.getNearestCreatureDistance() > leftFeelerDis) {
							creatureA.setNearestCreature(creatureB);
							creatureA.setNearestCreatureDistance(leftFeelerDis);
						}
					} 
				}
			}
		}
	}
	
	/**
	 * Calculates the Sps (Steps per second).
	 */
	private void calculateSps() {
		nanoFrameDuration = nanoTimeEnd - nanoTimeBegin;
		float sps = 0;
		if (!Main.pause) {
			if (nanoFrameDuration != 0) {
				sps = 1f / ((float) nanoFrameDuration / 1000000000);
			}
			spss.add(sps);
			
			while (spss.size() > sps + 1) {
				spss.remove(0);
			}
			
			int x = 0;
			for (int i = 0; i < spss.size(); i++) {
				x += spss.get(i);
			}
			stepsPerSecond = Math.round(x) / spss.size();
		}
	}
}
