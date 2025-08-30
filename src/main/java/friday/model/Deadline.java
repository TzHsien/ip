package friday.model;

import friday.logic.Parser;
import java.time.LocalDateTime;

/**
 * A {@link friday.model.Task} with a deadline.
 */
public class Deadline extends Task {
    final LocalDateTime due;
    public Deadline(String d, LocalDateTime due) {
        super(d);
        this.due = due;
    }
    String typeIcon() {
        return "[D]";
    }
    String extra() {
        return " (by: " + Parser.formatForDisplay(due) + ")";
    }
    @Override
    public String toStorage() {
        return String.format("D | %d | %s | %s", done ? 1 : 0, desc, due);
    }
}