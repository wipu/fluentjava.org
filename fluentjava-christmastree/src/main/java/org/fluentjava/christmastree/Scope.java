package org.fluentjava.christmastree;

public enum Scope {

	ABSTRACT_METHOD_CALL("absmeth-call"),

	CLASS_DECLARATION("cl-dec"),

	CONSTANT("const"),

	FIELD("field"),

	GENERIC_TYPE_DIAMOND("gen-diam"),

	GENERIC_TYPE_REF("gen-ref"),

	IMPORT("imp"),

	LOCAL_VAR("localvar"),

	METHOD_DECLARATION("me-dec"),

	METHOD_NAME("me-name"),

	PACKAGE_DECLARATION("pa-dec"),

	PARAMETER("para"),

	STATIC_FIELD("stfield"),

	STATIC_METHOD_CALL("stmeth-call"),

	;

	private final String shortName;

	Scope(String shortName) {
		this.shortName = shortName;
	}

	public String shortName() {
		return shortName;
	}

}
