package de.jrk.nevosim;

/**
 * The overlay shows some information about the simulation.
 * @author Jonas Keller
 *
 */
public class Overlay {
	
	private String text;
	private int targetSps;
	private int backgroundHeight = 150;
	private int backgroundWidth = 300;
	
	private int creatureInfoWidth = 500;
	private int creatureInfoHeight = 600;
	private int creatureNetworkHeight = (int) (creatureInfoHeight * 0.8);
	
	public Overlay() {
	}
	
// TODO re-implement draw methods
//	/**
//	 * Draws the overlay on the given SpriteBatch.
//	 * @param batch the batch on witch is to be drawn
//	 */
//	public void draw(SpriteBatch batch) {
//		updateText();
//		batch.draw(background, -NEvoSim.width/2, NEvoSim.height/2 - backgroundHeight, backgroundWidth, backgroundHeight);
//		font.draw(batch, text, -NEvoSim.width/2 + 10, NEvoSim.height/2 - 10);
//		
//		if (NEvoSim.selectedCreature != null) {
//			creatureInfoMap = new Pixmap(creatureInfoWidth, creatureInfoHeight, Format.RGBA8888);
//			creatureInfoMap.setColor(0.2f, 0.2f, 0.2f, 0.7f);
//			creatureInfoMap.fill();
//			String infoText = NEvoSim.selectedCreature.drawInfo(creatureInfoMap, creatureNetworkHeight);
//			creatureInfoTex.dispose();
//			creatureInfoTex = new Texture(creatureInfoMap);
//			batch.draw(creatureInfoTex, NEvoSim.width/2 - creatureInfoWidth, NEvoSim.height/2 - creatureInfoHeight);
//			creatureInfoMap.dispose();
//			font.draw(batch, infoText, NEvoSim.width/2 - creatureInfoWidth, NEvoSim.height/2 - creatureNetworkHeight);
//		}
//	}
	
	/**
	 * Updates the information text.
	 */
	private void updateText() {
		calculateTargetSps();
		String state;
		String attIndi;
		if (Main.pause) {
			state = "paused";
		} else if (Main.fastForward) {
			state = "running (fast)";
		} else {
			state = "running";
		}
		if (Renderer.showAttackIndicator) {
			attIndi = "On";
		} else {
			attIndi = "Off";
		}
		text = "";
		text += "State: " + state + "\n";
		text += "Target Sps: " + targetSps + "\n";
		text += "Sps: " + SimThread.stepsPerSecond + "\n";
		text += "Attack Indicators: " + attIndi + "\n";
		text += "Creatures: " + SimThread.creatures.size() + "\n";
		text += "Year: " + Math.round(Main.year) + "\n";
		
	}
	
	/**
	 * Calculates the target Sps (Steps per second) with the target frame duration.
	 */
	private void calculateTargetSps() {
		targetSps = (int) (1f / (Renderer.targetFrameDuration / 1000f));
		if (targetSps >= 30) {
			targetSps /= 10;
			targetSps *= 10;
		}
	}
}
