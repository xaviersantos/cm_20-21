package pt.ua.cm.myshoppinglist.entities;

import com.google.firebase.firestore.PropertyName;

public class ItemModel {
    private String productName, uuid;
    private boolean status;

    public ItemModel() {}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ItemModel(String uuid, String productName, boolean status) {
        this.uuid = uuid;
        this.productName = productName;
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
