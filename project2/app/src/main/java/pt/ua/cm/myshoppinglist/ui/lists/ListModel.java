package pt.ua.cm.myshoppinglist.ui.lists;

public class ListModel {
    private String listName;

    public ListModel() { }

    public ListModel(String listName) {
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
