import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.nio.charset.StandardCharsets;
import java.io.IOException;

/**
 * Friday: a Personal Assistant Chatbot that helps a person keep track of various things
 * <p>
 * Supported commands:
 * <ul>
 *   <li>{@code list} – show tasks</li>
 *   <li>{@code todo <desc>} – add a todo</li>
 *   <li>{@code deadline <desc> /by <when>} – add a deadline</li>
 *   <li>{@code event <desc> /from <start> /to <end>} – add an event</li>
 *   <li>{@code mark <n>} / {@code unmark <n>} – toggle completion</li>
 *   <li>{@code delete <n>} – remove task</li>
 *   <li>{@code bye} – exit</li>
 * </ul>
 */
public class Friday {

    // Horizontal divider
    private static final String LINE = "____________________________________________________________";

    /**
     * Base task model with description and completion flag.
     * Subclasses provide the task type and optional extra display info.
     */
    private static abstract class Task {
        final String desc;
        boolean done;
        Task(String d) { this.desc = d; }
        abstract String typeIcon();
        String statusIcon() { return done ? "[X]" : "[ ]"; }
        String extra() { return ""; }
        String display() { return typeIcon() + statusIcon() + " " + desc + extra(); }

        /** Encode one line: TYPE | done(0/1) | desc [| time(s)] */
        String toStorage() { return String.format("%s | %d | %s", typeIcon().substring(1,2), done ? 1 : 0, desc); }

        /** Parse one storage line back into a Task. */
        static Task fromStorage(String line) {
            String[] p = line.split("\\s*\\|\\s*", 4); // allow time field
            if (p.length < 3) throw new IllegalArgumentException("Bad line: " + line);
            String type = p[0], doneStr = p[1], desc = p[2];
            boolean done = "1".equals(doneStr);
            Task t;
            switch (type) {
            case "T":
                t = new ToDo(desc);
                break;
            case "D":
                if (p.length < 4) throw new IllegalArgumentException("Deadline missing time: " + line);
                t = new Deadline(desc, p[3]);
                break;
            case "E":
                if (p.length < 4) throw new IllegalArgumentException("Event missing time: " + line);
                // For events we stored a single “from..to..” string in p[3]
                String time = p[3];
                // optional: split back to from/to if you prefer (not strictly needed for display)
                t = new Event(desc, time, ""); // or keep a single field variant
                break;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
            }
            t.done = done;
            return t;
        }
    }

    /** To-do task without time information. */
    private static class ToDo extends Task {
        ToDo(String d) { super(d); }
        String typeIcon() { return "[T]"; }
    }

    /** Deadline task by a certain time */
    private static class Deadline extends Task {
        final String by;
        Deadline(String d, String by) {
            super(d);
            this.by = by;
        }
        String typeIcon() { return "[D]"; }
        String extra() { return " (by: " + by + ")"; }

        @Override
        String toStorage() {
            return String.format("D | %d | %s | %s", done ? 1 : 0, desc, by);
        }
    }

    /** Event task with a start and end time */
    private static class Event extends Task {
        final String from, to;
        Event(String d, String from, String to) { super(d); this.from = from; this.to = to; }
        String typeIcon() { return "[E]"; }
        String extra() { return " (from: " + from + " to: " + to + ")"; }
        @Override
        String toStorage() {
            return String.format("E | %d | %s | %s to %s", done ? 1 : 0, desc, from, to);
        }
    }

    /** List of tasks created. */
    private static final List<Task> tasks = new ArrayList<>();

    /** txt file containing tasks created. */
    private static final Path FILE_PATH = Paths.get("data", "texts.txt");

    /** Ensure parent folder exists (e.g., data/). */
    private static void ensureParentDir() throws IOException {
        Path parent = FILE_PATH.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private static void loadTasks() throws IOException {
        ensureParentDir();
        if (!Files.exists(FILE_PATH)) {
            Files.createFile(FILE_PATH);
            return; // first run → nothing to load
        }
        List<String> lines = Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8);
        tasks.clear();
        for (String line : lines) {
            if (line.isBlank()) continue;
            try {
                tasks.add(Task.fromStorage(line));
            } catch (IllegalArgumentException ex) {
                // Skip malformed lines; could log if desired.
            }
        }
    }

