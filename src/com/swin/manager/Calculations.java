package com.swin.manager;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import com.swin.bean.MMBean;
import com.swin.bean.PlotInputs;
import com.swin.bean.XYPair;
import com.swin.util.Constants;
public class Calculations implements Calculator{
	public Calculations() {
	}
	
	double fm(double x, double s, double f, double m1)
	{
	  double r;
	  r = x*x*x*s*s*s - f*x*x - 2*m1*f*x - f*m1*m1;
	  return r;
	}
	double gm(double x, double gamma, double gammak, double m1)
	{
	  double r;
	  r = gamma * Math.pow((m1 + x), Constants.four3rd) - gammak*x*(m1 + 2*x);
	  return r;
	}
	double mass(int k, double mi, double a1, double a2, double a3)
	{
	  int i;
	  double m=0, xn, xo, dif;

	  /* This routine finds zero of previously declared functions */

	  xo = 0.0;
	  xn = mi;
	  i = 0;
	  dif = Math.sqrt((xn - xo)*(xn-xo));
	  while (dif > Constants.LIM)
	    {
	      if (k ==0) m = ( fm(xn + 0.5*Constants.DE, a1, a2, a3) - fm(xn - 0.5*Constants.DE, a1, a2, a3) )/Constants.DE;
	      if (k ==1) m = ( gm(xn + 0.5*Constants.DE, a1, a2, a3) - gm(xn - 0.5*Constants.DE, a1, a2, a3) )/Constants.DE;
	      xo = xn;
	      if (k ==0) xn = xo - fm(xo, a1, a2, a3)/m;
	      if (k ==1) xn = xo - gm(xo, a1, a2, a3)/m;

	      dif = Math.sqrt((xn - xo)*(xn-xo));
	      i++;
	      if (i > 100){
	    	  System.err.println("*********************");
	    	  dif = 0;
	      }
	    }
	  return xn;
	}
	@Override
	public List<List<List<XYPair>>> getMassRatio(PlotInputs plotInputs){
		List<List<List<XYPair>>> dataList = new ArrayList<List<List<XYPair>>>();
		double steps = 3.0/Constants.NM1;
		double massRatio = 1.0714;
		double eMassRatio = 0.0011;
		dataList.add(null);
		for (int nm = 1; nm <= 3; nm++) {
			List<List<XYPair>> pairList = new LinkedList<List<XYPair>>();
			for(int sign=-1;sign<=1;sign++){
				if(sign==0) continue;
				List<XYPair> signList = new LinkedList<XYPair>();
				for (int k = 1; k < Constants.NM1; k++) {
					double ma = k*steps;
					double mb = ma/(massRatio + sign*nm*eMassRatio);
					signList.add(new XYPair(ma,mb));
				}
				pairList.add(signList);
			}
			dataList.add(pairList);

		}
		return dataList;
	}
	@Override
	public List<List<List<XYPair>>> getMassFunc(PlotInputs parFile){ // for all sigma ( all values)
		
		List<List<List<XYPair>>> dataList = new ArrayList<List<List<XYPair>>>();
		List<List<XYPair>> pairList = new LinkedList<List<XYPair>>();
		List<XYPair> massList = new LinkedList<XYPair>();

	  int s = 1; // what the hell is this?
	  /* calculate m2 for mp = 0 */
	  double m2_0 = parFile.getMassFunc()/(s*s*s);
	  massList.add(new XYPair(0.0,m2_0));
	  for (int k = 1; k < Constants.NM1; k++) {
		  double R = 10.0/(1.0*k); // R = Mc/Mp;
		  double x = m2_0 * (1+R)*(1+R)/(R*R*R);
		  double y = R*x;
		  massList.add(new XYPair(x,y));
	  }
	  pairList.add(massList);
	  dataList.add(pairList);
	  return dataList;
	}
	@Override
	public List<List<List<XYPair>>> getOmDot(PlotInputs parFile){
		
		double wdot = parFile.getOmDot() * Constants.PI / (180 * 365.2425 * Constants.DAY);

		double ewdot = parFile.geteOmDot() * Constants.PI / (180 * 365.2425 * Constants.DAY);
		
		List<List<List<XYPair>>> dataList = new ArrayList<List<List<XYPair>>>();
		double n = 2 * Constants.PI / (parFile.getPb() * Constants.DAY);
		dataList.add(null);
		 for (int nm = 1; nm <= 3; nm++)
 		{
		  List<List<XYPair>> pairList = new LinkedList<List<XYPair>>();
		  for(int sign=-1;sign<=1;sign++){
			  if(sign==0) continue;
			  List<XYPair> signList = new LinkedList<XYPair>();
			  double Mp = (wdot +sign* nm * ewdot) * 
				  		(1 - parFile.getEccintricity() * parFile.getEccintricity()) * 
				  			Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3.0;
		  
			  Mp = Math.pow (Mp,1.5);
			  /* How does the total mass translate at the mass function (sin i = 1)? */
	 		  
			  double y = Math.pow( ( parFile.getMassFunc() * Mp * Mp ), Constants.one3rd);
	 		  double x = Mp-y;
	 		  y = 0;
	 		  x = Mp-y;
	 		 signList.add(new XYPair(x, y));
	 		  x = 0.0;
	 		  y = Mp;
	 		 signList.add(new XYPair(x, y));
	 		// System.out.println(signList);

			  pairList.add(signList);
			  
			  
		  }
 		  dataList.add(pairList);
 		  
 		}
		return dataList;
	}
	
