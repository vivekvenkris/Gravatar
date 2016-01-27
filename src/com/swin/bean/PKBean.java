package com.swin.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.scene.shape.Polygon;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;

import com.sun.javafx.geom.AreaOp.XorOp;
import com.swin.util.Constants;

public class PKBean {
	private String name;
	private Double value;
	private Double error;
	private Double errorPercent;
	private Double errorSigma;
	private Bounds bounds;
	private Map<Double,List<List<XYPair>>> curves;

	private static Double eccintricity;
	private static Double pb;
	private static Double PbKin;
	private static Double ePbKin;
	private static Double PbGal;
	private static Double ePbGal;
	
	private static Double alpha;
	private static Double beta;
	private static Double ka;
	
	private static Double alphaPulsar;
	private static Double alphaCompanion;	
	private static Double betaPulsar;
	private static Double betaCompanion;
	
	
	private static Double constGamma;
	private static Double constOmDot;
	private static Double twoPiOverPb;
	private static Double dipolarPb;
	private static Double quadrupolarPb;
	private static Double dipolarEcc;
	private static Double quadrupolarEcc;
	private static Double dipolarST;
	private static Double quadrupolarST;
	private static Double restGamma;
	private static Double oneMinusEccSq;
	private static Double restOmDot;
	private static Double numeratorOmDotST;
	private static Double denominatorOmDotST;
	private static Double omDotST;
	private static Double dipolarRest;
	private static Double quadrupolarRest;
	private static Double restPbDot; 
	private static boolean recompute = true;

	public PKBean() {
		// TODO Auto-generated constructor stub
	}
	public PKBean(String name,Double value, Double error,Bounds bounds){
		this.name = name;
		this.value = value;
		this.error = error;
		this.errorPercent = this.error/this.value;
		this.errorSigma = 1.0/this.errorPercent;
		this.bounds = bounds;
		this.curves = computeCurves();
	}

	public Double getValueFixed(){
		Double value = 0.0;
		switch (this.name) {
		case Constants.pbdot:
			value= this.value+PbGal-PbKin;
		break;
		default:
			value= this.value;
		}
		return value;
	}
	public Double getErrorFixed(){
		Double error = 0.0;
		switch (this.name) {
		case Constants.pbdot:
			error= this.error+ePbGal+ePbKin;
		break;
		default:
			error= this.error;
		}
		return error;
	}
	public Map<Double,List<List<XYPair>>> computeCurves(){
		Map<Double,List<List<XYPair>>> curvesList = new HashMap<Double, List<List<XYPair>>>();
		for(int sigma = 1;sigma<=3;sigma++){
			curvesList.put(new Double(sigma),computeCurvesForSigma(new Double(sigma)));
		}
		return curvesList;
	}
	
	public List<List<XYPair>> getCurvesForSigma(Double sigma){
		if(this.curves!=null&& this.curves.get(sigma)!=null){
			return this.curves.get(sigma);
		}
		else{
			return computeCurvesForSigma(sigma);

		}
	}
	
	public List<XYPair> getCurveForSigma(Double sigma){
		if(this.curves!=null&& this.curves.get(sigma)!=null){
			if(sigma>=0){
				return this.curves.get(sigma).get(1);
			}
			else{
				return this.curves.get(sigma).get(0);
			}
		}
		else{
			return computeCurveForSigma(sigma);
		}
	}
	
	public List<List<XYPair>> computeCurvesForSigma(Double sigma){
		List<List<XYPair>> curvesForSigmaList = new ArrayList<List<XYPair>>();
		if(!this.name.equals(Constants.massfunc))
		curvesForSigmaList.add(computeCurveForSigma(-sigma));
		curvesForSigmaList.add(computeCurveForSigma(sigma));

		return curvesForSigmaList;
	}
	
	public static double getTotalMass(double omdot){
		omdot = (omdot)*Math.PI/(180*365.2425*Constants.secondsInADay);
		double mTotal = Math.pow (omdot,1.5)*restOmDot*omDotST;

		return mTotal;
	}
	
