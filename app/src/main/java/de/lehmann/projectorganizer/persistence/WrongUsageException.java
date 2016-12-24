package de.lehmann.projectorganizer.persistence;

/**
 * Created by barock on 10.10.16.
 */

public class WrongUsageException extends RuntimeException {

    public WrongUsageException(final String message) {
        super(message);
    }
}
