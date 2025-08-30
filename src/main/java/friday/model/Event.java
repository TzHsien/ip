package friday.model;

import friday.logic.Parser;
import java.time.LocalDateTime;

public class Event extends Task {
    final LocalDateTime from, to;
    public Event(String d, LocalDateTime from, LocalDateTime to) {
        super(d);
        this.from = from;
        this.to = to;
    }
    String typeIcon() {
        return "[E]";
    }
    String extra() {
        return " (from: " + Parser.formatForDisplay(from) + " to: " + Parser.formatForDisplay(to) + ")";
    }
    @Override
    public String toStorage() {
        return String.format("E | %d | %s | %s | %s", done ? 1 : 0, desc, from, to);
    }
}