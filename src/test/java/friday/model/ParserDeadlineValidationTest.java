package friday.model;

import java.nio.file.Path;   // 1) STANDARD_JAVA_PACKAGE first

import friday.exception.FridayException;  // 2) your project packages (friday.*), A–Z
import friday.logic.Parser;
import friday.storage.Storage;
import friday.ui.Ui;

import org.junit.jupiter.api.Test;         // 3) SPECIAL_IMPORTS (e.g., org.junit.*), A–Z
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;   // 4) STATIC imports last, no wildcard
import static org.junit.jupiter.api.Assertions.assertThrows;



public class ParserDeadlineValidationTest {

    /**
     * Gives the test a fresh, throwaway folder to use for files.
     * JUnit create it before each test, injects the path into tmpDir, and deletes it after
     * the test finishes.
     */
    @TempDir
    Path tmpDir;

    @Test
    void deadline_withoutByToken_throwsHelpfulUsageError() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage(tmpDir.resolve("tasks.txt"));

        FridayException ex = assertThrows(
                FridayException.class, () -> parser.handle("deadline return book", tasks, ui, storage)
        );
        assertEquals("Use: deadline <desc> /by <when>", ex.getMessage());
    }

    @Test
    void deadline_missingDescOrTime_throwsDescriptionAndTimeRequired() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage(tmpDir.resolve("tasks.txt"));

        // Missing description
        FridayException ex1 = assertThrows(
                FridayException.class, () -> parser.handle("deadline /by 2019-10-15", tasks, ui, storage)
        );
        assertEquals("Description and time required.", ex1.getMessage());

        // Missing time
        FridayException ex2 = assertThrows(
                FridayException.class, () -> parser.handle("deadline return book /by", tasks, ui, storage)
        );
        assertEquals("Description and time required.", ex2.getMessage());
    }
}
