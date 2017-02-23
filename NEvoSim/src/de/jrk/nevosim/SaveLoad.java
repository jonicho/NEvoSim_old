package de.jrk.nevosim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
		this.file = new File(System.getProperty("user.home"));
	}
	
	/**
	 * Saves the simulation.
	 * Uses a JFileChoose to ask for the path.
	 * @param quit whether the method is executed on quit.
	 * @return whether the simulation should be closed
	 */
	public boolean save(boolean quit) {
		if (quit) {
			int o = JOptionPane.showOptionDialog(Main.f, "Save?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			if (o == JOptionPane.CLOSED_OPTION || o == JOptionPane.CANCEL_OPTION) return false;
			else if (o == JOptionPane.NO_OPTION) return true;
		}
		JFileChooser fc = new JFileChooser(file);
		fc.setDialogTitle("Save");
		fc.setFileFilter(new FileNameExtensionFilter("NEvoSim Save File", "nessf"));
		fc.showDialog(Main.f, "Save");
		file = fc.getSelectedFile();
		if (file != null) {
			if (!file.getPath().endsWith(FILE_EXTENSION)) {
				file = new File(file.getPath() + FILE_EXTENSION);
			} 
		} else return false;
		dataSave = "";
		dataSave += Main.year + ";\n" + SimThread.world.save();
		for (Creature creature : SimThread.creatures) {
			dataSave += ";\n" + creature.save();
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			GZIPOutputStream zos = new GZIPOutputStream(fos);
			zos.write(dataSave.getBytes());
			zos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		if (quit) return true;
		else return false;
	}
	
	/**
	 * Loads the simulation.
	 */
	public void load() {
		int o = JOptionPane.showOptionDialog(Main.f, "Load?", "Load?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (o == JOptionPane.CLOSED_OPTION) System.exit(0);
		if (o == JOptionPane.YES_OPTION) {
			try {
				JFileChooser fc = new JFileChooser(file);
				fc.setDialogTitle("Save");
				fc.setFileFilter(new FileNameExtensionFilter("NEvoSim Save File", "nessf"));
				int fcs = fc.showOpenDialog(Main.f);
				file = fc.getSelectedFile();
				if (fcs == JFileChooser.APPROVE_OPTION && file != null) {
					FileInputStream fis = new FileInputStream(file);
					GZIPInputStream zis = new GZIPInputStream(fis);
					BufferedReader br = new BufferedReader(new InputStreamReader(zis));
					String string = "";
					boolean finished = false;
					while (!finished) {
						String line = br.readLine();
						if (line == null) {
							finished = true;
						} else {
							string += line;
						}
					}
					br.close();
					string.replaceAll("\n", "");
					databaseLoad = string.split(";");
					Main.year = Float.parseFloat(databaseLoad[0]);
					SimThread.creatures.removeAll(SimThread.creatures);
					SimThread.world.load(databaseLoad[1]);
					for (int i = 0; i < databaseLoad.length - 2; i++) {
						SimThread.creatures.add(new Creature(databaseLoad[i + 2]));
					}
					Main.f.setTitle("NEvoSim - " + file.getName());
					Main.f.setVisible(true);
				} else {
					System.exit(0);
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
				return;
			}
		}
		Main.f.setVisible(true);
		file = new File(System.getProperty("user.home"));
		SimThread.world.generateRandomWorld();
	}
}
