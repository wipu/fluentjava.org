package org.fluentjava.articles.syntaxcolour;

public class ServiceAbstractnessDemo {

	interface Service {

		void serveMe();

	}

	static class ServiceImpl implements Service {

		@Override
		public void serveMe() {
			// nothing to do
		}

	}

	// snippet-start concrete
	void useConcreteServiceImpl(ServiceImpl service) {
		service.serveMe();
	}
	// snippet-end concrete

	// snippet-start abstract
	void useAbstractService(Service service) {
		service.serveMe();
	}
	// snippet-end abstract

}
