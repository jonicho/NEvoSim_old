package de.jrk.nevosim;

public class ValueNoise {
	
	public double[][] world;
	
    public double frequency = 0.2;
    public double amplitude = 10;
    public int octaves = 1;
    public int randomseed = (int)(Math.random() * 10000);
    
    public ValueNoise() {
    	world = new double[100][100];
	}
	
	public void generateNoise() {
		randomseed = (int)(Math.random() * 10000);
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[0].length; j++) {
				world[i][j] = get(i, j);
			}
		}
	}
	
	
	//not my code [beginning]
	private double get(int x, int y) {
		double v = 0;
		
		for (int k = 0; k < octaves; k++) {
            v += getValue(y * frequency + randomseed, x * frequency + randomseed) * amplitude;
        }

		
		return v;
	}
	
	private double getValue(double x, double y) {
        int Xint = (int) x;
        int Yint = (int) y;

        double Xfrac = x - Xint;
        double Yfrac = y - Yint;

        double n01 = noise(Xint - 1, Yint - 1);
        double n02 = noise(Xint + 1, Yint - 1);
        double n03 = noise(Xint - 1, Yint + 1);
        double n04 = noise(Xint + 1, Yint + 1);
        double n05 = noise(Xint - 1, Yint);
        double n06 = noise(Xint + 1, Yint);
        double n07 = noise(Xint, Yint - 1);
        double n08 = noise(Xint, Yint + 1);
        double n09 = noise(Xint, Yint);
        double n12 = noise(Xint + 2, Yint - 1);
        double n14 = noise(Xint + 2, Yint + 1);
        double n16 = noise(Xint + 2, Yint);
        double n23 = noise(Xint - 1, Yint + 2);
        double n24 = noise(Xint + 1, Yint + 2);
        double n28 = noise(Xint, Yint + 2);
        double n34 = noise(Xint + 2, Yint + 2);

        double x0y0 = 0.0625 * (n01 + n02 + n03 + n04) + 0.1250 * (n05 + n06 + n07 + n08) + 0.2500 * n09;

        double x1y0 = 0.0625 * (n07 + n12 + n08 + n14) + 0.1250 * (n09 + n16 + n02 + n04) + 0.2500 * n06;

        double x0y1 = 0.0625 * (n05 + n06 + n23 + n24) + 0.1250 * (n03 + n04 + n09 + n28) + 0.2500 * n08;

        double x1y1 = 0.0625 * (n09 + n16 + n28 + n34) + 0.1250 * (n08 + n14 + n06 + n24) + 0.2500 * n04;

        double v1 = interpolate(x0y0, x1y0, Xfrac);
        double v2 = interpolate(x0y1, x1y1, Xfrac);

        double fin = interpolate(v1, v2, Yfrac);

        return fin;
    }

    private double interpolate(double x, double y, double a) {
        double negA = 1.0 - a;
        double negASqr = negA * negA;
        double fac1 = 3.0 * (negASqr) - 2.0 * (negASqr * negA);
        double aSqr = a * a;
        double fac2 = 3.0 * aSqr - 2.0 * (aSqr * a);

        return x * fac1 + y * fac2;
    }

    private double noise(int x, int y) {
        int n = x + y * 57;
        n = (n << 13) ^ n;
        int t = (n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff;
        return 1.0 - (double) t * 0.931322574615478515625e-9;
    }
    //not my code [ending]
}
