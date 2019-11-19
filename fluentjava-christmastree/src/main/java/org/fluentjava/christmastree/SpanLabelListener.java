package org.fluentjava.christmastree;

public interface SpanLabelListener {

	void spanHasLabel(Location loc, String spanContent, Label label,
			Scope scope);

}
