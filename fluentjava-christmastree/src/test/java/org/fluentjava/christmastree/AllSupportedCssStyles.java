package org.fluentjava.christmastree;

import java.util.ArrayList;
import java.util.List;

public class AllSupportedCssStyles<GENERIC extends Annotations> extends Literals
		implements Runnable {

	public static final int A_CONSTANT = 0;
	private char charLiteral = 'c';
	public String aField = "a String literal, " + charLiteral;
	public static boolean staticField;

	@Override
	public void run() {
		// just a comment
	}

	String varScopes(String param, GENERIC gen) {
		double localVar = 0.1D;
		GENERIC genCopy = gen;
		return "" + A_CONSTANT + staticField + aField + localVar + param
				+ genCopy;
	}

	void methodCalls(Runnable r, String s) {
		// abstract:
		r.run();
		// concrete:
		s.length();
		// static:
		String.valueOf(true);
	}

	List<String> typeArgument(List<String> in) {
		ArrayList<String> out = new ArrayList<>(in);
		return out;
	}

}
