package org.fluentjava.wsdefdef;

import org.fluentjava.iwant.api.javamodules.JavaBinModule;
import org.fluentjava.iwant.api.javamodules.JavaCompliance;
import org.fluentjava.iwant.api.javamodules.JavaModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule.IwantSrcModuleSpex;
import org.fluentjava.iwant.api.model.Source;
import org.fluentjava.iwant.api.wsdef.IwantPluginWishes;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleContext;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleProvider;
import org.fluentjava.iwant.core.javamodules.JavaModules;

public class FluentjavaWorkspaceProvider implements WorkspaceModuleProvider {

	public static FluentjavaBuildtimeModules buildtimeModules(
			IwantPluginWishes iwantPlugins) {
		return new FluentjavaBuildtimeModules(iwantPlugins);
	}

	@Override
	public JavaSrcModule workspaceModule(WorkspaceModuleContext ctx) {
		FluentjavaBuildtimeModules bt = buildtimeModules(ctx.iwantPlugin());
		return JavaSrcModule.with().name("fluentjava-wsdef")
				.locationUnderWsRoot("as-fluentjava-developer/i-have/wsdef")
				.mainJava("src/main/java").mainDeps(ctx.iwantApiModules())
				.mainDeps(ctx.wsdefdefModule())
				.mainDeps(JavaModules.runtimeDepsOf(bt.christmastree))
				.mainDeps(bt.commonsIo, bt.guava)
				.mainDeps(ctx.iwantPlugin().jacoco().withDependencies()).end();
	}

	@Override
	public String workspaceFactoryClassname() {
		return "org.fluentjava.wsdef.FluentjavaWorkspaceFactory";
	}

	/**
	 * These modules are being developed as part of this project, but they are
	 * also used for building.
	 */
	public static class FluentjavaBuildtimeModules extends JavaModules {

		private FluentjavaBuildtimeModules(IwantPluginWishes iwantPlugins) {
			this.christmastreeTestJava = JavaBinModule.providing(Source
					.underWsroot("fluentjava-christmastree/src/test/java"))
					.end();
			this.christmastree = srcModule("fluentjava-christmastree")
					.mainDeps(comGithubJavaparserCore,
							comGithubJavaparserSymbolSolverCore,
							comGithubJavaparserSymbolSolverLogic,
							comGithubJavaparserSymbolSolverModel, commonsLang,
							javassist, slf4jApi)
					.testDeps(commonsIo, guava, junit)
					.testRuntimeDeps(christmastreeTestJava)
					.testDeps(junit, junitJupiter, junitJupiterApi)
					.testRuntimeDeps(
							iwantPlugins.junit5runner().withDependencies())
					.testRuntimeDeps(logbackClassic, logbackCore).end();
		}

		public static final JavaCompliance JAVA_COMPLIANCE = JavaCompliance.JAVA_17;

		@Override
		public IwantSrcModuleSpex commonSettings(IwantSrcModuleSpex m) {
			return m.javaCompliance(JAVA_COMPLIANCE).mavenLayout();
		}

		private static JavaBinModule comGithubJavaparserModule(String subname) {
			return comGithubJavaparserModule(subname, "3.25.10");
		}

		private static JavaBinModule comGithubJavaparserModule(String subname,
				String version) {
			return binModule("com.github.javaparser", "javaparser-" + subname,
					version);
		}

		private final JavaBinModule comGithubJavaparserCore = comGithubJavaparserModule(
				"core");
		private final JavaBinModule comGithubJavaparserSymbolSolverCore = comGithubJavaparserModule(
				"symbol-solver-core");
		private final JavaBinModule comGithubJavaparserSymbolSolverLogic = comGithubJavaparserModule(
				"symbol-solver-logic", "3.15.15");
		private final JavaBinModule comGithubJavaparserSymbolSolverModel = comGithubJavaparserModule(
				"symbol-solver-model", "3.15.15");
		public final JavaBinModule commonsIo = binModule("commons-io",
				"commons-io", "2.16.0");
		private final JavaBinModule commonsLang = binModule("commons-lang",
				"commons-lang", "2.6");
		private final JavaBinModule guava = binModule("com.google.guava",
				"guava", "33.1.0-jre");
		private final JavaBinModule hamcrestCore = binModule("org/hamcrest",
				"hamcrest-core", "1.3");
		private final JavaBinModule javassist = binModule("org.javassist",
				"javassist", "3.30.2-GA");
		private final JavaBinModule junit = binModule("junit", "junit",
				"4.13.2", hamcrestCore);
		public static final String JUNIT_PLATFORM_VER = "1.10.2";
		public static final JavaModule junitPlatformLauncher = binModule(
				"org.junit.platform", "junit-platform-launcher",
				JUNIT_PLATFORM_VER);
		public static final JavaModule junitPlatformConsole = binModule(
				"org.junit.platform", "junit-platform-console",
				JUNIT_PLATFORM_VER, junitPlatformLauncher);
		private static final String JUNIT_JUPITER_VER = "5.10.2";
		public static final JavaModule junitPlatformCommons = binModule(
				"org.junit.platform", "junit-platform-commons",
				JUNIT_PLATFORM_VER);
		public static final JavaModule junitPlatformEngine = binModule(
				"org.junit.platform", "junit-platform-engine",
				JUNIT_PLATFORM_VER);
		public static final JavaModule junitJupiter = binModule(
				"org.junit.jupiter", "junit-jupiter", JUNIT_JUPITER_VER,
				junitPlatformCommons, junitPlatformEngine);
		public static final JavaModule junitJupiterApi = binModule(
				"org.junit.jupiter", "junit-jupiter-api", JUNIT_JUPITER_VER);
		public static final JavaModule junitJupiterEngine = binModule(
				"org.junit.jupiter", "junit-jupiter-engine", JUNIT_JUPITER_VER);
		public static final JavaModule junitJupiterParams = binModule(
				"org.junit.jupiter", "junit-jupiter-params", JUNIT_JUPITER_VER);
		public static final JavaModule junitVintageEngine = binModule(
				"org.junit.vintage", "junit-vintage-engine", JUNIT_JUPITER_VER);
		public static final JavaBinModule opentest4j = binModule(
				"org.opentest4j", "opentest4j", "1.3.0");

		final JavaBinModule slf4jApi = binModule("org.slf4j", "slf4j-api",
				"2.0.12");
		private static final String LOGBACK_VER = "1.5.3";
		final JavaBinModule logbackClassic = binModule("ch.qos.logback",
				"logback-classic", LOGBACK_VER);
		final JavaBinModule logbackCore = binModule("ch.qos.logback",
				"logback-core", LOGBACK_VER);

		private final JavaBinModule christmastreeTestJava;
		public final JavaSrcModule christmastree;
	}

}
