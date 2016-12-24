/*
 * Copyright (c) 2016 Tim Lehmann <l_@freenet.de>
 *
 * Diese informatische Anwendung, einschließlich all ihrer Bestandteile, wird im Folgenden mit dem Begriff "Software"
 * umfasst.
 *
 * Die Rechte dieser Software liegen allein bei dem oben erwähnten Inhaber. Davon unberührt bleibt das geistige Eigentum
 * des Autors/der Autoren innerhalb des von ihm/ihnen entworfenen Bestandteils der Software.
 *
 * Jegliche unentgeltliche Nutzung dieser Software durch andere Personen, als die des Inhabers, ist untersagt.
 *   Es wird keine Funktionalität dieser Software gewährleistet, was eine Haftung des Inhabers bei etwaiger vom Anwender
 * unerwünschter Verhaltensweise ausschließt.
 */

package de.lehmann.projectorganizer.controller.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import de.lehmann.projectorganizer.R;
import de.lehmann.projectorganizer.persistence.TransactionMods.AdapterNotifier;
import de.lehmann.projectorganizer.persistence.TransactionMods.GenericTransaction;
import de.lehmann.projectorganizer.persistence.Validation;
import de.lehmann.projectorganizer.persistence.data.Project;
import de.lehmann.projectorganizer.persistence.data.ValidationException;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * This class represents a {@link DataDialogFragment} that displays an {@link AlertDialog} with a
 * custom view to manage {@link Project} data objects.
 *
 * @author Tim Lehmann <l_@freenet.de />
 */

public class ProjectDialogFragment extends DataDialogFragment<Project> implements TextWatcher {

    /**
     * Static {@link String} object as the key for the setting of {@link #dialogTitle} by
     * {@link #onSaveInstanceState(Bundle)} and also finding of {@link #dialogTitle} by
     * {@link #setArguments(Bundle)}.
     */
    private static final String DIALOG_TITLE = "dialog_title";

    /**
     * Static {@link String} object as the key for the setting of {@link #positiveButtonText} by
     * {@link #onSaveInstanceState(Bundle)} and also finding of {@link #positiveButtonText} by
     * {@link #setArguments(Bundle)}.
     */
    private static final String POSITIVE_BUTTON_TEXT = "positive_button_text";

    /**
     * Title of the shown dialog. Todo: Find a way to outsource for an easy translation.
     */
    private String dialogTitle;

    /**
     * Title of the shown dialogs positive button. Todo: Find a way to outsource for an easy translation.
     */
    private String positiveButtonText;

    private AlertDialog alertDialog = null;
    private EditText title = null;
    private EditText description = null;

    /**
     * Use this method to creating a new {@link ProjectDialogFragment} object instead of the constructor.
     *
     * @param dialogTitle        the text that should be shown as the dialog title
     * @param positiveButtonText the text that should be shown in the positive button
     * @param transaction        the object whose {@link GenericTransaction#execute(Realm)} method should invoke on a
     *                           click of the positive button
     * @param onSuccess          the object whose {@link AdapterNotifier#onSuccess()} method should invoke after the
     *                           invocation of the transaction objects {@link GenericTransaction#execute(Realm)} method
     * @return new instance of class {@link ProjectDialogFragment}
     */
    public static ProjectDialogFragment newInstance(@NonNull final String dialogTitle,
                                                    @NonNull final String positiveButtonText,
                                                    @NonNull final GenericTransaction transaction,
                                                    final AdapterNotifier onSuccess) {

        final ProjectDialogFragment projectDialogFragment = new ProjectDialogFragment();
        projectDialogFragment.transaction = transaction;
        projectDialogFragment.onSuccess = onSuccess;

        final Bundle bdl = new Bundle(2);
        bdl.putString(DIALOG_TITLE, dialogTitle);
        bdl.putString(POSITIVE_BUTTON_TEXT, positiveButtonText);

        projectDialogFragment.setArguments(bdl);

        return projectDialogFragment;
    }