	@Override
	public List<List<List<XYPair>>> getGamma(PlotInputs parFile){
		
		List<List<List<XYPair>>> dataList = new ArrayList<List<List<XYPair>>>();
		 double m1l=0;
		 double m2l= 0;
		 double m1u=3;
		 double m2u= 3;
		dataList.add(null); // for 1- indexing
		for (int ng = 1; ng <= 3; ng++) {
			List<List<XYPair>> pairList = new LinkedList<List<XYPair>>();
			
			for(int sign=-1;sign<=1;sign++){
				if(sign==0) continue;
				List<XYPair> signList = new LinkedList<XYPair>();
				double gamma = parFile.getGamma() + sign*ng*parFile.geteGamma();
				double n = 2 * Constants.PI / (parFile.getPb() * Constants.DAY);
			    double gammak = parFile.getEccintricity()*Math.pow(n,-Constants.one3rd)*Math.pow(Constants.TSUN,Constants.two3rd);

				/* initialize the value of m2 for m1 = 0 */
				double m2=gamma/(2*gammak);
				m2=Math.pow(m2,1.5);
				double dm = (m1u - m1l)/(Constants.NM1-1);
				for (int k = 0; k < Constants.NM1; k++)
				{
					double m1 = k * dm + m1l;
					double x = m1;
					m2 = mass(1,m2,gamma,gammak,m1);
					double y = m2;
					/* see if, for sin i = 1, we get a value larger than the mass function */
					signList.add(new XYPair(x, y));
				}
				
				pairList.add(signList);
			}

			
			dataList.add(pairList);
			
		}

		return dataList;
	}
	
	@Override
	public List<List<List<XYPair>>> getPbDot(PlotInputs parFile){
		
		List<List<List<XYPair>>> dataList = new ArrayList<List<List<XYPair>>>();
		dataList.add(null);
		double n = 2 * Constants.PI / (parFile.getPb() * Constants.DAY);

		double pbdotk = -192 * Constants.PI * Math.pow ((n * Constants.TSUN), Constants.five3rd) / 5.0;
		pbdotk = pbdotk * (1 + parFile.getEccintricity()*parFile.getEccintricity()*73.0/24.0 +  Math.pow(parFile.getEccintricity(), 4)*37.0/96.0);
		pbdotk = pbdotk * Math.pow((1 - parFile.getEccintricity()*parFile.getEccintricity()),-3.5);


		for (int npb = 1; npb <= 3; npb++)
		{
			List<List<XYPair>> pairList = new LinkedList<List<XYPair>>();
			for(int sign=-1;sign<=1;sign++){
				if(sign==0) continue;
				List<XYPair> signlist = new LinkedList<XYPair>();
				double pbdot = parFile.getPbDot() + sign*npb*parFile.getePbDot();
				//if(npb==2)
					//pbdot += sign*0.2*parFile.getePbDot();
				for (int k = 1; k < Constants.NM1; k++)
				{
					/* First: cycle through mass rations */
					double R = 0.1 * k;
					double m2 = pbdot * Math.pow((1+R), Constants.one3rd)/(R * pbdotk);
					m2 = Math.pow(m2,0.6);
					double m1 = R * m2;
					signlist.add(new XYPair(m1, m2));
				}
				//System.out.println(signlist.size());
				pairList.add(signlist);
				
			}	
			dataList.add(pairList);
		}
		return dataList;
	}

	
	
