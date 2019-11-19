package org.fluentjava.christmastree;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LabeledLineTest {

	/**
	 * Here we are modeling roughly what happens, not exactly
	 */
	@Test
	public void minimalClassDeclarationLine() {
		LabeledLine line = new LabeledLine("class ClassName {");

		line.spanHasLabel(Location.fromOneBased(1, 1), "class", Label.KEYWORD,
				null);
		line.spanHasLabel(Location.fromOneBased(1, 7), "ClassName",
				Label.IDENTIFIER, Scope.CLASS_DECLARATION);

		assertEquals("[[KEYWORD]:[]:'class', []:[]:' ',"
				+ " [IDENTIFIER]:[CLASS_DECLARATION]:'ClassName', []:[]:' {']",
				line.spans().toString());
	}

}
