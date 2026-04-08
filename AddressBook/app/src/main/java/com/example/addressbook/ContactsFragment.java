package com.example.addressbook;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.addressbook.data.DatabaseDescription.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACTS_LOADER = 0;
    private ContactsFragmentListener listener;
    private ContactsAdapter contactsAdapter;

    public interface ContactsFragmentListener {
        void onContactSelected(Uri contactUri);
        void onAddContact();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (ContactsFragmentListener) context;
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

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        contactsAdapter = new ContactsAdapter(new ContactsAdapter.ContactClickListener() {
            @Override
            public void onClick(Uri contactUri) {
                listener.onContactSelected(contactUri);
            }
        });
        recyclerView.setAdapter(contactsAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ItemDivider(getActivity()));

        FloatingActionButton addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddContact();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager.getInstance(this).initLoader(CONTACTS_LOADER, null, this);
    }

    public void updateContactList() {
        contactsAdapter.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(getActivity(),
                Contact.CONTENT_URI,
                null,
                null,
                null,
                Contact.COLUMN_NAME + " COLLATE NOCASE ASC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        contactsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        contactsAdapter.swapCursor(null);
    }
}