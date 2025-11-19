package umd.cis296;

public interface State {
  State handle(Event event, Context context);
}
