package org.fluentjava.wsdef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fluentjava.iwant.api.model.SideEffect;
import org.fluentjava.iwant.api.model.Target;
import org.fluentjava.iwant.api.wsdef.SideEffectDefinitionContext;
import org.fluentjava.iwant.api.wsdef.TargetDefinitionContext;
import org.fluentjava.iwant.api.wsdef.Workspace;
import org.fluentjava.iwant.api.wsdef.WorkspaceContext;
import org.fluentjava.iwant.core.download.TestedIwantDependencies;
import org.fluentjava.iwant.core.javafinder.WsdefJavaOf;
import org.fluentjava.iwant.eclipsesettings.EclipseSettings;
import org.fluentjava.iwant.plugin.jacoco.JacocoDistribution;
import org.fluentjava.iwant.plugin.jacoco.JacocoTargetsOfJavaModules;

public class FluentjavaWorkspace implements Workspace {

	private final WsdefJavaOf wsdefJavaOf;
	private final FluentjavaModules modules = new FluentjavaModules();

	public FluentjavaWorkspace(WorkspaceContext wsCtx) {
		this.wsdefJavaOf = new WsdefJavaOf(wsCtx);
	}

	@Override
	public List<? extends Target> targets(TargetDefinitionContext ctx) {
		List<Target> t = new ArrayList<>();
		t.add(coverageReport());
		t.add(new FluentjavaSite(wsdefJavaOf));
		return t;
	}

	@Override
	public List<? extends SideEffect> sideEffects(
			SideEffectDefinitionContext ctx) {
		return Arrays.asList(EclipseSettings.with().name("eclipse-settings")
				.modules(ctx.wsdefdefJavaModule(), ctx.wsdefJavaModule())
				.modules(modules.allSrcModules()).end());
	}

	private Target coverageReport() {
		return JacocoTargetsOfJavaModules.with().jacoco(jacoco())
				.antJars(TestedIwantDependencies.antJar(),
						TestedIwantDependencies.antLauncherJar())
				.modules(modules.allSrcModules()).end()
				.jacocoReport("coverage-report");

	}

	private static JacocoDistribution jacoco() {
		return JacocoDistribution.newestTestedVersion();
	}

}
