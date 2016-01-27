package com.swin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.Set;

import com.swin.bean.MMBean2;
import com.swin.bean.PlotInputs;
import com.swin.bean.XYPair;
import com.swin.manager.ParParser;

public class StandaloneST {
	static PlotInputs plotInputs;

public static void main(String[] args) throws Exception {
	File parFile = new File(args[0]);
	File txtFile = new File(args[1]);
	File out = new File(args[2]);
	ParParser parParser = new ParParser(parFile);
	plotInputs = parParser.parsePar();
	plotInputs.setMassFunc(0.171706);
	plotInputs.setMassRatio(1.27/0.98);
	plotInputs.seteMassFunc(0.000006);
	MMBean2 mmBean= new MMBean2(plotInputs);
	plotInputs.setPlotGamma(true);
	plotInputs.setPlotOmDot(true);
	plotInputs.setPlotMassFunc(true);
	plotInputs.setPlotPbDot(true);
	plotInputs.setPlotIntersection(true);
//	mmBean.initMMBean();
//	mmBean.populateData();
	BufferedReader br = new BufferedReader(new FileReader(txtFile));
	BufferedWriter bw= new BufferedWriter(new FileWriter(out));
	Scanner scanner = new Scanner(System.in);
	String line;
	br.readLine();
	double beta0 = Double.parseDouble(br.readLine().split("=")[1]);
	int i=1;
	while((line=br.readLine())!=null){
		if(line.contains("#")) continue;
		System.err.println(i++);
		String values[] = line.split(",");
		plotInputs.setAlpha(Double.parseDouble(values[1]));
		plotInputs.setAlphaPulsar(Double.parseDouble(values[2]));
		plotInputs.setBetaPulsar(Double.parseDouble(values[3]));
		plotInputs.setKa(Double.parseDouble(values[4]));
		plotInputs.setBeta(beta0);
		mmBean.setInp(plotInputs);
		mmBean.recompute();
		mmBean.populateData();
		Set<XYPair> intersection = mmBean.getCirclePoints();
		if(intersection.size()>0)bw.write(line + " 1");
		else bw.write(line + " 0");
		System.err.println(line + " " + intersection.size());
		bw.newLine();
		bw.flush();
//		if(intersection.size()>0) System.err.println(line + " " +1);
//		else System.err.println(line + " " +0);
		
//		System.out.println("Press \"ENTER\" to continue...");
//        scanner.nextLine();
        
		   
	}
	br.close();
	scanner.close();
	bw.close();
	//calculations.calculate(mmBean);
	
}
}
