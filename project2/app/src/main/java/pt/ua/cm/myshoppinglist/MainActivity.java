package pt.ua.cm.myshoppinglist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pt.ua.cm.myshoppinglist.ui.lists.ListDetailAdapter;
import pt.ua.cm.myshoppinglist.ui.lists.ListModel;
import pt.ua.cm.myshoppinglist.ui.lists.ListsAdapter;
import pt.ua.cm.myshoppinglist.ui.lists.ListsModel;
import pt.ua.cm.myshoppinglist.ui.map.MapFragment;
import pt.ua.cm.myshoppinglist.ui.settings.SettingsFragment;
import pt.ua.cm.myshoppinglist.utils.DatabaseHandler;
import pt.ua.cm.myshoppinglist.utils.DialogCloseListener;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    final Fragment fragment1 = new ListsAdapter();
    final Fragment fragment2 = new MapFragment();
    final Fragment fragment3 = new SettingsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    private DatabaseHandler db;
    private ListDetailAdapter listsAdapter;
    private List<ListModel> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = new DatabaseHandler(this);
        db.openDatabase();

        listsAdapter = new ListDetailAdapter(db, this);
        itemList = getAllItems();
        listsAdapter.setItems(itemList);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_lists, R.id.navigation_settings)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Make the center the default selection
        navView.setSelectedItemId(R.id.navigation_lists);
    }

    public List<ListModel> getAllItems() {
        List<ListModel> itemList = db.getAllItems();
        Collections.reverse(itemList);

        return itemList;
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        listsAdapter.setItems(getAllItems());
        listsAdapter.notifyDataSetChanged();
    }

    public ListDetailAdapter getListsAdapter() {
        return listsAdapter;
    }
}