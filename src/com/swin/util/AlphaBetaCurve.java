package com.swin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.swin.bean.PlotInputs;
import com.swin.manager.ParParser;

public class AlphaBetaCurve {
	static PlotInputs plotInputs;

public static void main(String[] args) throws Exception {
	File parFile = new File(args[0]);
	File txtFile = new File(args[1]);
	File out = new File(args[2]);
	ParParser parParser = new ParParser(parFile);
	BufferedReader br = new BufferedReader(new FileReader(txtFile));
	BufferedWriter bw= new BufferedWriter(new FileWriter(out));

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
}
