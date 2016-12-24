package de.lehmann.projectorganizer.persistence.TransactionMods;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import io.realm.Realm;

/**
 * This abstract class provides some predefined classes ({@link Insert}, {@link Change},
 * {@link Remove}) you can use for notify an {@link android.support.v7.widget.RecyclerView.Adapter}
 * by an successful realm db transaction.
 *
 * @author Tim Lehmann <l_@freenet.de/>
 */
public abstract class AdapterNotifier implements Realm.Transaction.OnSuccess {

    protected final RecyclerView.Adapter adapter;
    protected Integer affectedItemPosition = null;

    public AdapterNotifier(final RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Only call this method timely if you need the current position of the affected item at the
     * time of or in your {@link #onSuccess()} method.
     *
     * @param affectedItemPosition position of the item at which an operation will be applied
     */
    public void setAffectedItemPosition(@NonNull final Integer affectedItemPosition) {
        this.affectedItemPosition = affectedItemPosition;
    }

    /**
     * This class represents an successful create operation.
     */
    public static class Insert extends AdapterNotifier {

        public Insert(RecyclerView.Adapter adapter) {
            super(adapter);
        }

        /**
         * If there is no value for {@link #affectedItemPosition} by {@link #setAffectedItemPosition(Integer)} set
         * then this method notify the {@link #adapter} to insert the new object on the last position.
         */
        @Override
        public void onSuccess() {
            // If the position of affected item is not set take the last position of the adapter.
            if (affectedItemPosition == null) {
                affectedItemPosition = adapter.getItemCount() - 1;
            }
            adapter.notifyItemInserted(affectedItemPosition);

            // Reset the position to avoid forget setting.
            affectedItemPosition = null;
        }
    }

    /**
     * This class represents an successful update operation.
     */
    public static class Change extends AdapterNotifier {

        public Change(RecyclerView.Adapter adapter) {
            super(adapter);
        }

        @Override
        public void onSuccess() {
            // If the position of affected item is not set take the last position of the adapter.
            if (affectedItemPosition == null) {
                throw new UnknownItemPositionException(this);
            }
            adapter.notifyItemChanged(affectedItemPosition);

            // Reset the position to avoid forget setting.
            affectedItemPosition = null;
        }
    }

    /**
     * This class represents an successful delete operation.
     */
    public static class Remove extends AdapterNotifier {

        public Remove(RecyclerView.Adapter adapter) {
            super(adapter);
        }

        @Override
        public void onSuccess() {
            // If the position of affected item is not set take the last position of the adapter.
            if (affectedItemPosition == null) {
                throw new UnknownItemPositionException(this);
            }
            adapter.notifyItemRemoved(affectedItemPosition);

            // Reset the position to avoid forget setting.
            affectedItemPosition = null;
        }
    }

    /**
     * Exception for a forgotten setting of the item position.
     */
    public static class UnknownItemPositionException extends RuntimeException {

        private static final String errorMessage = "The item position of %s wasn't set! Please " +
                "use setAffectedItemPosition(...) of %s to set the item position.";

        public UnknownItemPositionException(final AdapterNotifier AdapterNotifier) {
            super(String.format(
                    errorMessage,
                    AdapterNotifier.toString(),
                    AdapterNotifier.getClass())
            );
        }
    }
}