package de.jrk.nevosim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class Overlay implements Disposable{
	
	private Texture overlay;
	private BitmapFont font;
	private String text;
	
	public Overlay() {
		drawOverlay();
		font = new BitmapFont(Gdx.files.internal("assets/fonts/font.fnt"));
		font.setColor(Color.WHITE);
		font.getData().setScale(0.2f);
	}
	
	public void draw(SpriteBatch batch) {
		updateText();
		batch.draw(overlay, -NEvoSim.width/2, NEvoSim.height/2 - overlay.getHeight());
		font.draw(batch, text, -NEvoSim.width/2 + 10, NEvoSim.height/2 - 10);
	}
	
	private void updateText() {
		text = "";
		text += "Sps (steps per second): " + SimThread.stepsPerSecond + "\n";
		text += "Creatures: " + SimThread.creatures.size() + "\n";
		text += "Year: " + Math.round(NEvoSim.year) + "\n";
		text += "Fps: " + Gdx.graphics.getFramesPerSecond() + "\n";
		String state;
		if (NEvoSim.pause) {
			state = "paused";
		} else if (NEvoSim.fastForward) {
			state = "running (fast)";
		} else {
			state = "running";
		}
		text += "State: " + state + "\n";
		
	}
	
	private void drawOverlay() {
		Pixmap map = new Pixmap(400, 120, Format.RGBA8888);
		map.setColor(0.2f, 0.2f, 0.2f, 0.7f);
		map.fill();
		overlay = new Texture(map);
		map.dispose();
	}
	
	public void dispose() {
		overlay.dispose();
		font.dispose();
	}
}
