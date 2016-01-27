package com.swin.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;

import com.swin.bean.PlotInputs;
import com.swin.bean.XYPair;
import com.swin.util.Constants;

public class StrongFieldCalc implements Calculator{

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
	  double m2 = parFile.getMassFunc()/(s*s*s);
	  massList.add(new XYPair(0.0,m2));
	  for (int k = 1; k < Constants.NM1; k++) {
		  double R = 10.0/(1.0*k);
		  double x = m2 * (1+R)*(1+R)/(R*R*R);
		  double y = R*x;
		  massList.add(new XYPair(x,y));
	  }
	  pairList.add(massList);
	  dataList.add(pairList);
	  return dataList;
	}

	@Override
	public List<List<List<XYPair>>> getOmDot(PlotInputs plotInputs) {
		List<List<List<XYPair>>> dataList = new ArrayList<List<List<XYPair>>>();
		dataList.add(null);

		double omDot = plotInputs.getOmDot();
		double eOmDot = plotInputs.geteOmDot();

		omDot = omDot * Math.PI/(180*365.2425*Constants.secondsInADay);
		eOmDot = eOmDot * Math.PI/(180*365.2425*Constants.secondsInADay);

		double twoPiOverPb = 2 * Math.PI / (plotInputs.getPb() * Constants.secondsInADay);
		double oneMinusEccSq = 1.0 - plotInputs.getEccintricity() * plotInputs.getEccintricity();
		double rest = oneMinusEccSq*Math.pow(twoPiOverPb, -5.0/3.0)* Math.pow(Constants.TSUN , -2.0/3.0)/3.0;
		double numPts = plotInputs.getNumPts();
		double mPulsarSteps = plotInputs.getMaxMp()/numPts;
		double alpha = plotInputs.getAlpha();
		double beta = plotInputs.getBeta();
		for (int nSig = 1; nSig <= 3; nSig++)
		{
			List<List<XYPair>> pairList = new LinkedList<List<XYPair>>();
			
			for(int sign=-1;sign<=1;sign++){
				
				if(sign==0) continue;
				List<XYPair> signList = new LinkedList<XYPair>();
				
				double mTotal = Math.pow ((omDot +sign* nSig * eOmDot)*rest,1.5); 
				
				/* deleted a mass function thing here -  seems unnecessary*/
				
				/* scalar tensor part*/
				double numerator = 6*Math.pow((1+alpha*alpha),4.0/3.0);
				double denominator =  6 + ((4 - beta)*alpha*alpha) - (2*alpha*alpha*alpha*alpha);
				double stPart = Math.pow((numerator/denominator),1.5);
				
				mTotal = mTotal*stPart;
				// omdot
				for( int n=0;n<numPts;n++){
					double mPulsar = n*mPulsarSteps;
					signList.add(new XYPair(mPulsar,mTotal-mPulsar));
				}
				pairList.add(signList);
				  

			}
	 		  dataList.add(pairList);
		}
		return dataList;
	}

	
	@Override
	public List<List<List<XYPair>>> getGamma(PlotInputs plotInputs) {
		// gamma = massterms* rest of the terms
		List<List<List<XYPair>>> dataList = new ArrayList<List<List<XYPair>>>();
		dataList.add(null); // for 1- indexing
		
		double gamma = plotInputs.getGamma();
		double eGamma = plotInputs.geteGamma();
		
		double twoPiOverPb = 2 * Math.PI / (plotInputs.getPb() * Constants.secondsInADay);
		double rest = plotInputs.getEccintricity()*Math.pow(twoPiOverPb, -1.0/3.0)*Math.pow(Constants.TSUN,2.0/3.0);
		double numPts = plotInputs.getNumPts();
		double mPulsarSteps = plotInputs.getMaxMp()/numPts;
		double alpha = plotInputs.getAlpha();
		double ka = plotInputs.getKa();

		
		for (int nSig = 1; nSig <= 3; nSig++)
		{
			List<List<XYPair>> pairList = new LinkedList<List<XYPair>>();
			
			for(int sign=-1;sign<=1;sign++){
				
				if(sign==0) continue;
				List<XYPair> signList = new LinkedList<XYPair>();
				
				double loopGamma = gamma + sign*nSig*eGamma;
				for(int n=0;n<numPts;n++){
					double mPulsar = n*mPulsarSteps;
					UnivariateFunction uf = new UnivariateFunction() {
						
						@Override
						public double value(double mCompanion) {
							double left = loopGamma*Math.pow((mPulsar+mCompanion),4.0/3.0);
							double right = rest*mCompanion*((mPulsar+mCompanion*(2+alpha*alpha))+ka*alpha*(mPulsar+mCompanion))*Math.pow(1+alpha*alpha, -1.0/3);
							return (left-right);
						}
					};
					
					
					BrentSolver solver = new BrentSolver();
					double mCompanion = solver.solve(10000,uf, 0, 3);
					signList.add(new XYPair(mPulsar,mCompanion));
					}
				pairList.add(signList);
			}
			dataList.add(pairList);
		}

		return dataList;
	}
	

	@Override
	public List<List<List<XYPair>>> getPbDot(PlotInputs plotInputs) {
		List<List<List<XYPair>>> dataList = new ArrayList<List<List<XYPair>>>();
		dataList.add(null); // for 1- indexing
		double pbDot = plotInputs.getPbDot() + plotInputs.getPbGal() - plotInputs.getPbKin();
		double ePbDot = plotInputs.getePbDot() + plotInputs.getePbGal()+plotInputs.getePbKin();
		double e = plotInputs.getEccintricity();
		
		double twoPiOverPb = 2 * Math.PI / (plotInputs.getPb() * Constants.secondsInADay);
		double numPts = plotInputs.getNumPts();
		double alpha = plotInputs.getAlpha();
		double beta = plotInputs.getBeta();
				
		double dipolarPb = -(Math.PI/3.0)*Math.pow(twoPiOverPb,5.0/3.0)*Math.pow(Constants.TSUN, 5.0/3.0);
		double quadrupolarPb = -(32*Math.PI/5.0)*Math.pow(twoPiOverPb,5.0/3.0)*Math.pow(Constants.TSUN, 5.0/3.0);
		
		double dipolarEcc = e*e*(1+0.25*e*e)*Math.pow(1-e*e, -3.5);
		double quadrupolarEcc = (1+73*e*e/24.0+37*e*e*e*e/96.0)*Math.pow(1-e*e, -3.5);
		
		double dipolarAlpha = alpha*alpha*Math.pow((8*(1+alpha*alpha)-6*beta),2)*Math.pow(1+alpha*alpha,-4.0/3.0);
		double quadrupolarAlpha = Math.pow(1+alpha*alpha,2.0/3.0)*(6+alpha*alpha);
		
		double dipolarRest = dipolarPb*dipolarEcc*dipolarAlpha;
		double quadrupolarRest = quadrupolarPb*quadrupolarEcc*quadrupolarAlpha;
		
		//System.err.println(dipolarRest +"  "+quadrupolarRest);
		
		double rest  = dipolarRest + quadrupolarRest;
		
		
		
		for (int nSig = 1; nSig <= 3; nSig++)
		{
			List<List<XYPair>> pairList = new LinkedList<List<XYPair>>();
			
			for(int sign=-1;sign<=1;sign++){
				
				if(sign==0) continue;
				List<XYPair> signList = new LinkedList<XYPair>();
				double loopPbDot = pbDot + nSig*sign*ePbDot;
				for(int n=0;n<numPts;n++){
					double mRatio = 0.1*n;
					double mCompanion = Math.pow(loopPbDot * Math.pow((1+mRatio), 1/3.0)/(mRatio * rest),3/5.0);
					double mPulsar = mRatio*mCompanion;
				//	System.err.println(mPulsar + " "+mCompanion);
					signList.add(new XYPair(mPulsar, mCompanion));
				}

				pairList.add(signList);
			}
			dataList.add(pairList);
		}
		
		
		return dataList;
	}
}
