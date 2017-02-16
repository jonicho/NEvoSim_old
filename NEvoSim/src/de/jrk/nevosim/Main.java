package de.jrk.nevosim;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;

public class Main {
	
	private static boolean activated = false;
	public static boolean pause = false;
	public static boolean fastForward = false;
	public static SimThread simThread;
	public static Creature selectedCreature;
	public static File file;
	public static boolean save;
	public static float year = 0;
	public static Random rand = new Random();
	private static Renderer rend = new Renderer();
	
	private static JFrame f = new JFrame("NEvoSim");
	private static JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private static JTabbedPane info = new JTabbedPane();
	private static Screen screen = new Screen();
	private static InfoScreen infoScreen = new InfoScreen();
	private static JToggleButton pauseBut = new JToggleButton("Pause");
	private static JButton saveBut = new JButton("save");
	private static JPanel options = new JPanel(new FlowLayout());
	private static JToggleButton fastBut = new JToggleButton("Fast");
	
	public static void init() {
		simThread = new SimThread();
		simThread.start();
	}

	public static void main(String[] args) {
		init();
		
		f.addKeyListener(new Input());
		f.setVisible(true);
		f.setSize(800, 600);
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowListenerImpl());
		f.setLocationRelativeTo(null);
		
		pauseBut.addActionListener(e -> {pause = !pause;});
		saveBut.addActionListener(e -> {save = true;});
		fastBut.addActionListener(e -> {fastForward = !fastForward;});

		options.add(pauseBut);
		options.add(saveBut);
		options.add(fastBut);
		
		info.addTab("Options", options);
		info.addTab("Creature Info", infoScreen);
		
		splitPane.add(screen);
		splitPane.add(info);
		f.add(splitPane);
		
		splitPane.setDividerSize(0);
		
		// main loop
		while(true) {
			splitPane.setDividerLocation((int)(f.getHeight() < f.getWidth() * 0.7 ? f.getHeight() : f.getWidth() * 0.7));
			Renderer.width = screen.getWidth();
			Renderer.height = screen.getHeight();
			screen.repaint();
			
			if (!simThread.isAlive()) { // exit if the simThread is not alive anymore
				close();
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static synchronized void close() {
		System.out.println("Close called!");
		SimThread.save = true;
	}
	
	public static boolean isActivated() {
		return activated;
	}
	
	private static class Screen extends JLabel {
		private static final long serialVersionUID = -1914630763930892849L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			rend.render(g);
		}
	}
	
	private static class InfoScreen extends JLabel{
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
	}
	
	private static class Input implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_SPACE:
				pauseBut.doClick();
				break;
			case KeyEvent.VK_F:
				fastBut.doClick();
				break;
			default:
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
		}
		
	}

	private static class WindowListenerImpl implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
			System.out.println("Opened");
			splitPane.setDividerLocation(0.5);
		}
		
		@Override
		public void windowIconified(WindowEvent e) {}
		
		@Override
		public void windowDeiconified(WindowEvent e) {}
		
		@Override
		public void windowDeactivated(WindowEvent e) {
			System.out.println("Deactivated");
			activated = false;
		}
		
		@Override
		public void windowClosing(WindowEvent e) {
			System.out.println("Closing");
			close();
		}
		
		@Override
		public void windowClosed(WindowEvent e) {
			System.out.println("Closed");
		}
		
		@Override
		public void windowActivated(WindowEvent e) {
			System.out.println("Activated");
			activated = true;
			splitPane.setDividerLocation(0.5);
		}
		
	}
}
