package friday.app;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Scanner;
import friday.ui.Ui;
import friday.storage.Storage;
import friday.logic.Parser;
import friday.model.TaskList;
import friday.exception.FridayException;


/**
 * friday.app.Friday: a Personal Assistant Chatbot that helps a person keep track of various things
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
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(Paths.get("data", "tasks.txt"));
        TaskList tasks = new TaskList();

        try {
            tasks.setAll(storage.load());                 // load on startup
        } catch (Exception e) {
            ui.error("Failed to load file: " + e.getMessage());
        }

        ui.greet();
        Parser parser = new Parser();

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                boolean exit = parser.handle(line, tasks, ui, storage); // parse + execute
                if (exit) {
                    break;
                }
            } catch (FridayException e) {
                ui.error(e.getMessage());
            } catch (Exception e) {
                ui.error("Unexpected error: " + e.getMessage());
            }
        }
    }
    /**
     * Generates a response for the user's chat message in GUI mode.
     */
    public String getResponse(String input) {
        Ui ui = new Ui();
        Storage storage = new Storage(Paths.get("data", "tasks.txt"));
        TaskList tasks = new TaskList();

        try {
            tasks.setAll(storage.load());                 // load on startup
        } catch (Exception e) {
            System.out.println("Failed to load file: " + e.getMessage());
        }

        Parser parser = new Parser();

        // redirect System.out to capture prints, since existing code uses System.out.printl
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream oldOut = System.out;
        System.setOut(ps);

        try {
            parser.handle(input, tasks, ui, storage);   // may print or throw
        } catch (FridayException e) {
            // capture the exception’s message as the “response”
            System.out.println(e.getMessage());
        } catch (Exception e) {
            // catch any other unexpected exception
            System.out.println(e.getMessage());
        } finally {
            System.out.flush();
            System.setOut(oldOut);
        }

        return baos.toString().trim();
    }
}