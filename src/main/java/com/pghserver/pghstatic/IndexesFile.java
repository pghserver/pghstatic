package com.pghserver.pghstatic;

import com.pghserver.api.PghLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class IndexesFile {
    private final @NotNull List<String> indexes = new ArrayList<>();
    private final @NotNull Path file;
    private final @NotNull PghLogger logger;

    public IndexesFile(@NotNull Path file, @NotNull PghLogger logger) {
        this.file = file;
        this.logger = logger;
        reload();
    }

    public @Nullable String attempt(Path directory) {
        if (!Files.exists(directory)) return null;
        for (String index : indexes) {
            Path indexFile = directory.resolve(index);
            if (Files.exists(indexFile) && !Files.isDirectory(indexFile)) return index;
        }

        return null;
    }

    public void reload() {
        saveDefault();
        try (Stream<String> lines = Files.lines(file)) {
            indexes.clear();
            for (String raw : lines.toList()) {
                String line = raw.trim();
                if (!line.isBlank() && !line.startsWith("#")) indexes.add(line);
            }
        } catch (IOException ex) {
            logger.error("Could not reload", file.getFileName() + "!", ex);
        }
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (int i = 0; i < indexes.size(); ++i) {
                String index = indexes.get(i).trim();
                writer.write(index);
                if (i != indexes.size() - 1) writer.write('\n');
            }

            indexes.removeIf(raw -> {
                String line = raw.trim();
                return line.isBlank() || line.startsWith("#");
            });
        } catch (IOException ex) {
            logger.error("Could not save", file.getFileName() + "!", ex);
        }

    }

    public void saveDefault() {
        if (Files.exists(file)) return;
        indexes.clear();
        indexes.add("### List of index files #########################################################################");
        indexes.add("#                                                                                               #");
        indexes.add("#   When a link specifies a directory instead, each of these files will be located inside the   #");
        indexes.add("#   directory and will be attempted to use from top to bottom.                                  #");
        indexes.add("#                                                                                               #");
        indexes.add("#   Lines starting with '#' are ignored!                                                        #");
        indexes.add("#                                                                                               #");
        indexes.add("#################################################################################################");
        indexes.add("index.html");
        indexes.add("index.htm");
        indexes.add("home.html");
        indexes.add("home.htm");
        indexes.add("default.html");
        indexes.add("default.htm");
        save();
    }
}
