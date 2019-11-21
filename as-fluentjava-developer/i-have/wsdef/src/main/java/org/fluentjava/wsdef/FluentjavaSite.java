package org.fluentjava.wsdef;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.fluentjava.christmastree.JavasrcToHtml;
import org.fluentjava.iwant.api.model.Source;
import org.fluentjava.iwant.api.model.TargetEvaluationContext;
import org.fluentjava.iwant.api.target.TargetBase;
import org.fluentjava.iwant.core.javafinder.WsdefJavaOf;

import com.google.common.io.Resources;

public class FluentjavaSite extends TargetBase {

	private final Source me;
	private final Source syntaxColorDemos;

	public FluentjavaSite(WsdefJavaOf wsdefJavaOf) {
		super("site");
		this.me = wsdefJavaOf.classUnderSrcMainJava(getClass());
		this.syntaxColorDemos = Source
				.underWsroot("fluentjava-christmastree/src/test/java");
	}

	@Override
	protected IngredientsAndParametersDefined ingredientsAndParameters(
			IngredientsAndParametersPlease iUse) {
		return iUse.ingredients("me", me)
				.ingredients("syntaxColorDemos", syntaxColorDemos)
				.nothingElse();
	}

	@Override
	public void path(TargetEvaluationContext ctx) throws Exception {
		JavasrcToHtml j2h = new JavasrcToHtml(4,
				Arrays.asList(ctx.cached(syntaxColorDemos)),
				getClass().getClassLoader());
		File demoJavaFile = new File(ctx.cached(syntaxColorDemos),
				"org/fluentjava/christmastree/AllKindsOfVariables.java");
		System.err.println("Reading " + demoJavaFile);
		String demoJava = FileUtils.readFileToString(demoJavaFile);

		StringBuilder html = new StringBuilder();
		html.append("<html>\n");
		html.append("<head>\n");
		html.append("<title>syntax color proto</title>\n");
		html.append(
				"<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\" charset=\"utf-8\" />\n");
		html.append("</head>\n");
		html.append("<body>\n");
		html.append(
				"<p>See how clearly we can see the scope of different variables/constants,"
						+ " because we are utilizing syntax-colouring more than IDEs do by default:</p>\n");
		html.append("<div class='java'>\n");

		html.append(j2h.toHtml(demoJava));

		html.append("</div>\n");
		html.append("</body>\n");
		html.append("</html>\n");

		File dest = ctx.cached(this);
		System.err.println("Generating " + dest);
		FileUtils.forceMkdir(dest);

		File syntaxDemoFile = new File(dest, "utilize-syntax-colouring.html");
		System.err.println("Writing " + syntaxDemoFile);
		FileUtils.writeStringToFile(syntaxDemoFile, html.toString());

		String styleCss = Resources.toString(
				getClass().getResource(
						"/org/fluentjava/christmastree/demostyle.css"),
				StandardCharsets.UTF_8);
		File styleCssFile = new File(dest, "style.css");
		System.err.println("Writing " + styleCssFile);
		FileUtils.writeStringToFile(styleCssFile, styleCss);

		System.err.println("Done populating " + dest);
	}

}
