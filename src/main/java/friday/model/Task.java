package friday.model;

import friday.logic.Parser;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contains the blueprint of all tasks (
 * {@link friday.model.ToDo}, {@link friday.model.Deadline}
 * , {@link friday.model.Event}) that can be added,
 * removed or marked.
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
    public String display() {
        return typeIcon() + statusIcon() + " " + desc + extra();
    }

    /** Encode: TYPE | done(0/1) | desc [| time(s)] */
    public String toStorage() {
        return String.format("%s | %d | %s", typeIcon().substring(1,2), done ? 1 : 0, desc);
    }

    public static Task fromStorage(String line) {
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
                throw new IllegalArgumentException("friday.model.Deadline missing time: " + line);
            }
            t = new Deadline(desc, parseIsoOrFlexible(p[3])); break;
        case "E":
            if (p.length < 5) {
                throw new IllegalArgumentException("friday.model.Event missing time: " + line);
            }
            t = new Event(desc, parseIsoOrFlexible(p[3]), parseIsoOrFlexible(p[4])); break;
        default: throw new IllegalArgumentException("Unknown type: " + type);
        }
        t.done = done;
        return t;
    }

    // ISO or fallback used by friday.storage:
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

    public boolean matches(String keyword) {
        return desc.toLowerCase().contains(keyword.toLowerCase());
    }
}

