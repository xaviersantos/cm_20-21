package pt.ua.cm.myshoppinglist.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import pt.ua.cm.myshoppinglist.entities.ItemModel;
import pt.ua.cm.myshoppinglist.entities.ListModel;
import pt.ua.cm.myshoppinglist.entities.LocationModel;

public class FirebaseDbHandler {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private static final String TAG = "Anonymous";

    public FirebaseDbHandler(FirebaseUser currentUser) {
        this.db = FirebaseFirestore.getInstance();
        this.currentUser = currentUser;
    }

    public void addItem(String listName, String productName) {
        String uuid = UUID.randomUUID().toString();
        ItemModel item = new ItemModel(uuid, productName, false);

        db.collection(currentUser.getUid())
                .document(listName)
                .collection("items")
                .document(uuid)
                .set(item);
    }

    public void addLocation(String listName, LatLng coords) {
        String uuid = UUID.randomUUID().toString();
        LocationModel location = new LocationModel(uuid, coords);

        db.collection(currentUser.getUid())
                .document(listName)
                .collection("locations")
                .document(uuid)
                .set(location);
    }

    public void deleteItem(String listName, String itemId) {
        DocumentReference docRef = db.collection(currentUser.getUid())
                .document(listName)
                .collection("items")
                .document(itemId);

        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void changeItemStatus(String listName, String itemId, boolean status) {
        DocumentReference docRef = db.collection(currentUser.getUid())
                .document(listName)
                .collection("items")
                .document(itemId);

        docRef.update("status", status);
    }

    public void editItem(String listName, String itemId, String newName) {
        DocumentReference docRef = db.collection(currentUser.getUid())
                .document(listName)
                .collection("items")
                .document(itemId);

        docRef.update("productName", newName);
    }

    public void editList(String listName, String text) {

    }

    public void deleteList(String listName) {

    }

    public void addList(String listName) {
        ListModel list = new ListModel(listName);
        db.collection(currentUser.getUid()).document(listName).set(list);
    }
}