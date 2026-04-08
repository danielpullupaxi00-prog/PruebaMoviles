package com.example.addressbook.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.addressbook.data.DatabaseDescription.Contact;

public class AddressBookContentProvider extends ContentProvider {

    private AddressBookDatabaseHelper dbHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int ONE_CONTACT = 1;
    private static final int CONTACTS = 2;

    static {
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, Contact.TABLE_NAME + "/#", ONE_CONTACT);
        uriMatcher.addURI(DatabaseDescription.AUTHORITY, Contact.TABLE_NAME, CONTACTS);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new AddressBookDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contact.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                queryBuilder.appendWhere(Contact._ID + "=" + uri.getLastPathSegment());
                break;
            case CONTACTS:
                break;
            default:
                throw new UnsupportedOperationException("Invalid query Uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) != CONTACTS) {
            throw new UnsupportedOperationException("Invalid insert Uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(Contact.TABLE_NAME, null, values);

        if (rowId > 0) {
            Uri newUri = Contact.buildContactUri(rowId);
            getContext().getContentResolver().notifyChange(uri, null);
            return newUri;
        }
        throw new android.database.SQLException("Insert failed: " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) != ONE_CONTACT) {
            throw new UnsupportedOperationException("Invalid update Uri: " + uri);
        }

        String id = uri.getLastPathSegment();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updatedRows = db.update(Contact.TABLE_NAME, values, Contact._ID + "=" + id, selectionArgs);

        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRows;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) != ONE_CONTACT) {
            throw new UnsupportedOperationException("Invalid delete Uri: " + uri);
        }

        String id = uri.getLastPathSegment();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete(Contact.TABLE_NAME, Contact._ID + "=" + id, selectionArgs);

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}