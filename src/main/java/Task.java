import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contains the blueprint of all tasks that can be added, removed or marked.
 */

public abstract class Task {
    final String desc;
    boolean done;

    Task(String d) {
        this.desc = d;
    }
    abstract String typeIcon();                 // [T], [D], [E]
    String statusIcon() {
        return done ? "[X]" : "[ ]";
    }
    String extra() {
        return "";
    }
    String display() {
        return typeIcon() + statusIcon() + " " + desc + extra();
    }

    /** Encode: TYPE | done(0/1) | desc [| time(s)] */
    String toStorage() {
        return String.format("%s | %d | %s", typeIcon().substring(1,2), done ? 1 : 0, desc);
    }

    static Task fromStorage(String line) {
        String[] p = line.split("\\s*\\|\\s*");
        if (p.length < 3) {
            throw new IllegalArgumentException("Bad line: " + line);
        }
        String type = p[0]; boolean done = "1".equals(p[1]); String desc = p[2];
        Task t;
        switch (type) {
        case "T": t = new ToDo(desc); break;
        case "D":
            if (p.length < 4) {
                throw new IllegalArgumentException("Deadline missing time: " + line);
            }
            t = new Deadline(desc, parseIsoOrFlexible(p[3])); break;
        case "E":
            if (p.length < 5) {
                throw new IllegalArgumentException("Event missing time: " + line);
            }
            t = new Event(desc, parseIsoOrFlexible(p[3]), parseIsoOrFlexible(p[4])); break;
        default: throw new IllegalArgumentException("Unknown type: " + type);
        }
        t.done = done;
        return t;
    }

    // ISO or fallback used by storage:
    private static LocalDateTime parseIsoOrFlexible(String s) {
        try {
            return LocalDateTime.parse(s);
        } catch (Exception ignored) {}
        try {
            return LocalDate.parse(s).atStartOfDay();
        } catch (Exception ignored) {}
        try {
            return Parser.parseDT(s);
        } catch (Exception ignored) {}
        return LocalDate.of(1970,1,1).atStartOfDay();
    }
}

class ToDo extends Task {
    ToDo(String d) {
        super(d);
    }
    String typeIcon() {
        return "[T]";
    }
}

class Deadline extends Task {
    final LocalDateTime due;
    Deadline(String d, LocalDateTime due) {
        super(d);
        this.due = due;
    }
    String typeIcon() {
        return "[D]";
    }
    String extra() {
        return " (by: " + Parser.formatForDisplay(due) + ")";
    }
    @Override String toStorage() {
        return String.format("D | %d | %s | %s", done ? 1 : 0, desc, due);
    }
}

class Event extends Task {
    final LocalDateTime from, to;
    Event(String d, LocalDateTime from, LocalDateTime to) {
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
    @Override String toStorage() {
        return String.format("E | %d | %s | %s | %s", done ? 1 : 0, desc, from, to);
    }
}


