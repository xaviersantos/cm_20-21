package pt.ua.cm.myshoppinglist.ui.lists;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.entities.ItemModel;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.FirebaseDbHandler;

public class ListPreviewAdapter extends FirestoreRecyclerAdapter<ItemModel, ListPreviewAdapter.listsViewholder> {

    private final MainActivity activiy;
    private final String listName;

    public ListPreviewAdapter(@NonNull FirestoreRecyclerOptions<ItemModel> options, String listName, MainActivity activity) {
        super(options);
        this.activiy = activity;
        this.listName = listName;
    }

    @Override
    protected void onBindViewHolder(@NonNull listsViewholder holder, int position, @NonNull ItemModel model) {
        holder.item.setText(model.getProductName());
        if (!model.getStatus())
            holder.item.setPaintFlags(holder.item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            holder.item.setPaintFlags(holder.item.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));


    }

    @NonNull
    @Override
    public listsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_preview, parent, false);

        TextView listTitle = view.findViewById(R.id.listTitle);
        listTitle.setText(listName);

        return new ListPreviewAdapter.listsViewholder(view);
    }

    class listsViewholder extends RecyclerView.ViewHolder {
        TextView item;

        public listsViewholder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.listItemText);
        }
    }
    @Override
    public void onError(FirebaseFirestoreException e) {
        Log.e("error", e.getMessage());
    }
}