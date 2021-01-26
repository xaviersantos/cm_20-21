package pt.ua.cm.myshoppinglist.entities;

import java.util.UUID;

public class ListModel {
    private String listName, uuid;
    private ItemModel items;

    public ListModel() { }

    public ListModel(String listName) {
        this.uuid = UUID.randomUUID().toString();
        this.listName = listName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
