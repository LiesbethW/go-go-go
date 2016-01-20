package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  test.game.GameTestSuite.class,
  test.controllers.ControllerTestSuite.class,
  test.network.NetworkTestSuite.class
})


public class TotalTestSuite {

}
