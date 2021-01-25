package pt.ua.cm.myshoppinglist.ui.lists;

import java.util.Map;

import pt.ua.cm.myshoppinglist.entities.Item;

public class ListModel {
    private Map<String, Item> list;

    public ListModel() { }

    public ListModel(Map<String, Item> aList) {
        list = aList;
    }

    public Map<String, Item> getList() {
        return list;
    }

    public void setList(Map<String, Item> list) {
        this.list = list;
    }
}
