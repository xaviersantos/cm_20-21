package pt.ua.cm.myshoppinglist.entities;

public class Item {
    private String itemName;
    private boolean status;

    public Item () {}

    public Item(String itemName, boolean status) {
        this.itemName = itemName;
        this.status = status;
    }

    public String getName() {
        return itemName;
    }

    public void setName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
