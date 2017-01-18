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
	private SpriteBatch batchOverlays;
	private OrthographicCamera camera;
	private OrthographicCamera cameraOverlays;
	private Overlay overlays;
	private float zoom = 1;
	public static boolean pause = true;
	public static boolean fastForward = false;
	public static int targetFrameDuration = 16;
	public static Random rand = new Random();
	public static float year = 0;
	public static boolean save;
	public static SimThread simThread;
	public static ArrayList<Creature> deadCreatures = new ArrayList<Creature>();
	public static File file;
	public static boolean showOverlays = true;
	public static boolean showAttackIndicator = true;
	
	public NEvoSim(File file) {
		NEvoSim.file = file;
	}
	
	@Override
	public void create() {
		simThread = new SimThread();
		camera = new OrthographicCamera(1000, 1000);
		overlays = new Overlay();
		batch = new SpriteBatch();
		cameraOverlays = new OrthographicCamera(1000, 1000);
		batchOverlays = new SpriteBatch();
		Gdx.input.setInputProcessor(new Input());
		simThread.start();
		camera.zoom = 1;
		camera.update();
		cameraOverlays.zoom = 1;
		cameraOverlays.update();
		batch.setProjectionMatrix(camera.combined);
		batchOverlays.setProjectionMatrix(cameraOverlays.combined);
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
				} catch (NullPointerException e) { // catch null pointer exception. Sometimes the Creature dies while it is tried to draw it
					e.printStackTrace();
					System.out.println("Draw skipped");
				}
			}
		}
		batch.end();
		batchOverlays.begin();
		// draw the overlay
		if (showOverlays) overlays.draw(batchOverlays);
		batchOverlays.end();
		for (int i = 0; i < deadCreatures.size(); i++) {
			// delete dead Creatures
			try {
				deadCreatures.get(0).dispose();
			} catch (NullPointerException e) { // catch null pointer exception
				e.printStackTrace();
				System.out.println("Dispose skipped");
			}
			deadCreatures.remove(0);
		}
		
		if (!simThread.isAlive()) { // exit if the simThread is not alive anymore
			Gdx.app.exit();
		}
	}
	
	@Override
	public void dispose() {
		simThread.dispose();
		batch.dispose();
		batchOverlays.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		if (width == 0) width = 1;
		if (height == 0) height = 1;
		resize(width, height, camera, batch);
		resize(width, height, cameraOverlays, batchOverlays);
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
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	
	public class Input implements InputProcessor {
		
		private float lastMouseX;
		private float lastMouseY;
		private float mouseX;
		private float mouseY;

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
				showOverlays = !showOverlays;
			} else if (keycode == Keys.UP && targetFrameDuration >= 2) {
				targetFrameDuration /= 2;
			} else if (keycode == Keys.DOWN && targetFrameDuration <= 128) {
				targetFrameDuration *= 2;
			} else if (keycode == Keys.A) {
				showAttackIndicator = !showAttackIndicator;
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
			mouseX = (int) (width * (screenX / (float)Gdx.graphics.getWidth()) * zoom * zoom * zoom);
			mouseY = (int) (height * (screenY / (float)Gdx.graphics.getHeight()) * zoom * zoom * zoom);
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			lastMouseX = mouseX;
			lastMouseY = mouseY;
			mouseX = width * ((float)screenX / (float)Gdx.graphics.getWidth()) * zoom * zoom * zoom;
			mouseY = height * ((float)screenY / (float)Gdx.graphics.getHeight()) * zoom * zoom * zoom;
			x -= ((lastMouseX - mouseX)/(zoom * zoom));
			y += ((lastMouseY - mouseY)/(zoom * zoom));
			
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
