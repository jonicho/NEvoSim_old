import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.text.*;

public class Mustererkennung extends Applet implements Runnable, MouseListener, MouseMotionListener
{

	//------------------------------------------------------------------------------
	//
	// Attribute
	//
	//------------------------------------------------------------------------------	
	public double[]   zustand        = new double[10];
	public double[][] gewicht        = new double[10][7];
	public double[]   ausgangsWert   = new double[10];
	public double[]   eingangsWert   = new double[7];	
	public int        ergebnis       = 0;
	public int        gesucht        = 0;
	
	public int        treffer        = 0;
	public int        versuche       = 0;
	public double     rauschen       = 1;
	
	public int        warteZeit      = 200;
	public boolean    laeuft         = true;
	public boolean    lernt          = true;	
	
	public Thread     thread         = null;	
	public Image      buffer         = null;
        public int[][]    fehler         = new int[10][10];
	
	public int        mausObjekt     = 0;

	//------------------------------------------------------------------------------
	//
	// Konstruktor
	//
	//------------------------------------------------------------------------------	
	public Mustererkennung()
	{
	
		// Gewichtmatrix mit Zufallswerten füllen
		for( int i=0; i<10; i++ )
			for( int j=0; j<7; j++ )
				gewicht[i][j] = Math.random()*.2+.1;
			
		addMouseListener(this);	
		addMouseMotionListener(this);
			
		thread = new Thread(this);
		thread.start();
		resize(3000, 3000);
	}
	
