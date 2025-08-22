import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Friday {
    private static final String LINE = "____________________________________________________________";
    private static List<String> tasks = new ArrayList<>();
    private static int size = 0;

    public static void main(String[] args) {
        box(" Hello! I'm Friday", " What can I do for you?");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String input = sc.nextLine();

            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else if (input.equals("list")) {
              showList();
            } else {
                addTask(input);
            }
        }
    }

    private static void addTask(String s) {
        tasks.add(s);
        box("added: " + s);
    }

    private static void showList() {
        List<String> lines = new ArrayList<>();
        lines.add("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            lines.add((i + 1) + "." + tasks.get(i));
        }
        box(lines.toArray(new String[0]));
    }

    private static void box(String... lines) {
        System.out.println(LINE);
        for (String l : lines) System.out.println(l);
        System.out.println(LINE);
    }
}
