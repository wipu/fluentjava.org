package org.fluentjava.christmastree;

import java.util.SortedSet;

public class LabeledSpan {

	private final String rawContent;
	private final SortedSet<Label> labels;
	private final SortedSet<Scope> scopes;

	public LabeledSpan(String rawContent, SortedSet<Label> labels,
			SortedSet<Scope> scopes) {
		this.rawContent = rawContent;
		this.labels = labels;
		this.scopes = scopes;
	}

	public String rawContent() {
		return rawContent;
	}

	public SortedSet<Label> labels() {
		return labels;
	}

	public SortedSet<Scope> scopes() {
		return scopes;
	}

	@Override
	public String toString() {
		return labels + ":" + scopes + ":'" + rawContent + "'";
	}

}
