package de.jrk.nevosim.neuralnetwork;

abstract class Neuron {

	protected float value = -2;
	protected String name;
	
	public abstract float getValue();
	
	public String getName() {
		return name;
	}
	
	public static float funktion(float t) {
		//TODO bei Fehlern wieder rein:
//		if (Float.isNaN(t)) {
//			return 0;
//		}
//		if (t > 355) {
//			return 1.0f;
//		}
		float x = t / (1 + Math.abs(t));
		return x;
	}

}