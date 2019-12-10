package org.fluentjava.christmastree;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringEscapeUtils;

public class HtmlRenderer implements Renderer {

	private static final String NBSP = "&nbsp;";
	private final String cssClassPrefix;

	public HtmlRenderer(String cssClassPrefix) {
		this.cssClassPrefix = cssClassPrefix;
	}

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
				b.append(escaped(span));
				b.append("</span>");
			}
			b.append("<br/>\n");
		}
		return b.toString();
	}

	private static String escaped(LabeledSpan span) {
		String text = span.rawContent();
		text = StringEscapeUtils.escapeHtml(text);
		text = text.replace(" ", NBSP);
		return text;
	}

	private void labelClasses(StringBuilder b, LabeledSpan span) {
		Stream<String> labels = span.labels().stream()
				.map(x -> cssClassPrefix + x.shortName());
		Stream<String> scopes = span.scopes().stream()
				.map(x -> cssClassPrefix + x.shortName());
		Stream<String> both = Stream.concat(labels, scopes);
		b.append(both.collect(Collectors.joining(" ")));
	}

	private static void whitespace(StringBuilder b, LabeledSpan span) {
		for (int i = 0; i < span.rawContent().length(); i++) {
			b.append(NBSP);
		}
	}

}
