package org.fluentjava.christmastree;

public class Literals {

	// TODO fix color of this comment
	public boolean bo = false;
	public byte by = 127;
	public char c = 'c';
	public double d = 1.2D;
	public float f = 3.4F;
	public int i = 10;
	public long lo = 20L;
	public String s = "string";

	@SuppressWarnings("unused")
	public void localVars() {
		boolean bo = false;
		byte by = 127;
		char c = 'c';
		double d = 1.2D;
		float f = 3.4F;
		int i = 10;
		long lo = 20L;
		String s = "string";
	}

	public boolean methodCallArgs(Object o) {
		return o.equals(true) || o.equals(1) || o.equals(2.3D)
				|| o.equals("str") || o.equals('c');
	}

	public boolean inExpr(Object o) {
		return (o == null) == true || o.hashCode() == 1 || o.hashCode() == 2.3D
				|| o == "str" || o.hashCode() == 'c';
	}

}
