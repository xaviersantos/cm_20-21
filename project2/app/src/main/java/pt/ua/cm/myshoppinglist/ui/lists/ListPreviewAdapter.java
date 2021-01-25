package pt.ua.cm.myshoppinglist.ui.lists;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.entities.Item;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.DatabaseHandler;
import pt.ua.cm.myshoppinglist.utils.FirebaseDbHandler;

public class ListPreviewAdapter extends FirestoreRecyclerAdapter<Item, ListPreviewAdapter.listsViewholder> {
    private List<Item> listInstance;
    private FirebaseDbHandler db;
    private String listName;
    private MainActivity activity;

    public ListPreviewAdapter(@NonNull FirestoreRecyclerOptions<Item> options, FirebaseDbHandler db, String listName, MainActivity activity) {
        super(options);
        this.db = db;
        this.listName = listName;
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull listsViewholder holder, int position, @NonNull Item model) {

        //final Item item = listInstance.get(position);

        holder.item.setText(model.getName());
        holder.item.setChecked(model.isStatus());
        holder.item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //db.changeItemStatus(listName, model.getItem(), true);
                } else {
                    //db.changeItemStatus(listName, model.getItem(), false);
                }
            }
        });
    }

    public Context getContext() {
        return activity;
    }

    public void deleteItem(int position) {
        Item item = listInstance.get(position);
        //db.deleteItem("listName", item.getItem());
        listInstance.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        Item item = listInstance.get(position);
        Bundle bundle = new Bundle();
        //bundle.putInt("id", item.getId());
        //bundle.putString("item", item.getItem());
        AddNewItem fragment = new AddNewItem();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewItem.TAG);
    }

    @NonNull
    @Override
    public listsViewholder
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_detail, parent, false);
        return new ListPreviewAdapter.listsViewholder(view);
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