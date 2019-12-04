package org.fluentjava.articles.syntaxcolour;

// snippet-start gen-vs-type
public class GenericVsTypeDemo<T1> {

	interface T2 {
		// a non-generic type
	}

	T1 handle(T1 in) {
		return in;
	}

	T2 handle(T2 in) {
		return in;
	}

}
// snippet-end gen-vs-type
