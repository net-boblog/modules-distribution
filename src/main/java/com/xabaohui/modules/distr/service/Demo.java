package com.xabaohui.modules.distr.service;

public class Demo {

	public boolean function () {
		// do sth...
		// update / save ...
		return true;
	}
	
	// update* 
	// save*
	// create*
	// ...
}

class DemoProxy extends Demo {
	
	@Override
	public boolean function() {
		try {
			doBefore();
			boolean result = super.function();
			doAfter();
			return result;
		} catch (RuntimeException e) {
			doOnException();
			throw e;
		}
	}
	
	public void doBefore() {
		// begin tx...
	}
	
	public void doAfter() {
		// commit
	}
	
	public void doOnException() {
		// rollback
	}
	
}

class Main {
	
	public static void main(String[] args) {
		Demo demo = new DemoProxy();
		demo.function();
	}
}
