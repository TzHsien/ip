import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Friday {
    private static final String LINE = "____________________________________________________________";

    static class Task {
        String desc;
        boolean done;
        Task(String d) { this.desc = d; }
        String shortLabel() { return (done ? "[X] " : "[ ] ") + desc; }
    }

    private static final List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        box(" Hello! I'm Friday", " What can I do for you?");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String s = sc.nextLine().trim();
            if (s.isEmpty()) continue;

            if (s.equals("bye")) { box(" Bye. Hope to see you again soon!"); break; }
            if (s.equals("list")) { showList(); continue; }
            if (s.startsWith("mark ") || s.startsWith("unmark ")) { toggle(s); continue; }

            tasks.add(new Task(s));
            box(" added: " + s);
        }
    }

    private static void showList() {
        if (tasks.isEmpty()) { box(" (no items yet)"); return; }
        List<String> lines = new ArrayList<>();
        lines.add(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            lines.add(" " + (i + 1) + ".[" + (t.done ? "X" : " ") + "] " + t.desc);
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
                    "   " + t.shortLabel());
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
