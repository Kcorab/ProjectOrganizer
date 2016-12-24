package de.lehmann.projectorganizer.persistence.data;

import android.support.annotation.NonNull;

import de.lehmann.projectorganizer.persistence.Validation;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * An data object that holds all data for a project.
 *
 * @author Tim Lehmann <l_@freenet.de>
 */

public class Project extends RealmObject {

    private static final byte MAX_TITLE_LENGTH = 32;
    @PrimaryKey
    protected Long id;
    private String title;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull final String title) throws ValidationException {

        if (title.length() > MAX_TITLE_LENGTH) {
            throw new ValidationException(
                    "The title length doesn't have to be lager that" +
                            MAX_TITLE_LENGTH
            );
        }
        if (!Validation.ALPHANUMERICS.matcher(title).matches()) {
            throw new ValidationException(
                    "For the title only alphanumeric characters are allowed! Your current title" +
                            title
            );
        }

        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * All values from the given {@link Project} object which are not null are apply to this object beside the primary
     * key (id).
     * <p>
     * This method is the inverse of the {@link #updateUnsetValuesFrom(Project)} method.
     *
     * @param entity The {@link Project} object whose values should apply to this object.
     */
    public void updateSetValuesFrom(final Project entity) {

        if (entity.title != null) {
            this.title = entity.title;
        }
        if (entity.description != null) {
            this.description = entity.description;
        }
    }

    /**
     * All values of this object which are <code>null</code> are filled by the values of the given {@link Project}
     * object. Accidentally the values of the given {@link Project} object could be null, too. So, there is no null
     * check.
     * <p>
     * This method is the inverse of the {@link #updateSetValuesFrom(Project)} method.
     *
     * @param entity The {@link Project} object whose values should apply to this object.
     */
    public void updateUnsetValuesFrom(final Project entity) {

        if (this.title == null) {
            this.title = entity.title;
        }
        if (this.description == null) {
            this.description = entity.description;
        }
    }

    /**
     * In this method the highest id, which is currently used as primary key, is detected. After this the increment of
     * the detected value is returned. Note that of curse there can be free ids lower than the returned value.
     *
     * @return a free primary key
     */
    public static Long generateId() {
        final Number id = Realm.getDefaultInstance().where(Project.class).max("id");
        return id == null ? 0 : id.longValue() + 1;
    }
}
