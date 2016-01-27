package com.swin.manager;


//package manager;
//import bean.MMBean;
//import utilities.*;
//public class calcopy2 {
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
//	public void calculate(MMBean mmBean)
//	{
//
//		  /* declare orbital and derived parameters */
//		  double f, m1, m2, s=0, ds, pb, n, e;
//		  double wdot, ewdot, M, Ml, Mu, Mp;
//		  double gamma, gammam, egamma, gammak, pbdot, pbdotm, epbdot, pbdotk;
//		  double R;
//
//
//		  /* declare counters and flags*/
//		  int d1, k;
//
//		  /* declare graphical parameters */
//		  double dm, m1l, m1u, m2l, m2u,mlow, mhigh;
//
//		  /* declare line and probability/countour parameters */
//		  int ni, nilines, nm, nmlines, ng, nglines, npb, npblines;
//		  double[] si= new double[Constants.NLMAX];
//		  double[] sigmam= new double[Constants.NLMAX];
//		  double[] sigmag= new double[Constants.NLMAX];
//		  double[] sigmapb= new double[Constants.NLMAX];
//		  double[] mx= new double[Constants.NM];
//		  double[] my= new double[Constants.NM];
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
//		  
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
//		  nmlines =3;
//		  sigmam[0] = -1;
//		  sigmam[1] = 0;
//		  sigmam[2] = 1;
//
//	      gammak = e*Math.pow(n,-Constants.one3rd)*Math.pow(Constants.TSUN,Constants.two3rd);
//	      nglines = 3;
//	      sigmag[0] = -1;
//	      sigmag[1] = 0;
//	      sigmag[2] = 1;
//
//	      npblines = 3;
//	      sigmapb[0] = -1;
//	      sigmapb[1] = 0;
//	      sigmapb[2] = 1;
//
//	      pbdotk = -192 * Constants.PI * Math.pow ((n * Constants.TSUN), Constants.five3rd) / 5.0;
//	      pbdotk = pbdotk * (1 + e*e*73.0/24.0 +  Math.pow(e , 4)*37.0/96.0);
//	      pbdotk = pbdotk * Math.pow((1 - e*e),-3.5);
//
//	      d1 = 'y'; //hatch
//
//		  m1l=1.2;
//		  m1u=1.4;
//		  m2l=0.9;
//		  m2u=1.1;
//		  m1l=m2l= 0;
//		  m1u=m2u= 2.5;
//		  dm = (m1u - m1l)/(Constants.NM1-1);
//		    
//		  mlow = 1.20;
//	      mhigh = 1.4408;
//	
////	      mx[0] = mlow;
////	      my[0] = m2l;
////	      mx[1] = mlow;
////	      my[1] = m2u;
////	      mx[3] = mhigh;
////	      my[3] = m2l;
////	      mx[2] = mhigh;
////	      my[2] = m2u;
//	      
//	      for (ni = 0; ni < nilines; ni++)
//    	    {
//	    	  double massfuncx[] = new double[Constants.NM1];
//    		  double massfuncy[] = new double[Constants.NM1];
//    	      s = si[ni];
//    	      /* calculate m2 for mp = 0 */
//    	      m2 = f/(s*s*s);
//    	      massfuncx[0] = 0; massfuncy[0] = m2;
//
//    	      for (k = 1; k < Constants.NM1; k++)
//    		{
//    		  /* First: cycle through mass ratios */
//    		  R = 10.0/(1.0*k);
//    		  massfuncx[k] = m2 * (1+R)*(1+R)/(R*R*R);
//    		  massfuncy[k] = R*massfuncx[k];
//    		}
//    	      mmBean.getMassFunc().populateMassFuncdot(massfuncx, massfuncy);
//    	      // plot mx vs my NM1 elements
//    	    }
//	      
//	      for (nm = 0; nm < nmlines; nm++)
//    		{
//    		  Mp = (wdot + sigmam[nm] * ewdot) * (1 - e * e) * Math.pow ( n , -Constants.five3rd) * Math.pow( Constants.TSUN , -Constants.two3rd) / 3.0;
//    		  Mp = Math.pow (Mp,1.5);
//
//    		  /* How does the total mass translate at the mass function (sin i = 1)? */
//    		  double omdotx[] = new double[2];
//    		  double omdoty[] = new double[2];
//    		  omdoty[0] = Math.pow( ( f * Mp * Mp ), Constants.one3rd);
//    		  omdotx[0] = Mp - omdoty[0];
//
//    		  omdotx[1] = 0.0;
//    		  omdoty[1] = Mp;
//    		  // plot mx vs my 2  
//    		  
//    		  mmBean.getOmdot().populateOmdot(omdotx, omdoty);
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
//    		  double gammax[] = new double[Constants.NM1];
//    		  double gammay[] = new double[Constants.NM1];
//    		  /* printf("\n e = %f, n = %f\n", e, n);
//    		     printf("\n Initial mass guess = %f solar masses\n", m2);*/
//    		 /* if (m2 < m2u)
//    		    {*/
//    		      for (k = 0; k < Constants.NM1; k++)
//    			{
//    			  m1 = k * dm + m1l;
//    			  gammax[k] = m1;
//    			  m2 = mass(1,m2,gamma,gammak,m1);
//    			  gammay[k] = m2;
//    			  /* see if, for sin i = 1, we get a value larger than the mass function */
//    			}
//    		      mmBean.getGamma().populateGamma(gammax, gammay);
//    		     // cpgline(Constants.NM1, mx, my);
//    		    //}
//    		}
//	      
//	      for (npb = 0; npb < npblines; npb++)
//    		{
//    		  pbdot = pbdotm + sigmapb[npb]*epbdot;
//
//    		  /* ludicrous values for first entry in vector - not likely to be displayed */
//    		  double pbdotx[] = new double[Constants.NM1];
//    		  double pbdoty[] = new double[Constants.NM1];
//    		  pbdotx[0] = 10;
//    		  pbdoty[0] = 0;
//    		  for (k = 1; k < Constants.NM1; k++)
//    		    {
//    		      /* First: cycle through mass rations */
//    		      R = 0.1 * k;
//    		      m2 = pbdot * Math.pow((1+R), Constants.one3rd)/(R * pbdotk);
//    		      m2 = Math.pow(m2,0.6);
//    		      m1 = R * m2;
//    		      pbdotx[k] = m1;
//    		      pbdoty[k] = m2;
//    		    }
//    		  mmBean.getPbdot().populatePbdot(pbdotx,pbdoty);
//    		 // cpgline(Constants.NM1, mx, my);
//    		}
//	      if (d1 == 'y')
//  	    {
//  	      ds = (m1u - m1l) / Constants.NHH;
//
//  	      for (k = 0; k < Constants.NHH; k++)
//  		{
//  	      double massfuncx[] = new double[Constants.NM1];
//  		  double massfuncy[] = new double[Constants.NM1];
//  		  /* now, we specify points near the X axis */
//  		massfuncx[0] = m1l + k * ds;
//  		massfuncy[0] = m2l;
//
//  		  M = massfuncx[0] + massfuncy[0];
//  		  /* How does the total mass translate at the mass function (sin i = 1)? */
//  		massfuncy[1] = Math.pow( ( f * M * M ), Constants.one3rd);
//  		massfuncx[1] = M - massfuncy[1];
//  		 // mmBean.getMassFunc().populateMassFuncdot(massfuncx, massfuncy);
//  		  //cpgline(2, mx, my);
//  		}
//
//  	      for (k = 0; k < Constants.NHH; k++)
//  		{
//  	    	 double massfuncx[] = new double[Constants.NM1];
//  	  		  double massfuncy[] = new double[Constants.NM1];
//  		  /* now, we specify points near the Y axis */
//  	  		massfuncx[0] = m1u;
//  	  	massfuncy[0] = m2l + k * ds;
//
//  		  M = massfuncx[0] + massfuncy[0];
//  		  /* How does the total mass translate at the mass function (sin i = 1)? */
//  		massfuncy[1] = Math.pow( ( f * M * M ), Constants.one3rd);
//  		massfuncx[1] = M - massfuncy[1];
//  		//  mmBean.getMassFunc().populateMassFuncdot(massfuncx, massfuncy);
//
//  		  //cpgline(2, mx, my);
//  		}
//  	    } 	
//	}
//	
//}





//        TableView<ParTable> table = new TableView<>();
//        TableColumn<ParTable,String> parameter = new TableColumn<ParTable, String>("parameter");
//        parameter.setEditable(false);
//        TableColumn<ParTable,String> value = new TableColumn<ParTable, String>("value");
//        value.setEditable(true);
//        System.out.println(table.getColumns().size());
//        final ObservableList<ParTable> data = FXCollections.observableArrayList();
//        data.add(new ParTable(ParConstants.massFunc, parFile.getMassFunc()));
//        data.add(new ParTable(ParConstants.eccintricity, parFile.getEccintricity()));
//        data.add(new ParTable(ParConstants.pb, parFile.getPb()));
//        //data.add(new ParTable(ParConstants.omDot, parFile.getOmDot()));
//
//        parameter.setCellValueFactory(new PropertyValueFactory<ParTable, String>("parameter"));
//        value.setCellValueFactory(new PropertyValueFactory<ParTable, String>("value"));
//        table.setItems(data);
//        table.getColumns().add(parameter);
//        table.getColumns().add(value);
//        table.autosize();
