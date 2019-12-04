package org.fluentjava.christmastree;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.common.io.Resources;

public class AltStyleTest {

	@Test
	public void altCssIsDerivedFromDefaultCss() throws IOException {
		String javaDefaultCss = Resources.toString(
				getClass().getResource("java-default.css"),
				StandardCharsets.UTF_8);
		String javaAltCss = alt("alt_", javaDefaultCss);

		String expected = Resources.toString(
				getClass().getResource("java-alt.css"), StandardCharsets.UTF_8);

		assertEquals(expected, javaAltCss);
	}

	static String alt(String prefix, String css) {
		String altCss = css.replaceAll("\\.([^.{]*)", "." + prefix + "$1")
				.replaceAll("}\n",
						" border-color:lightgray; border-style: solid; border-width: 1px;}\n");
		return altCss;
	}

}
