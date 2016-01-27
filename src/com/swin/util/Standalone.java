package com.swin.util;

import java.io.File;
import java.util.List;

import com.swin.bean.Bounds;
import com.swin.bean.PKBean;
import com.swin.bean.PlotInputs;
import com.swin.bean.XYPair;
import com.swin.exception.InvalidBoundsException;
import com.swin.manager.ParParser;

public class Standalone {
public static void main(String[] args) throws Exception {
	ParParser p = new ParParser(new File("/Users/vkrishnan/Desktop/poster.par"));
	PlotInputs inp = p.parsePar();
	Bounds bounds = new Bounds(0.0, 3.0, 1000, false);
	PKBean.setPb(0.19765096245435733278);
	PKBean.setEccintricity(0.17188249393977410962);
	PKBean.setAlpha(0.0);
	PKBean.setBeta(0.0);
	PKBean.setAlphaPulsar(0.0);
	PKBean.setBetaPulsar(0.0);
	PKBean.setAlphaCompanion(0.0);
	PKBean.setBetaCompanion(0.0);
	PKBean.compute();
	System.err.println(PKBean.getTotalMass(5.3102844266744118193));
	double val1 = PKBean.getValue(Constants.pbdot, 1.31298220, 0.9726287319007441);
	double val2 = PKBean.getValue(Constants.pbdot, 1.3170187, 0.9766251548859686);
	inp.setPbKin(7.96E-15);
	inp.setPbKin(5.05E-15);
	inp.setePbGal(4.4E-16);
	double val3 = inp.getPbDotFixed();
	System.err.println(val1);
	System.err.println(val2);
	System.err.println(val3);
	System.err.println((val3-val1)/val1);
	System.err.println((val3-val2)/val2);


	// mass func 0.176556918153
	
	PKBean omdotBean  = new PKBean(Constants.omdot, 5.3102844266744118193, 0.00006147865539663221, bounds);
List<XYPair> pairs = omdotBean.computeCurveForSigma(0.0);
System.err.println(pairs.get(0).getX() + " " +pairs.get(0).getY());
	}
}
