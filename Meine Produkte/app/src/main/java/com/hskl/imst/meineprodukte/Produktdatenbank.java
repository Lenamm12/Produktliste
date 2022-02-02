package com.hskl.imst.meineprodukte;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Produktdatenbank extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "produktdb";
    private static final String DATABASE_TABLE = "notetables";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_VERFALLSDATUM = "verfallsdatum";
    private static final String KEY_IMAGE = "image";

    // Superkonstruktor: Context wird übergeben
    // Übergeben der oben initialisierten Argumente: (Context), Database_Name, Database_version
    Produktdatenbank(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Datenbank anlegen
        String query = "CREATE TABLE "+ DATABASE_TABLE + "("+ KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                KEY_TITLE + " TEXT,"+
                KEY_CONTENT + " TEXT,"+
                KEY_VERFALLSDATUM + " TEXT,"+
                KEY_IMAGE + " BLOB"+")";
        db.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Wenn neue Version verfügbar ist, alte Tabelle löschen mithilfe DROP TABLE
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }


    public long addNote(Produkt produkt) {
        //Hinzufügen der Datensätze zu ContentValues
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(KEY_TITLE, produkt.getTitle());
        c.put(KEY_CONTENT, produkt.getContent());
        c.put(KEY_VERFALLSDATUM, produkt.getVerfallsdatum());
        c.put(KEY_IMAGE, produkt.getImage());

        //Einfügen von ContentValues in Tabelle, Speicherung in ID
        long ID = db.insert(DATABASE_TABLE, null, c);
        Log.d("Inserted", "ID"+ ID);
        return ID;

    }


    public Produkt getNote(long id) {
        // select * from databaseTable where id=1
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE,new String[]{KEY_ID,KEY_TITLE,KEY_CONTENT,KEY_VERFALLSDATUM,KEY_IMAGE},KEY_ID+"=?",
                new String[]{String.valueOf(id)},null,null,null);
        if (cursor != null) cursor.moveToFirst();
        return new Produkt(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getBlob(4));
    }

    public List<Produkt> getProdukte(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Produkt> allProdukte = new ArrayList<>();
        // select * from databaseName

        String query = "SELECT * FROM "+DATABASE_TABLE+" ORDER BY "+KEY_ID+" DESC";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
                do {
                    Produkt produkt = new Produkt();
                    produkt.setID(cursor.getLong(0));
                    produkt.setTitle(cursor.getString(1));
                    produkt.setContent(cursor.getString(2));
                    produkt.setVerfallsdatum(cursor.getString(3));
                    produkt.setImage(cursor.getBlob(4));
                    allProdukte.add(produkt);

                } while (cursor.moveToNext());
            cursor.close();
            }
            return allProdukte;
    }

    void deleteNote(long id){
        //Datensatz (Produkt) löschen
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE,KEY_ID+"=?",new String[]{String.valueOf(id)});
        db.close();
    }

    public int editNote(Produkt produkt) {
        //Datensatz aktualisieren/updaten (=Produkt bearbeiten)
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(KEY_TITLE, produkt.getTitle());
        c.put(KEY_CONTENT, produkt.getContent());
        c.put(KEY_VERFALLSDATUM, produkt.getVerfallsdatum());
        c.put(KEY_IMAGE, produkt.getImage());
        return db.update(DATABASE_TABLE,c,KEY_ID+"=?",new String[]{String.valueOf(produkt.getID())});
    }
}