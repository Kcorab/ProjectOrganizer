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

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import android.widget.Button;
import android.app.Dialog;

import de.lehmann.projectorganizer.persistence.TransactionMods.AdapterNotifier;
import de.lehmann.projectorganizer.persistence.TransactionMods.GenericTransaction;
import de.lehmann.projectorganizer.persistence.data.ValidationException;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * This class represents an {@link DialogFragment} within you can hold a {@link RealmObject} instance and a
 * {@link GenericTransaction} object which should execute with the mentioned instance.
 *
 * @param <T> any class that extends {@link RealmObject} to make sure that the object can
 * @author Tim Lehmann <l_@freenet.de />
 */
public abstract class DataDialogFragment<T extends RealmObject> extends DialogFragment implements
        DialogInterface.OnClickListener {

    /**
     * This instance is holt as member because it have to pass to the {@link GenericTransaction} object at the right
     * time.
     */
    protected T realmObject = null;

    /**
     * {@link io.realm.Realm.Transaction} object whose
     * {@link io.realm.Realm.Transaction#execute(Realm)} method should used for the transaction.
     * This object have to be set. Otherwise it will be run into a {@link NullPointerException}.
     */
    protected GenericTransaction transaction = null;

    /**
     * If a async transaction is wanted this {@link io.realm.Realm.Transaction.OnSuccess} object
     * should be set. Otherwise the execution of transaction will be sync.
     */
    protected AdapterNotifier onSuccess = null;

    /**
     * Set a {@link RealmObject} which should be represented by this object. If such an object is
     * set the input fields of the represented dialog should fill with the informations of the
     * specific {@link RealmObject}.
     *
     * @param realmObject The {@link RealmObject} which should be represented by this object.
     */
    public void setRealmObject(@NonNull final T realmObject) {
        this.realmObject = realmObject;
    }

    /**
     * This method have to be call before the transaction starts.
     *
     * @param transaction The {@link io.realm.Realm.Transaction} object which should used for the
     *                    transaction.
     */
    public void setTransaction(@NonNull final GenericTransaction transaction) {
        this.transaction = transaction;
    }

    /**
     * If an async transaction is wanted this method should call before the transaction starts.
     *
     * @param onSuccess The {@link io.realm.Realm.Transaction.OnSuccess} object which should used
     *                  for the transaction.
     */
    public void setOnSuccess(@NonNull final AdapterNotifier onSuccess) {
        this.onSuccess = onSuccess;
    }

    /**
     * This method pass the given position to the associated {@link AdapterNotifier} object.
     *
     * @param setAffectedItemPosition The adapters item position that should be update.
     */
    public void setAffectedItemPosition(@NonNull Integer setAffectedItemPosition) {
        this.onSuccess.setAffectedItemPosition(setAffectedItemPosition);
    }

    /**
     * If there is a RealmObject already associated with this class by
     * {@link #setRealmObject(RealmObject)} then this associated object is returned. Otherwise it is
     * built by the information which are entered by user at the time the dialog was shown.
     *
     * @return An object of type {@link RealmObject}.
     * @throws ValidationException This exception is thrown if the creating of {@link RealmObject}
     *                             doesn't conform the validation.
     */
    @NonNull
    public abstract RealmObject buildRealmObject() throws ValidationException;

    /**
     * This method makes all input fields empty.
     */
    public abstract void resetInputFields();

    /**
     * Standard method for clicking the positive {@link Button} object of the {@link Dialog} object which is
     * represented by this class. It starts the transaction which is set by {@link #setTransaction(GenericTransaction)}
     * with the {@link #realmObject} which is set by {@link #setRealmObject}. If there was an exception, a {@link Toast}
     * is popped up.
     */
    protected void positiveClicked() {

        try {
            transaction.setRealmObject(buildRealmObject());
            resetInputFields();
        } catch (ValidationException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        final Realm realm = Realm.getDefaultInstance();

        if (onSuccess == null) {
            realm.executeTransaction(this.transaction);
        } else {
            realm.executeTransactionAsync(this.transaction, this.onSuccess);
        }
    }

    @Override
    public final void onClick(DialogInterface dialog, int which) {

        if (dialog == this.getDialog())

            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    positiveClicked();
                    break;

                case AlertDialog.BUTTON_NEGATIVE: // Do nothing.
                    break;

                case AlertDialog.BUTTON_NEUTRAL: // Do nothing.
                    break;
                default:
            }
    }
}