	public void calculate(MMBean mmBean)
	{

		  /* declare orbital and derived parameters */
		  double f, m1, m2, s=0, ds, pb, n, e;
		  double wdot, ewdot, M, Ml, Mu, Mp;
		  double gamma, gammam, egamma, gammak, pbdot, pbdotm, epbdot, pbdotk;
		  double R;


		  /* declare counters and flags*/
		  int d1, k;

		  /* declare graphical parameters */
		  double dm, m1l, m1u, m2l, m2u,mlow, mhigh;

		  /* declare line and probability/countour parameters */
		  int ni, nilines, nm, nmlines, ng, nglines, npb, npblines;
		  double[] si= new double[Constants.NLMAX];
		  double[] sigmam= new double[Constants.NLMAX];
		  double[] sigmag= new double[Constants.NLMAX];
		  double[] sigmapb= new double[Constants.NLMAX];
		  double[] mx= new double[Constants.NM];
		  double[] my= new double[Constants.NM];
		  
		  
		  f = 0.171706; //mass function
		  e= 0.17188097150810268052;//eccintricity
		  pb = 0.19765096237922123941;// period
		  wdot = 5.3099519151404161437; //omega dot
		  ewdot =0.00024106232441522529; //error on omega dot
		  gammam = 0.00074464069051630476359;//gamma
		  egamma= 0.00000746451486205490; //error on gamma
		  pbdotm=-3.9462909083283078272e-13 + 6.71e-15 -5.05e-15 ;//pbdot
		  epbdot= 1.0003284543358517209e-14 +1.73e-15 +0.44e-15;//error on pbdot
		  
		  n = 2 * Constants.PI / (pb * Constants.DAY);
		  wdot = wdot * Constants.PI / (180 * 365.2425 * Constants.DAY);

		  ewdot = ewdot * Constants.PI / (180 * 365.2425 * Constants.DAY);

		  M = wdot * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
		  M = Math.pow (M,1.5);
		  System.out.println("\n The total mass is"+M+" solar masses.\n");

		  Ml = (wdot - 1 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
		  Ml = Math.pow (Ml,1.5);
		  Mu = (wdot + 1 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
		  Mu = Math.pow (Mu,1.5);

		  System.out.println("\n Lower limit (1-sigma) is "+ Ml+" solar masses.\n");
		  System.out.println("\n Upper limit (1-sigma) is "+ Mu+" solar masses.\n");

		  Ml = (wdot - 2 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
		  Ml = Math.pow (Ml,1.5);
		  Mu = (wdot + 2 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
		  Mu = Math.pow (Mu,1.5);

		  System.out.println("\n Lower limit (2-sigma) is "+ Ml+" solar masses.\n");
		  System.out.println("\n Upper limit (2-sigma) is "+ Mu+" solar masses.\n");

		  Ml = (wdot - 3 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
		  Ml = Math.pow (Ml,1.5);
		  Mu = (wdot + 3 * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3;
		  Mu = Math.pow (Mu,1.5);

		  System.out.println("\n Lower limit (3-sigma) is "+ Ml+" solar masses.\n");
		  System.out.println("\n Upper limit (3-sigma) is"+ Mu+" solar masses.\n");

		  m2 = M * M * f * Math.pow (s , -3);
		  m2 = Math.pow( m2, Constants.one3rd);

		  s = 1;

		  System.out.println("\n For sin i = "+s+" the mass of the companion is "+m2+" and the mass of the pulsar is"+(M-m2));

		  m2 = Ml * Ml * f * Math.pow (s , -3);
		  m2 = Math.pow( m2, Constants.one3rd);

		  System.out.println("\n In the 3-sigma lower limit of total mass, the mass of the companion is "+m2);
		  System.out.println("   the mass of the pulsar is " + (Ml-m2));

		  m2 = Mu * Mu * f * Math.pow (s , -3);
		  m2 = Math.pow( m2, Constants.one3rd);

		  System.out.println("\n In the 3-sigma upper limit of total mass, the mass of the companion is "+ m2);
		  System.out.println("   the mass of the pulsar is "+ (Mu-m2));

		  si[0] = 1;
		  nilines=1;

		  nmlines =3;
		  sigmam[0] = -1;
		  sigmam[1] = 0;
		  sigmam[2] = 1;

	      gammak = e*Math.pow(n,-Constants.one3rd)*Math.pow(Constants.TSUN,Constants.two3rd);
	      nglines = 3;
	      sigmag[0] = -1;
	      sigmag[1] = 0;
	      sigmag[2] = 1;

	      npblines = 3;
	      sigmapb[0] = -1;
	      sigmapb[1] = 0;
	      sigmapb[2] = 1;

	      pbdotk = -192 * Constants.PI * Math.pow ((n * Constants.TSUN), Constants.five3rd) / 5.0;
	      pbdotk = pbdotk * (1 + e*e*73.0/24.0 +  Math.pow(e , 4)*37.0/96.0);
	      pbdotk = pbdotk * Math.pow((1 - e*e),-3.5);

	      d1 = 'y'; //hatch

		  m1l=1.2;
		  m1u=1.4;
		  m2l=0.9;
		  m2u=1.1;
		  m1l=m2l= 0;
		  m1u=m2u= 2.5;
		  dm = (m1u - m1l)/(Constants.NM1-1);
		    
		  mlow = 1.20;
	      mhigh = 1.4408;

	      
	     
	      
	     
	      
	    
	      
	     
	      if (d1 == 'y')
  	    {
  	      ds = (m1u - m1l) / Constants.NHH;

  	      for (k = 0; k < Constants.NHH; k++)
  		{
  	      double massfuncx[] = new double[Constants.NM1];
  		  double massfuncy[] = new double[Constants.NM1];
  		  /* now, we specify points near the X axis */
  		massfuncx[0] = m1l + k * ds;
  		massfuncy[0] = m2l;

  		  M = massfuncx[0] + massfuncy[0];
  		  /* How does the total mass translate at the mass function (sin i = 1)? */
  		massfuncy[1] = Math.pow( ( f * M * M ), Constants.one3rd);
  		massfuncx[1] = M - massfuncy[1];
  		 // mmBean.getMassFunc().populateMassFuncdot(massfuncx, massfuncy);
  		  //cpgline(2, mx, my);
  		}

  	      for (k = 0; k < Constants.NHH; k++)
  		{
  	    	 double massfuncx[] = new double[Constants.NM1];
  	  		  double massfuncy[] = new double[Constants.NM1];
  		  /* now, we specify points near the Y axis */
  	  		massfuncx[0] = m1u;
  	  	massfuncy[0] = m2l + k * ds;

  		  M = massfuncx[0] + massfuncy[0];
  		  /* How does the total mass translate at the mass function (sin i = 1)? */
  		massfuncy[1] = Math.pow( ( f * M * M ), Constants.one3rd);
  		massfuncx[1] = M - massfuncy[1];
  		//  mmBean.getMassFunc().populateMassFuncdot(massfuncx, massfuncy);

  		  //cpgline(2, mx, my);
  		}
  	    } 	
	}
	
}
