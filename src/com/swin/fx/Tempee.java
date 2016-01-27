package com.swin.fx;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.SimpleUnivariateValueChecker;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import com.swin.bean.XYPair;
import com.swin.util.Constants;

public class Tempee {
public static void main(String[] args) {
	double pbDot = -1.2492767114557762494e-12 ;
	double ePbDot = 2.774854456471300237e-15 ;
	double e = 0.087791481627623297065;
	
	double twoPiOverPb = 2 * Math.PI / ( 0.10225156238190813864 * Constants.secondsInADay);
	double numPts = 1000;
	double alpha = 0;
	double beta = 0;
			
	double dipolarPb = -(Math.PI/3.0)*Math.pow(twoPiOverPb,-5.0/3.0)*Math.pow(Constants.TSUN, 5.0/3.0);
	double quadrupolarPb = -(32*Math.PI/5.0)*Math.pow(twoPiOverPb,5.0/3.0)*Math.pow(Constants.TSUN, 5.0/3.0);
	
	double dipolarEcc = e*e*(1+0.25*e*e)*Math.pow(1-e*e, -3.5);
	double quadrupolarEcc = (1+73*e*e/24.0+37*e*e*e*e/96.0)*Math.pow(1-e*e, -3.5);
	
	double dipolarAlpha = alpha*alpha*Math.pow((8*(1+alpha*alpha)-6*beta),2)*Math.pow(1+alpha*alpha,4.0/3.0);
	double quadrupolarAlpha = Math.pow(1+alpha*alpha,2.0/3.0)*(6+alpha*alpha);
	
	double dipolarRest = dipolarPb*dipolarEcc*dipolarAlpha;
	double quadrupolarRest = quadrupolarPb*quadrupolarEcc*quadrupolarAlpha;
	
	double rest  = dipolarRest + quadrupolarRest;
	
	System.err.println(quadrupolarRest);
	double loopPbDot = pbDot + ePbDot;
	System.err.println("loop pbdot "+loopPbDot);
		double mRatio = 0.1*1.07;
		double mCompanion = Math.pow(loopPbDot * Math.pow((1+mRatio), 1/3.0)/(mRatio * rest),3/5.0);
		double mPulsar = mRatio*mCompanion;
	System.err.println(mRatio+" "+mPulsar + " "+mCompanion);
	
	
	double n = 2 * Constants.PI / (0.10225156238190813864 * Constants.DAY);

	double pbdotk = -32 * Constants.PI * Math.pow ((n * Constants.TSUN), Constants.five3rd) / 5.0;
	System.err.println(pbdotk);
	pbdotk = pbdotk * (1 + e*e*73.0/24.0 +  Math.pow(e, 4)*37.0/96.0);
	pbdotk = pbdotk * Math.pow((1 - e*e),-3.5);
	System.err.println(pbdotk);
	System.err.println(pbdotk/6.0);

}
}
