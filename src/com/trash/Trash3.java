package com.trash;

public class Trash3 {
	public static void main(String[] args) {

		for(int i=1;i<100000000;i++){
			System.err.println(Math.log10(i*0.00000001));
		}
	}
}
