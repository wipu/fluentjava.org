package org.fluentjava.wsdefdef;

import org.fluentjava.iwant.api.javamodules.JavaBinModule;
import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
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

		private static JavaBinModule comGithubJavaparserModule(String subname) {
			return binModule("com.github.javaparser", "javaparser-" + subname,
					"3.14.12");
		}

		private final JavaBinModule comGithubJavaparserCore = comGithubJavaparserModule(
				"core");
		private final JavaBinModule comGithubJavaparserSymbolSolverCore = comGithubJavaparserModule(
				"symbol-solver-core");
		private final JavaBinModule comGithubJavaparserSymbolSolverLogic = comGithubJavaparserModule(
				"symbol-solver-logic");
		private final JavaBinModule comGithubJavaparserSymbolSolverModel = comGithubJavaparserModule(
				"symbol-solver-model");
		public final JavaBinModule commonsIo = binModule("commons-io",
				"commons-io", "2.4");
		private final JavaBinModule commonsLang = binModule("commons-lang",
				"commons-lang", "2.6");
		private final JavaBinModule guava = binModule("com.google.guava",
				"guava", "18.0");
		private final JavaBinModule hamcrestCore = binModule("org/hamcrest",
				"hamcrest-core", "1.3");
		private final JavaBinModule javassist = binModule("org.javassist",
				"javassist", "3.25.0-GA");
		private final JavaBinModule junit = binModule("junit", "junit", "4.11",
				hamcrestCore);
		private final JavaBinModule log4j = binModule("log4j", "log4j",
				"1.2.16");

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
								commonsLang, javassist, log4j)
						.testDeps(commonsIo, guava, junit)
						.testRuntimeDeps(christmastreeTestJava).end();
	}

}
