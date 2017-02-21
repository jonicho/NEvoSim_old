package de.jrk.nevosim;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class CreatureInfo extends JSplitPane {
	private static final long serialVersionUID = -2651257171669234953L;
	private JLabel infoLabel = new JLabel();
	private NetworkScreen networkScreen = new NetworkScreen();
	
	public static int width;
	public static int height;
	
	public static Vector mousePos = new Vector(0, 0);
	
	public CreatureInfo() {
		setOrientation(JSplitPane.VERTICAL_SPLIT);
		setDividerSize(0);
		setLeftComponent(infoLabel);
		setRightComponent(networkScreen);
	}
	
	public void draw() {
		width = networkScreen.getWidth();
		height = networkScreen.getHeight();
		if (Main.selectedCreature != null) {
			infoLabel.setText(Main.selectedCreature.createInfoText());
			networkScreen.repaint();
		} else {
			infoLabel.setText("No Creature selected!");
		}
		setDividerLocation((int)infoLabel.getPreferredSize().getHeight());
	}
	
	private class NetworkScreen extends JPanel {
		private static final long serialVersionUID = 1932043463540643431L;
		
		public NetworkScreen() {
			super();
			addMouseMotionListener(new MouseMotionListener() {
				
				@Override
				public void mouseMoved(MouseEvent e) {
					mousePos = new Vector(e.getX(), e.getY());
				}
				
				@Override
				public void mouseDragged(MouseEvent e) {}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (Main.selectedCreature != null)Main.selectedCreature.drawBrain(g);
		}
	}
}
