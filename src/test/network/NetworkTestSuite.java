package test.network;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.network.protocol.PresenterTest;
import test.network.protocol.ServerSideInterpreterTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  ServerTest.class,
  ServerSideInterpreterTest.class,
  PresenterTest.class
})

public class NetworkTestSuite {

	public NetworkTestSuite() {
		// TODO Auto-generated constructor stub
	}

}
