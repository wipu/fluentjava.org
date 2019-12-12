package org.fluentjava.wsdef;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fluentjava.articles.syntaxcolour.GenericVsTypeDemo;
import org.fluentjava.articles.syntaxcolour.ServiceAbstractnessDemo;
import org.fluentjava.articles.syntaxcolour.VariableScopeDemo;
import org.fluentjava.christmastree.JavasrcToHtml;
import org.fluentjava.iwant.api.model.Path;
import org.fluentjava.iwant.api.model.Source;
import org.fluentjava.iwant.api.model.TargetEvaluationContext;
import org.fluentjava.iwant.api.target.TargetBase;
import org.fluentjava.iwant.core.javafinder.WsdefJavaOf;

import com.google.common.io.LineReader;
import com.google.common.io.Resources;

public class FluentjavaSite extends TargetBase {

	private static final List<String> STYLES_SUPPORTED_BY_DULL = Arrays
			.asList(".kw", ".comm");
	private static final List<Source> manualFiles = Arrays.asList(
			docsFile("CNAME"), docsFile("christmastree/index.html"),
			docsFile("index.html"), docsFile("legal-disclaimer.html"),
			docsFile("style.css"));

	private final Source me;
	private final Source wsdefJava;
	private final Path christmastreeClasses;

	public FluentjavaSite(WsdefJavaOf wsdefJavaOf, Path christmastreeClasses) {
		super("site");
		this.christmastreeClasses = christmastreeClasses;
		this.me = wsdefJavaOf.classUnderSrcMainJava(getClass());
		// TODO ask from an iwant context when available:
		this.wsdefJava = Source.underWsroot(
				"as-fluentjava-developer/i-have/wsdef/src/main/java/");
	}

	private static Source docsFile(String relpath) {
		return Source.underWsroot("docs/" + relpath);
	}

	@Override
	protected IngredientsAndParametersDefined ingredientsAndParameters(
			IngredientsAndParametersPlease iUse) {
		return iUse.ingredients("me", me)
				.ingredients("manualFiles", manualFiles)
				.ingredients("christmastreeClasses", christmastreeClasses)
				.ingredients("wsdefJava", wsdefJava).nothingElse();
	}

	private class Utils {
		private final TargetEvaluationContext ctx;
		private final JavasrcToHtml j2h;

		Utils(TargetEvaluationContext ctx) {
			this.ctx = ctx;
			this.j2h = new JavasrcToHtml(4,
					Arrays.asList(ctx.cached(wsdefJava)),
					getClass().getClassLoader());
		}

		Java java(Class<?> theClass) throws IOException {
			String java = javaContent(ctx, theClass);
			return new Java(java);
		}

		private class Java {

			private final String java;

			Java(String java) {
				this.java = java;
			}

			StyledJavaHtml asHtml() {
				return new StyledJavaHtml(j2h.toHtml(java));
			}

		}

	}

	private class StyledJavaHtml {

		private final String html;

		public StyledJavaHtml(String html) {
			this.html = html;
		}

		String snippet(String snippetName) throws IOException {
			return snippetFromJavaHtml(snippetName, html);
		}

		@Override
		public String toString() {
			return html;
		}

	}

