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

package de.lehmann.projectorganizer.controller.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.lehmann.projectorganizer.R;
import de.lehmann.projectorganizer.controller.activities.ProjectActivity;
import de.lehmann.projectorganizer.controller.fragments.dialogs.DataDialogFragment;
import de.lehmann.projectorganizer.controller.fragments.dialogs.ProjectDialogFragment;
import de.lehmann.projectorganizer.logic.gui.ProjectAdapter;
import de.lehmann.projectorganizer.persistence.TransactionMods.AdapterNotifier;
import de.lehmann.projectorganizer.persistence.TransactionMods.GenericTransaction;
import de.lehmann.projectorganizer.persistence.data.Project;
import io.realm.Realm;

/**
 * A {@link Fragment} object that liable for the CRUD operations of
 * {@link de.lehmann.projectorganizer.persistence.data.Project} objects.
 *
 * @author Tim Lehmann <l_@freenet.de />
 */
public class ProjectFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener, PopupMenu
        .OnMenuItemClickListener {

    private static final String CREATE_DIALOG_TAG = "create_dialog";
    private static final String UPDATE_DIALOG_TAG = "update_dialog";

    private final ProjectAdapter adapter;
    private final DataDialogFragment<Project> creatorDialogFragment;
    private final DataDialogFragment<Project> updateDialogFragment;

    private final GenericTransaction deleteGenericTransaction;
    private final AdapterNotifier deleteAdapterNotifier;

    private RecyclerView recyclerView = null;

    private Pair<Project, Integer> currentProjectToPosition = null;

    public ProjectFragment() {

        this.adapter = new ProjectAdapter(this);

        this.creatorDialogFragment = ProjectDialogFragment.newInstance(
                "Insert a Project", "Create", new GenericTransaction.Create(), new AdapterNotifier.Insert(this.adapter)
        );
        this.updateDialogFragment = ProjectDialogFragment.newInstance(
                "Update a Project", "Update", new GenericTransaction.Update(), new AdapterNotifier.Change(this.adapter)
        );

        this.deleteGenericTransaction = new GenericTransaction.Delete();
        this.deleteAdapterNotifier = new AdapterNotifier.Remove(adapter);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_projects, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
//            ((AppCompatActivity) activity).getSupportActionBar().
//                    setTitle(getResources().getString(R.string.fragment_projects_title));
        }
        if (activity instanceof ProjectActivity) {
            ((ProjectActivity) activity).getFloatingActionButton().setOnClickListener(this);
        }
    }

    /**
     * Click on a {@link android.support.design.widget.FloatingActionButton} object.
     *
     * @param v The clicked {@link View} object. In this case a
     *          {@link android.support.design.widget.FloatingActionButton}.
     */
    @Override
    public void onClick(final View v) {

        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

//        creatorDialogFragment.setAffectedItemPosition(0);

        /* In this case there can be only one dialog. So the removing of an previous advertised
        dialog is not needed. */

        // Insert and show the dialog.
        creatorDialogFragment.show(fragmentTransaction, CREATE_DIALOG_TAG);
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.popup_update: // update call dialog

                this.updateDialogFragment.setRealmObject(currentProjectToPosition.first);
                this.updateDialogFragment.setAffectedItemPosition(currentProjectToPosition.second);
                this.updateDialogFragment.show(getFragmentManager(), UPDATE_DIALOG_TAG);

                break;
            case R.id.popup_delete: // delete call deleteMethod

                deleteRealmObject();

                break;
            default:
        }

        return true;
    }

    private void deleteRealmObject() {


        this.deleteGenericTransaction.setRealmObject(currentProjectToPosition.first);
        this.deleteAdapterNotifier.setAffectedItemPosition(currentProjectToPosition.second);

        final Realm realm = Realm.getDefaultInstance();

        // A async delete isn't allowed.
//        realm.executeTransactionAsync(this.deleteGenericTransaction, this.deleteAdapterNotifier);

        // A realm delete have to be sync.
        realm.executeTransaction(this.deleteGenericTransaction);
        // Call the success method manually.
        this.deleteAdapterNotifier.onSuccess();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onLongClick(final View v) {

        currentProjectToPosition = (Pair<Project, Integer>) v.getTag();


        //Creating the instance of PopupMenu
        final PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        //Inflating the Popup using xml file
        popupMenu.getMenuInflater().inflate(R.menu.popup_project_operations, popupMenu.getMenu());

        //registering popup with OnMenuItemClickListener
        popupMenu.setOnMenuItemClickListener(this);

        popupMenu.show(); //showing popup menu

        return false;
    }
}