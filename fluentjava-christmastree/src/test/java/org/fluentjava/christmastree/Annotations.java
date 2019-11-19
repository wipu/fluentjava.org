package org.fluentjava.christmastree;

import org.junit.Ignore;

@Ignore
public class Annotations {

	Annotations(@SuppressWarnings("unused") int unusedParameter) {
		// nothing here
	}

	@SuppressWarnings("unused")
	private String unusedField;

	@SuppressWarnings("unused")
	private void unusedMethod() {
		// nothing here
	}

	public void a(@SuppressWarnings("unused") int unusedParameter) {
		@SuppressWarnings("unused")
		int unusedLocalVar;
	}

}
