package test.system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  ApplyingNewClientTest.class,
  ChatTest.class
})

public class SystemTestSuite {

	public SystemTestSuite() {
		// TODO Auto-generated constructor stub
	}

	public static void waitForProcessing() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			
		}		
	}	
	
}
