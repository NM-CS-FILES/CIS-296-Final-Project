package umd.cis296.states;

import umd.cis296.Context;
import umd.cis296.State;
import umd.cis296.Event;

public class InitialState implements State {

  public State handle(Event event, Context context) {
    return this;
  }

}
