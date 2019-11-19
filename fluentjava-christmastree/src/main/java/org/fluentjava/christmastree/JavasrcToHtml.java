package org.fluentjava.christmastree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;

public class JavasrcToHtml {

	private final String tabSpaces;
	private final JavaParserFacade solvingParser;

	public JavasrcToHtml(int tabWidth, List<File> srcDirs,
			ClassLoader classLoader) {
		this.tabSpaces = spacesForTab(tabWidth);
		CombinedTypeSolver comb = new CombinedTypeSolver();
		for (File srcDir : srcDirs) {
			comb.add(new JavaParserTypeSolver(srcDir));
		}
		comb.add(new ClassLoaderTypeSolver(classLoader));
		this.solvingParser = JavaParserFacade.get(comb);
	}

	private static String spacesForTab(int tabWidth) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < tabWidth; i++) {
			b.append(" ");
		}
		return b.toString();
	}

	public String toHtml(String java) {
		String untabbedJava = java.replaceAll("\t", tabSpaces);
		CompilationUnit cu = StaticJavaParser.parse(untabbedJava);
		SpanLabelListenerImpl spanListener = new SpanLabelListenerImpl(
				untabbedJava);
		VisitorImpl astVisitor = new VisitorImpl(spanListener, solvingParser);
		cu.accept(astVisitor, null);
		Renderer renderer = new HtmlRenderer();
		return renderer.render(spanListener.lines);
	}

	private static class SpanLabelListenerImpl implements SpanLabelListener {

		private final List<LabeledLine> lines = new ArrayList<>();

		SpanLabelListenerImpl(String src) {
			String[] rawLines = src.split("\n");
			for (String rawLine : rawLines) {
				lines.add(new LabeledLine(rawLine));
			}
		}

		@Override
		public void spanHasLabel(Location loc, String spanContent, Label label,
				Scope scope) {
			LabeledLine line = lines.get(loc.zeroBasedLine());
			line.spanHasLabel(loc, spanContent, label, scope);
		}

	}

}
