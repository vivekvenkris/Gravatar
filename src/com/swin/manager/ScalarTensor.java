package com.swin.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.swin.bean.MMBean2;
import com.swin.bean.PlotInputs;
import com.swin.bean.XYPair;
import com.swin.exception.InvalidBoundsException;

public class ScalarTensor extends Thread {
	private Double alphaMin;
	private Double alphaMax;
	private Double alphaPts;
	
	private Double logMin;
	private Double logMax;
	private Double delta;
	
	
	private Double betaMin;
	private Double betaMax;
	private Double betaPts;
	private Map<Integer,Double> stepMap;
	
	private PlotInputs plotInputs;
	public ScalarTensor(Double alphaMin, Double alphaMax, Double alphaPts,
			Double betaMin, Double betaMax, Double betaPts,PlotInputs plotInputs) {
		super();
		this.alphaMin = alphaMin;
		this.alphaMax = alphaMax;
		this.alphaPts = alphaPts;
		this.betaMin = betaMin;
		this.betaMax = betaMax;
		this.betaPts = betaPts;
		this.plotInputs = plotInputs;
		this.logMin = Math.log(alphaMin);
		this.logMax = Math.log(alphaMax);
		this.delta = (this.logMax - this.logMin) /alphaPts;
		this.stepMap = new HashMap<Integer, Double>();
		generateAlphaSteps();
	}
	
	public void generateAlphaSteps(){
		
		double accDelta = 0;
		for(int i=1;i<=alphaPts;i++){
			stepMap.put(i,Math.pow(10, logMin + accDelta));
			accDelta +=delta;
		}
	}
	
	@Override
	public void run() {
		double betaSteps = (betaMax - betaMin)/betaPts;
		if(betaMax==betaMin) betaSteps = 1;
		File file = new File("/Users/vkrishnan/Documents/GR_workspace/values.dat");
		BufferedWriter bw = null;
		System.err.println("Starting....." +" "+ betaSteps);
		try {
			plotInputs.setAlpha(0.0);
			plotInputs.setBeta(0.0);
			plotInputs.setPlotIntersection(true);
			MMBean2 grBean  = new MMBean2(plotInputs);
			grBean.initMMBean();
			grBean.populateData();
			Set<XYPair> grPoints = grBean.getCirclePoints();
			Limits grLimits = getLimits(grPoints);
			System.err.println(grPoints);
			System.err.println(grLimits);
			 bw = new BufferedWriter(new FileWriter(file));
		for(int alphaInt = 1;alphaInt<=alphaPts;alphaInt++){
			for(double beta = betaMin; beta<=betaMax; beta = beta+ betaSteps){
				System.err.println(alphaInt + " "+beta);
				double alpha = stepMap.get(alphaInt);
				plotInputs.setAlpha(alpha);
				plotInputs.setBeta(beta);
				MMBean2 mmBean  = new MMBean2(plotInputs);
				mmBean.initMMBean();
				mmBean.populateData();
				for(XYPair stPoint:mmBean.getCirclePoints()){
					if(withinLimits(stPoint, grLimits)) {
						System.err.println(alpha + " "+ beta + " " + stPoint );
						bw.write(alpha+ " "+beta+"\n");
						break;
					}
				}
				
				
			}
			bw.flush();
//			System.err.println(alpha);
		}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(bw!=null){
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public Limits getLimits(Set<XYPair> grPoints){
		Limits l = new Limits();
		int i=0;
		for (XYPair xyPair : grPoints) {
			if(i==0){
				l.pMin = l.pMax = xyPair.getX();
				l.cMin = l.cMax = xyPair.getY();
			}
			i++;
			if(xyPair.getX()>l.pMax) l.pMax = xyPair.getX();
			if(xyPair.getX()<l.pMin) l.pMin = xyPair.getX();
			if(xyPair.getY()>l.cMax) l.cMax = xyPair.getY();
			if(xyPair.getY()<l.cMin) l.cMin = xyPair.getY();
		}
		return l;
	}
	public boolean withinLimits(XYPair pair, Limits l){
		return (pair.getX()>=l.pMin && pair.getX() <= l.pMax && pair.getY() >= l.cMin && pair.getY() <= l.cMax);
	}
	
	class Limits{
		double pMin;
		double pMax;
		double cMin;
		double cMax;
		@Override
		public String toString() {
			return "pMin: "+pMin + " pMax: "+pMax + " cMin:"+cMin+ " cMax:"+cMax;
		}
		
	}


}
