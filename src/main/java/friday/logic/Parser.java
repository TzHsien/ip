package friday.logic;

import friday.model.Deadline;
import friday.model.ToDo;
import friday.model.Event;
import friday.ui.Ui;
import friday.storage.Storage;
import friday.exception.FridayException;
import friday.model.TaskList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Contains the logic of {@link friday.app.Friday}
 * Checks for proper formatting of input, if improper
 * a {@link friday.exception.FridayException} will be thrown
 * Else the stored list of tasks will be modified
 * according to the input, either by adding or removing
 * a task, or marking a task as complete/incomplete.
 */
public class Parser {
    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter OUT_DT   = DateTimeFormatter.ofPattern("MMM d yyyy, h:mma");

    /** yyyy-MM-dd[ HHmm] or d/M/yyyy[ HHmm]; date-only -> 00:00 */
    public static LocalDateTime parseDT(String s) throws FridayException {
        s = s.trim();
        for (String p : new String[]{"yyyy-MM-dd HHmm","yyyy-MM-dd","d/M/yyyy HHmm","d/M/yyyy"}) {
            DateTimeFormatter f = DateTimeFormatter.ofPattern(p);
            try {
                return p.contains("HHmm") ? LocalDateTime.parse(s, f) : LocalDate.parse(s, f).atStartOfDay();
            } catch (DateTimeParseException ignored) {}
        }
        throw new FridayException("Cannot parse date/time. Use yyyy-MM-dd[ HHmm] or d/M/yyyy[ HHmm].");
    }
    static String fmt(LocalDateTime dt) {
        return (dt.getHour()==0 && dt.getMinute()==0) ? dt.toLocalDate().format(OUT_DATE) : dt.format(OUT_DT);
    }

    /** Returns true if user asked to exit. */
    public boolean handle(String cmd, TaskList tasks, Ui ui, Storage storage) throws Exception {
        if (cmd.equals("bye")) {
            return true;
        }

        if (cmd.equals("list")) {
            ui.showList(tasks.all());
            return false;
        }

        if (cmd.startsWith("mark ") || cmd.startsWith("unmark ")) {
            boolean mark = cmd.startsWith("mark ");
            String nStr = cmd.substring(mark ? 5 : 7).trim();
            int n = Integer.parseInt(nStr);
            tasks.toggle(n, mark, ui, storage);
            return false;
        }

        if (cmd.startsWith("delete ")) {
            int n = Integer.parseInt(cmd.substring(6).trim());
            tasks.remove(n, ui, storage);
            return false;
        }

        if (cmd.startsWith("todo")) {
            String desc = cmd.substring(4).trim();
            if (desc.isEmpty()) {
                throw new FridayException("friday.model.Task description cannot be empty.");
            }
            tasks.add(new ToDo(desc), ui, storage);
            return false;
        }

        if (cmd.startsWith("deadline")) {
            String rest = cmd.substring(8).trim();              // "<desc> /by <when>"
            int i = rest.indexOf("/by");
            if (i < 0) {
                throw new FridayException("Use: deadline <desc> /by <when>");
            }
            String desc = rest.substring(0, i).trim();
            String when = rest.substring(i + 3).trim();
            if (desc.isEmpty() || when.isEmpty()) {
                throw new FridayException("Description and time required.");
            }
            tasks.add(new Deadline(desc, parseDT(when)), ui, storage);
            return false;
        }

        if (cmd.startsWith("event")) {
            String rest = cmd.substring(5).trim();              // "<desc> /from <start> /to <end>"
            int iFrom = rest.indexOf("/from"), iTo = rest.indexOf("/to");
            if (iFrom < 0 || iTo < 0 || iTo <= iFrom) {
                throw new FridayException("Use: event <desc> /from <start> /to <end>");
            }
            String desc = rest.substring(0, iFrom).trim();
            String from = rest.substring(iFrom + 5, iTo).trim();
            String to   = rest.substring(iTo + 3).trim();
            if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new FridayException("Provide desc, from, and to.");
            }
            tasks.add(new Event(desc, parseDT(from), parseDT(to)), ui, storage);
            return false;
        }

        if (cmd.startsWith("find")) {
            String keyword = cmd.substring(4).trim();
            if (keyword.isEmpty()) throw new FridayException("Provide a keyword to find.");
            ui.showMatches(tasks.find(keyword));
            return false;
        }

        throw new FridayException("What talk you bro");
    }

    // expose formatters to friday.model.Task subclasses:
    public static String formatForDisplay(LocalDateTime dt) {
        return fmt(dt);
    }
}

