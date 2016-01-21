package test.network;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.network.protocol.InterpreterTest;
import test.network.protocol.PresenterTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  ServerTest.class,
  InterpreterTest.class,
  PresenterTest.class
})

public class NetworkTestSuite {

	public NetworkTestSuite() {
		// TODO Auto-generated constructor stub
	}

}