	public void paint( Graphics graphics )
	{
		if( buffer==null )
			buffer = createImage(getSize().width,getSize().height);
		Graphics g = buffer.getGraphics();
		
		g.setColor( Color.white );
		g.fillRect(0,0,getSize().width,getSize().height);
		
		int f=0;
		for( int i=0; i<10; i++ )
		{
			g.setColor( Color.black );
			g.drawRect(i*30+200,200,25,25);
			g.drawLine(i*30+215,225,i*30+215,300);
			g.drawRect(i*30+200,300,25,25);		

			f=255-(int)(ausgangsWert[i]*255);
			if(f<0) f=0;
			if(f>255)f=255;			
			g.setColor(new Color(f,f,f));
			g.fillRect(i*30+201,201,24,24);
			
			// Linien einzeichnen
			for( int j=0; j<7; j++ )
			{
				f=255-(int)(gewicht[i][j]*255);
				if(f<0) f=0;
				if(f>255)f=255;
				g.setColor( new Color(f,f,f) );
				if( f<255 )				
					g.drawLine(i*30+215,325,j*30+265,400);
			}
		}
		for( int i=0; i<7; i++ )
		{
			g.setColor(Color.black);		
			g.drawRect(i*30+250,400,25,25);
			f=255-(int)(eingangsWert[i]*255);
			if(f<0) f=0;
			if(f>255)f=255;			
			g.setColor(new Color(f,f,f));
			g.fillRect(i*30+251,401,24,24);
		}		
		g.setColor( Color.red );
		g.drawLine(ergebnis*30+210,180,ergebnis*30+220,190);
		g.drawLine(ergebnis*30+210,190,ergebnis*30+220,180);
		g.drawRect(gesucht*30+210,180,10,10);
	
		if( gesucht!=ergebnis )
		    {
			g.drawString("Falsch!",70,360);
			fehler[gesucht][ergebnis]++;
		    }
	
		g.setColor( Color.black );
		g.drawRect(  20,195,70,140 );
		g.drawRect( 110,195,70,140 );
		
		g.drawString("Eingabe",20,190);
		g.drawString("Ausgabe",110,190);		
	
		double[] e = eingangsWert;
		f=255-(int)(255*e[0]); g.setColor(new Color(f,f,f)); g.fillRect(30,200,50,10);
		f=255-(int)(255*e[1]); g.setColor(new Color(f,f,f)); g.fillRect(25,210,10,50);
		f=255-(int)(255*e[2]); g.setColor(new Color(f,f,f)); g.fillRect(75,210,10,50);
		f=255-(int)(255*e[3]); g.setColor(new Color(f,f,f)); g.fillRect(30,260,50,10);
		f=255-(int)(255*e[4]); g.setColor(new Color(f,f,f)); g.fillRect(25,270,10,50);
		f=255-(int)(255*e[5]); g.setColor(new Color(f,f,f)); g.fillRect(75,270,10,50);
		f=255-(int)(255*e[6]); g.setColor(new Color(f,f,f)); g.fillRect(30,320,50,10);
		e = muster(ergebnis);
		f=255-(int)(255*e[0]); g.setColor(new Color(f,f,f)); g.fillRect(120,200,50,10);
		f=255-(int)(255*e[1]); g.setColor(new Color(f,f,f)); g.fillRect(115,210,10,50);
		f=255-(int)(255*e[2]); g.setColor(new Color(f,f,f)); g.fillRect(165,210,10,50);
		f=255-(int)(255*e[3]); g.setColor(new Color(f,f,f)); g.fillRect(120,260,50,10);
		f=255-(int)(255*e[4]); g.setColor(new Color(f,f,f)); g.fillRect(115,270,10,50);
		f=255-(int)(255*e[5]); g.setColor(new Color(f,f,f)); g.fillRect(165,270,10,50);
		f=255-(int)(255*e[6]); g.setColor(new Color(f,f,f)); g.fillRect(120,320,50,10);
		
		g.setColor( Color.black );
		g.drawString("Eingangsneuronen",530,420);
		g.drawString("Verarbeitungsneuronen",530,320);
		g.drawString("Ausgangsneuronen",530,220);
		
//		for( int i=0; i<10; i++ )
//			for( int j=0; j<7; j++ )
//				g.drawString(new DecimalFormat("0.000").format(gewicht[i][j]),i*50+40,j*15+450);
		
		
		g.drawString("Geschwindigkeit:",30,47);
		g.setColor(Color.black);
		g.drawLine(30,55,230,55);
		g.fillOval((int)((Math.log(warteZeit)/Math.log(10)-1)*200/3+25),50,10,10);

		g.drawString("Rauschen:",30,77);
		g.setColor(Color.black);
		g.drawLine(30,85,230,85);
		g.fillOval((int)(rauschen*200+25),80,10,10);
		
		int max = 1;
		for( int i=0; i<10; i++ )
		    for( int j=0; j<10; j++ )
			if( fehler[i][j]>max )
			    max = fehler[i][j];

		for( int i=0; i<10; i++ )
		    for( int j=0; j<10; j++ )
			{
			    int m = fehler[i][j]*255*100/(versuche-treffer);
			    if( m>255 ) m=255;
			    g.setColor( new Color(m,m,m) );
			    g.fillRect( 400+10*i,20+10*j,10,10 );
			}

		graphics.drawImage( buffer,0,0,null );
	}		
	
	public void update( Graphics g )
	{
		paint(g);
	}

	//------------------------------------------------------------------------------
	//
	// Methoden
	//
	//------------------------------------------------------------------------------	
	public double[] effEingang( double[] ein )
	{
		double[] erg = new double[10];
		for( int i=0; i<10; i++ )
		{
			for( int j=0; j<7; j++ )
			{
				erg[i]+=gewicht[i][j]*ein[j];
			}
		}
		return erg;
	}
	
	public double[] aktivierung( double[] ein )
	{
		double[] erg = new double[10];
		
		erg = effEingang(ein);
		
		// DMA-Aktivierungsfunktion (vgl. Hoffmann, Neuronale Netze S.19f)
		double min = 0;					// minimaler Wert der Zustandsfunktion
		double max = 1;					// maximaler Wert der Zustandsfunktion
		double s   = 0.2;				// Skalierungsfaktor
		double d   = 0.5;				// Abklingkonstante
		double c0  = 0.5;				// Ruhewert der Aktivität
		erg = effEingang(ein);
		for( int i=0; i<10; i++ )
			if( erg[i]<0 )
				zustand[i] += s*erg[i]*(zustand[i]-min)-d*(zustand[i]-c0);
			else	
				zustand[i] += s*erg[i]*(max-zustand[i])-d*(zustand[i]-c0);
		erg = zustand;	
		
		return erg;
	}
	
