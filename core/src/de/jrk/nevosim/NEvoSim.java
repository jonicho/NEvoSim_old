package de.jrk.nevosim;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * NEvoSim simulates an evolution with creatures that have neural networks.
 * With each time a Creature splits, the weights of the neural network changes a bit.
 * In this way the creatures are getting more intelligent and can survive better.
 * @author Jonas Keller
 *
 */
public class NEvoSim extends ApplicationAdapter {
	public static float x;
	public static float y;
	public static float width;
	public static float height;
	private SpriteBatch batch;
	private SpriteBatch batchOverlay;
	private OrthographicCamera camera;
	private OrthographicCamera cameraOverlay;
	private Overlay overlay;
	private float zoom = 1;
	public static boolean pause = true;
	public static boolean fastForward = false;
	public static int targetFrameDuration = 16;
	public static Random rand = new Random();
	public static float year = 0;
	public static boolean save;
	public static SimThread simThread;
	public static Input input;
	public static ArrayList<Creature> deadCreatures = new ArrayList<Creature>();
	public static File file;
	public static boolean showOverlay = true;
	public static boolean showAttackIndicator = true;
	public static Creature selectedCreature;
	private float siteDifference;
	
	public NEvoSim(File file) {
		NEvoSim.file = file;
	}
	
	@Override
	public void create() {
		simThread = new SimThread();
		camera = new OrthographicCamera(1000, 1000);
		overlay = new Overlay();
		batch = new SpriteBatch();
		cameraOverlay = new OrthographicCamera(1000, 1000);
		batchOverlay = new SpriteBatch();
		input = new Input();
		Gdx.input.setInputProcessor(input);
		simThread.setPriority(Thread.MIN_PRIORITY);
		simThread.start();
		camera.zoom = 1;
		camera.update();
		cameraOverlay.zoom = 1;
		cameraOverlay.update();
		batch.setProjectionMatrix(camera.combined);
//		batchOverlay.setProjectionMatrix(cameraOverlay.combined);
	}
	
	@Override
	public void render() {
		Gdx.gl.glClearColor(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b - 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if (SimThread.isStarted) {
			// draw the world and all Creatures
			SimThread.world.draw(batch);
			for (int i = 0; i < SimThread.creatures.size(); i++) {
				try {
					SimThread.creatures.get(i).draw(batch);
				} catch (Exception e) { // catch exception. Sometimes the Creature dies while it is tried to draw it
					System.err.println("Draw skipped");
					e.printStackTrace();
				}
			}
		}
		batch.end();
		batchOverlay.begin();
		// draw the overlay
		if (showOverlay) overlay.draw(batchOverlay);
		batchOverlay.end();
		for (int i = 0; i < deadCreatures.size(); i++) {
			// delete dead Creatures
			try {
				deadCreatures.get(0).dispose();
			} catch (Exception e) { // catch exception
				System.err.println("Dispose skipped");
				e.printStackTrace();
			}
			deadCreatures.remove(0);
		}
		
		if (!simThread.isAlive()) { // exit if the simThread is not alive anymore
			Gdx.app.exit();
		}
	}
	
	
	/**
	 * Calculates witch creature was clicked
	 */
	private void calculateClickedCreature() {
		if (pause || true ) {
			float mouseX = 0;
			float mouseY = 0;
			mouseX = (input.mouseX - 500) * zoom + 500 - x;
			mouseY = (input.mouseY - 500) * zoom + 500 - y;
			if (siteDifference < 0) {
				mouseY -= Math.abs(siteDifference/2) * zoom;
			} else {
				mouseX -= Math.abs(siteDifference/2) * zoom;
			}
			Creature c = null;
			float distance = 12;
			for (int i = 0; i < SimThread.creatures.size(); i++) {
				Creature cr = SimThread.creatures.get(i);
				float disX = mouseX - cr.getX();
				float disY = mouseY - cr.getY();
				float dis = (float) Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2));
				if (dis < distance) {
					c = cr;
					distance = dis;
				}
			}
			if (c != null) selectedCreature = c;
		}
	}
	
	@Override
	public void dispose() {
		simThread.dispose();
		batch.dispose();
		batchOverlay.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		if (width == 0) width = 1;
		if (height == 0) height = 1;
		resize(width, height, camera, batch);
		resize(width, height, cameraOverlay, batchOverlay);
		NEvoSim.width = camera.viewportWidth;
		NEvoSim.height = camera.viewportHeight;
	}
	
	private void resize(int width, int height, OrthographicCamera camera, SpriteBatch batch) {
		if (width > height) {
			camera.viewportWidth = 1000 * width / height;
			camera.viewportHeight = 1000;
		} else if (height > width) {
			camera.viewportWidth = 1000;
			camera.viewportHeight = 1000 * height / width;
		} else {
			camera.viewportWidth = 1000;
			camera.viewportHeight = 1000;
		}
		siteDifference = camera.viewportWidth - camera.viewportHeight;
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	
	public class Input implements InputProcessor {
		
		private float lastMouseX;
		private float lastMouseY;
		public float mouseX;
		public float mouseY;
		private boolean wasDragged;

		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.R) {
				x = 0;
				y = 0;
				zoom = 1;
				camera.zoom = 1;
				camera.update();
				batch.setProjectionMatrix(camera.combined);
			} else if (keycode == Keys.SPACE) {
				pause = !pause;
			} else if (keycode == Keys.F) {
				fastForward = !fastForward;
			} else if (keycode == Keys.S) {
				save = true;
			} else if (keycode == Keys.O) {
				showOverlay = !showOverlay;
			} else if (keycode == Keys.UP && targetFrameDuration >= 2) {
				targetFrameDuration /= 2;
			} else if (keycode == Keys.DOWN && targetFrameDuration <= 128) {
				targetFrameDuration *= 2;
			} else if (keycode == Keys.A) {
				showAttackIndicator = !showAttackIndicator;
			} else if (keycode == Keys.ESCAPE) {
				selectedCreature = null;
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			screenY = -screenY + Gdx.graphics.getHeight();
			mouseX = (int)(width * ((float)screenX / (float)Gdx.graphics.getWidth()));
			mouseY = (int)(height * ((float)screenY / (float)Gdx.graphics.getHeight()));
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			if (!wasDragged) {
				calculateClickedCreature();
			}
			wasDragged = false;
			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			screenY = -screenY + Gdx.graphics.getHeight();
			wasDragged = true;
			lastMouseX = mouseX;
			lastMouseY = mouseY;
			mouseX = width * ((float)screenX / (float)Gdx.graphics.getWidth());
			mouseY = height * ((float)screenY / (float)Gdx.graphics.getHeight());
			x -= ((lastMouseX - mouseX));
			y -= ((lastMouseY - mouseY));
			
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			if (amount < 0) zoom -= 0.2f;
			else if (amount > 0) zoom += 0.2f;
			if (zoom < 0.2) zoom = 0.2f;
			if (zoom > 1.2) zoom = 1.2f;
			camera.zoom = zoom;
			camera.update();
			batch.setProjectionMatrix(camera.combined);
			return false;
		}
	}
}