	public List<XYPair> computeCurveForSigma(Double sigma){
		if(recompute) compute();
		List<XYPair> result = new ArrayList<XYPair>();
		switch (this.name) {
		case Constants.massfunc:

			double mf = (this.value + sigma*this.error)/(1*1*1); //  sini = 1 --> max limit
			result.add(new XYPair(0.0,mf));
			for(int i=1;i<=bounds.getNumPts();i++){
				double R = bounds.getValueForPoint(i);// R = Mc/Mp
				double x = mf * (1+R)*(1+R)/(R*R*R);
				double y = R*x;
				result.add(new XYPair(x,y));
			}

			break;
		case Constants.intersection:

			double m2 = (this.value + sigma*this.error)/(1*1*1); //  sini = 1 --> max limit
			result.add(new XYPair(0.0,m2));
			for(int i=1;i<=bounds.getNumPts();i++){
				double R = 10.0/(1.0*i);// R = Mc/Mp
				double x = m2 * (1+R)*(1+R)/(R*R*R);
				double y = R*x;
				result.add(new XYPair(x,y));
			}

			break;
		case Constants.massratio:
			double massRatio = this.value + sigma*this.error;
			System.err.println("mass ratio"+bounds);
			for(int i=1;i<=bounds.getNumPts();i++){
				double mPulsar = bounds.getValueForPoint(i);
				double mCompanion = mPulsar/massRatio;
				result.add(new XYPair(mPulsar,mCompanion));
			}

			break;
		case Constants.pbdot:
			double pbDot = this.value - PbKin + PbGal+ sigma*(this.error+ePbGal+ePbKin);
			for(int i=1;i<=bounds.getNumPts();i++){
				double mRatio = bounds.getValueForPoint(i);
				double molopole5third = -3*Math.PI* Math.pow(Constants.TSUN*twoPiOverPb, 5.0/3.0)*Math.pow((1+alphaPulsar*alphaCompanion), 2/3.0)*Math.pow(mRatio+1, -1/3.0)*mRatio*eccintricity*eccintricity*
						(1+0.25*eccintricity*eccintricity)*Math.pow((1-eccintricity*eccintricity),-3.5)*Math.pow((5*(alphaPulsar + alphaCompanion)/3.0
								-2*(alphaPulsar*mRatio+alphaCompanion)/(3*(1+mRatio)) + 
								(betaPulsar*alphaCompanion + betaCompanion*alphaPulsar)/(1+alphaPulsar*alphaCompanion)),2);
				
				
				double dipole5third = -4*Math.PI*Math.pow(Constants.TSUN*twoPiOverPb, 5.0/3.0)*Math.pow(mRatio+1, -1/3.0)*mRatio*Math.pow((1-eccintricity*eccintricity),-3.5)*
						Math.pow((1+alphaPulsar*alphaCompanion),2/3.0)*((8*(1+31*eccintricity*eccintricity/8.0 + 19*Math.pow(eccintricity, 4)/32.0)/5.0)*(alphaPulsar - alphaCompanion)
								* ((alphaPulsar*mRatio+alphaCompanion)/(mRatio+1))*((mRatio-1)/(mRatio+1)) + (1 + 3*eccintricity*eccintricity + 3*Math.pow(eccintricity, 4)/8.0)
								*(alphaPulsar - alphaCompanion)* (betaCompanion*alphaPulsar*mRatio - betaPulsar*alphaCompanion)/((1+alphaPulsar*alphaCompanion)*(mRatio+1)));
				
				
				double dipolemb = -2*Math.PI*Constants.TSUN*twoPiOverPb*Math.pow((1-eccintricity*eccintricity),-2.5)*(1+ 0.5*eccintricity*eccintricity)*(1/(mRatio+1))*mRatio
						*Math.pow((alphaPulsar-alphaCompanion),2);
				
				double quadphi5third =-(1/5.0)* 32*Math.PI*Math.pow(Constants.TSUN*twoPiOverPb, 5.0/3.0)*Math.pow(mRatio+1, -1/3.0)*mRatio* (1 + 73*eccintricity*eccintricity/24.0 +37*Math.pow(eccintricity, 4)/96.0)
						*Math.pow((1-eccintricity*eccintricity),-3.5)*((alphaPulsar+alphaCompanion)- (alphaPulsar*mRatio + alphaCompanion)/(1+mRatio));
				double quadg5third = -(1/5.0)*192*Math.PI*mRatio*Math.pow((1+alphaPulsar*alphaCompanion),2/3.0)*Math.pow(Constants.TSUN*twoPiOverPb, 5/3.0)*Math.pow(mRatio+1, -1/3.0)*(1 + 73*eccintricity*eccintricity/24.0 +37*Math.pow(eccintricity, 4)/96.0)
						*Math.pow((1-eccintricity*eccintricity),-3.5);
				
				double coeff5third = molopole5third + dipole5third + quadg5third + quadphi5third;
				double coefflin= dipolemb;
				//System.err.println(molopole5third + " "+dipole5third +" "+ quadg5third +" "+ quadphi5third + " "+ pbDot+ " "+coeff5third + " " + coefflin);
			//	double mCompanion = Math.pow(pbDot/quadg5third,3/5.0); 
				
				UnivariateFunction uf = new UnivariateFunction() {
					@Override
					public double value(double mCompanion) {
						double left = coeff5third* Math.pow(mCompanion, 5/3.0);
						double right = (-coefflin*mCompanion + pbDot);
					//	System.err.println( mCompanion + " " +left + "-" + right + "="+(left-right)+ " "+ mRatio);
						return (left-right);
					}
				};
				BrentSolver solver = new BrentSolver();
				double mCompanion = 0;
				try{
					mCompanion = solver.solve(10000,uf, 0.001,3.0);
					double mPulsar = mRatio*mCompanion;
					result.add(new XYPair(mPulsar, mCompanion));
					//System.err.println(i + " "  + mRatio + " "+ mPulsar + " "+ mCompanion);
				}
				catch(Exception e){
					//System.err.println(i + " "+e.getMessage());
				}
			}
			
			break;
		case Constants.pbdotFreire:
			double pbDotFreire = this.value - PbKin + PbGal+ sigma*(this.error+ePbGal+ePbKin);
			System.err.println("inside this:" + pbDotFreire);
			
			double gammaPPN = 1-2*alpha*alpha/(1+alpha*alpha);
			double betaPPN = 1+ 0.5* beta*alpha*alpha/(1+alpha*alpha);
			double etaPPN = 4*betaPPN - gammaPPN -3;
			
			double kd = 0;
			if(alpha !=0 || beta!=0) kd = 2*etaPPN*etaPPN/(1-gammaPPN);
			System.err.println(gammaPPN + " "+betaPPN + " "+etaPPN + " "+kd);
			double sp = 0.15;
			for(int i=1;i<=bounds.getNumPts();i++){
				double mRatio = bounds.getValueForPoint(i);
				double pbDotGR = -192.0*Math.PI/5.0;
				pbDotGR*=Math.pow(twoPiOverPb*Constants.TSUN, 5.0/3.0);
				pbDotGR*=quadrupolarEcc;
				pbDotGR= pbDotGR*mRatio/Math.pow((1+mRatio),0.33333);
				double pbDotD = -2*Math.PI*twoPiOverPb*Constants.TSUN*kd*sp*sp;
				System.err.println("pbdot D: "+pbDotD);
				pbDotD*=dipolarEcc;
				System.err.println("pbdot D: "+pbDotD);
				pbDotD= pbDotD*mRatio/(1+mRatio);
				double finPbDotGR = pbDotGR;
				double finPbDotD = pbDotD;
				System.err.println(mRatio+" "+finPbDotGR + " "+finPbDotD);
				UnivariateFunction uf = new UnivariateFunction() {
					@Override
					public double value(double mCompanion) {
						double result= (pbDotFreire - finPbDotGR*Math.pow(mCompanion,-1.666666) + finPbDotD*mCompanion);
						System.err.println("result:"+mCompanion + " "+result);
						return result;
					}
				};
				BrentSolver solver = new BrentSolver();
				double mCompanion = solver.solve(10000,uf, 0.01,3.0);
				double mPulsar = mRatio*mCompanion;
				result.add(new XYPair(mPulsar,mCompanion));
				
			}
			break;
		case Constants.omdot:
			double omDot = (this.value + sigma*this.error)*Math.PI/(180*365.2425*Constants.secondsInADay);
			//System.err.println(constOmDot*omDot); System.exit(0);
			for(int i=1;i<=bounds.getNumPts();i++){
				double mRatio = bounds.getValueForPoint(i);
				UnivariateFunction uf = new UnivariateFunction() {

					public double value(double mCompanion) {
						double left = Math.pow(mRatio+1,2/3.0)*(6+4*alphaPulsar*alphaCompanion-2*alphaPulsar*alphaPulsar*alphaCompanion*alphaCompanion)*Math.pow(mCompanion, 2/3.0);
						double right = +mCompanion*(mRatio*alphaPulsar*alphaPulsar*betaCompanion + betaPulsar*alphaCompanion*alphaCompanion)+ constOmDot*omDot*Math.pow(1+alphaCompanion*alphaPulsar, 4/3.0);
						//System.err.println("kekka pikka woodwards gryp water.");
						return (left - right);
					}
				};
				BrentSolver solver = new BrentSolver();
			//	System.err.println(mRatio);
				double mCompanion = solver.solve(10000,uf, 0.001,3);
				double mPulsar = mRatio*mCompanion;
				//System.err.println(mPulsar + " " + mCompanion);
				result.add(new XYPair(mPulsar,mCompanion));
			}
			break;
		case Constants.gamma:
			double gamma = this.value + sigma*this.error;
			for(int i=1;i<=bounds.getNumPts();i++){
				double mRatio =bounds.getValueForPoint(i);
				double mCompanion = Math.pow(constGamma*gamma*Math.pow(1+alphaCompanion*alphaPulsar, 1/3.0)*Math.pow(mRatio+1, 4/3.0)/(mRatio+2+alphaPulsar*alphaCompanion + (mRatio+1)*ka*alphaCompanion),1.5);
				double mPulsar = mRatio*mCompanion;
				result.add(new XYPair(mPulsar,mCompanion));
			}
			break;
		
		default:
			break;
		}
		return result;
	}

