import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Friday {
    private static final String LINE = "____________________________________________________________";

    // ----- Task model -----
    private static abstract class Task {
        final String desc;
        boolean done;
        Task(String d) { this.desc = d; }
        abstract String typeIcon();                  // [T], [D], [E]
        String statusIcon() { return done ? "[X]" : "[ ]"; }
        String extra() { return ""; }                // for deadlines/events
        String display() {
            return typeIcon() + statusIcon() + " " + desc + extra();
        }
    }
    private static class ToDo extends Task {
        ToDo(String d) { super(d); }
        String typeIcon() { return "[T]"; }
    }
    private static class Deadline extends Task {
        final String by;
        Deadline(String d, String by) { super(d); this.by = by; }
        String typeIcon() { return "[D]"; }
        String extra() { return " (by: " + by + ")"; }
    }
    private static class Event extends Task {
        final String from, to;
        Event(String d, String from, String to) { super(d); this.from = from; this.to = to; }
        String typeIcon() { return "[E]"; }
        String extra() { return " (from: " + from + " to: " + to + ")"; }
    }

    private static final List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        box(" Hello! I'm Friday", " What can I do for you?");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String cmd = sc.nextLine().trim();
            if (cmd.isEmpty()) continue;

            if (cmd.equals("bye")) { box(" Bye. Hope to see you again soon!"); break; }
            if (cmd.equals("list")) { showList(); continue; }
            if (cmd.startsWith("mark ") || cmd.startsWith("unmark ")) { toggle(cmd); continue; }

            if (cmd.startsWith("todo ")) {
                String desc = cmd.substring(5).trim();
                if (desc.isEmpty()) { box(" Usage: todo <description>"); continue; }
                addTask(new ToDo(desc));
            } else if (cmd.startsWith("deadline ")) {
                String rest = cmd.substring(9).trim();
                int i = indexOfToken(rest, "/by");
                if (i < 0) { box(" Usage: deadline <description> /by <when>"); continue; }
                String desc = rest.substring(0, i).trim();
                String by = rest.substring(i + 3).trim();  // after "/by"
                if (desc.isEmpty() || by.isEmpty()) { box(" Usage: deadline <description> /by <when>"); continue; }
                addTask(new Deadline(desc, by));
            } else if (cmd.startsWith("event ")) {
                String rest = cmd.substring(6).trim();
                int iFrom = indexOfToken(rest, "/from");
                int iTo   = indexOfToken(rest, "/to");
                if (iFrom < 0 || iTo < 0 || iTo <= iFrom) { box(" Usage: event <desc> /from <start> /to <end>"); continue; }
                String desc = rest.substring(0, iFrom).trim();
                String from = rest.substring(iFrom + 5, iTo).trim(); // after "/from"
                String to   = rest.substring(iTo + 3).trim();        // after "/to"
                if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) { box(" Usage: event <desc> /from <start> /to <end>"); continue; }
                addTask(new Event(desc, from, to));
            } else {
                // Fallback: treat as a quick ToDo to remain user-friendly
                addTask(new ToDo(cmd));
            }
        }
    }

    // Finds token index accepting either " /token " or "/token "
    private static int indexOfToken(String s, String token) {
        int i = s.indexOf(" " + token + " ");
        if (i >= 0) return i + 1;                // skip the leading space
        i = s.indexOf(token + " ");
        if (i >= 0) return i;
        i = s.indexOf(" " + token);              // allow end-of-line after token
        if (i >= 0) return i + 1;
        return s.indexOf(token);
    }

    private static void addTask(Task t) {
        tasks.add(t);
        box(" Got it. I've added this task:",
                "   " + t.display(),
                " Now you have " + tasks.size() + " tasks in the list.");
    }

    private static void showList() {
        if (tasks.isEmpty()) { box(" (no items yet)"); return; }
        List<String> lines = new ArrayList<>();
        lines.add(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            lines.add(" " + (i + 1) + "." + tasks.get(i).display());
        }
        box(lines.toArray(new String[0]));
    }

    private static void toggle(String cmd) {
        boolean mark = cmd.startsWith("mark ");
        String nStr = cmd.substring(mark ? 5 : 7).trim();
        try {
            int n = Integer.parseInt(nStr);
            if (n < 1 || n > tasks.size()) { box(" Invalid task number: " + nStr); return; }
            Task t = tasks.get(n - 1);
            t.done = mark;
            box(mark ? " Nice! I've marked this task as done:"
                            : " OK, I've marked this task as not done yet:",
                    "   " + t.display());
        } catch (NumberFormatException e) {
            box(" Not a valid number: \"" + nStr + "\"");
        }
    }

    private static void box(String... lines) {
        System.out.println(LINE);
        for (String l : lines) System.out.println(l);
        System.out.println(LINE);
    }
}
