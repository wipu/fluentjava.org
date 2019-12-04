package org.fluentjava.articles.syntaxcolour;

public class VariableScopeDemo {

	interface Session {
		String getAttribute(String name);
	}

	Session theSession;

	private void useSessionAttribute(
			@SuppressWarnings("unused") String attribute) {
		// nothing to do
	}

	// snippet-start demo
	String field;
	static String staticField;
	public static final String CONSTANT = "";

	String variableRefs(String parameter) {
		String localVar = "";
		return localVar + parameter + field + staticField + CONSTANT;
	}
	// snippet-end demo

	// snippet-start accidental-field-scope
	void accidentalFieldScope(Session session) {
		// all ok, we use the given (current) session:
		useSessionAttribute(session.getAttribute("attr1"));

		// here we have so many lines of code that we cannot see the method
		// parameter declaration anymore

		// the colour immediately tells us we are not using the given session
		// but some saved one from a field:
		useSessionAttribute(theSession.getAttribute("attr2"));
	}
	// snippet-end accidental-field-scope

}