	public Double getSigma(Double newValue){
		return (this.value-newValue)/this.error;
	}

	public static Double getValue(String name, Double mPulsar, Double mCompanion){
		Double value = 0.0;
		Double mTotal = mPulsar+mCompanion;
		Double mProduct = mPulsar*mCompanion;
		Double mNu = mProduct/(mTotal*mTotal);
		Double alphaProd = alphaPulsar*alphaCompanion;
		Double alphaSum = alphaPulsar + alphaCompanion;
		Double alphaDiff = alphaPulsar - alphaCompanion;
		Double xPulsar = mPulsar/mTotal;
		Double xCompanion = mCompanion/mTotal;
		Double xDiff = xPulsar-xCompanion;
		switch (name) {
		case Constants.massfunc:
			// for sin i = 1;
			value =  mCompanion*mCompanion*mCompanion/((mPulsar+mCompanion)*(mPulsar+mCompanion));
			
			break;
		case Constants.massratio:

			value = mPulsar/mCompanion;
			
			break;
		case Constants.pbdot:
			
			double monopole,dipole1,dipole2,quadphi,quadg;
			
			monopole = -3*Math.PI* Math.pow(twoPiOverPb*Constants.TSUN*mTotal, 5/3.0) * mNu*Math.pow(1+alphaProd, 2/3.0)*eccintricity*eccintricity
					*(1+0.25*eccintricity*eccintricity)*Math.pow(1-eccintricity*eccintricity, -3.5)*Math.pow((5*alphaSum/3.0 - 2*(alphaPulsar*xPulsar + alphaCompanion*xCompanion)/3.0 
							+ (betaPulsar*alphaCompanion + betaCompanion*alphaPulsar)/(1+alphaSum)),2);
			
			dipole1 = -2*Math.PI*mNu*twoPiOverPb*Constants.TSUN*mTotal*alphaDiff*alphaDiff*(1+0.5*eccintricity*eccintricity)*Math.pow(1-eccintricity*eccintricity, -2.5);
			
			dipole2 = -4*Math.PI*mNu*Math.pow(twoPiOverPb*Constants.TSUN*mTotal, 5/3.0) * mNu*Math.pow(1+alphaProd, 2/3.0)*Math.pow(1-eccintricity*eccintricity, -3.5)*
					((8/5.0)*(1+31*eccintricity*eccintricity/8.0+ 19*Math.pow(eccintricity, 4)/32.0)*alphaDiff*xDiff
							*(alphaPulsar*xPulsar+alphaCompanion*xCompanion)+ (1+3*eccintricity*eccintricity+ 3*Math.pow(eccintricity, 4)/8.0)*alphaDiff
							*(betaCompanion*alphaPulsar*xPulsar - betaPulsar*alphaCompanion*xCompanion)/(1+alphaProd));
			
			quadphi = -(32/5.0)*Math.PI*Math.pow(twoPiOverPb*Constants.TSUN*mTotal, 5/3.0) * mNu*Math.pow(1+alphaProd, 2/3.0)*Math.pow(1-eccintricity*eccintricity, -3.5)
					*(1+73*eccintricity*eccintricity/24.0+37*Math.pow(eccintricity, 4)/96.0)*Math.pow(alphaSum -(alphaPulsar*xPulsar+ alphaCompanion*xCompanion),2);
			
			quadg = -(192/5.0)*Math.PI*Math.pow(twoPiOverPb*Constants.TSUN*mTotal, 5/3.0) * mNu*Math.pow(1+alphaProd, 2/3.0)*Math.pow(1-eccintricity*eccintricity, -3.5)
					*(1+73*eccintricity*eccintricity/24.0+37*Math.pow(eccintricity, 4)/96.0);
			value = monopole+dipole1+dipole2+quadg+quadphi;
			
		//	System.err.println(monopole+" "+dipole1+" "+dipole2+" "+quadg+" "+quadphi);
			break;
		case Constants.omdot:
			
			value = (3/oneMinusEccSq)*Math.pow(twoPiOverPb,5.0/3.0)*Math.pow(Constants.TSUN*(mTotal)*(1+alphaCompanion*alphaPulsar), 2/3.0);
			value*= ((3-alphaPulsar*alphaCompanion/(3*(1+alphaPulsar*alphaCompanion)))- (mPulsar*betaCompanion*alphaPulsar*alphaPulsar + mCompanion*betaPulsar*alphaCompanion*alphaCompanion)/(6*mTotal*Math.pow(1+alphaCompanion*alphaPulsar,2)));
			value *=(180*365.2425*Constants.secondsInADay)/Math.PI;
			break;
		case Constants.gamma:
			
			value = eccintricity*mCompanion*Math.pow(Constants.TSUN, 2/3.0);
			value*= Math.pow(twoPiOverPb* mTotal*(1+alphaPulsar*alphaCompanion),-1/3.0);
			value*=(mCompanion*(1+alphaPulsar*alphaCompanion)/mTotal + ka*alphaCompanion+1);
			
			break;


		default:
			break;
		}
		return value;

	}

