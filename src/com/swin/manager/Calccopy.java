package com.swin.manager;
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.layout.BorderPane;
//import javafx.stage.Stage;

//package manager;
//import utilities.*;
//public class Calccopy {
//	double fm(double x, double s, double f, double m1)
//	{
//	  double r;
//	  r = x*x*x*s*s*s - f*x*x - 2*m1*f*x - f*m1*m1;
//	  return r;
//	}
//	double gm(double x, double gamma, double gammak, double m1)
//	{
//	  double r;
//	  r = gamma * Math.pow((m1 + x), Constants.four3rd) - gammak*x*(m1 + 2*x);
//	  return r;
//	}
//	double mass(int k, double mi, double a1, double a2, double a3)
//	{
//	  int i;
//	  double m=0, xn, xo, dif;
//
//	  /* This routine finds zero of previously declared functions */
//
//	  xo = 0.0;
//	  xn = mi;
//	  i = 0;
//	  dif = Math.sqrt((xn - xo)*(xn-xo));
//	  while (dif > Constants.LIM)
//	    {
//	      if (k ==0) m = ( fm(xn + 0.5*Constants.DE, a1, a2, a3) - fm(xn - 0.5*Constants.DE, a1, a2, a3) )/Constants.DE;
//	      if (k ==1) m = ( gm(xn + 0.5*Constants.DE, a1, a2, a3) - gm(xn - 0.5*Constants.DE, a1, a2, a3) )/Constants.DE;
//	      xo = xn;
//	      if (k ==0) xn = xo - fm(xo, a1, a2, a3)/m;
//	      if (k ==1) xn = xo - gm(xo, a1, a2, a3)/m;
//
//	      dif = Math.sqrt((xn - xo)*(xn-xo));
//	      i++;
//	      if (i > 100)
//	        dif = 0;
//	    }
//	  return xn;
//	}
//	void main()
//	{
//
//		  /* declare orbital and derived parameters */
//		  double f, ftest, m1, m2, inc, s=0, ds, asini, pb, n, e;
//		  double wdot, ewdot, odot, dodot, M, Ml, Mu, Mp;
//		  double gamma, gammam, egamma, gammak, pbdot, pbdotm, epbdot, pbdotk;
//		  double rs, rsm, ers, ss, ssm, ess, R, Rm, eR;
//
//		  double q, a, rl, rc, mue2, w, exponent;
//		  double mtotal, mptotal, mctotal, total, height, height0;
//		  double cosi, sini, sin1, sin2, cos1, cos2, cosidiff, sinidiff;
//
//		  /* declare counters and flags*/
//		  int b, c, d1, d2, d3, i, j, k, l;
//		  int s0, s1, s2, s3, s4, s5, t;
//		  int pk0, pk1, pk2, pk3, pk4, pk5, pk6;
//		  int pnow, pbefore, finish;
//
//		  /* declare graphical parameters */
//		  double dm, dm1, dm2, m1l, m1u, m2l, m2u, c1, c2, mlow, mhigh;
//
//		  /* declare line and probability/countour parameters */
//		  int ni, nilines, nm, nmlines, ng, nglines, npb, npblines;
//		  int  nrs, nrslines, nss, nsslines, nR, nRlines;
//		  double[] si= new double[Constants.NLMAX];
//		  double[] sigmam= new double[Constants.NLMAX];
//		  double[] sigmag= new double[Constants.NLMAX];
//		  double[] sigmapb= new double[Constants.NLMAX];
//		  double[] sigmars= new double[Constants.NLMAX];
//		  double[] sigmass= new double[Constants.NLMAX];
//		  double[] sigmaR= new double[Constants.NLMAX];
//		  double[] tr= new double[6];
//		  double[] alev= new double[Constants.NCONT];
//		  double[] alevc= new double[Constants.NCONT];
//		  double[] mpalev= new double[Constants.NCONT];
//		  double[] mcalev= new double[Constants.NCONT];
//		  double[] mpalevy= new double[Constants.NCONT];
//		  double[] mcalevy= new double[Constants.NCONT];
//		  double[] target= new double[Constants.NCONT];
//		  double[] mtarget= new double[Constants.NCONT];
//		  double[] mx= new double[Constants.NM];
//		  double[] my= new double[Constants.NM];
//		  double[] nx= new double[2];
//		  double[] ny= new double[2];
//		  double[] fnp= new double[Constants.NM];
//		  double[] fnc= new double[Constants.NM];
//		  double[] fmp= new double[Constants.NM];
//		  double[] fmc= new double[Constants.NM];
//		  double[] np= new double[Constants.NM];
//		  double[] nc= new double[Constants.NM];
//		  double[] fnpc= new double[Constants.NM2];
//		  double[] npc= new double[Constants.NM2];
//		  double[] fgamma= new double[Constants.NM];
//		  double[] fpbdot= new double[Constants.NM];
//		  double[] frs= new double[Constants.NM];
//		  double[] fss= new double[Constants.NM];
//		  double[] fR= new double[Constants.NM];
//		  double[] flagp= new double[Constants.NCONT];
//		  double[] flagc= new double[Constants.NCONT];
//		  double npcmax,npmax,ncmax;
//
//		  f = 0.171706; //mass function
//		  e= 0.17188097150810268052;//eccintricity
//		  pb = 0.19765096237922123941;// period
//		  wdot = 5.3099519151404161437; //omega dot
//		  ewdot =0.00024106232441522529; //error on omega dot
//		  gammam = 0.00074464069051630476359;//gamma
//		  egamma= 0.00000746451486205490; //error on gamma
//		  pbdotm=-3.9462909083283078272e-13 + 6.71e-15 -5.05e-15 ;//pbdot
//		  epbdot= 1.0003284543358517209e-14 +1.73e-15 +0.44e-15;//error on pbdot
//		  n = 2 * Constants.PI / (pb * Constants.DAY);
//		  wdot = wdot * Constants.PI / (180 * 365.2425 * Constants.DAY);
//
//		  ewdot = ewdot * Constants.PI / (180 * 365.2425 * Constants.DAY);
//
//		  M = wdot * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
//		  M = Math.pow (M,1.5);
//		  System.out.println("\n The total mass is"+M+" solar masses.\n");
//
//		  Ml = (wdot - 1 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
//		  Ml = Math.pow (Ml,1.5);
//		  Mu = (wdot + 1 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
//		  Mu = Math.pow (Mu,1.5);
//
//		  System.out.println("\n Lower limit (1-sigma) is "+ Ml+" solar masses.\n");
//		  System.out.println("\n Upper limit (1-sigma) is "+ Mu+" solar masses.\n");
//
//		  Ml = (wdot - 2 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
//		  Ml = Math.pow (Ml,1.5);
//		  Mu = (wdot + 2 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
//		  Mu = Math.pow (Mu,1.5);
//
//		  System.out.println("\n Lower limit (2-sigma) is "+ Ml+" solar masses.\n");
//		  System.out.println("\n Upper limit (2-sigma) is "+ Mu+" solar masses.\n");
//
//		  Ml = (wdot - 3 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
//		  Ml = Math.pow (Ml,1.5);
//		  Mu = (wdot + 3 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
//		  Mu = Math.pow (Mu,1.5);
//
//		  System.out.println("\n Lower limit (3-sigma) is "+ Ml+" solar masses.\n");
//		  System.out.println("\n Upper limit (3-sigma) is"+ Mu+" solar masses.\n");
//
//		  m2 = M * M * f * Math.pow (s , -3);
//		  m2 = Math.pow( m2, Constants.one3rd);
//
//		  s = 1;
//
//		  System.out.println("\n For sin i = "+s+" the mass of the companion is "+m2+" and the mass of the pulsar is"+(M-m2));
//
//		  m2 = Ml * Ml * f * Math.pow (s , -3);
//		  m2 = Math.pow( m2, Constants.one3rd);
//
//		  System.out.println("\n In the 3-sigma lower limit of total mass, the mass of the companion is "+m2);
//		  System.out.println("   the mass of the pulsar is " + (Ml-m2));
//
//		  m2 = Mu * Mu * f * Math.pow (s , -3);
//		  m2 = Math.pow( m2, Constants.one3rd);
//
//		  System.out.println("\n In the 3-sigma upper limit of total mass, the mass of the companion is "+ m2);
//		  System.out.println("   the mass of the pulsar is "+ (Mu-m2));
//
//		  si[0] = 1;
//		  nilines=1;
//
//		  nmlines =2;
//		  sigmam[0] = 0;
//		  sigmam[1] = 1;
//
//	      gammak = e*Math.pow(n,-Constants.one3rd)*Math.pow(Constants.TSUN,Constants.two3rd);
//	      nglines = 2;
//	      sigmag[0] = 0;
//	      sigmag[1] = 1;
//
//	      npblines = 2;
//	      sigmapb[0] = 0;
//	      sigmapb[1] = 1;
//
//	      pbdotk = -192 * Constants.PI * Math.pow ((n * Constants.TSUN), Constants.five3rd) / 5.0;
//	      pbdotk = pbdotk * (1 + e*e*73.0/24.0 +  Math.pow(e , 4)*37.0/96.0);
//	      pbdotk = pbdotk * Math.pow((1 - e*e),-3.5);
//
//	      d1 = 'y'; //hatch
//	      d2= 'n' ; //contour
//	      d3='n'; //pdf
//
//		  m1l=1.2;
//		  m1u=1.4;
//		  m2l=0.9;
//		  m2u=1.1;
//		  m1l=m2l= 0.5;
//		  m1u=m2u= 2.5;
//		  dm = (m1u - m1l)/(Constants.NM1-1);
//		    
//		  mlow = 1.20;
//	      mhigh = 1.4408;
//	
//	      mx[0] = mlow;
//	      my[0] = m2l;
//	      mx[1] = mlow;
//	      my[1] = m2u;
//	      mx[3] = mhigh;
//	      my[3] = m2l;
//	      mx[2] = mhigh;
//	      my[2] = m2u;
//	      
//	      for (ni = 0; ni < nilines; ni++)
//    	    {
//    	      s = si[ni];
//    	      /* calculate m2 for mp = 0 */
//    	      m2 = f/(s*s*s);
//    	      mx[0] = 0; my[0] = m2;
//
//    	      for (k = 1; k < Constants.NM1; k++)
//    		{
//    		  /* First: cycle through mass ratios */
//    		  R = 10.0/(1.0*k);
//    		  mx[k] = m2 * (1+R)*(1+R)/(R*R*R);
//    		  my[k] = R*mx[k];
//    		}
//    	      
//    	      // plot mx vs my NM1 elements
//    	    }
//	      
//	      for (nm = 0; nm < nmlines; nm++)
//    		{
//    		  Mp = (wdot + sigmam[nm] * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
//    		  Mp = Math.pow (Mp,1.5);
//
//    		  /* How does the total mass translate at the mass function (sin i = 1)? */
//    		  my[0] = Math.pow( ( f * Mp * Mp ), Constants.one3rd);
//    		  mx[0] = Mp - my[0];
//
//    		  mx[1] = 0;
//    		  my[1] = Mp;
//    		  // plot mx vs my 2  
//    		 // cpgline(2, mx, my);
//    		}
//	      
//	      for (ng = 0; ng < nglines; ng++)
//    		{
//    		  gamma = gammam + sigmag[ng]*egamma;
//
//    		  /* initialize the value of m2 for m1 = 0 */
//    		  m2=gamma/(2*gammak);
//    		  m2=Math.pow(m2,1.5);
//
//    		  /* printf("\n e = %f, n = %f\n", e, n);
//    		     printf("\n Initial mass guess = %f solar masses\n", m2);*/
//    		 /* if (m2 < m2u)
//    		    {*/
//    		      for (k = 0; k < Constants.NM1; k++)
//    			{
//    			  m1 = k * dm + m1l;
//    			  mx[k] = m1;
//    			  m2 = mass(1,m2,gamma,gammak,m1);
//    			  my[k] = m2;
//    			  /* see if, for sin i = 1, we get a value larger than the mass function */
//    			}
//    		     // cpgline(Constants.NM1, mx, my);
//    		    //}
//    		}
//	      
//	      for (npb = 0; npb < npblines; npb++)
//    		{
//    		  pbdot = pbdotm + sigmapb[npb]*epbdot;
//
//    		  /* ludicrous values for first entry in vector - not likely to be displayed */
//    		  mx[0] = 100;
//    		  my[0] = 0;
//    		  for (k = 1; k < Constants.NM1; k++)
//    		    {
//    		      /* First: cycle through mass rations */
//    		      R = 0.1 * k;
//    		      m2 = pbdot * Math.pow((1+R), Constants.one3rd)/(R * pbdotk);
//    		      m2 = Math.pow(m2,0.6);
//    		      m1 = R * m2;
//    		      mx[k] = m1;
//    		      my[k] = m2;
//    		    }
//    		 // cpgline(Constants.NM1, mx, my);
//    		}
//	      if (d1 == 'y')
//  	    {
//  	      ds = (m1u - m1l) / Constants.NHH;
//
//  	      for (k = 0; k < Constants.NHH; k++)
//  		{
//
//  		  /* now, we specify points near the X axis */
//  		  mx[0] = m1l + k * ds;
//  		  my[0] = m2l;
//
//  		  M = mx[0] + my[0];
//  		  /* How does the total mass translate at the mass function (sin i = 1)? */
//  		  my[1] = Math.pow( ( f * M * M ), Constants.one3rd);
//  		  mx[1] = M - my[1];
//
//  		  //cpgline(2, mx, my);
//  		}
//
//  	      for (k = 0; k < Constants.NHH; k++)
//  		{
//
//  		  /* now, we specify points near the Y axis */
//  		  mx[0] = m1u;
//  		  my[0] = m2l + k * ds;
//
//  		  M = mx[0] + my[0];
//  		  /* How does the total mass translate at the mass function (sin i = 1)? */
//  		  my[1] = Math.pow( ( f * M * M ), Constants.one3rd);
//  		  mx[1] = M - my[1];
//
//  		  //cpgline(2, mx, my);
//  		}
//  	    }
//
//
//	
//	      	
//	}
//}

//public class Main extends Application {
//	@Override
//	public void start(Stage primaryStage) {
//		try {
//			BorderPane root = new BorderPane();
//			Scene scene = new Scene(root,400,400);
//			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//			primaryStage.setScene(scene);
//			primaryStage.show();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static void main(String[] args) {
//		launch(args);
//	}
//}

