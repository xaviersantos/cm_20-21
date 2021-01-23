package pt.ua.cm.myshoppinglist.ui.lists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.RecyclerItemTouchHelper;

public class ListsAdapter extends Fragment {

    private ListsModel listsModel;
    private RecyclerView listsRecyclerView;
    private ListDetailAdapter listsAdapter;
    private MainActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listsModel = new ViewModelProvider(this).get(ListsModel.class);
        View root = inflater.inflate(R.layout.fragment_lists, container, false);
        final TextView textView = root.findViewById(R.id.text_lists);

        activity = (MainActivity) getActivity();

        listsRecyclerView = root.findViewById(R.id.itemsRecyclerView);
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listsAdapter = activity.getListsAdapter();
        listsRecyclerView.setAdapter(listsAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(listsAdapter));
        itemTouchHelper.attachToRecyclerView(listsRecyclerView);

        ImageButton addButton = root.findViewById(R.id.bt_addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
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
}