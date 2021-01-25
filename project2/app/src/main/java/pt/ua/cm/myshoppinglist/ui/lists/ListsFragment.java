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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pt.ua.cm.myshoppinglist.MainActivity;
import pt.ua.cm.myshoppinglist.R;
import pt.ua.cm.myshoppinglist.utils.AddNewItem;
import pt.ua.cm.myshoppinglist.utils.RecyclerItemTouchHelper;

public class ListsFragment extends Fragment {

    private ListsModel listsModel;
    private RecyclerView listsRecyclerView;
    private ListPreviewAdapter listsAdapter;
    private MainActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listsModel = new ViewModelProvider(this).get(ListsModel.class);
        View root = inflater.inflate(R.layout.fragment_lists, container, false);
        final TextView textView = root.findViewById(R.id.text_lists);

        activity = (MainActivity) getActivity();

        listsRecyclerView = root.findViewById(R.id.itemsRecyclerView);
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listsAdapter = activity.getListsAdapter("listName");
        // Connecting Adapter class with the Recycler view*/
        listsRecyclerView.setAdapter(listsAdapter);

        FloatingActionButton fab = root.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("listName", "listName");
                AddNewItem fragment = new AddNewItem();
                fragment.setArguments(bundle);
                fragment.show(activity.getSupportFragmentManager(), AddNewItem.TAG);
            }
        });

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(listsAdapter));
        itemTouchHelper.attachToRecyclerView(listsRecyclerView);

        ImageButton addButton = root.findViewById(R.id.bt_addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("listName", "listName");
                AddNewItem fragment = new AddNewItem();
                fragment.setArguments(bundle);
                fragment.show(activity.getSupportFragmentManager(), AddNewItem.TAG);
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

    // Function to tell the app to start getting
    // data from database on starting of the activity
    @Override
    public void onStart() {
        super.onStart();
        listsAdapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stoping of the activity
    @Override
    public void onStop() {
        super.onStop();
        listsAdapter.stopListening();
    }
}