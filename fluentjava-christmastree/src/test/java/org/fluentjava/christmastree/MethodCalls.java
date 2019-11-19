package org.fluentjava.christmastree;

public class MethodCalls {

	@SuppressWarnings("static-method")
	private MethodCalls nonStaticFactory() {
		return new MethodCalls();
	}

	private static MethodCalls staticFactory() {
		return new MethodCalls();
	}

	public String unqualifiedCaller() {
		return "" + nonStaticFactory() + staticFactory();
	}

	public static String qualifiedaller(MethodCalls inst) {
		return "" + inst.nonStaticFactory() + MethodCalls.staticFactory();
	}

	public static String nestedCaller(MethodCalls inst) {
		return "" + inst.nonStaticFactory().nonStaticFactory();
	}

	public static void callerOfAbstract(Runnable r) {
		r.run();
	}

}
