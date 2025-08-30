package friday.model;

/**
 * A {@link friday.model.Task} with no time information.
 */
public class ToDo extends Task {
    public ToDo(String d) {
        super(d);
    }

    String typeIcon() {
        return "[T]";
    }
}