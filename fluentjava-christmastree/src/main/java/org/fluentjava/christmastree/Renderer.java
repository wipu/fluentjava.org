package org.fluentjava.christmastree;

import java.util.List;

public interface Renderer {

	String render(List<LabeledLine> lines);

}
