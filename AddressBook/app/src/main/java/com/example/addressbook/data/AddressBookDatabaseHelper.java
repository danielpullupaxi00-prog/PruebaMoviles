package com.example.addressbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.addressbook.data.DatabaseDescription.Contact;

public class AddressBookDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AddressBook.db";
    private static final int DATABASE_VERSION = 1;

    public AddressBookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createContactsTable = "CREATE TABLE " + Contact.TABLE_NAME + "(" +
                Contact._ID + " INTEGER PRIMARY KEY, " +
                Contact.COLUMN_NAME + " TEXT, " +
                Contact.COLUMN_PHONE + " TEXT, " +
                Contact.COLUMN_EMAIL + " TEXT, " +
                Contact.COLUMN_STREET + " TEXT, " +
                Contact.COLUMN_CITY + " TEXT, " +
                Contact.COLUMN_STATE + " TEXT, " +
                Contact.COLUMN_ZIP + " TEXT)";
        db.execSQL(createContactsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No upgrade needed
    }
}