	public static void compute(){
	//	alphaPulsar = alphaCompanion = alpha;
	//	betaPulsar = betaCompanion = beta;

		/* 2π/Pb*/
		twoPiOverPb = 2 * Math.PI / (pb * Constants.secondsInADay);

		/* 1-e^2*/
		oneMinusEccSq =  (1.0 - eccintricity*eccintricity);

		/* ((1-e^2)*(2π/Pb)^(-5/3)* (T⊙)^(-2/3)/3)^1.5 */
		restOmDot = Math.pow(oneMinusEccSq*Math.pow(twoPiOverPb, -5.0/3.0)* Math.pow(Constants.TSUN , -2.0/3.0)/3.0,1.5);
		
		/* (-2* (1-e^2)*(2π/Pb)^(-5/3)* (T⊙)^(-2/3))*/
		constOmDot = 2*oneMinusEccSq*Math.pow(twoPiOverPb, -5.0/3.0)* Math.pow(Constants.TSUN,-2.0/3.0);
		
		constGamma = Math.pow(twoPiOverPb,1/3.0)*Math.pow(Constants.TSUN, -2/3.0)/eccintricity;
		
		
		
		
		

		/* 6(1+α^2)^(4/3) */
		numeratorOmDotST = 6*Math.pow((1+alpha*alpha),4.0/3.0);

		/* 6 + (4-β)*α^2 + 2α^4 */
		denominatorOmDotST =  6 + ((4 - beta)*alpha*alpha) - (2*alpha*alpha*alpha*alpha);

		/*(6(1+α^2)^(4/3)) / (6 + (4-β)*α^2 + 2α^4))^1.5 */
		omDotST = Math.pow((numeratorOmDotST/denominatorOmDotST),1.5);

		/*e(2π/Pb)^(-1/3)* (T⊙)^(2/3)*/
		restGamma = eccintricity*Math.pow(twoPiOverPb, -1.0/3.0)*Math.pow(Constants.TSUN,2.0/3.0);

		/*-(π/3)(2πT⊙/Pb)^(5/3)**/
		dipolarPb = -(Math.PI/3.0)*Math.pow(twoPiOverPb,5.0/3.0)*Math.pow(Constants.TSUN, 5.0/3.0);

		/*(-32π/5)(2πT⊙/Pb)^(5/3)*/
		quadrupolarPb = -(32*Math.PI/5.0)*Math.pow(twoPiOverPb,5.0/3.0)*Math.pow(Constants.TSUN, 5.0/3.0);

		/*e^2(1+e^2)/(1-e^2)^7/2*/
		dipolarEcc = eccintricity*eccintricity*(1+0.25*eccintricity*eccintricity)*Math.pow(oneMinusEccSq, -3.5);

		/*(1+ 73/24*e^2 + 37/96 * e^4)((1-e^2)^7/2) */
		quadrupolarEcc = (1+73*eccintricity*eccintricity/24.0+37*eccintricity*eccintricity*eccintricity*eccintricity/96.0)*Math.pow(oneMinusEccSq, -3.5);

		/*α^2(8(1+α^2)-6β)^2 / (1+α^2)^-4/3*/
		dipolarST = alpha*alpha*Math.pow((8*(1+alpha*alpha)-6*beta),2)*Math.pow(1+alpha*alpha,-4.0/3.0);
		/*6α^2(1+α^2)^2/3*/
		quadrupolarST = Math.pow(1+alpha*alpha,2.0/3.0)*(6+alpha*alpha);
		/**/
		dipolarRest = dipolarPb*dipolarEcc*dipolarST;
		/**/
		quadrupolarRest = quadrupolarPb*quadrupolarEcc*quadrupolarST;

		restPbDot  = dipolarRest + quadrupolarRest;

		/* setting recompute to false */
		recompute = false;
	}
	
