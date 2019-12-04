package org.fluentjava.wsdef;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.fluentjava.articles.syntaxcolour.ServiceAbstractnessDemo;
import org.fluentjava.articles.syntaxcolour.SyntaxColourDemo;
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

	@Override
	protected IngredientsAndParametersDefined ingredientsAndParameters(
			IngredientsAndParametersPlease iUse) {
		return iUse.ingredients("me", me)
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

		String colourHtml(String java) {
			return j2h.toHtml(java, "");
		}

		String dullHtml(String java) {
			return j2h.toHtml(java, "min_");
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

			StyledJavaHtml coloured() {
				return new StyledJavaHtml(j2h.toHtml(java, ""));
			}

			StyledJavaHtml dull() {
				return new StyledJavaHtml(j2h.toHtml(java, "min_"));
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
		String syntaxColourDemo = javaContent(ctx, SyntaxColourDemo.class);

		StringBuilder html = new StringBuilder();
		html.append("<html>\n");
		html.append("<head>\n");
		html.append("<title>syntax color proto</title>\n");
		html.append(
				"<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\" charset=\"utf-8\" />\n");
		html.append(
				"<link rel=\"stylesheet\" href=\"java-default.css\" type=\"text/css\" charset=\"utf-8\" />\n");
		html.append("</head>\n");
		html.append("<body>\n");
		html.append("<h1>Utilize syntax colouring</h1>\n");

		Utils.Java serviceAbstractnessDemoJava = u
				.java(ServiceAbstractnessDemo.class);
		StyledJavaHtml serviceAbstractnessDemoColoured = serviceAbstractnessDemoJava
				.coloured();
		String useConcreteServiceImplHtml = serviceAbstractnessDemoColoured
				.snippet("concrete");
		String useAbstractServiceHtml = serviceAbstractnessDemoColoured
				.snippet("abstract");

		Utils.Java variableScopeDemoJava = u.java(
				VariableScopeDemo.class);
		String variableScopeDemo = variableScopeDemoJava.coloured().html;

		html.append("<p>Concr</p>\n");
		html.append("<div class='java'>\n");
		html.append(useConcreteServiceImplHtml);
		html.append("</div>\n");

		html.append("<p>Abs</p>\n");
		html.append("<div class='java'>\n");
		html.append(useAbstractServiceHtml);
		html.append("</div>\n");

		html.append("<p>Consider these two:</p>\n");

		html.append("<table><tr>");
		html.append("<td><div class='java'>\n");
		html.append(variableScopeDemoJava.dull());
		html.append("</div></td>\n");

		html.append("<td><div class='java'>\n");
		html.append(variableScopeDemoJava.coloured());
		html.append("</div></td>\n");
		html.append("</tr></table>");

		html.append(
				"<p>See blah and todo and how the latter is better, and how IDE defaults are somewhere in between etc.</p>\n");

		html.append("</body>\n");
		html.append("</html>\n");

		File dest = ctx.cached(this);
		System.err.println("Generating " + dest);
		FileUtils.forceMkdir(dest);

		write(new File(dest, "utilize-syntax-colouring.html"), html.toString());
		write(new File(dest, "style.css"),
				cssWidth("38em", christmastreeResource("demostyle.css")));
		write(new File(dest, "java-default.css"),
				christmastreeResource("java-default.css"));

		System.err.println("Done populating " + dest);
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
		String start = "snippet-start " + snippetName;
		String end = "snippet-end " + snippetName;

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

	private static String cssWidth(String width, String css) {
		String replaced = css.replaceFirst("    width:.*;",
				"    width:" + width + ";");
		if (replaced.equals(css)) {
			throw new IllegalStateException(
					"CSS width change wasn't effective.");
		}
		return replaced;
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
