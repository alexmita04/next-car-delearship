package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AuditService {
    private static final AuditService INSTANCE = new AuditService();
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String DEFAULT_AUDIT_FILE = "audit.csv";

    private final Path auditFile;

    private AuditService() {
        this.auditFile = Paths.get(DEFAULT_AUDIT_FILE);
    }

    public static AuditService getInstance() {
        return INSTANCE;
    }

    public synchronized void logAction(String actionName) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String line = actionName + ", " + timestamp + System.lineSeparator();
        try {
            Files.writeString(auditFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write audit log to " + auditFile, e);
        }
    }
}
