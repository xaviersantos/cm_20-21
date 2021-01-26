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
import pt.ua.cm.myshoppinglist.utils.RecyclerItemTouchHelper;

import static pt.ua.cm.myshoppinglist.utils.LocationUtils.MARKERS;
import static pt.ua.cm.myshoppinglist.utils.LocationUtils.SET_LIST_MARKERS;

public class ListScrollerAdapter extends FirestoreRecyclerAdapter<ListModel, ListScrollerAdapter.listsViewholder> {

    private LinearLayoutManager lln;
    private MainActivity activity;
    private ListPreviewAdapter listPreviewAdapter;

    public ListScrollerAdapter(@NonNull FirestoreRecyclerOptions<ListModel> options, MainActivity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull listsViewholder holder, int position, @NonNull ListModel model) {
        holder.initList(holder.listRecyclerView, model.getListName());
    }

    @NonNull
    @Override
    public listsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lists_list, parent, false);

        return new ListScrollerAdapter.listsViewholder(view);
    }

    class listsViewholder extends RecyclerView.ViewHolder {
        RecyclerView listRecyclerView;

        public listsViewholder(@NonNull View itemView) {
            super(itemView);
            listRecyclerView = itemView.findViewById(R.id.listsRecyclerView);
        }

        public void initList(View root, String listName) {
            listRecyclerView = root.findViewById(R.id.listsRecyclerView);
            listRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            listPreviewAdapter = activity.getListPreviewAdapter(listName);
            // Connecting Adapter class with the Recycler view*/
            listRecyclerView.setAdapter(listPreviewAdapter);
        }
    }
    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }

}