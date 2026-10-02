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

	// snippet-start the-calls
	void useConcreteServiceImpl(ServiceImpl srv) {
		srv.serveMe();
	}

	void useAbstractService(Service srv) {
		srv.serveMe();
	}
	// snippet-end the-calls

}
