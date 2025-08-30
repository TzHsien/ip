package friday.app;

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
                    ui.bye();
                    break;
                }
            } catch (FridayException e) {
                ui.error(e.getMessage());
            } catch (Exception e) {
                ui.error("Unexpected error: " + e.getMessage());
            }
        }
    }
}