package pt.ua.cm.myshoppinglist.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class FirebaseDbHandler {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private static final String TAG = "Anonymous";

    public FirebaseDbHandler(FirebaseUser currentUser) {
        this.db = FirebaseFirestore.getInstance();
        this.currentUser = currentUser;
    }

    public void addItem(String listName, String productName) {
        ItemModel item = new ItemModel(productName, false);
        String uniqueID = UUID.randomUUID().toString();

        db.collection(currentUser.getUid())
                .document(listName)
                .collection("items")
                .document(uniqueID)
                .set(item);
    }

    public void deleteItem(String listName, String itemId) {
        DocumentReference docRef = db.collection(currentUser.getUid()).document(listName);

        // Remove the 'item' field from the document
        Map<String,Object> updates = new HashMap<>();
        updates.put(itemId, FieldValue.delete());

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            // [START_EXCLUDE]
            @Override
            public void onComplete(@NonNull Task<Void> task) {}
            // [START_EXCLUDE]
        });
        // [END update_delete_field]
    }

    public void changeItemStatus(String listName, String itemId, boolean status) {
        DocumentReference docRef = db.collection(currentUser.getUid()).document(listName);
        docRef.update(itemId, status);
    }

    public DocumentReference getDocRef(String collection, String document) {
        return db.collection(collection).document(document);
    }

    public Map<String, Object> getItems(DocumentReference docRef) {
        Map<String, Object> data;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return null;
    }
}