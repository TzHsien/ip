package friday.ui;

import java.util.List;

import friday.model.Task;

/**
 * All printing and reading functions are handled by friday.app.Ui.friday.app.Ui class.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    public void greet() {
        box(" Hello! I'm Friday", " What can I do for you?");
    }
    public void bye()   {
        box(" Bye. Hope to see you again soon!");
    }

    public void error(String msg) { box(" " + msg); }

    /** Returns a friday.model.TaskList which contains all the Tasks created. */
    public void showList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            box(" (no items yet)");
            return;
        }
        String[] lines = new String[tasks.size() + 1];
        lines[0] = " Here are the tasks in your list:";
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = " " + (i + 1) + "." + tasks.get(i).display();
        }
        box(lines);
    }

    /** Acknowledges that a friday.model.Task has been added to friday.model.TaskList. */
    public void added(Task t, int size) {
        box(" Got it. I've added this task:",
                "   " + t.display(), " Now you have " + size + " tasks in the list.");
    }

    /** Acknowledges that a friday.model.Task has been removed from friday.model.TaskList, based on friday.model.Task number. */
    public void removed(Task t, int size) {
        box(" Noted. I've removed this task:",
                "   " + t.display(), " Now you have " + size + " tasks in the list.");
    }

    /** Acknowledges that a friday.model.Task status has been updated in friday.model.TaskList. */
    public void toggled(Task t, boolean marked) {
        box(marked ? " Nice! I've marked this task as done:" :
                        " OK, I've marked this task as not done yet:", "   " + t.display());
    }

    /** Formats friday.app.Friday's response by sandwiching her response in between two lines. */
    private void box(String... lines) {
        System.out.println(LINE);
        for (String l : lines) System.out.println(l);
        System.out.println(LINE);
    }

    public void showMatches(List<Task> matches) {
        if (matches.isEmpty()) {
            box(" You have no task containing this keyword");
            return;
        }
        String[] lines = new String[matches.size() + 1];
        lines[0] = " Here are the tasks in your list containing this keyword:";
        for (int i = 0; i< matches.size(); i++) {
            lines[i + 1] = " " + (i + 1) + "." + matches.get(i).display();
        }
        box(lines);
    }
}