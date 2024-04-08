package org.fluentjava.wsdefdef;

import org.fluentjava.iwant.api.javamodules.JavaBinModule;
import org.fluentjava.iwant.api.javamodules.JavaCompliance;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule.IwantSrcModuleSpex;
import org.fluentjava.iwant.api.model.Source;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleContext;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleProvider;
import org.fluentjava.iwant.core.javamodules.JavaModules;

public class FluentjavaWorkspaceProvider implements WorkspaceModuleProvider {

	public static final FluentjavaBuildtimeModules BUILDTIME_MODULES = new FluentjavaBuildtimeModules();

	@Override
	public JavaSrcModule workspaceModule(WorkspaceModuleContext ctx) {
		FluentjavaBuildtimeModules bt = BUILDTIME_MODULES;
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
		private final JavaBinModule junit = binModule("junit", "junit", "4.11",
				hamcrestCore);
		final JavaBinModule slf4jApi = binModule("org.slf4j", "slf4j-api",
				"2.0.12");
		private static final String LOGBACK_VER = "1.5.3";
		final JavaBinModule logbackClassic = binModule("ch.qos.logback",
				"logback-classic", LOGBACK_VER);
		final JavaBinModule logbackCore = binModule("ch.qos.logback",
				"logback-core", LOGBACK_VER);

		private final JavaBinModule christmastreeTestJava = JavaBinModule
				.providing(Source
						.underWsroot("fluentjava-christmastree/src/test/java"))
				.end();
		public final JavaSrcModule christmastree = srcModule(
				"fluentjava-christmastree")
						.mainDeps(comGithubJavaparserCore,
								comGithubJavaparserSymbolSolverCore,
								comGithubJavaparserSymbolSolverLogic,
								comGithubJavaparserSymbolSolverModel,
								commonsLang, javassist, slf4jApi)
						.testDeps(commonsIo, guava, junit)
						.testRuntimeDeps(christmastreeTestJava)
						.testRuntimeDeps(logbackClassic, logbackCore).end();
	}

}
