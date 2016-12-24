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

package de.lehmann.projectorganizer.controller;

import android.app.Application;

import de.lehmann.projectorganizer.persistence.RealmDatabaseManager;

/**
 * @author Tim Lehmann <l_@freenet.de />
 */

public class ProjectOrganizerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RealmDatabaseManager.initRealm(this);
    }
}
