package pt.ua.cm.myshoppinglist.ui.lists;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.DatabaseHandler;
import pt.ua.cm.myshoppinglist.utils.DialogCloseListener;
import pt.ua.cm.myshoppinglist.utils.RecyclerItemTouchHelper;

public class ListsAdapter extends Fragment implements DialogCloseListener {

    private ListsModel listsModel;
    private RecyclerView itemsRecyclerView;
    private ListDetailAdapter itemsAdapter;
    private FloatingActionButton fab;
    private DatabaseHandler db;
    private MainActivity activity;

    private List<ListModel> itemList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listsModel = new ViewModelProvider(this).get(ListsModel.class);
        View root = inflater.inflate(R.layout.fragment_lists, container, false);
        final TextView textView = root.findViewById(R.id.text_lists);

        activity = (MainActivity) getActivity();

        db = new DatabaseHandler(activity);
        db.openDatabase();

        itemsRecyclerView = root.findViewById(R.id.listCheckBox);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemsAdapter = new ListDetailAdapter(db, activity);
        itemsRecyclerView.setAdapter(itemsAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(itemsAdapter));
        itemTouchHelper.attachToRecyclerView(itemsRecyclerView);

        fab = getView().findViewById(R.id.fab);

        itemList = db.getAllItems();
        Collections.reverse(itemList);

        itemsAdapter.setItems(itemList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewItem.newInstance().show(activity.getSupportFragmentManager(), AddNewItem.TAG);
            }
        });

        listsModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        
        return root;
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        itemList = db.getAllItems();
        Collections.reverse(itemList);
        itemsAdapter.setItems(itemList);
        itemsAdapter.notifyDataSetChanged();
    }
}