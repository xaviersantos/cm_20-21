package pt.ua.cm.myshoppinglist.entities;

import com.google.firebase.firestore.PropertyName;

public class ItemModel {
    private String productName;
    private boolean status;

    public ItemModel() {}

    public ItemModel(String productName, boolean status) {
        this.productName = productName;
        this.status = status;
    }

    @PropertyName("productName")
    public String getProductName() {
        return productName;
    }

    @PropertyName("productName")
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @PropertyName("status")
    public boolean getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(boolean status) {
        this.status = status;
    }
}