	public List<Polygon> findIntersection(List<PKBean> PKBean){
		return null;
		
	}

	public static Double getEccintricity() {
		return eccintricity;
	}

	public static void setEccintricity(Double eccintricity) {
		PKBean.eccintricity = eccintricity;
		recompute = true;
	}

	public static Double getPb() {
		return pb;
	}

	public static void setPb(Double pb) {
		PKBean.pb = pb;
		recompute = true;
	}

	public static Double getAlpha() {
		return alpha;
	}

	public static void setAlpha(Double alpha) {
		PKBean.alpha = alpha;
		recompute = true;
	}

	public static Double getBeta() {
		return beta;
	}

	public static void setBeta(Double beta) {
		PKBean.beta = beta;
		recompute = true;
	}

	public static Double getKa() {
		return ka;
	}

	public static void setKa(Double ka) {
		PKBean.ka = ka;
		recompute = true;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getError() {
		return error;
	}
	public void setError(Double error) {
		this.error = error;
	}
	public Double getErrorPercent() {
		return errorPercent;
	}
	public void setErrorPercent(Double errorPercent) {
		this.errorPercent = errorPercent;
	}
	public Double getErrorSigma() {
		return errorSigma;
	}
	public void setErrorSigma(Double errorSigma) {
		this.errorSigma = errorSigma;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public static Double getPbKin() {
		return PbKin;
	}

	public static void setPbKin(Double pbKin) {
		PbKin = pbKin;
	}

	public static Double getePbKin() {
		return ePbKin;
	}

	public static void setePbKin(Double ePbKin) {
		PKBean.ePbKin = ePbKin;
	}

	public static Double getPbGal() {
		return PbGal;
	}

	public static void setPbGal(Double pbGal) {
		PbGal = pbGal;
	}

	public static Double getePbGal() {
		return ePbGal;
	}

	public static void setePbGal(Double ePbGal) {
		PKBean.ePbGal = ePbGal;
	}

	public Map<Double, List<List<XYPair>>> getCurves() {
		return curves;
	}

	public void setCurves(Map<Double, List<List<XYPair>>> curves) {
		this.curves = curves;
	}
	public static Double getAlphaPulsar() {
		return alphaPulsar;
	}
	public static void setAlphaPulsar(Double alphaPulsar) {
		PKBean.alphaPulsar = alphaPulsar;
	}
	public static Double getAlphaCompanion() {
		return alphaCompanion;
	}
	public static void setAlphaCompanion(Double alphaCompanion) {
		PKBean.alphaCompanion = alphaCompanion;
	}
	public static Double getBetaPulsar() {
		return betaPulsar;
	}
	public static void setBetaPulsar(Double betaPulsar) {
		PKBean.betaPulsar = betaPulsar;
	}
	public static Double getBetaCompanion() {
		return betaCompanion;
	}
	public static void setBetaCompanion(Double betaCompanion) {
		PKBean.betaCompanion = betaCompanion;
	}

	
	

}
