package pt.ua.cm.myshoppinglist.ui.lists;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import pt.ua.cm.myshoppinglist.ActivitySetLocation;
import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.entities.ItemModel;
import pt.ua.cm.myshoppinglist.entities.ListModel;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.FirebaseDbHandler;
import pt.ua.cm.myshoppinglist.utils.ListItemClickListener;
import pt.ua.cm.myshoppinglist.utils.RecyclerItemTouchHelper;

import static pt.ua.cm.myshoppinglist.utils.LocationUtils.MARKERS;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.SET_LIST_MARKERS;

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
        db.deleteList(list.getListName());
    }

    public void editList(int position) {
        ListModel list = this.getItem(position);
        Bundle bundle = new Bundle();
        //bundle.putString("id", item.getUuid());
        //bundle.putString("item", item.getProductName());
        //bundle.putString("listName", listName);
        //AddNewItem fragment = new AddNewItem();
        //fragment.setArguments(bundle);
        //fragment.show(activity.getSupportFragmentManager(), AddNewItem.TAG);
    }

    public void openList(int position) {
        ListModel list = this.getItem(position);
        activity.getListDetailAdapter(list.getListName());
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
            String listName = getItem(position).getListName();
            activity.onListItemClick(listName);
        }
    }
    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

}