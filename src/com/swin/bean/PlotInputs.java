package com.swin.bean;

import com.swin.util.Constants;


public class PlotInputs {
	  Double massFunc;
	  Double eMassFunc;
	  
	  Double eccintricity;
	  Double pb;
	  
	  Double omDot;
	  Double eOmDot;
	  
	  Double gamma;
	  Double eGamma;
	  
	  Double pbDot;
	  Double ePbDot;
	  
	  Double minMp;
	  Double maxMp;
	  Integer numPts;
	  
	  Double alpha;
	  Double beta;
	  Double ka;
	  
	  Double alphaPulsar;
	  Double alphaCompanion;
	  Double betaPulsar;
	  Double betaCompanion;
	  
	  
	  Double PbKin;
	  Double ePbKin;
	  Double PbGal;
	  Double ePbGal;
	  
	  Double massRatio;
	  Double eMassRatio;
	  
	  boolean plotPbDot;
	  boolean plotOmDot;
	  boolean plotGamma;
	  boolean plotMassFunc;
	  boolean plotMassRatio;
	  boolean plotIntersection;
	 
	public PlotInputs(){
		minMp = 0.01;
		maxMp = 3.0;
		alpha = 0.0;
		beta = 0.0;
		alphaPulsar = alphaCompanion = betaCompanion = betaPulsar = 0.00;
		numPts = Constants.numPts;
		eMassRatio =0.0;
		ka = 0.3;
		
	}
	
	public PlotInputs clone(){
		PlotInputs pi = new PlotInputs();
		pi.setEccintricity(eccintricity.doubleValue());
		pi.setPb(pb.doubleValue());
		
		pi.setPbGal(PbGal.doubleValue());
		pi.setePbGal(ePbGal.doubleValue());
		
		pi.setPbKin(PbKin.doubleValue());
		pi.setePbKin(ePbKin.doubleValue());
		
		pi.setPbDot(pbDot.doubleValue());
		pi.setePbDot(ePbDot.doubleValue());
		
		pi.setOmDot(omDot.doubleValue());
		pi.seteOmDot(eOmDot.doubleValue());
		
		pi.setGamma(gamma.doubleValue());
		pi.seteGamma(eGamma.doubleValue());
		
		pi.setMassFunc(massFunc.doubleValue());
		pi.seteMassFunc(eMassFunc.doubleValue());
		
		pi.setMassRatio(massRatio.doubleValue());
		pi.seteMassRatio(eMassRatio.doubleValue());
		
		
		pi.setAlpha(alpha.doubleValue());
		pi.setAlphaCompanion(alphaCompanion.doubleValue());
		pi.setAlphaPulsar(alphaPulsar.doubleValue());
		
		pi.setBeta(beta.doubleValue());
		pi.setBetaCompanion(betaCompanion.doubleValue());
		pi.setBetaPulsar(betaPulsar.doubleValue());
		pi.setKa(ka.doubleValue());
		
		pi.setMinMp(minMp.doubleValue());
		pi.setMaxMp(maxMp.doubleValue());
		pi.setNumPts(numPts.intValue());
		
		pi.setPlotGamma(plotGamma);
		pi.setPlotOmDot(plotOmDot);
		pi.setPlotMassFunc(plotMassFunc);
		pi.setPlotPbDot(plotPbDot);
		pi.setPlotIntersection(plotIntersection);
		
		
		
		return pi;
	}
	
	  
	public boolean isPlotIntersection() {
		return plotIntersection;
	}



	public void setPlotIntersection(boolean plotIntersection) {
		this.plotIntersection = plotIntersection;
	}



	public Double getMassRatio() {
		return massRatio;
	}



	public void setMassRatio(Double massRatio) {
		this.massRatio = massRatio;
	}



	public boolean isPlotPbDot() {
		return plotPbDot;
	}



	public void setPlotPbDot(boolean plotPbDot) {
		this.plotPbDot = plotPbDot;
	}



	public boolean isPlotOmDot() {
		return plotOmDot;
	}



	public void setPlotOmDot(boolean plotOmDot) {
		this.plotOmDot = plotOmDot;
	}



	public boolean isPlotGamma() {
		return plotGamma;
	}



	public void setPlotGamma(boolean plotGamma) {
		this.plotGamma = plotGamma;
	}



	public boolean isPlotMassFunc() {
		return plotMassFunc;
	}



	public void setPlotMassFunc(boolean plotMassFunc) {
		this.plotMassFunc = plotMassFunc;
	}



	public boolean isPlotMassRatio() {
		return plotMassRatio;
	}



