import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Friday {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        box(" Hello! I'm Friday", " What can I do for you?");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String input = sc.nextLine();

            if (input.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                break;
            } else {
                box(input);
            }
        }
    }

    private static void box(String... lines) {
        System.out.println(LINE);
        for (String l : lines) System.out.println(l);
        System.out.println(LINE);
    }
}
