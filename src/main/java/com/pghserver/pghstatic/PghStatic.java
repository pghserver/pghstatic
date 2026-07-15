package com.pghserver.pghstatic;

import com.pghserver.api.PghAPI;
import com.pghserver.api.PghLogger;
import com.pghserver.api.PghPlugin;
import com.pghserver.api.type.RequestMethod;
import com.pghserver.api.type.ResponseStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PghStatic implements PghPlugin {
    private Path staticDirectory;
    private IndexesFile indexesFile;
    private boolean isOk = true;

    private void setupDirectory(PghAPI server, PghLogger logger) {
        staticDirectory = server.directory().resolve("static");
        if (!Files.exists(staticDirectory)) {
            try {
                Files.createDirectories(staticDirectory);
            } catch (IOException ex) {
                logger.fatal("Could not create static directory!", ex);
                isOk = false;
            }
        }

        indexesFile = new IndexesFile(server.directory().resolve("indexes.txt"), logger);
    }

    public void onEnable(PghAPI server, PghLogger logger) {
        setupDirectory(server, logger);
        if (!isOk) throw new RuntimeException();

        server.route("/.*", (req, res, next) -> {
            if (req.method() != RequestMethod.GET) {
                res.status(ResponseStatus.METHOD_NOT_ALLOWED);
                next.run();
            } else if (!req.url().path.contains("..") && !req.url().path.contains("\\")) {
                Path file = staticDirectory.resolve(req.url().path.substring(1));
                if (!Files.exists(file)) {
                    res.status(ResponseStatus.NOT_FOUND);
                    next.run();
                    return;
                }

                if (Files.isDirectory(file)) {
                    String indexFile = indexesFile.attempt(file);
                    if (indexFile == null) {
                        res.status(ResponseStatus.NOT_FOUND);
                        next.run();
                        return;
                    }

                    file = file.resolve(indexFile);
                }

                String mimeType;
                try {
                    mimeType = Files.probeContentType(file);
                } catch (IOException var9) {
                    mimeType = "application/octet-stream";
                }

                res.contentType(mimeType);

                byte[] data;
                try {
                    data = Files.readAllBytes(file);
                } catch (IOException var8) {
                    data = new byte[0];
                }

                res.body(data);
            } else {
                res.status(ResponseStatus.BAD_REQUEST);
                next.run();
            }
        });
    }

    public void onDisable(PghAPI server, PghLogger logger) {
    }
}

