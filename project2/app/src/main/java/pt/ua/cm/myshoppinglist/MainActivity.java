package pt.ua.cm.myshoppinglist;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pt.ua.cm.myshoppinglist.ui.lists.ListDetailAdapter;
import pt.ua.cm.myshoppinglist.ui.lists.ListModel;
import pt.ua.cm.myshoppinglist.ui.lists.ListPreviewAdapter;
import pt.ua.cm.myshoppinglist.ui.lists.ListsAdapter;
import pt.ua.cm.myshoppinglist.utils.DatabaseHandler;

public class MainActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private ListsAdapter listAdapter;

    private List<ListModel> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = new DatabaseHandler(this);
        db.openDatabase();

        listAdapter = new ListsAdapter(db, MainActivity.this);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_lists, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}