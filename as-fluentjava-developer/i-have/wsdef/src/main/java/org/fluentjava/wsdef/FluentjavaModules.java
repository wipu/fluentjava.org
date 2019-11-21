package org.fluentjava.wsdef;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.core.javamodules.JavaModules;
import org.fluentjava.wsdefdef.FluentjavaWorkspaceProvider;

public class FluentjavaModules extends JavaModules {

	private final FluentjavaWorkspaceProvider.FluentjavaBuildtimeModules buildtime = FluentjavaWorkspaceProvider.BUILDTIME_MODULES;
	private final JavaSrcModule christmastree = buildTimeModule(
			buildtime.christmastree);

	private JavaSrcModule buildTimeModule(JavaSrcModule module) {
		// make sure the module is counted in e.g. for coverage report
		allSrcModules().add(module);
		return module;
	}

	public SortedSet<JavaSrcModule> dependencyRoots() {
		return new TreeSet<>(Arrays.asList(christmastree));
	}

}
