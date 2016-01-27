package com.swin.util;

import java.util.List;

public class Util {
	public static Double getMax(Double... values){
		Double d = values[0];
		for (int i = 0; i < values.length; i++) {
			if(values[i]>d)
				d = values[i];
		}
		return d;
	}
	public static Double getMax(List<Double> values){
		Double d = values.get(0);
		for (int i = 0; i < values.size(); i++) {
			if(values.get(i)>d)
				d = values.get(i);
		}
		return d;
	}
}