    /**
     * Use this method to creating a new {@link ProjectDialogFragment} object instead of the constructor.
     *
     * @param dialogTitle        the text that should be shown as the dialog title
     * @param positiveButtonText the text that should be shown in the positive button
     * @param transaction        the object whose {@link GenericTransaction#execute(Realm)} method should invoke on a
     *                           click of the positive button
     * @return new instance of class {@link ProjectDialogFragment}
     */
    public static ProjectDialogFragment newInstance(@NonNull final String dialogTitle,
                                                    @NonNull final String positiveButtonText,
                                                    @NonNull final GenericTransaction transaction) {

        return newInstance(dialogTitle, positiveButtonText, transaction, null);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DIALOG_TITLE, dialogTitle);
        outState.putString(POSITIVE_BUTTON_TEXT, positiveButtonText);
    }

    @Override
    public void setArguments(final Bundle args) {
        this.dialogTitle = args.getString(DIALOG_TITLE);
        this.positiveButtonText = args.getString(POSITIVE_BUTTON_TEXT);

        super.setArguments(args);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Cache the dialog.
        if (alertDialog == null) {

            final View layout = getActivity().getLayoutInflater().
                    inflate(R.layout.dialog_create_project, null);

            if (title == null) {
                title = (EditText) layout.
                        findViewById(R.id.dialog_project_title);
            }
            if (description == null) {
                description = (EditText) layout.
                        findViewById(R.id.dialog_project_description);
            }

            /* This TextChangedListener will access to the positive button of the AlertDialog!
            So make sure that the button is built and exist at the time of text changing! */
            title.addTextChangedListener(this);

            builder
                    .setView(layout)
                    .setTitle(this.dialogTitle)
                    .setPositiveButton(this.positiveButtonText,
                            this)
                    .setNegativeButton(R.string.dialog_negative,
                            null);

            alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    /* Fill the fields when the dialog is shown because the TextChangedListener will
                    access to the positive button which is created firstly when the dialog is shown. */
                    fillInputFields();
                }
            });
        } else {
            fillInputFields();
        }

        return alertDialog;
    }

    /**
     * Call this method only if you are sure {@link #title}, {@link #description} and {@link #realmObject} are exist at
     * this time. It fills these input fields with the information of the set {@link RealmObject}.
     */
    private void fillInputFields() {
        if (realmObject != null) {
            // If a realm object is set fill the field of dialog with its values
            title.setText(realmObject.getTitle());
            description.setText(realmObject.getDescription());
        }else{
            resetInputFields();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private boolean areInputValuesDifferent() {

        return realmObject == null ||
                !title.getText().toString().equals(realmObject.getTitle()) ||
                !description.getText().toString().equals(realmObject.getDescription());
    }

    @Override
    protected void positiveClicked() {

        if (areInputValuesDifferent()) {
            super.positiveClicked();
        }
    }

    /**
     * In this method a new {@link Project} object is created. This method assumed that the values of input fields are
     * new and not the same as before.
     *
     * @return A new {@link RealmObject} objects that is not managed by realm yet.
     * @throws ValidationException
     */
    @NonNull
    @Override
    public RealmObject buildRealmObject() throws ValidationException {

        final Project project = new Project();

        if (realmObject == null) {
            // Set a new unused primary key for the new project entity.
            project.setId(Project.generateId());
        } else {
            // Update the exist RealmObject.

            /* In this case the exist RealmObject are directly from the database. Don't change the values by the
            RealmObjects' set methods because there is no open realm transaction! */

            // Set the used primary key.
            project.setId(this.realmObject.getId());

            // Reset the old entity.
            this.realmObject = null;
        }

        project.setTitle(this.title.getText().toString());
        project.setDescription(this.description.getText().toString());

        return project;
    }

    /**
     * Call this for deleting the text of the input fields for title and project description. Make sure that both input
     * fields exists at this time.
     */
    public void resetInputFields() {
        this.title.setText("");
        this.description.setText("");
    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
    }

    @Override
    public void afterTextChanged(final Editable s) {

        final CharSequence text = s.toString();
        final boolean isPatternMatched = Validation.ALPHANUMERICS.matcher(text).matches();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isPatternMatched);
    }


}