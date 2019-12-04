package org.fluentjava.articles.syntaxcolour;

public abstract class SyntaxColourDemo<FROM, TO> {
	public static final String A_CONSTANT = "String literal";
	protected int aField = 123;

	public abstract TO transformGenericType(FROM from);

	public abstract TO transformType(From from);

	public String referToDifferentScopes(boolean param) {
		double localVar = 4.5F;
		return A_CONSTANT + aField + param + localVar;
	}

	@SuppressWarnings("static-access")
	public void methodCalls(Runnable r, String s, Thread t)
			throws InterruptedException {
		// abstract method, use ctrl-T instead of ctrl-click:
		r.run();
		// concrete instance method, ctrl-click to impl:
		aField = s.length();
		// static method:
		Thread.sleep(100L);
		// static method accidentally called via an instance:
		t.sleep(200L);
	}
}
