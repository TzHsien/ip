package friday.model;

import friday.ui.Ui;
import friday.storage.Storage;
import friday.exception.FridayException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Consists of all methods that modify the task list.
 */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    public List<Task> all() {
        return tasks;
    }

    public void setAll(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
    }

    public Task get(int oneBased) throws FridayException {
        int i = oneBased - 1;
        if (i < 0 || i >= tasks.size()) {
            throw new FridayException("That task number does not exist.");
        }
        return tasks.get(i);
    }

    public void add(Task t, Ui ui, Storage storage) throws IOException {
        tasks.add(t);
        storage.save(tasks);
        ui.added(t, tasks.size());
    }

    public void remove(int oneBased, Ui ui, Storage storage) throws FridayException, IOException {
        assert get(oneBased) != null;
        Task t = get(oneBased);
        tasks.remove(oneBased - 1);
        storage.save(tasks);
        ui.removed(t, tasks.size());
    }

    public void toggle(int oneBased, boolean mark, Ui ui, Storage storage) throws FridayException, IOException {
        assert get(oneBased) != null;
        Task t = get(oneBased);
        t.done = mark;
        storage.save(tasks);
        ui.toggled(t, mark);
    }

    public List<Task> find(String keyword) {
        String k = keyword.toLowerCase();
        List<Task> out = new ArrayList<>();
        for (Task t : tasks) {
            if (t.matches(k)) out.add(t);
        }
        return out;
    }
}

