package pt.ua.cm.myshoppinglist.ui.lists;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.entities.ItemModel;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.FirebaseDbHandler;

public class ListDetailAdapter extends FirestoreRecyclerAdapter<ItemModel, ListDetailAdapter.listsViewholder> {
    private FirebaseDbHandler db;
    private String listId;
    private MainActivity activity;

    public ListDetailAdapter(@NonNull FirestoreRecyclerOptions<ItemModel> options, FirebaseDbHandler db, String listId, MainActivity activity) {
        super(options);
        this.db = db;
        this.listId = listId;
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull listsViewholder holder, int position, @NonNull ItemModel model) {
        holder.item.setText(model.getProductName());
        holder.item.setChecked(model.getStatus());
        holder.item.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                db.changeItemStatus(listId, model.getUuid(), true);
            } else {
                db.changeItemStatus(listId, model.getUuid(), false);
            }
        });
    }

    public Context getContext() {
        return activity;
    }

    public void deleteItem(int position) {
        ItemModel item = this.getItem(position);
        db.deleteItem(listId, item.getUuid());
    }

    public void editItem(int position) {
        ItemModel item = this.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString("id", item.getUuid());
        bundle.putString("item", item.getProductName());
        bundle.putString("listId", listId);
        AddNewItem fragment = new AddNewItem();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewItem.TAG);
    }

    @NonNull
    @Override
    public listsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_detail, parent, false);

        return new ListDetailAdapter.listsViewholder(view);
    }

    class listsViewholder extends RecyclerView.ViewHolder {
        CheckBox item;

        public listsViewholder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.listCheckBox);
        }
    }
    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }
}