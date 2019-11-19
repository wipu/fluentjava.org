package org.fluentjava.christmastree;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class LabeledLine {

	private final String rawContent;
	private final List<SortedSet<Label>> charLabels;
	private final List<SortedSet<Scope>> charScopes;

	public LabeledLine(String rawContent) {
		this.rawContent = rawContent;
		this.charLabels = new ArrayList<>();
		for (int i = 0; i < rawContent.length(); i++) {
			charLabels.add(new TreeSet<Label>());
		}
		this.charScopes = new ArrayList<>();
		for (int i = 0; i < rawContent.length(); i++) {
			charScopes.add(new TreeSet<Scope>());
		}
	}

	public void spanHasLabel(Location loc, String spanContent, Label label,
			Scope scope) {
		for (int i = 0; i < spanContent.length(); i++) {
			int col = loc.zeroBasedColumn() + i;
			charLabels.get(col).add(label);
			if (scope != null) {
				charScopes.get(col).add(scope);
			}
		}
	}

	public List<LabeledSpan> spans() {
		List<LabeledSpan> spans = new ArrayList<>();
		SortedSet<Label> currentLabels = null;
		StringBuilder currentContent = new StringBuilder();
		SortedSet<Scope> currentScopes = null;
		for (int i = 0; i < rawContent.length(); i++) {
			char c = rawContent.charAt(i);
			// if the scope set changes when labels don't, we don't care:
			// (it shouldn't happen)
			if (!("" + currentLabels).equals(charLabels.get(i).toString())) {
				// new span starts
				if (currentContent.length() > 0) {
					spans.add(new LabeledSpan(currentContent.toString(),
							currentLabels, currentScopes));
				}
				currentLabels = new TreeSet<>(charLabels.get(i));
				currentScopes = new TreeSet<>(charScopes.get(i));
				currentContent = new StringBuilder();
			}
			currentContent.append(c);
		}
		// add still the last span
		if (currentContent.length() > 0) {
			spans.add(new LabeledSpan(currentContent.toString(), currentLabels,
					currentScopes));
		}
		return spans;
	}

}
