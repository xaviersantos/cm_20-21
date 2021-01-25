package pt.ua.cm.myshoppinglist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Objects;

import pt.ua.cm.myshoppinglist.entities.ItemModel;
import pt.ua.cm.myshoppinglist.ui.lists.ListPreviewAdapter;
import pt.ua.cm.myshoppinglist.utils.DialogCloseListener;
import pt.ua.cm.myshoppinglist.utils.FirebaseDbHandler;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String TAG = "MainActivity";

    private FirebaseDbHandler db;
    private ListPreviewAdapter listsAdapter;

    private List<ItemModel> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        loginAnonymously();
    }

//    public List<ListModel> getAllItems() {
//        List<ListModel> itemList = db.getAllItems();
//        Collections.reverse(itemList);
//
//        return itemList;
//    }

    @Override
    public void onStart() {
        super.onStart();
        if (listsAdapter != null)
            listsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listsAdapter != null)
            listsAdapter.stopListening();
    }

    private void loginAnonymously() {
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        this.currentUser = currentUser;

        if(currentUser != null)
            db = new FirebaseDbHandler(currentUser);

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

    @Override
    public void handleDialogClose(DialogInterface dialog){
        listsAdapter.notifyDataSetChanged();
    }

    public ListPreviewAdapter getListsAdapter(String listName) {

        Query query = FirebaseFirestore.getInstance()
                .collection(currentUser.getUid())
                .document(listName)
                .collection("items");

        FirestoreRecyclerOptions<ItemModel> options = new FirestoreRecyclerOptions.Builder<ItemModel>()
                .setQuery(query, ItemModel.class)
                .build();

        // Connecting object of required Adapter class to
        // the Adapter class itself
        listsAdapter = new ListPreviewAdapter(options, db, "listName", this);
        listsAdapter.startListening();

        return listsAdapter;
    }


    public FirebaseDbHandler getDb() {
        return db;
    }

    public String getCurrentUserName() {
        return currentUser.getDisplayName();
    }
}