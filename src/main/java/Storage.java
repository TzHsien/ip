import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final Path file;

    public Storage(Path file) { this.file = file; }

    public List<Task> load() throws IOException {
        ensureParentDir();
        if (!Files.exists(file)) {
            Files.createFile(file);
            return new ArrayList<>();
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<Task> result = new ArrayList<>();
        for (String s : lines) {
            if (s.isBlank()) {
                continue;
            }
            try {
                result.add(Task.fromStorage(s));
            } catch (IllegalArgumentException ignored) {}
        }
        return result;
    }

    public void save(List<Task> tasks) throws IOException {
        ensureParentDir();
        List<String> lines = new ArrayList<>(tasks.size());
        for (Task t : tasks) lines.add(t.toStorage());
        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void ensureParentDir() throws IOException {
        Path parent = file.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }
}

