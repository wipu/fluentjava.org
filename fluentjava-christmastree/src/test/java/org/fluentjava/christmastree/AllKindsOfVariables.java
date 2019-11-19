package org.fluentjava.christmastree;

public class AllKindsOfVariables {

	public static final String CONSTANT = "const value";
	public String field;
	private static String staticField;

	public AllKindsOfVariables(String constrParameter) {
		this.field = constrParameter + staticField + CONSTANT;
	}

	public String varRefs(String methodParameter) {
		int localVar = 1;
		String multiVar1 = "1", multiVar2 = "2";
		return methodParameter + field + staticField + CONSTANT + localVar
				+ multiVar1 + multiVar2;
	}

	public int varMethodCalls(String methodParameter) {
		String localVar = "1";
		return methodParameter.length() + localVar.length() + field.length()
				+ staticField.length() + CONSTANT.length();
	}

	public double externalConstant() {
		return Math.PI;
	}

}
