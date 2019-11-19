package org.fluentjava.christmastree;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;

public class Location {

	private final int line;
	private final int column;

	private Location(int line, int column) {
		this.line = line;
		this.column = column;
	}

	public static Location of(Node n) {
		Position pos = n.getBegin().get();
		return from(pos);
	}

	public static Location from(Position pos) {
		return fromOneBased(pos.line, pos.column);
	}

	public static Location fromOneBased(int line, int column) {
		return new Location(line - 1, column - 1);
	}

	public int zeroBasedLine() {
		return line;
	}

	public int zeroBasedColumn() {
		return column;
	}

	@Override
	public String toString() {
		return zeroBasedLine() + ":" + zeroBasedColumn();
	}

	public Location columnAddedBy(int columnDelta) {
		return new Location(line, column + columnDelta);
	}

}
