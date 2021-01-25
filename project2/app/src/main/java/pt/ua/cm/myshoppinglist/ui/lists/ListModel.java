package pt.ua.cm.myshoppinglist.ui.lists;

import java.util.Map;

import pt.ua.cm.myshoppinglist.entities.ItemModel;

public class ListModel {
    private Map<String, ItemModel> list;

    public ListModel() { }

    public ListModel(Map<String, ItemModel> aList) {
        list = aList;
    }

    public Map<String, ItemModel> getList() {
        return list;
    }

    public void setList(Map<String, ItemModel> list) {
        this.list = list;
    }
}
