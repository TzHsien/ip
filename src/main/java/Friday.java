import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Friday {
    private static final String LINE = "____________________________________________________________";

    private static abstract class Task {
        final String desc;
        boolean done;
        Task(String d) { this.desc = d; }
        abstract String typeIcon();                  // [T], [D], [E]
        String statusIcon() { return done ? "[X]" : "[ ]"; }
        String extra() { return ""; }
        String display() { return typeIcon() + statusIcon() + " " + desc + extra(); }
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

            try {
                if (cmd.equals("bye")) { box(" Bye. Hope to see you again soon!"); break; }
                else if (cmd.equals("list")) { showList(); }
                else if (cmd.startsWith("mark ") || cmd.startsWith("unmark ")) { toggle(cmd); }
                else if (cmd.startsWith("todo")) { addTodo(cmd); }
                else if (cmd.startsWith("deadline")) { addDeadline(cmd); }
                else if (cmd.startsWith("event")) { addEvent(cmd); }
                else if (cmd.startsWith("delete ")) { removeEvent(cmd); }
                else {
                    throw new FridayException("What talk you bro");
                }
            } catch (FridayException e) {
                box(" " + e.getMessage());
            }
        }
    }

    // ----- Command handlers (throw on bad input) -----
    private static void removeEvent(String cmd) throws FridayException {
        String index = cmd.substring(6).trim();
        int n;
        try {
            n = Integer.parseInt(index);
        } catch (NumberFormatException e) {
            throw new FridayException("Valid number please...");
        }
        if (n < 1 || n > tasks.size()) {
            throw new FridayException("You don't even know how many tasks you have???");
        }
        Task toRemove = tasks.get(n - 1);
        tasks.remove(n - 1);
        int newTaskSize = tasks.size();
        box("Noted. I've removed this task:",
                toRemove.display(),
                "Now you have " + (newTaskSize) + " tasks in the list.");
    }
    private static void addTodo(String cmd) throws FridayException {
        String rest = cmd.length() >= 4 ? cmd.substring(4) : "";
        String desc = rest.startsWith(" ") ? rest.substring(1).trim() : rest.trim();
        if (desc.isEmpty()) {
            throw new FridayException("Walao how can a task be empty OII!");
        }
        addTask(new ToDo(desc));
    }

    private static void addDeadline(String cmd) throws FridayException {
        // "deadline <desc> /by <when>"
        String rest = cmd.substring(8).trim();
        int i = indexOfToken(rest, "/by");
        if (i < 0) throw new FridayException("Eh you blur or what where's your deadline");
        String desc = rest.substring(0, i).trim();
        String by = rest.substring(i + 3).trim();
        if (desc.isEmpty() || by.isEmpty()) {
            throw new FridayException("Walao do you know how to fill in a deadline or not");
        }
        addTask(new Deadline(desc, by));
    }

    private static void addEvent(String cmd) throws FridayException {
        // "event <desc> /from <start> /to <end>"
        String rest = cmd.substring(5).trim();
        int iFrom = indexOfToken(rest, "/from");
        int iTo   = indexOfToken(rest, "/to");
        if (iFrom < 0 || iTo < 0 || iTo <= iFrom) {
            throw new FridayException("Bro you need to type from and to for me to know your event time");
        }
        String desc = rest.substring(0, iFrom).trim();
        String from = rest.substring(iFrom + 5, iTo).trim();
        String to   = rest.substring(iTo + 3).trim();
        if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new FridayException("Bro you know how to give event time or not");
        }
        addTask(new Event(desc, from, to));
    }

    private static void toggle(String cmd) throws FridayException {
        boolean mark = cmd.startsWith("mark ");
        String nStr = cmd.substring(mark ? 5 : 7).trim();
        int n;
        try {
            n = Integer.parseInt(nStr);
        } catch (NumberFormatException e) {
            throw new FridayException("OOPS!!! Please provide a valid task number.");
        }
        if (n < 1 || n > tasks.size()) {
            throw new FridayException("OOPS!!! That task number does not exist.");
        }
        Task t = tasks.get(n - 1);
        t.done = mark;
        box(mark ? " Nice! I've marked this task as done:"
                        : " OK, I've marked this task as not done yet:",
                "   " + t.display());
    }

    // ----- Helpers -----
    private static int indexOfToken(String s, String token) {
        int i = s.indexOf(" " + token + " ");
        if (i >= 0) return i + 1;
        i = s.indexOf(token + " ");
        if (i >= 0) return i;
        i = s.indexOf(" " + token);
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

    private static void box(String... lines) {
        System.out.println(LINE);
        for (String l : lines) System.out.println(l);
        System.out.println(LINE);
    }
}