package com.swin.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.swin.exception.InvalidBoundsException;
import com.swin.util.Constants;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class MMBean2 {
	PlotInputs inp;
	private Map<String,PKBean> pkBeanMap;
	private Map<String,MMLine> mmLinesMap;
	
	private List<Polygon> intersectionList;
	private Set<XYPair> circlePoints; 
	private Integer intersectionNumPts;
	
	private List<String> PKNames;
	private String leastErrorPKName;
	private Double epsilon = 1E-18;

	public MMBean2(PlotInputs plotInputs){
		circlePoints = new HashSet<XYPair>();
		intersectionList = new ArrayList<Polygon>();
		pkBeanMap = new HashMap<String, PKBean>();
		mmLinesMap = new HashMap<String, MMLine>();
		PKNames = new ArrayList<String>();
		intersectionNumPts = 100000;
		this.inp = plotInputs;
	}
	public void recompute() throws InvalidBoundsException{
		this.initMMBean();
	}
	public void initMMBean() throws InvalidBoundsException{
		PKNames.clear();
		circlePoints.clear();
		PKBean.setAlpha(inp.getAlpha());
		PKBean.setBeta(inp.getBeta());
		PKBean.setEccintricity(inp.getEccintricity());
		PKBean.setKa(inp.getKa());
		PKBean.setPb(inp.getPb());
		PKBean.setPbGal(inp.getPbGal());
		PKBean.setPbKin(inp.getPbKin());
		PKBean.setePbGal(inp.getePbGal());
		PKBean.setePbKin(inp.getePbKin());
		PKBean.setAlphaPulsar(inp.getAlphaPulsar());
		PKBean.setAlphaCompanion(inp.getAlphaCompanion());
		PKBean.setBetaPulsar(inp.getBetaPulsar());
		PKBean.setBetaCompanion(inp.getBetaCompanion());
		Bounds linBounds =  new Bounds(inp.getMinMp(), inp.getMaxMp(), inp.getNumPts(), false);
		Bounds logBounds = new Bounds(inp.getMinMp(), inp.getMaxMp(), inp.getNumPts(), true);
		Bounds ratioBounds = new Bounds(inp.getMinMp()/inp.getMaxMp(),inp.getMaxMp()/inp.getMinMp(), inp.getNumPts(), true);
		Bounds ratioLinBounds = new Bounds(inp.getMinMp()/(double)inp.getMaxMp(),inp.getMaxMp()/(double)inp.getMinMp(), inp.getNumPts(), false);


		if(inp.isPlotMassFunc()){
			PKBean massFunc = new PKBean(Constants.massfunc, inp.getMassFunc(), inp.geteMassFunc(), ratioBounds.copy());
			MMLine massFuncLine = new MMLine(Constants.massfunc);
			massFuncLine.setColor(Color.MEDIUMAQUAMARINE);
			massFuncLine.setActive(true);
			pkBeanMap.put(Constants.massfunc, massFunc);
			mmLinesMap.put(Constants.massfunc, massFuncLine);
		}

		if(inp.isPlotMassRatio()){
			PKBean massRatio = new PKBean(Constants.massratio, inp.getMassRatio(), inp.geteMassFunc(), linBounds.copy());
			MMLine massRatioLine = new MMLine(Constants.massratio);
			massRatioLine.setColor(Color.MEDIUMPURPLE);
			massRatioLine.setActive(true);
			pkBeanMap.put(Constants.massratio, massRatio);
			mmLinesMap.put(Constants.massratio, massRatioLine);
			leastErrorPKName = Constants.massratio;
			PKNames.add(Constants.massratio);
			//System.err.println("****** Least Error Line: "+leastErrorPKName);
		}

		if(inp.isPlotPbDot()){
			PKBean pbDot = new PKBean(Constants.pbdot, inp.getPbDot(), inp.getePbDot(), ratioBounds);
			MMLine pbDotLine = new MMLine(Constants.pbdot);
			pbDotLine.setColor(Color.MEDIUMVIOLETRED);
			pbDotLine.setActive(true);
			pkBeanMap.put(Constants.pbdot, pbDot);
			mmLinesMap.put(Constants.pbdot, pbDotLine);
			leastErrorPKName = getLeastErrorLine(pkBeanMap.get(leastErrorPKName), pbDot);
			PKNames.add(Constants.pbdot);
			//System.err.println("****** Least Error Line: "+leastErrorPKName);
			
//			PKBean pbDotFreire = new PKBean(Constants.pbdotFreire, inp.getPbDot(), inp.getePbDot(), ratioLinBounds.copy());
//			MMLine pbDotFreireLine = new MMLine(Constants.pbdotFreire);
//			pbDotFreireLine.setColor(Color.MEDIUMAQUAMARINE);
//			pbDotFreireLine.setActive(true);
//			pkBeanMap.put(Constants.pbdotFreire, pbDotFreire);
//			mmLinesMap.put(Constants.pbdotFreire, pbDotFreireLine);
//			System.err.println("****** Least Error Line: "+leastErrorPKName);
//			
		}

		if(inp.isPlotOmDot()){
			PKBean omDot = new PKBean(Constants.omdot, inp.getOmDot(), inp.geteOmDot(), ratioBounds.copy());
			MMLine omDotLine = new MMLine(Constants.omdot);
			omDotLine.setColor(Color.DARKGOLDENROD);
			omDotLine.setActive(true);
			pkBeanMap.put(Constants.omdot, omDot);
			mmLinesMap.put(Constants.omdot, omDotLine);
			leastErrorPKName = getLeastErrorLine(pkBeanMap.get(leastErrorPKName), omDot);
			PKNames.add(Constants.omdot);
			//System.err.println("****** Least Error Line: "+leastErrorPKName);
		}

		if(inp.isPlotGamma()){
			PKBean gamma = new PKBean(Constants.gamma, inp.getGamma(), inp.geteGamma(), ratioBounds.copy());
			MMLine gammaLine = new MMLine(Constants.gamma);
			gammaLine.setColor(Color.DARKSALMON);
			gammaLine.setActive(true);
			pkBeanMap.put(Constants.gamma, gamma);
			mmLinesMap.put(Constants.gamma, gammaLine);
			leastErrorPKName = getLeastErrorLine(pkBeanMap.get(leastErrorPKName), gamma);
			PKNames.add(Constants.gamma);
		//	System.err.println("****** Least Error Line: "+leastErrorPKName);

		}
	

		//System.err.println("mmLinesMap"+mmLinesMap.size());


	}

	public String getLeastErrorLine(PKBean line1, PKBean line2){
		if(line1==null && line2== null) return null;
		if(line1==null) return line2.getName();
		if(line2==null) return line1.getName();
		//System.err.println(line1.getErrorPercent() + " " + line2.getErrorPercent());
		if(Math.abs(line1.getErrorPercent()) < Math.abs(line2.getErrorPercent()))
			return line1.getName();
		return line2.getName();
	}

	public void populateData() throws InvalidBoundsException{
		Set<Entry<String, PKBean>> pkBeanEntrySet = pkBeanMap.entrySet();

		for(Entry<String, PKBean> pkBeanEntry: pkBeanEntrySet){
			MMLine line = mmLinesMap.get(pkBeanEntry.getKey());
			PKBean pkBean = pkBeanEntry.getValue();
			Double sigma = line.getSigma();
			List<List<XYPair>> curve = pkBean.getCurves().get(sigma);
			if(curve == null){
				curve = pkBean.computeCurvesForSigma(sigma);
				pkBean.getCurves().put(sigma, curve); // review if this is needed.
			}
			addDataToLine(line, curve, line.getName());
		}
		if(inp.isPlotIntersection()){			
			getIntersections();
		}
		else{
			circlePoints.clear();
		}
	}

	public List<Polygon> getIntersections() throws InvalidBoundsException {
		intersectionList.clear();
		circlePoints.clear();
		MMLine line = mmLinesMap.get(leastErrorPKName);
		PKBean refPKBean = pkBeanMap.get(leastErrorPKName);
		//inp.setNumPts(10);
		Bounds bounds = new Bounds(refPKBean.getBounds().getMassMin(), refPKBean.getBounds().getMassMax(), intersectionNumPts, false);
		PKBean reference = new PKBean(refPKBean.getName(), refPKBean.getValue(), refPKBean.getError(), bounds);		
		findIntersections(reference, line.getSigma());

		return intersectionList;
	}
	
	public Double computeSigma(Double oiginalValue, Double measuredValue, Double error){
		return (oiginalValue - measuredValue)/error;
	}

	
	public void findIntersections(PKBean refPKBean,Double refSigma){

		List<List<XYPair>> reference = refPKBean.getCurvesForSigma(refSigma);
		PKNames.remove(leastErrorPKName);
		//System.err.println("Inside:" +PKNames);
		
		for(List<XYPair> ref: reference){
			
			Map<String, List<Set<XYPair>>> intersectionsMap = new HashMap<String, List<Set<XYPair>>>();
			
			for(String name: PKNames){
				
				boolean storing = false;
				int storingStartIndex = 0;
				List<Set<XYPair>> intersectingSections = new ArrayList<Set<XYPair>>();
				
				PKBean pkBean = pkBeanMap.get(name);

				for(int i=0;i<ref.size()-1;i++){
					XYPair posNow = ref.get(i);
					XYPair posNext = ref.get(i+1);
					
					if(PKBean.getValue(Constants.massfunc, posNext.getX(), posNext.getY())< inp.getMassFunc()){
						if(!storing) continue;
						else{
							storing=false;
							Set<XYPair> intersection = new HashSet<XYPair>(ref.subList(storingStartIndex, i));
							intersectingSections.add(intersection);
						}
					}

					double valueNow = PKBean.getValue(name, posNow.getX(), posNow.getY());
					double sigmaNow = computeSigma(pkBean.getValueFixed(), valueNow, pkBean.getErrorFixed());

					double valueNext = PKBean.getValue(name, posNext.getX(), posNext.getY());
					double sigmaNext = computeSigma(pkBean.getValueFixed(), valueNext, pkBean.getErrorFixed());

					if(Math.abs(sigmaNow)>1 && Math.abs(sigmaNext)<1){
						storing=true;
						storingStartIndex=i+1;
					}
					else if(storing && Math.abs(sigmaNow)<1 && Math.abs(sigmaNext)>1){
						storing=false;
						Set<XYPair> intersection = new HashSet<XYPair>(ref.subList(storingStartIndex, i));
						intersectingSections.add(intersection);
					}


				}
				intersectionsMap.put(name, intersectingSections);

			}
			
			List<Set<XYPair>> initial = intersectionsMap.get(PKNames.get(0));
			for (Set<XYPair> initialSet : initial) {
				List<Set<XYPair>> intersections = new ArrayList<Set<XYPair>>();
				for(int i=1;i<PKNames.size();i++){
					List<Set<XYPair>> iter = intersectionsMap.get(PKNames.get(i));
					Set<XYPair> small,big;
		            Set<XYPair> res = new HashSet<XYPair>();
					for (Set<XYPair> iterSet : iter) {
						if (initialSet.size() <= iterSet.size()) {
							small = initialSet;
			                big = iterSet;           
			            } else {
			            	small = iterSet;
			                big = initialSet;
			            }
						for (XYPair s : small) {
			                if (big.contains(s)) {
			                    res.add(s);
			                }           
			            }
					}
					if(res.size()>0)intersections.add(res);
				}
				if(intersections.size()==PKNames.size()-1){
					//System.err.println("We have an intersection.");
					for (Set<XYPair> set : intersections) {
						circlePoints.addAll(set);
					}
				}
				
			}
			
			
		}



	}
	void addDataToLine(MMLine line,List<List<XYPair>> pairList, String name){
		ObservableList<XYChart.Series<Number, Number>> seriesList = line.getSeriesList();
		seriesList.clear();
		for(List<XYPair> signList: pairList){
			XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
			//System.err.println(line.getName()+signList);
			for(XYPair pair: signList){
				series.getData().add(new Data<Number,Number>(pair.getX(),pair.getY()));

			}
			series.setName(name);
			seriesList.add(series);
		}

	}


	public Double getMaxSigma(XYPair pair){
		double mPulsar = pair.getX();
		double mCompanion = pair.getY();
		List<Double> sigmaList = new ArrayList<Double>();
		List<Double> signedSigmaList = new ArrayList<Double>();
		PKNames.remove(leastErrorPKName);
		for(String name: PKNames){
			//if(name.equals(leastErrorPKName)) continue;
			PKBean pkBean = pkBeanMap.get(name);
			double value = PKBean.getValue(name, mPulsar, mCompanion);
			double sigma = computeSigma(pkBean.getValueFixed(), value, pkBean.getErrorFixed());
			sigmaList.add(Math.abs(sigma));
			signedSigmaList.add(sigma);
		}
		return Collections.max(sigmaList);//signedSigmaList.get(sigmaList.indexOf(Collections.max(sigmaList)));
	}

	
	public void findIntersections(PKBean refPKBean,Double refSigma,Double minSigma) throws InvalidBoundsException{
		Double startX = null;
		Double endX = null;

		List<XYPair> posRef = refPKBean.getCurveForSigma(refSigma);
		List<XYPair> negRef = refPKBean.getCurveForSigma(-refSigma);

		List<Double> posMaxSig = new ArrayList<Double>();
		List<Double> negMaxSig = new ArrayList<Double>();

		for(int i=0;i<posRef.size();i++){
			posMaxSig.add(getMaxSigma(posRef.get(i)));
			negMaxSig.add(getMaxSigma(negRef.get(i)));
		}

		if(posRef.size()>0 && negRef.size()>0 && posRef.size()==negRef.size()){
			for(int i=1;i<posRef.size()-1;i++){
				XYPair posNow = posRef.get(i);
				XYPair negNow = negRef.get(i);

				double posMaxSigNow  = posMaxSig.get(i);
				double negMaxSigNow = negMaxSig.get(i);

				XYPair posNext = posRef.get(i+1);
				XYPair negNext = negRef.get(i+1);

				double posMaxSigNext  = posMaxSig.get(i+1);
				double negMaxSigNext = negMaxSig.get(i+1);

				XYPair posPrev = posRef.get(i-1);
				XYPair negPrev = negRef.get(i-1);

				double posMaxSigPrev  = posMaxSig.get(i-1);
				double negMaxSigPrev = negMaxSig.get(i-1);

				// find if the difference changes sign meaning the intersecting curve is changing course.
				//System.err.println(posPrev + " "+ posNow+ " "+posNext + " "+posMaxSigPrev + " "+posMaxSigNow + " "+posMaxSigNext + " "
				//+Math.signum(posMaxSigPrev-posMaxSigNow)+ " "+Math.signum(posMaxSigNow-posMaxSigNext));
				if((Math.signum(posMaxSigPrev-posMaxSigNow)!=Math.signum(posMaxSigNow-posMaxSigNext)) ||
						(Math.signum(negMaxSigPrev-negMaxSigNow)!=Math.signum(negMaxSigNow-negMaxSigNext))){

					startX = Math.min(posPrev.getX(), negPrev.getX());
					endX = Math.min(posNext.getX(), negNext.getX());
					//System.err.println("Turns! "+posMaxSigPrev + " "+posMaxSigNow + " "+posMaxSigNext + " "+startX+" "+endX + " " +Math.abs((startX-endX)/inp.numPts));

					if(Math.abs((startX-endX)/inp.numPts)>epsilon){
						Bounds bounds = new Bounds(startX, endX, inp.getNumPts(), false);
						PKBean subRefPKBean = new PKBean(leastErrorPKName, refPKBean.getValue(), refPKBean.getError(), bounds);
						findIntersections(subRefPKBean,refSigma,minSigma);
						//System.err.println("came out"+ startX+ " " + endX);
					}
					else{
						if(posMaxSigNow <1 || negMaxSigNow <1){
							Polygon polygon = new Polygon();
							polygon.getPoints().addAll(new Double[] { 
									posPrev.getX(),posPrev.getY(),
									posNext.getX(),posNext.getY(),
									negPrev.getX(),negPrev.getY(),
									negNext.getX(),negNext.getY()

							});
							//System.err.println("Polygon" + polygon.getPoints());
							intersectionList.add(polygon);
						}
						else{
							System.err.println(posMaxSigNow + " "+ negMaxSigNow);
						}

					}


				}
				/* find if the individual sigma changes sign*/
				//				else if( (Math.signum(posMaxSigPrev)==Math.signum(posMaxSigNow)) || (Math.signum(negMaxSigPrev)==Math.signum(negMaxSigNow)) ){
				//					
				//				}

			}
		}
	}

	
	public void findIntersections2(PKBean refPKBean,Double refSigma,Double minSigma) throws InvalidBoundsException{
		Double startX = null;
		Double endX = null;
		XYPair positiveStartPair = null;
		XYPair negativeStartPair = null;	
		XYPair positiveEndPair = null;
		XYPair negativeEndPair = null;
		boolean insideMinSigma = false;
		boolean signChanged = false;
		List<XYPair> positiveRefCurve = refPKBean.getCurveForSigma(refSigma);
		List<XYPair> negativeRefCurve= refPKBean.getCurveForSigma(-refSigma);
		if(positiveRefCurve.size()>0)
			for(int i=1;i<positiveRefCurve.size();i++){
				XYPair positivePt = positiveRefCurve.get(i);
				XYPair negativePt = negativeRefCurve.get(i);

				XYPair positivePtPrev = positiveRefCurve.get(i-1);
				XYPair negativePtPrev = negativeRefCurve.get(i-1);

				double positiveMaxSigma  = getMaxSigma(positivePt);
				double negativeMaxSigma = getMaxSigma(negativePt);

				double positiveMaxSigmaPrev  = getMaxSigma(positivePtPrev);
				double negativeMaxSigmaPrev = getMaxSigma(negativePtPrev);
				if((positiveMaxSigmaPrev<=0 &&positiveMaxSigma>=0 ||positiveMaxSigmaPrev>=0 &&positiveMaxSigma<=0||
						negativeMaxSigmaPrev<=0 && negativeMaxSigma >=0 || negativeMaxSigmaPrev>=0 && negativeMaxSigma <=0)){
					signChanged = true;
					/* sign of sigma changes but no point is within minsigma*/
					if(startX==null){
						startX = Math.min(positivePtPrev.getX(), negativePtPrev.getX());
						positiveStartPair = positivePtPrev;
						negativeStartPair = negativePtPrev;
					}

					endX =  Math.min(positivePt.getX(), negativePt.getX());
					positiveEndPair = positivePt;
					negativeEndPair = negativePt;

					Bounds bounds = new Bounds(startX, endX, inp.getNumPts(), false);
					PKBean subRefPKBean = new PKBean(leastErrorPKName, refPKBean.getValue(), refPKBean.getError(), bounds);

					System.err.println("sign changed:"+positiveMaxSigma+" "
							+ +positiveMaxSigmaPrev +" "+positiveMaxSigma + " "+negativeMaxSigmaPrev + " minSigma: "+minSigma);
					findIntersections(subRefPKBean,refSigma,minSigma);
					startX = null;



				}

				System.err.println("positiveMaxSigma: "+positiveMaxSigma + " negativeMaxSigma: "+negativeMaxSigma + " minSigma: "+minSigma);
				if((Math.abs(positiveMaxSigma) <minSigma || Math.abs(negativeMaxSigma) <minSigma) ){
					insideMinSigma = true;
					if(startX==null){
						startX = Math.min(positivePtPrev.getX(), negativePtPrev.getX());
						positiveStartPair = positivePtPrev;
						negativeStartPair = negativePtPrev;
						System.err.println("Selected start"
								+ " positiveMaxSigma: "+positiveMaxSigmaPrev + " negativeMaxSigma: "+negativeMaxSigmaPrev + " minSigma: "+minSigma);
					}

				}
				else{
					if(startX!=null && insideMinSigma){
						insideMinSigma = false;
						endX =  Math.min(positivePt.getX(), negativePt.getX());
						System.err.println("Selected end"
								+ " positiveMaxSigma: "+positiveMaxSigma + " negativeMaxSigma: "+negativeMaxSigma + " minSigma: "+minSigma);
						positiveEndPair = positivePt;
						negativeEndPair = negativePt;
						if(minSigma>1){ 
							System.err.println("minSigma:"+minSigma+ " "+Math.abs((startX-endX)/inp.numPts));
							if(Math.abs((startX-endX)/inp.numPts)>epsilon){
								Bounds bounds = new Bounds(startX, endX, inp.getNumPts(), false);
								PKBean subRefPKBean = new PKBean(leastErrorPKName, refPKBean.getValue(), refPKBean.getError(), bounds);

								findIntersections(subRefPKBean,refSigma,minSigma-1);
								startX = null;
							}
							else{
								Bounds bounds = new Bounds(startX, endX, Math.round((float)(Math.abs((startX-endX))/epsilon)), false);
								PKBean subRefPKBean = new PKBean(leastErrorPKName, refPKBean.getValue(), refPKBean.getError(), bounds);
								findIntersections(subRefPKBean,refSigma,1.0);
								startX = null;

							}
						}
						else{
							if(Math.abs((startX-endX)/inp.numPts)>epsilon){
								Bounds bounds = new Bounds(startX, endX, inp.getNumPts(), false);
								PKBean subRefPKBean = new PKBean(leastErrorPKName, refPKBean.getValue(), refPKBean.getError(), bounds);
								findIntersections(subRefPKBean,refSigma,1.0);
								startX = null;

							}
							else{
								Polygon polygon = new Polygon();
								polygon.getPoints().addAll(new Double[] { 
										positiveStartPair.getX(),positiveStartPair.getY(),
										positiveEndPair.getX(),positiveEndPair.getY(),
										negativeStartPair.getX(),negativeStartPair.getY(),
										negativeEndPair.getX(),negativeEndPair.getY()

								});
								intersectionList.add(polygon);
							}
						}

					}

				}
			}
	}



	
	public Map<String, PKBean> getPkBeanMap() {
		return pkBeanMap;
	}
	public void setPkBeanMap(Map<String, PKBean> pkBeanMap) {
		this.pkBeanMap = pkBeanMap;
	}
	public Map<String, MMLine> getMmLinesMap() {
		return mmLinesMap;
	}
	public void setMmLinesMap(Map<String, MMLine> mmLinesMap) {
		this.mmLinesMap = mmLinesMap;
	}
	public PlotInputs getInp() {
		return inp;
	}
	public void setInp(PlotInputs inp) {
		this.inp = inp;
	}
	public List<Polygon> getIntersectionList() {
		return intersectionList;
	}
	public void setIntersectionList(List<Polygon> intersectionList) {
		this.intersectionList = intersectionList;
	}
	public Set<XYPair> getCirclePoints() {
		return circlePoints;
	}
	public void setCirclePoints(Set<XYPair> circlePoints) {
		this.circlePoints = circlePoints;
	}
	
	public Integer getIntersectionNumPts() {
		return intersectionNumPts;
	}
	public void setIntersectionNumPts(Integer intersectionNumPts) {
		this.intersectionNumPts = intersectionNumPts;
	}



}
