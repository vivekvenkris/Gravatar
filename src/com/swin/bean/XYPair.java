package com.swin.bean;

import java.text.DecimalFormat;

public class XYPair {
	Double x;
	Double y;
	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = x;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = y;
	}
	public XYPair(Double x, Double y) {
		super();
		this.x = x;
		this.y = y;
	}
	@Override
	public String toString() {
		DecimalFormat f = new DecimalFormat("#0.000000000000");
		String s = "["+f.format(this.x)+ "  "+ f.format(this.y)+"]";
		return s;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof XYPair){
			return (((XYPair)obj).x.equals(this.x) && ((XYPair)obj).y.equals(this.y));
		}
		return super.equals(obj);
	}
	@Override
	public int hashCode() {
		int hash = 17;
	    hash = hash * 31 + x.hashCode();
	    hash = hash * 31 + y.hashCode();
		return hash;
	}
}
