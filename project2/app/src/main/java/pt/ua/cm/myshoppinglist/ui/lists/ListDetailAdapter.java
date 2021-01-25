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
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.DatabaseHandler;

public class ListDetailAdapter extends RecyclerView.Adapter<ListDetailAdapter.ViewHolder>{
    private List<ListModel> listInstance;
    private DatabaseHandler db;
    private MainActivity activity;

    public ListDetailAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_detail, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();

        final ListModel item = listInstance.get(position);
        //holder.item.setText(item.getItem());
        //holder.item.setChecked(toBoolean(item.getStatus()));
        holder.item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //db.updateStatus(item.getItem(), true);
                } else {
                    //db.updateStatus(item.getItem(), true);
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

    public void setItems(List<ListModel> todoList) {
        this.listInstance = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ListModel item = listInstance.get(position);
        //db.deleteItem(item.getId());
        listInstance.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ListModel item = listInstance.get(position);
        Bundle bundle = new Bundle();
        //bundle.putString("item", item.getItem());
        AddNewItem fragment = new AddNewItem();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewItem.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox item;

        ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.listCheckBox);
        }
    }
}
