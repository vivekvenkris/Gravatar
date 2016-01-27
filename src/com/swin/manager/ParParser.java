package com.swin.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.swin.bean.Parameter;
import com.swin.bean.PlotInputs;
import com.swin.util.ParConstants;

public class ParParser {
	File parIOFile;
	PlotInputs plotInputs = new PlotInputs();
	List<Parameter<String>> strParams = new ArrayList<Parameter<String>>();
	List<Parameter<Double>> doubleParams = new ArrayList<Parameter<Double>>();

	public ParParser(String parIOFileName) {
		this.parIOFile = new File(parIOFileName);
	}
	public ParParser(File parIOFile){
		this.parIOFile=parIOFile;
	}
	public PlotInputs parsePar() throws Exception {
		try{
		BufferedReader br = new BufferedReader(new FileReader(parIOFile));
		String line;
		while((line = br.readLine())!=null){
			String[]  linearr = line.split("\\s+");
			if(linearr!=null && linearr.length>1){
				
			}
			
			if(linearr!=null && linearr.length>0){
				String param = linearr[0];
				switch(param){
				case ParConstants.eccintricity:
					plotInputs.setEccintricity(Double.parseDouble(linearr[1]));
					break;
				case ParConstants.pb:
					plotInputs.setPb(Double.parseDouble(linearr[1]));
					break;
				case ParConstants.gamma:
					plotInputs.setGamma(Double.parseDouble(linearr[1]));
					plotInputs.seteGamma(Double.parseDouble(linearr[3]));
					break;
				case ParConstants.omDot:
					plotInputs.setOmDot(Double.parseDouble(linearr[1]));
					plotInputs.seteOmDot(Double.parseDouble(linearr[3]));
					break;
				case ParConstants.pbDot:
					double pbDot = Double.parseDouble(linearr[1]);
					plotInputs.setPbGal(5.05e-15);
					plotInputs.setPbKin(7.96e-16);
					plotInputs.setePbGal(0.44e-15);
					plotInputs.setePbKin(0.0);
					//pbDot -=3.4e-16; // for double pulsar
					//pbDot +=5.05e-15;
					//pbDot -= 7.96e-16; // acc 40 km/s
					//pbDot -= 6.71e-15;
					double ePbDot = Double.parseDouble(linearr[3]);
					//ePbDot +=  0.02*pbDot;
					//ePbDot += 0.44e-15;
					//ePbDot +=1.73e-15;
					plotInputs.setPbDot(pbDot);
					plotInputs.setePbDot(ePbDot);
					//parFile.setPbDot(Double.parseDouble(linearr[1]) +5.05e-15); //- 6.71e-15 
					//parFile.setePbDot(Double.parseDouble(linearr[3]) +0.44e-15); //+1.73e-15
					break;
				}
			}
		}
		//System.out.println(plotInputs);
		}
		catch (NumberFormatException e){
			e.printStackTrace();
			throw new Exception("Invalid par file. See stack trace on terminal for more details.");
			
		}
		catch(Exception e){
			e.printStackTrace();
			throw new Exception("Invalid par file. See stack trace on terminal for more details");
		}
		return plotInputs;
	}

}
