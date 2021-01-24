package pt.ua.cm.myshoppinglist.ui.lists;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.type.Date;

public class ListModel {
    private int id, status;
    private String item;
    private Date mTimestamp;

    public ListModel() { }

    public ListModel(int uid, String itemName, int mStatus) {
        id = uid;
        item = itemName;
        status = mStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @ServerTimestamp
    public Date getTimestamp() { return mTimestamp; }

    public void setTimestamp(Date timestamp) { mTimestamp = timestamp; }
}
