package de.jrk.nevosim.desktop;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.jrk.nevosim.NEvoSim;

public class DesktopLauncher {
	
	private static File file = new File(System.getProperty("user.home"));
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 1000;
		config.width = 1000;
		config.resizable = true;
		config.vSyncEnabled = false;
		config.foregroundFPS = 60;
		config.backgroundFPS = 30;
		config.title = "NEvoSim";

		JFileChooser fc = new JFileChooser(file);
		fc.setDialogTitle("Open");
		fc.setFileFilter(new FileNameExtensionFilter("NEvoSim Save File", "nessf"));
		int fcs = fc.showOpenDialog(null);
		if (fcs == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			config.title += " - " + file.getName();
		} else {
			file = null;
			config.title += " - unnamed";
		}
		new LwjglApplication(new NEvoSim(file), config);
	}
}
