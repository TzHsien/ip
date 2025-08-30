package friday.model;

public class ToDo extends Task {
    public ToDo(String d) {
        super(d);
    }

    String typeIcon() {
        return "[T]";
    }
}