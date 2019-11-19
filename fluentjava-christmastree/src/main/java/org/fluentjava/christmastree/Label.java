package org.fluentjava.christmastree;

public enum Label {

	ANNOTATION("annot"),

	CHAR_LITERAL("char"),

	COMMENT("comm"),

	IDENTIFIER("id"),

	KEYWORD("kw"),

	NUMBER_LITERAL("num"),

	PUNCTUATION("pu"),

	STRING_LITERAL("str"),

	TYPE("type"),

	WHITESPACE("ws"),

	;

	private final String shortName;

	Label(String shortName) {
		this.shortName = shortName;
	}

	public String shortName() {
		return shortName;
	}

}