	public double[] ausgang( double[] ein )
	{
		double[] erg = new double[10];
		
		erg = aktivierung(ein);
		
		// Fermi-Funktion (vgl. Hoffmann, Neuronale Netze S.23)
		double max   = 1;				// minimaler Wert der Ausgangsfunktion
		double min   = 0;				// maximaler Wert der Ausgangsfunktion
		double theta = 1;				// Schwelle
		double sigma = 2;				// Steigung
		
		for( int i=0; i<10; i++ )
			erg[i] = min+(max-min)/(1+Math.exp(-4*sigma*(erg[i]-theta)/(max-min)));
		
		return erg;	
	}
	
	public void lerne( double[] ein, double[] aus, double[] soll )
	{		
		// Delta-Lernregel (vgl. Hoffmann, Neuronale Netze S.73f)
		double lern = .2;			// Lernrate
		
		for( int i=0; i<7; i++ )
			for( int j=0; j<10; j++ )
				gewicht[j][i] += lern*ein[i]*(soll[j]-aus[j]);
	}

	public double[] muster( int i )
	{
		double[][] m =	{ {1,1,1,0,1,1,1},
						  {0,0,1,0,0,1,0},
						  {1,0,1,1,1,0,1},
						  {1,0,1,1,0,1,1},
						  {0,1,1,1,0,1,0},
						  {1,1,0,1,0,1,1},
						  {1,1,0,1,1,1,1},
						  {1,0,1,0,0,1,0},
						  {1,1,1,1,1,1,1},
						  {1,1,1,1,0,1,1} };
		return m[i];
	}
	
	public void zeitschritt()
	{
		int i=(int)(Math.random()*10);
			
		double[] ein = muster(i);
		
		for( int j=0; j<7; j++ )
			if( ein[j]==1 )
				ein[j]=1-Math.random()*rauschen*.5;
			else	
				ein[j]=Math.random()*rauschen*.5;
		
		double[] soll = new double[10];
		soll[i] = 1;
		double[] aus = ausgang( ein );

		if( lernt )
			lerne( ein,aus,soll );
	
		double wert = 0;
		int nr = 0;
		
		for( int j=0; j<10; j++ )
			if ( aus[j]>wert )
			{
				wert = aus[j];
				nr = j;
			}
			
		if( i==nr ) treffer++;
		versuche++;
		
		ausgangsWert = aus;
		eingangsWert = ein;
		gesucht = i;
		ergebnis = nr;

	}

	
	//------------------------------------------------------------------------------
	//
	// Überladene Methoden
	//
	//------------------------------------------------------------------------------		
	public void run()
	{
		while ( true )
		{
			try { thread.sleep(warteZeit); } catch(InterruptedException exc) {}
			if ( laeuft )
			{
				zeitschritt();
				repaint();
			}
		}
	}
	
	public void mousePressed      ( MouseEvent  me ) 
	{
		if( me.getX()>25 && me.getX()<235 && me.getY()>45 && me.getY()<70 )  mausObjekt = 1;
		if( me.getX()>25 && me.getX()<235 && me.getY()>75 && me.getY()<100 ) mausObjekt = 2;
	}
	public void mouseClicked      ( MouseEvent  me ) {}	
	public void mouseReleased     ( MouseEvent  me ) {}	
	public void mouseEntered      ( MouseEvent  me ) {}	
	public void mouseExited       ( MouseEvent  me ) {}	
	public void mouseMoved        ( MouseEvent  me ) {}
	public void mouseDragged      ( MouseEvent  me )
	{
		int x=me.getX();
		if( x<30 ) x=30;
		if( x>230 ) x=230;
		if( mausObjekt==1 )	warteZeit = (int)(Math.pow(10,(x-30)*3/200.0+1));		
		if( mausObjekt==2 )	rauschen = (x-30)/200.0;				
		repaint();
	}		
}
