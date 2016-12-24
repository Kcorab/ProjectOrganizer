package de.lehmann.projectorganizer.persistence.TransactionMods;

import de.lehmann.projectorganizer.persistence.data.Project;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;

/**
 * This abstract class is the base for some
 *
 * @author Tim Lehmann <l_@freenet.de />
 */

public abstract class GenericTransaction implements Realm.Transaction {

    protected RealmObject realmObject = null;

    public void setRealmObject(final RealmObject realmObject) {
        this.realmObject = realmObject;
    }

    public static class Create extends GenericTransaction {

        @Override
        public void execute(Realm realm) {
            realm.insert(realmObject);
            realmObject = null;
        }
    }

    public static class Update extends GenericTransaction {

        @Override
        public void execute(Realm realm) {
            realm.insertOrUpdate(realmObject);
            realmObject = null;
        }
    }

    public static class Delete extends GenericTransaction {

        /**
         * This method have to run sync.
         *
         * @param realm the current {@link Realm} instance
         */
        @Override
        public void execute(Realm realm) {
            realmObject.deleteFromRealm();
            realmObject = null;
        }
    }
}
