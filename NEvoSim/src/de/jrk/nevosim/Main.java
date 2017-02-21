package de.jrk.nevosim;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;

public class Main {
	
	private static boolean activated = true;
	public static boolean pause = true;
	public static boolean fastForward = false;
	public static SimThread simThread;
	public static Creature selectedCreature;
	public static boolean save;
	public static float year = 0;
	public static Random rand = new Random();
	private static Renderer rend = new Renderer();
	
	public static JFrame f = new JFrame("NEvoSim");
	private static JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private static Screen screen = new Screen();
	private static JSplitPane splitPaneGui = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private static JPanel menuPanel = new JPanel();
	private static JTabbedPane infoPane = new JTabbedPane();
	private static JToggleButton pauseBut = new JToggleButton("Pause");
	private static JButton saveBut = new JButton("Save");
	private static JToggleButton fastBut = new JToggleButton("Fast");
	private static JLabel infoLabel = new JLabel();
	private static JPanel butPanel = new JPanel();
	private static CreatureInfo creatureInfo = new CreatureInfo();
	
	public static void init() {
		simThread = new SimThread();
		simThread.start();
	}

	public static void main(String[] args) {
		init();
		
		f.addKeyListener(new Input());
		f.setVisible(true);
		f.setSize(800, 600);
		f.setMinimumSize(new Dimension(800, 600));
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new WindowListenerImpl());
		f.setLocationRelativeTo(null);
		
		pauseBut.addActionListener(e -> {
			pause = !pause;
			pauseBut.setSelected(pause);
		});
		pauseBut.setSelected(pause);
		saveBut.addActionListener(e -> {
			save = true;
		});
		fastBut.addActionListener(e -> {
			fastForward = !fastForward;
			fastBut.setSelected(fastForward);
		});
		
		menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		
		butPanel.setLayout(new GridLayout(3, 1, 5, 5));
		
		butPanel.add(pauseBut);
		butPanel.add(fastBut);
		butPanel.add(saveBut);
		
		menuPanel.add(butPanel);
		menuPanel.add(infoLabel);
		
		infoPane.add("Creature Info", creatureInfo);
		
		splitPaneGui.setDividerSize(0);
		splitPaneGui.setLeftComponent(menuPanel);
		splitPaneGui.setRightComponent(infoPane);
		
		splitPane.setDividerSize(0);
		splitPane.setLeftComponent(screen);
		splitPane.setRightComponent(splitPaneGui);
		
		f.add(splitPane);
		
		// main loop
		while(true) {
			infoLabel.setText(generateInfoText());
			
			splitPane.setDividerLocation((int)(f.getHeight() < f.getWidth() * 0.6 ? f.getHeight() : f.getWidth() * 0.6));
			Renderer.width = screen.getWidth();
			Renderer.height = screen.getHeight();
			screen.repaint();
			creatureInfo.draw();
			
			if (!simThread.isAlive()) { // exit if the simThread is not alive anymore
				close();
			}
			
			try {
				if (activated) Thread.sleep(10);
				else Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void calculateClickedCreature(double x, double y) {
		Creature c = null;
		double distance = 0.01;
		for (int i = 0; i < SimThread.creatures.size(); i++) {
			Creature cr = SimThread.creatures.get(i);
			double disX = x - cr.getX();
			double disY = y - cr.getY();
			double dis = Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2));
			if (dis < distance) {
				c = cr;
				distance = dis;
			}
		}
		if (c != null) selectedCreature = c;
	}
	
	private static String generateInfoText() {
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
		String text = "<html><body>";
		text += "State: " + state + "<br>";
		text += "Sps: " + SimThread.stepsPerSecond + "<br>";
		text += "Attack Indicators: " + attIndi + "<br>";
		text += "Creatures: " + SimThread.creatures.size() + "<br>";
		text += "Year: " + (int)Main.year + "<br>";
		text += "</body></html>";
		return text;
	}
	
	private static synchronized void close() {
		SimThread.save = true;
		if (!simThread.isAlive()) System.exit(0);
	}
	
	public static boolean isActivated() {
		return activated;
	}
	
	private static class Screen extends JLabel {
		private static final long serialVersionUID = -1914630763930892849L;
		
		public Screen() {
			super();
			addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {}
				
				@Override
				public void mousePressed(MouseEvent e) {
					calculateClickedCreature((double)e.getX() / (double)Renderer.size, (double)e.getY() / (double)Renderer.size);
				}
				
				@Override
				public void mouseExited(MouseEvent e) {}
				
				@Override
				public void mouseEntered(MouseEvent e) {}
				
				@Override
				public void mouseClicked(MouseEvent e) {}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			rend.render(g);
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
		public void windowOpened(WindowEvent e) {}
		
		@Override
		public void windowIconified(WindowEvent e) {}
		
		@Override
		public void windowDeiconified(WindowEvent e) {}
		
		@Override
		public void windowDeactivated(WindowEvent e) {
			activated = false;
		}
		
		@Override
		public void windowClosing(WindowEvent e) {
			close();
		}
		
		@Override
		public void windowClosed(WindowEvent e) {}
		
		@Override
		public void windowActivated(WindowEvent e) {
			activated = true;
		}
		
	}
}
