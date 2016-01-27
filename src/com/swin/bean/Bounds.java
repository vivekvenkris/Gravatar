package com.swin.bean;

import java.util.HashMap;
import java.util.Map;

import com.swin.exception.InvalidBoundsException;

public class Bounds {
	private Double massMin;
	private Double massMax;
	private Integer numPts;
	private Double nSteps;
	private boolean logSteps;
	 Map<Integer,Double> stepMap;
	
	
	public Bounds(Double massMin,Double massMax,Integer numPts, boolean logSteps, Map<Integer,Double> stepMap) throws InvalidBoundsException{
		if(massMin<=0 && massMax >3) throw new  InvalidBoundsException();
		this.massMax = massMax;
		this.massMin = massMin;
		this.numPts = numPts;
		this.logSteps = logSteps;
		this.stepMap = stepMap;
	}

	public Bounds(Double massMin,Double massMax,Integer numPts, boolean logSteps) throws InvalidBoundsException{
		if(massMin<=0 && massMax >3) throw new  InvalidBoundsException();
		this.massMax = massMax;
		this.massMin = massMin;
		this.numPts = numPts;
		this.logSteps = logSteps;
		computeMap();
	}
	public Bounds copy() throws InvalidBoundsException{
		return new Bounds(massMin, massMax, numPts, logSteps,stepMap);
	}
	
	private void computeMap(){
		stepMap = new HashMap<Integer,Double>();
		if(!logSteps){
			for(int i=1;i<=numPts;i++){
				stepMap.put(i,massMin+ i*(massMax - massMin)/numPts);
			}
		}
		else{
			double logMin = Math.log10(massMin);
			double logMax = Math.log10(massMax);
			double delta = (logMax - logMin) /numPts;
			double accDelta = 0;
			for(int i=1;i<=numPts;i++){
				stepMap.put(i,Math.pow(10, logMin + accDelta));
				accDelta +=delta;
			}
		}
		
	}
	public void recomputeSteps(){
		computeMap();
	}
	public void recomputeSteps(double massMin, double massMax){
		this.massMin = massMin;
		this.massMax = massMax;
		computeMap();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.massMin +" to "+ this.massMax + " - "+ this.numPts+"="+this.stepMap.size() + " log="+ this.logSteps; 
	}

	public Double getValueForPoint(Integer iPt){
		return stepMap.get(iPt);
	}

	public Double getMassMin() {
		return massMin;
	}
	public void setMassMin(Double massMin) {
		this.massMin = massMin;
	}
	public Double getMassMax() {
		return massMax;
	}
	public void setMassMax(Double massMax) {
		this.massMax = massMax;
	}
	public Integer getNumPts() {
		return numPts;
	}
	public void setNumPts(Integer numPts) {
		this.numPts = numPts;
	}
	public Double getnSteps() {
		return nSteps;
	}
	public void setnSteps(Double nSteps) {
		this.nSteps = nSteps;
	}
	public boolean isLogSteps() {
		return logSteps;
	}
	public void setLogSteps(boolean logSteps) {
		this.logSteps = logSteps;
	}


}
