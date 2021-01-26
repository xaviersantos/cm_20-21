package pt.ua.cm.myshoppinglist.entities;

import java.util.Map;

public class ListModel {
    private String listName;

    public ListModel() { }

    public ListModel(String listName) {
        this.listName = listName;
    }

    public Map<String, ItemModel> getItems() {
        return items;
    }

    public void setItems(Map<String, ItemModel> items) {
        this.items = items;
    }

    private Map<String, ItemModel> items;

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
