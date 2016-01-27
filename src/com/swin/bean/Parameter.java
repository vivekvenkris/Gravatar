package com.swin.bean;

import java.util.ArrayList;
import java.util.List;

public class Parameter<T>{
	String name;
	T value;
	Double uncertainity;
	protected List<String> doubleParams = new ArrayList<>();
	
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public Double getUncertainity() {
		return uncertainity;
	}
	public void setUncertainity(Double uncertainity) {
		this.uncertainity = uncertainity;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Parameter(){
		
	}
	public Parameter(String name, T value) {
		this.name = name;
		this.value = value;
	}
	public Parameter(String name, T value, Double uncertainity) {
		this.name = name;
		this.value = value;
		this.uncertainity = uncertainity;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Parameter<?>){
			if(((Parameter<T>)obj).getName().equals(this.getName())){
				return true;
			}
		}
		return false;
	}
	
	public boolean isDoubleParam(String parameter){
		 return false; 
	}
	
}
