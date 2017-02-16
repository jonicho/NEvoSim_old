package de.jrk.nevosim;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This is to save and load the simulation.
 * It uses the .nessf (<b>NE</b>vo<b>S</b>im <b>s</b>ave <b>f</b>ile) file format.
 * @author Jonas Keller
 *
 */
public class SaveLoad {
	private String dataSave = "";
	private String[] databaseLoad;
	private File file;
	public static final String FILE_EXTENSION = ".nessf";
	
	public SaveLoad() {
		this.file = Main.file;
	}
	
	/**
	 * Saves the simulation.
	 * Uses a JFileChoose to ask for the path.
	 * @param quit whether the method is executed on quit.
	 */
	public void save(boolean quit) {
		JFileChooser fc = new JFileChooser(file);
		fc.setDialogTitle("Save");
		fc.setFileFilter(new FileNameExtensionFilter("NEvoSim Save File", "nessf"));
		int fcs = fc.showSaveDialog(null);
		file = fc.getSelectedFile();
		if (file != null) {
			if (!file.getPath().endsWith(FILE_EXTENSION)) {
				file = new File(file.getPath() + FILE_EXTENSION);
			} 
		}
		if (fcs == JFileChooser.APPROVE_OPTION) {
			dataSave = "";
			dataSave += Main.year + ";\n" + SimThread.world.save();
			for (Creature creature : SimThread.creatures) {
				dataSave += ";\n" + creature.save();
			}
			//Gdx.files.absolute(file.getPath()).writeString(dataSave, false); TODO
		} else if (quit) {
			int option = JOptionPane.showConfirmDialog(null, "Really quit without saving?", "Attention!", 
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (option == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (option != JOptionPane.OK_OPTION) {
				save(quit);
			}
			System.exit(0);
		}
	}
	
	/**
	 * Loads the simulation.
	 */
	public void load() {
		if (file != null) {
			try {
				//String string = Gdx.files.absolute(file.getPath()).readString(); TODO
//				string.replaceAll("\n", "");
//				databaseLoad = string.split(";");
				Main.year = Float.parseFloat(databaseLoad[0]);
				SimThread.creatures.removeAll(SimThread.creatures);
				SimThread.world.load(databaseLoad[1]);
				for (int i = 0; i < databaseLoad.length - 2; i++) {
					SimThread.creatures.add(new Creature(databaseLoad[i + 2]));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			file = new File(System.getProperty("user.home"));
			SimThread.world.generateRandomWorld();
		}
	}
}
