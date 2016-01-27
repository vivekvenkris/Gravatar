package com.swin.exception;

public class InvalidBoundsException extends Exception {
@Override
public String getMessage() {
	return "Invalid Bounds";
}
}
