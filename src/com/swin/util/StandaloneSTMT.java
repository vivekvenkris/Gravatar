package com.swin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import com.swin.bean.MMBean2;
import com.swin.bean.PlotInputs;
import com.swin.bean.XYPair;
import com.swin.manager.ParParser;

public class StandaloneSTMT {
	static PlotInputs plotInputs;
	static int max_threads = 200;

	public static void main(String[] args) throws Exception {
		File parFile = new File(args[0]);
		File txtFile = new File(args[1]);
		File out = new File(args[2]);
//		ParParser parParser = new ParParser(parFile);
//		plotInputs = parParser.parsePar();
//		plotInputs.setMassFunc(0.171706);
//		plotInputs.setMassRatio(1.27/0.98);
//		plotInputs.seteMassFunc(0.000006);
//		plotInputs.setPlotGamma(true);
//		plotInputs.setPlotOmDot(true);
//		plotInputs.setPlotMassFunc(true);
//		plotInputs.setPlotPbDot(true);
//		plotInputs.setPlotIntersection(true);
		BufferedReader br = new BufferedReader(new FileReader(txtFile));
		String line;
		br.readLine();
		int i=1;
		int count = 0;
		List<IntersectionFinder> intersectionFinders = new ArrayList<IntersectionFinder>();
		while((line=br.readLine())!=null){
			//System.err.println(i);
			IntersectionFinder intersectionFinder = new IntersectionFinder(i++, line, parFile);
			intersectionFinders.add(intersectionFinder);
			intersectionFinder.start();
			count++;
			if(count ==max_threads){
				for(IntersectionFinder temp: intersectionFinders){
					//System.err.println("waiting for:" + temp.getThreadId());
					temp.join();
				}
				count  = 0;
				//IntersectionFinder.write(out);
				intersectionFinders.clear();
			}

		}
		
		br.close();

	}

}
class IntersectionFinder extends Thread{
	static Map<Integer, String> lineMap;
	PlotInputs plotInputs;
	Integer threadId;
	String line;
	static {
		lineMap= new TreeMap<Integer, String>();
	}
	public IntersectionFinder(Integer id, String line, File parFile){
		this.threadId = id;
		this.line = line;
		try {
			ParParser parParser = new ParParser(parFile);
			plotInputs = parParser.parsePar();
			plotInputs.setMassFunc(0.171706);
			plotInputs.setMassRatio(1.27/0.98);
			plotInputs.seteMassFunc(0.000006);
			plotInputs.setPlotGamma(true);
			plotInputs.setPlotOmDot(true);
			plotInputs.setPlotMassFunc(true);
			plotInputs.setPlotPbDot(true);
			plotInputs.setPlotIntersection(true);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//this.plotInputs = plotInputs;
	}
	@Override
	public void run() {
		try {
			String values[] = line.split(",");
			MMBean2 mmBean= new MMBean2(plotInputs);
			mmBean.initMMBean();
			mmBean.populateData();
			plotInputs.setAlpha(Double.parseDouble(values[1]));
			plotInputs.setAlphaPulsar(Double.parseDouble(values[2]));
			plotInputs.setBetaPulsar(Double.parseDouble(values[3]));
			plotInputs.setKa(Double.parseDouble(values[4]));
			mmBean.setInp(plotInputs);
			mmBean.recompute();
			mmBean.populateData();
			Set<XYPair> intersection = mmBean.getCirclePoints();
			if(intersection.size()>0)line += " 1";
			else line += " 0";
			lineMap.put(threadId, line);
			System.err.println(line);
			//System.err.println("received"+ lineMap.size());
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public static void write(File out){
		try {
			System.err.println("writeout..." + lineMap.size());
			BufferedWriter bw= new BufferedWriter(new FileWriter(out,true));
			Set<Entry<Integer, String>> entrySet = lineMap.entrySet();
			for(Entry<Integer, String> entry: entrySet){
				bw.write(entry.getValue());
				bw.newLine();
			}
			bw.flush();
			bw.close();
			lineMap.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public Integer getThreadId() {
		return threadId;
	}
	public void setThreadId(Integer threadId) {
		this.threadId = threadId;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.threadId+ "";
	}
}
