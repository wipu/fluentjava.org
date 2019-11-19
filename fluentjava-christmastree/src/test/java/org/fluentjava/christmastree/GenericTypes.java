package org.fluentjava.christmastree;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class GenericTypes<CLASSTYPE>
		implements Comparator<String>, Comparable<GenericTypes<?>> {

	public final CLASSTYPE arg;

	GenericTypes(CLASSTYPE arg) {
		this.arg = arg;
	}

	@Override
	public int compareTo(GenericTypes<?> o) {
		throw new UnsupportedOperationException("not really");
	}

	@Override
	public int compare(String o1, String o2) {
		return o2.compareTo(o1);
	}

	public static <TYPE extends Runnable> TYPE meth(Class<TYPE> clazz)
			throws Exception {
		TYPE inst = clazz.newInstance();
		return inst;
	}

	public static <T1, T2> Map<T1, T2> twoGenerics(T1 t1, T2 t2) {
		Map<T1, T2> m = new HashMap<>();
		m.put(t1, t2);
		return m;
	}

}
