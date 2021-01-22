package pt.ua.cm.myshoppinglist.ui.lists;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.utils.DatabaseHandler;

public class ListPreviewAdapter extends RecyclerView.Adapter<ListPreviewAdapter.ViewHolder>{
    private List<ListModel> listInstance;
    private DatabaseHandler db;
    private MainActivity activity;

    public ListPreviewAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ListPreviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_preview, parent, false);
        return new ListPreviewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListPreviewAdapter.ViewHolder holder, int position) {
        db.openDatabase();

        final ListModel item = listInstance.get(position);
        holder.item.setText(item.getItem());
        holder.item.setChecked(toBoolean(item.getStatus()));
        holder.item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                } else {
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return listInstance.size();
    }

    public Context getContext() {
        return activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox item;

        ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.listPreview);
        }
    }
}
