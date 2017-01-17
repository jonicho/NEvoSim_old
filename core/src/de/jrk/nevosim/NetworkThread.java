package de.jrk.nevosim;

import java.util.ArrayList;

public class NetworkThread extends Thread {
	
	public static ArrayList<Creature> creatures = new ArrayList<Creature>();

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
}
