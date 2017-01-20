package de.jrk.nevosim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * The overlay shows some information about the simulation.
 * @author Jonas Keller
 *
 */
public class Overlay implements Disposable {
	
	private Texture background;
	private Texture nightOverlay;
	private BitmapFont font;
	private String text;
	private int targetSps;
	private int backgroundHeight = 150;
	private int backgroundWidth = 300;
	
	public Overlay() {
		createBackground();
		font = new BitmapFont(Gdx.files.internal("assets/fonts/font.fnt"));
		font.setColor(Color.WHITE);
		font.getData().setScale(0.2f);
		generateNightOverlay();
	}
	
	/**
	 * Generates the overlay for the night.
	 */
	private void generateNightOverlay() {
		Pixmap map = new Pixmap(1, 1, Format.RGBA8888);
		map.setColor(new Color(0.3f, 0.3f, 0.3f, 0.7f));
		map.drawPixel(0, 0);
		nightOverlay = new Texture(map);
		map.dispose();
	}
	
	public void drawNightOverlay(SpriteBatch batch) {
		if (!World.day) {
			batch.draw(nightOverlay, -500 + NEvoSim.x, -500 + NEvoSim.y, 1000, 1000);
		}
	}
	
	/**
	 * Draws the overlay on the given SpriteBatch.
	 * @param batch the batch on witch is to be drawn
	 */
	public void draw(SpriteBatch batch) {
		updateText();
		batch.draw(background, -NEvoSim.width/2, NEvoSim.height/2 - backgroundHeight, backgroundWidth, backgroundHeight);
		font.draw(batch, text, -NEvoSim.width/2 + 10, NEvoSim.height/2 - 10);
	}
	
	/**
	 * Updates the information text.
	 */
	private void updateText() {
		calculateTargetSps();
		String state;
		String attIndi;
		if (NEvoSim.pause) {
			state = "paused";
		} else if (NEvoSim.fastForward) {
			state = "running (fast)";
		} else {
			state = "running";
		}
		if (NEvoSim.showAttackIndicator) {
			attIndi = "On";
		} else {
			attIndi = "Off";
		}
		text = "";
		text += "State: " + state + "\n";
		text += "Target Sps: " + targetSps + "\n";
		text += "Sps: " + SimThread.stepsPerSecond + "\n";
		text += "Attack Indicators: " + attIndi + "\n";
		text += "Fps: " + Gdx.graphics.getFramesPerSecond() + "\n";
		text += "Creatures: " + SimThread.creatures.size() + "\n";
		text += "Year: " + Math.round(NEvoSim.year) + "\n";
		
	}
	
	/**
	 * Calculates the target Sps (Steps per second) with the target frame duration.
	 */
	private void calculateTargetSps() {
		targetSps = (int) (1f / (NEvoSim.targetFrameDuration / 1000f));
		if (targetSps >= 30) {
			targetSps /= 10;
			targetSps *= 10;
		}
	}

	/**
	 * Creates a transparent background. 
	 */
	private void createBackground() {
		Pixmap map = new Pixmap(1, 1, Format.RGBA8888);
		map.setColor(0.2f, 0.2f, 0.2f, 0.7f);
		map.fill();
		background = new Texture(map);
		map.dispose();
	}
	
	public void dispose() {
		background.dispose();
		font.dispose();
	}
}
