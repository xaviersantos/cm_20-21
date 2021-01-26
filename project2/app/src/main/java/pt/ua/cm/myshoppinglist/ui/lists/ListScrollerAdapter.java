package pt.ua.cm.myshoppinglist.ui.lists;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.entities.ListModel;
import pt.ua.cm.myshoppinglist.utils.AddNewList;
import pt.ua.cm.myshoppinglist.utils.FirebaseDbHandler;

public class ListScrollerAdapter extends FirestoreRecyclerAdapter<ListModel, ListScrollerAdapter.listsViewholder> {
    private MainActivity activity;
    private FirebaseDbHandler db;

    public ListScrollerAdapter(@NonNull FirestoreRecyclerOptions<ListModel> options, MainActivity activity) {
        super(options);
        this.activity = activity;
        this.db = activity.getDb();
    }

    @Override
    protected void onBindViewHolder(@NonNull listsViewholder holder, int position, @NonNull ListModel model) {
        holder.listName.setText(model.getListName());
    }

    @NonNull
    @Override
    public listsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_preview, parent, false);

        return new ListScrollerAdapter.listsViewholder(view);
    }

    public Context getContext() {
        return activity;
    }

    public void deleteList(int position) {
        ListModel list = this.getItem(position);
        db.deleteList(list.getUuid());
    }

    public void editList(int position) {
        ListModel list = this.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString("listId", list.getUuid());
        bundle.putString("listName", list.getListName());
        AddNewList fragment = new AddNewList();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewList.TAG);
    }

    public void openList(int position) {
        ListModel list = this.getItem(position);
        activity.getListDetailAdapter(list.getUuid());
    }

    class listsViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView listName;

        public listsViewholder(@NonNull View itemView) {
            super(itemView);
            listName = itemView.findViewById(R.id.listName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ListModel list = getItem(position);
            activity.onListItemClick(list);
        }
    }
    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

}