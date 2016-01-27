package com.swin.manager;

import java.util.List;

import com.swin.bean.PlotInputs;
import com.swin.bean.XYPair;

public interface Calculator {
	public List<List<List<XYPair>>> getMassRatio(PlotInputs plotInputs);
	public List<List<List<XYPair>>> getMassFunc(PlotInputs plotInputs);
	public List<List<List<XYPair>>> getOmDot(PlotInputs plotInputs);
	public List<List<List<XYPair>>> getGamma(PlotInputs plotInputs);
	public List<List<List<XYPair>>> getPbDot(PlotInputs plotInputs);

}
