package pt.ua.cm.myshoppinglist.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import pt.ua.cm.myshoppinglist.entities.ListModel;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String ITEM_TABLE = "todo";
    private static final String ID = "id";
    private static final String ITEM = "item";
    private static final String STATUS = "status";
    private static final String CREATE_ITEM_TABLE = "CREATE TABLE " + ITEM_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertItem(ListModel item){
        ContentValues cv = new ContentValues();
        //cv.put(ITEM, item.getItem());
        cv.put(STATUS, 0);
        db.insert(ITEM_TABLE, null, cv);
    }

    public List<ListModel> getAllItems(){
        List<ListModel> itemList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(ITEM_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        ListModel item = new ListModel();
                        //item.setId(cur.getInt(cur.getColumnIndex(ID)));
                        //item.setItem(cur.getString(cur.getColumnIndex(ITEM)));
                        //item.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        itemList.add(item);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return itemList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(ITEM_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateItem(int id, String item) {
        ContentValues cv = new ContentValues();
        cv.put(ITEM, item);
        db.update(ITEM_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteItem(int id){
        db.delete(ITEM_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}
