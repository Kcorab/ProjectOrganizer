package de.lehmann.projectorganizer.persistence;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

/**
 * Author: Tim Lehabarock on 05.10.16.
 */
public class RealmDatabaseManager {

    /**
     * <p>This method initialise the realm database and set a default configuration.</p>
     *
     * @param context
     */
    public static void initRealm(Context context) {

        Realm.init(context);

        // The Realm file will be located in Context.getFilesDir() with name "po.realm"
        final RealmConfiguration config = new RealmConfiguration.Builder()
                .name("po.realm")
                //.encryptionKey()
                .schemaVersion(0)
                //.modules()
                //.migration()
                .build();

        Realm.setDefaultConfiguration(config);
    }
}