	@Override
	public void path(TargetEvaluationContext ctx) throws Exception {
		Utils u = new Utils(ctx);

		StringBuilder html = new StringBuilder();
		html.append("<html>\n");
		html.append("<head>\n");
		html.append("<title>syntax color proto</title>\n");
		html.append(
				"<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\" charset=\"utf-8\" />\n");
		html.append(
				"<link rel=\"stylesheet\" href=\"div-java.css\" type=\"text/css\" charset=\"utf-8\" />\n");
		html.append(
				"<link rel=\"stylesheet\" href=\"div-dulljava.css\" type=\"text/css\" charset=\"utf-8\" />\n");
		html.append(
				"<link rel=\"stylesheet\" href=\"java-default.css\" type=\"text/css\" charset=\"utf-8\" />\n");
		html.append(
				"<link rel=\"stylesheet\" href=\"java-dull.css\" type=\"text/css\" charset=\"utf-8\" />\n");
		html.append("</head>\n");
		html.append("<body>\n");
		html.append("<h1>Utilize syntax colouring</h1>\n");

		html.append(
				"<h2>Detecting use of a concrete implementation of a service</h2>\n");
		snippetPair(html, u.java(ServiceAbstractnessDemo.class), "the-calls");
		html.append(
				"<p>Note how the colour of 'serveMe' helps us detect the (accidental?) use of a concrete class. Also, in the case of an abstract method"
						+ " we know it's not useful to ctrl-click to it unless you really want to see the abstract declaration. Ctrl-T instead to select an implementation.</p>\n");

		html.append("<h2>Accidental reference of a field</h2>\n");
		snippetPair(html, u.java(VariableScopeDemo.class),
				"accidental-field-scope");
		html.append(
				"<p>Even though we cannot see the method signature, colour tells us when we are using the given (current) session 'session'"
						+ " and when we are (accidentally?) using some other session, saved in the field 'theSession'.</p>\n");

		html.append("<h2>Generic type vs non-generic type</h2>\n");
		snippetPair(html, u.java(GenericVsTypeDemo.class), "gen-vs-type");
		html.append(
				"<p>No use trying to find a type called T2, which we can see from its colour.</p>");

		html.append(
				"<p>See blah and todo and how the latter is better, and how IDE defaults are somewhere in between etc.</p>\n");

		html.append("</body>\n");
		html.append("</html>\n");

		File dest = ctx.cached(this);
		System.err.println("Generating " + dest);
		FileUtils.forceMkdir(dest);

		write(new File(dest, "utilize-syntax-colouring.html"), html.toString());

		deployManualFiles(ctx, dest);

		String divJavaCss = christmastreeResource("div-java.css");
		write(new File(dest, "div-java.css"), divJavaCss);
		write(new File(dest, "div-dulljava.css"),
				divJavaCss.replace(".java", ".dulljava"));

		String javaDefaultCss = christmastreeResource("java-default.css");
		write(new File(dest, "java-default.css"), javaDefaultCss);
		write(new File(dest, "java-dull.css"), dullJavaCss(javaDefaultCss));

		System.err.println("Done populating " + dest);
	}

	private static void deployManualFiles(TargetEvaluationContext ctx,
			File dest) throws IOException {
		for (Path mf : manualFiles) {
			File relFile = new File(mf.name().replaceFirst("^docs/", ""));
			File destDir = dest;
			if (relFile.getParent() != null) {
				destDir = new File(dest, relFile.getParent());
			}
			System.err
					.println("Copying manual file " + mf + " under " + destDir);
			FileUtils.copyFileToDirectory(ctx.cached(mf), destDir);
		}
	}

	private static String dullJavaCss(String css) throws IOException {
		StringBuilder b = new StringBuilder();
		try (StringReader r = new StringReader(css)) {
			LineReader lr = new LineReader(r);
			while (true) {
				String line = lr.readLine();
				if (line == null) {
					return b.toString();
				}
				if (isCssLineSupportedByDull(line)) {
					b.append(line.replace(".java ", ".dulljava ")).append("\n");
				}
			}
		}
	}

	private static boolean isCssLineSupportedByDull(String line) {
		for (String style : STYLES_SUPPORTED_BY_DULL) {
			if (line.contains(" " + style + " ")) {
				return true;
			}
		}
		return false;
	}

	private static void snippetPair(StringBuilder html, Utils.Java java,
			String snippet) throws IOException {
		html.append("<table><tr>\n");

		html.append("<td>");
		html.append("<div class='dulljava'>\n");
		html.append(java.asHtml().snippet(snippet));
		html.append("</div>\n");
		html.append("</td>");

		html.append("<td>");
		html.append("<div class='java'>\n");
		html.append(java.asHtml().snippet(snippet));
		html.append("</div>\n");
		html.append("</td>");

		html.append("</tr></table>\n");
	}

	private String javaContent(TargetEvaluationContext ctx, Class<?> theClass)
			throws IOException {
		String relPath = theClass.getCanonicalName().replace(".", "/");
		File demoJavaFile = new File(ctx.cached(wsdefJava), relPath + ".java");
		System.err.println("Reading " + demoJavaFile);
		return FileUtils.readFileToString(demoJavaFile);
	}

	// TODO use some ready-made util
	private static String snippetFromJavaHtml(String snippetName,
			String javaHtml) throws IOException {
		String start = "snippet-start&nbsp;" + snippetName;
		String end = "snippet-end&nbsp;" + snippetName;

		StringBuilder out = new StringBuilder();
		boolean reading = false;

		try (StringReader r = new StringReader(javaHtml)) {
			LineReader lr = new LineReader(r);
			while (true) {
				String line = lr.readLine();
				if (line.contains(end)) {
					return out.toString();
				}
				if (line.contains(start)) {
					reading = true;
					continue;
				}
				if (reading) {
					out.append(line).append("\n");
				}
			}
		}
	}

	private String christmastreeResource(String name) throws IOException {
		String styleCss = Resources.toString(
				getClass().getResource("/org/fluentjava/christmastree/" + name),
				StandardCharsets.UTF_8);
		return styleCss;
	}

	private static void write(File file, String content) throws IOException {
		System.err.println("Writing " + file);
		FileUtils.writeStringToFile(file, content);
	}

}
