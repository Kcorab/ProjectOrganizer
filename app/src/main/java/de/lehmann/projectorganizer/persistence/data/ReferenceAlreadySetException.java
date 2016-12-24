package de.lehmann.projectorganizer.persistence.data;

import io.realm.RealmObject;

/**
 * Created by barock on 05.10.16.
 */

public class ReferenceAlreadySetException extends Exception {

    private static final String MESSAGE = "A reference of %s is already set for !";

    public <T extends RealmObject>ReferenceAlreadySetException(Class<T> referenceThatExist) {
        super(String.format(MESSAGE, referenceThatExist.toString()));
    }
}
