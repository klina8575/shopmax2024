package com.shopmax.exception;

public class OutOfStockException extends RuntimeException {
	
	//재고부족 exception
	public OutOfStockException(String message) {
		super(message);
	}
}
