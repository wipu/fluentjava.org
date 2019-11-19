package org.fluentjava.christmastree;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringEscapeUtils;

public class HtmlRenderer implements Renderer {

	@Override
	public String render(List<LabeledLine> lines) {
		StringBuilder b = new StringBuilder();
		for (LabeledLine line : lines) {
			for (LabeledSpan span : line.spans()) {
				if (span.labels().contains(Label.WHITESPACE)) {
					whitespace(b, span);
					continue;
				}
				b.append("<span class='");
				labelClasses(b, span);
				b.append("'>");
				b.append(StringEscapeUtils.escapeHtml(span.rawContent()));
				b.append("</span>");
			}
			b.append("<br/>\n");
		}
		return b.toString();
	}

	private static void labelClasses(StringBuilder b, LabeledSpan span) {
		Stream<String> labels = span.labels().stream().map(x -> x.shortName());
		Stream<String> scopes = span.scopes().stream().map(x -> x.shortName());
		Stream<String> both = Stream.concat(labels, scopes);
		b.append(both.collect(Collectors.joining(" ")));
	}

	private static void whitespace(StringBuilder b, LabeledSpan span) {
		for (int i = 0; i < span.rawContent().length(); i++) {
			b.append("&nbsp;");
		}
	}

}
