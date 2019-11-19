package org.fluentjava.christmastree;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Resources;

public class JavasrcToHtmlTest {

	private static final Logger LOG = Logger.getLogger(JavasrcToHtmlTest.class);
	private static final int TAB_WIDTH = 4;
	private static File tmp;
	private static String demoheader;
	private static String demofooter;
	private JavasrcToHtml javasrcToHtml;

	@BeforeClass
	public static void beforeClass() throws IOException {
		tmp = new File("/tmp/" + JavasrcToHtmlTest.class.getCanonicalName());
		LOG.info("(Re)creating " + tmp);
		FileUtils.deleteDirectory(tmp);
		FileUtils.forceMkdir(tmp);
		String css = resourceAsString("demostyle.css");
		writeTmp("demostyle.css", css);
		demoheader = resourceAsString("demoheader.htmlf");
		demofooter = resourceAsString("demofooter.htmlf");
	}

	@Before
	public void before() {
		javasrcToHtml = new JavasrcToHtml(TAB_WIDTH,
				Arrays.asList(testFodderDir()), getClass().getClassLoader());
	}

	private static File testFodderDir() {
		URL marker = JavasrcToHtmlTest.class
				.getResource("/ClassInDefaultPackage.java");
		try {
			return new File(marker.toURI()).getParentFile();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static String fodderSrc(Class<?> fodderClass) {
		String resourcePath = "/"
				+ fodderClass.getCanonicalName().replace(".", "/") + ".java";
		File java = new File(testFodderDir(), resourcePath);
		LOG.debug("Loading fodder file " + java);
		try {
			return com.google.common.io.Files.toString(java,
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static String goldenMaster(Class<?> fodderClass) {
		String resourcePath = "/"
				+ JavasrcToHtml.class.getPackage().getName().replace(".", "/")
				+ "/" + fodderClass.getSimpleName() + ".htmlf";
		LOG.debug("Loading golden master for " + fodderClass);
		return resourceAsString(resourcePath);
	}

	private static String resourceAsString(String resourcePath) {
		LOG.debug("Loading resource " + resourcePath);
		try {
			return Resources.toString(
					JavasrcToHtmlTest.class.getResource(resourcePath),
					StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Failed to load resource " + resourcePath, e);
		}
	}

	private void fodderCase(Class<?> fodderClass) {
		String src = fodderSrc(fodderClass);
		LOG.info("Converting to html");
		String html = javasrcToHtml.toHtml(src);
		writeTmp(fodderClass.getName() + ".html",
				demoheader + html + demofooter);
		String goldenMaster = goldenMaster(fodderClass);
		LOG.debug("Comparing to golden master");
		assertEquals(goldenMaster, html);
		LOG.info("Fodder case passed: " + fodderClass.getCanonicalName());
	}

	private static void writeTmp(String fileName, String html) {
		File file = new File(tmp, fileName);
		LOG.debug("Writing " + file);
		try {
			FileUtils.writeStringToFile(file, html);
		} catch (IOException e) {
			throw new IllegalArgumentException("Failed to write to tmp", e);
		}
	}

	// the tests
	// *****************************************************************************'

	@Test
	public void minimalClass() {
		fodderCase(MinimalClass.class);
	}

	@Test
	public void minimalInterface() {
		fodderCase(MinimalInterface.class);
	}

	@Test
	public void classInDefaultPackage() throws ClassNotFoundException {
		fodderCase(Class.forName("ClassInDefaultPackage"));
	}

	@Test
	public void minimalBean() {
		fodderCase(MinimalBean.class);
	}

	@Test
	public void extendsAndImplements() {
		fodderCase(ExtendsAndImplements.class);
	}

	@Test
	public void allKindsOfVariables() {
		fodderCase(AllKindsOfVariables.class);
	}

	@Test
	public void methodCalls() {
		fodderCase(MethodCalls.class);
	}

	@Test
	public void annotations() {
		fodderCase(Annotations.class);
	}

	@Test
	public void literals() {
		fodderCase(Literals.class);
	}

	@Test
	public void genericTypes() {
		fodderCase(GenericTypes.class);
	}

}
