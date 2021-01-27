package pt.ua.cm.myshoppinglist.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void addItem(String listId, String productName) {
        String uuid = UUID.randomUUID().toString();
        ItemModel item = new ItemModel(uuid, productName, false);

        db.collection(currentUser.getUid())
                .document(listId)
                .collection("items")
                .document(uuid)
                .set(item);
    }

    public void addLocation(String listId, String markerId, LatLng coords) {
        Log.d("MAP", "adding:"+markerId);
        LocationModel location = new LocationModel(markerId, coords);
        db.collection(currentUser.getUid())
                .document(listId)
                .collection("locations")
                .document(markerId)
                .set(location);
    }


    public void deleteLocation(String listId, String markerId) {
        Log.d("MAP", "removing:"+markerId);
        DocumentReference docRef = db.collection(currentUser.getUid())
                .document(listId)
                .collection("locations")
                .document(markerId);
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("MAP", "DocumentSnapshot successfully deleted!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("MAP", "Error deleting document", e);
            }
        });
    }


    public void deleteItem(String listId, String itemId) {
        DocumentReference docRef = db.collection(currentUser.getUid())
                .document(listId)
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

    public void changeItemStatus(String listId, String itemId, boolean status) {
        DocumentReference docRef = db.collection(currentUser.getUid())
                .document(listId)
                .collection("items")
                .document(itemId);

        docRef.update("status", status);
    }

    public void editItem(String listId, String itemId, String newName) {
        DocumentReference docRef = db.collection(currentUser.getUid())
                .document(listId)
                .collection("items")
                .document(itemId);

        docRef.update("productName", newName);
    }

    public void editList(String listId, String newName) {
        DocumentReference docRef = db.collection(currentUser.getUid())
                .document(listId);

        docRef.update("listName", newName);
    }

    public void deleteList(String listId) {
        DocumentReference docRef = db.collection(currentUser.getUid())
                .document(listId);

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

    public void addList(String listId) {
        ListModel list = new ListModel(listId);
        db.collection(currentUser.getUid()).document(list.getUuid()).set(list);
    }
}