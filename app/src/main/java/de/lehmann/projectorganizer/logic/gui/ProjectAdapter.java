package de.lehmann.projectorganizer.logic.gui;

import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.lehmann.projectorganizer.R;
import de.lehmann.projectorganizer.persistence.TransactionMods.AdapterNotifier;
import de.lehmann.projectorganizer.persistence.TransactionMods.GenericTransaction;
import de.lehmann.projectorganizer.persistence.data.Project;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author Tim Lehmann <l_@freenet.de />
 */

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private final RealmResults<Project> list;
    public final Realm realm;

    private final View.OnLongClickListener onLongClickListener;

    public ProjectAdapter(final View.OnLongClickListener onLongClickListener) {

        this.realm = Realm.getDefaultInstance();
        this.list = realm.where(Project.class).findAllAsync();
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        final View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_element, parent, false);

        view.setOnLongClickListener(this.onLongClickListener);

        return new ProjectViewHolder((CardView) view);
    }

    public void deleteProject(int pos){
        list.deleteFromRealm(pos);
    }

    @Override
    public void onBindViewHolder(final ProjectViewHolder holder, final int position) {

        final Project project = list.get(position);
        final Pair<Project, Integer> projectToPosition = new Pair<>(project, position);

        holder.itemView.setTag(projectToPosition);
        holder.textView.setText(project.getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        realm.close();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {

        public final TextView textView;

        public ProjectViewHolder(final CardView cardView) {
            super(cardView);
            if (cardView.getChildCount() > 0) {
                textView = (TextView) cardView.getChildAt(0);
            } else {
                textView = null;
            }
        }
    }
}