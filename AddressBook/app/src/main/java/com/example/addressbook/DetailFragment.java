package com.example.addressbook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.example.addressbook.data.DatabaseDescription.Contact;
import com.google.android.material.snackbar.Snackbar;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String deletedContactName;
    private ContentValues deletedContactValues;
    private static final int CONTACT_LOADER = 0;
    private DetailFragmentListener listener;
    private Uri contactUri;
    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView streetTextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView zipTextView;

    public interface DetailFragmentListener {
        void onContactDeleted();
        void onEditContact(Uri contactUri);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();
        if (arguments != null) {
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        nameTextView = view.findViewById(R.id.nameTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        streetTextView = view.findViewById(R.id.streetTextView);
        cityTextView = view.findViewById(R.id.cityTextView);
        stateTextView = view.findViewById(R.id.stateTextView);
        zipTextView = view.findViewById(R.id.zipTextView);

        LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            listener.onEditContact(contactUri);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            deleteContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteContact() {
        deletedContactValues = new ContentValues();
        deletedContactValues.put(Contact.COLUMN_NAME, nameTextView.getText().toString());
        deletedContactValues.put(Contact.COLUMN_PHONE, phoneTextView.getText().toString());
        deletedContactValues.put(Contact.COLUMN_EMAIL, emailTextView.getText().toString());
        deletedContactValues.put(Contact.COLUMN_STREET, streetTextView.getText().toString());
        deletedContactValues.put(Contact.COLUMN_CITY, cityTextView.getText().toString());
        deletedContactValues.put(Contact.COLUMN_STATE, stateTextView.getText().toString());
        deletedContactValues.put(Contact.COLUMN_ZIP, zipTextView.getText().toString());
        deletedContactName = nameTextView.getText().toString();

        getActivity().getContentResolver().delete(contactUri, null, null);

        if (listener != null) {
            listener.onContactDeleted();
        }

        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout),
                        "Contacto eliminado", Snackbar.LENGTH_LONG)
                .setAction("DESHACER", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getActivity().getContentResolver().insert(Contact.CONTENT_URI, deletedContactValues);
                          if (listener != null) {
                            listener.onContactDeleted();
                        }
                    }
                })
                .show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getActivity(), contactUri, null, null, null, null);
    }

    @SuppressLint("Range")
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            nameTextView.setText(data.getString(data.getColumnIndex(Contact.COLUMN_NAME)));
            phoneTextView.setText(data.getString(data.getColumnIndex(Contact.COLUMN_PHONE)));
            emailTextView.setText(data.getString(data.getColumnIndex(Contact.COLUMN_EMAIL)));
            streetTextView.setText(data.getString(data.getColumnIndex(Contact.COLUMN_STREET)));
            cityTextView.setText(data.getString(data.getColumnIndex(Contact.COLUMN_CITY)));
            stateTextView.setText(data.getString(data.getColumnIndex(Contact.COLUMN_STATE)));
            zipTextView.setText(data.getString(data.getColumnIndex(Contact.COLUMN_ZIP)));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { }
}