	public void setPlotMassRatio(boolean plotMassRatio) {
		this.plotMassRatio = plotMassRatio;
	}



	public Double getPbKin() {
		return PbKin;
	}

	public void setPbKin(Double pbKin) {
		PbKin = pbKin;
	}

	public Double getePbKin() {
		return ePbKin;
	}

	public void setePbKin(Double ePbKin) {
		this.ePbKin = ePbKin;
	}

	public Double getPbGal() {
		return PbGal;
	}

	public void setPbGal(Double pbGal) {
		PbGal = pbGal;
	}

	public Double getePbGal() {
		return ePbGal;
	}

	public void setePbGal(Double ePbGal) {
		this.ePbGal = ePbGal;
	}

	public Double getKa() {
		return ka;
	}

	public void setKa(Double ka) {
		this.ka = ka;
	}

	public Double getAlpha() {
		return alpha;
	}
	public void setAlpha(Double alpha) {
		this.alpha = alpha;
	}
	public Double getBeta() {
		return beta;
	}
	public void setBeta(Double beta) {
		this.beta = beta;
	}
	
	public Integer getNumPts() {
		return numPts;
	}

	public void setNumPts(Integer numPts) {
		this.numPts = numPts;
	}

	
	public Double geteMassRatio() {
		return eMassRatio;
	}



	public void seteMassRatio(Double eMassRatio) {
		this.eMassRatio = eMassRatio;
	}


	public Double getPbDotFixed(){
		return this.pbDot -this.PbKin + this.PbGal;
	}
	public Double getePbDotFixed(){
		return this.ePbDot +this.ePbKin + this.ePbGal;
	}
	public Double getMaxMp() {
		return maxMp;
	}
	public void setMaxMp(Double maxMp) {
		this.maxMp = maxMp;
	}
	public Double getMassFunc() {
		return massFunc;
	}
	public void setMassFunc(Double massFunc) {
		this.massFunc = massFunc;
	}
	public Double getEccintricity() {
		return eccintricity;
	}
	public void setEccintricity(Double eccintricity) {
		this.eccintricity = eccintricity;
	}
	public Double getPb() {
		return pb;
	}
	public void setPb(Double pb) {
		this.pb = pb;
	}
	public Double getOmDot() {
		return omDot;
	}
	public void setOmDot(Double omDot) {
		this.omDot = omDot;
	}
	public Double geteOmDot() {
		return eOmDot;
	}
	public void seteOmDot(Double eOmDot) {
		this.eOmDot = eOmDot;
	}
	public Double getGamma() {
		return gamma;
	}
	public void setGamma(Double gamma) {
		this.gamma = gamma;
	}
	public Double geteGamma() {
		return eGamma;
	}
	public void seteGamma(Double eGamma) {
		this.eGamma = eGamma;
	}
	public Double getPbDot() {
		return pbDot;
	}
	public void setPbDot(Double pbDot) {
		this.pbDot = pbDot;
	}
	public Double getePbDot() {
		return ePbDot;
	}
	public void setePbDot(Double ePbDot) {
		this.ePbDot = ePbDot;
	}
	
	  public Double geteMassFunc() {
		return eMassFunc;
	}



	public Double getMinMp() {
		return minMp;
	}



	public void setMinMp(Double minMp) {
		this.minMp = minMp;
	}



	public void seteMassFunc(Double eMassFunc) {
		this.eMassFunc = eMassFunc;
	}



	public Double getAlphaPulsar() {
		return alphaPulsar;
	}



	public void setAlphaPulsar(Double alphaPulsar) {
		this.alphaPulsar = alphaPulsar;
	}



	public Double getAlphaCompanion() {
		return alphaCompanion;
	}



	public void setAlphaCompanion(Double alphaCompanion) {
		this.alphaCompanion = alphaCompanion;
	}



	public Double getBetaPulsar() {
		return betaPulsar;
	}



	public void setBetaPulsar(Double betaPulsar) {
		this.betaPulsar = betaPulsar;
	}



	public Double getBetaCompanion() {
		return betaCompanion;
	}



	public void setBetaCompanion(Double betaCompanion) {
		this.betaCompanion = betaCompanion;
	}



	@Override
	public String toString() {
		  String s = "ECC:"+this.eccintricity+"\n"+"PB:"+this.pb +"\n"+"PBDOT:"+this.pbDot+"\n"+"EPBDOT:"+this.ePbDot+
				  "OMDOT:"+this.omDot+"\n"+"EOMDOT"+this.eOmDot+"\n"+"GAMMA:"+this.gamma+"\n"+"EGAMMA:"+this.eGamma;
		  return s;
	}
	  
	  
	  
}