    /** Overwrite disk so it mirrors the in-memory list. */
    private static void updateTasks() throws IOException {
        ensureParentDir();
        List<String> lines = new ArrayList<>(tasks.size());
        for (Task t : tasks) lines.add(t.toStorage());
        Files.write(FILE_PATH, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void main(String[] args) throws IOException{
        loadTasks(); // <-- load on startup
        box(" Hello! I'm Friday", " What can I do for you?");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String cmd = sc.nextLine().trim();
            if (cmd.isEmpty()) continue;

            try {
                if (cmd.equals("bye")) {
                    box(" Bye. Hope to see you again soon!");
                    break;
                } else if (cmd.equals("list")) {
                    showList();
                } else if (cmd.startsWith("mark ") || cmd.startsWith("unmark ")) {
                    toggle(cmd);
                } else if (cmd.startsWith("todo")) {
                    addTodo(cmd);
                } else if (cmd.startsWith("deadline")) {
                    addDeadline(cmd);
                } else if (cmd.startsWith("event")) {
                    addEvent(cmd);
                } else if (cmd.startsWith("delete ")) {
                    removeEvent(cmd);
                } else {
                    throw new FridayException("What talk you bro");
                }
            } catch (FridayException e) {
                box(" " + e.getMessage());
            }
        }
    }

    /** Shows the list of tasks created, with their task type, completion status and description. */
    private static void showList() {
        if (tasks.isEmpty()) {
            box(" (no items yet)");
            return;
        }
        List<String> lines = new ArrayList<>();
        lines.add(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            lines.add(" " + (i + 1) + "." + tasks.get(i).display());
        }
        box(lines.toArray(new String[0]));
    }

    /** Removes a task from List, throws exception if input is invalid. */
    private static void removeEvent(String cmd) throws FridayException{
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
        box("Noted. I've removed this task:",
                toRemove.display(),
                "Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Adds a toDo task to List, throws exception if input is invalid. */
    private static void addTodo(String cmd) throws FridayException, IOException {
        String desc = cmd.substring(4).trim();
        if (desc.isEmpty()) {
            throw new FridayException("Walao how can a task be empty OII!");
        }
        addTask(new ToDo(desc));
        updateFile();
    }

    /** Adds a deadline Task to List, throws exception if input is invalid. */
    private static void addDeadline(String cmd) throws FridayException, IOException {
        // "deadline <desc> /by <when>"
        String rest = cmd.substring(8).trim();
        int i = rest.indexOf("/by");
        if (i < 0) throw new FridayException("Eh you blur or what where's your deadline");
        String desc = rest.substring(0, i).trim();
        String by = rest.substring(i + 3).trim();
        if (desc.isEmpty() || by.isEmpty()) {
            throw new FridayException("Walao do you know how to fill in a deadline or not");
        }
        addTask(new Deadline(desc, by));
        updateFile();
    }

    /** Adds an event Task to List, throws exception if input is invalid. */
    private static void addEvent(String cmd) throws FridayException, IOException {
        // "event <desc> /from <start> /to <end>"
        String rest = cmd.substring(5).trim();
        int iFrom = rest.indexOf("/from");
        int iTo   = rest.indexOf("/to");
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
        updateFile();
    }

    /** Toggles completion status of task in List, throws exception if input is invalid. */
    private static void toggle(String cmd) throws FridayException, IOException {
        boolean mark = cmd.startsWith("mark ");
        String taskNumber = cmd.substring(mark ? 5 : 7).trim();
        int n;
        try {
            n = Integer.parseInt(taskNumber);
        } catch (NumberFormatException e) {
            throw new FridayException("Ah bang can you provide a valid task number.");
        }
        if (n < 1 || n > tasks.size()) {
            throw new FridayException("Brother that task number don't exist lah.");
        }
        Task t = tasks.get(n - 1);
        t.done = mark;
        updateTasks(); // <-- save after change
        box(mark ? " Nice! I've marked this task as done:"
                        : " OK, I've marked this task as not done yet:",
                "   " + t.display());
    }

    // ----- Helpers -----

    private static void addTask(Task t) {
        tasks.add(t);
        box(" Got it. I've added this task:",
                "   " + t.display(),
                " Now you have " + tasks.size() + " tasks in the list.");
    }

    private static void box(String... lines) {
        System.out.println(LINE);
        for (String l : lines) System.out.println(l);
        System.out.println(LINE);
    }

    /** Saves the task into task.txt */
    private static void updateFile() throws IOException {
        if (!Files.exists(FILE_PATH)) {
            Files.createFile(FILE_PATH);
        }

        Files.write(FILE_PATH, (Iterable<? extends CharSequence>) tasks, StandardCharsets.UTF_8, (OpenOption) StandardOpenOption.TRUNCATE_EXISTING);
    }
}