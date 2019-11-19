package org.fluentjava.wsdefdef;

import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleContext;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleProvider;

public class FluentjavaWorkspaceProvider implements WorkspaceModuleProvider {

	@Override
	public JavaSrcModule workspaceModule(WorkspaceModuleContext ctx) {
		return JavaSrcModule.with().name("fluentjava-wsdef")
				.locationUnderWsRoot("as-fluentjava-developer/i-have/wsdef")
				.mainJava("src/main/java").mainDeps(ctx.iwantApiModules())
				.mainDeps(ctx.wsdefdefModule())
				.mainDeps(ctx.iwantPlugin().jacoco().withDependencies()).end();
	}

	@Override
	public String workspaceFactoryClassname() {
		return "org.fluentjava.wsdef.FluentjavaWorkspaceFactory";
	}

}
