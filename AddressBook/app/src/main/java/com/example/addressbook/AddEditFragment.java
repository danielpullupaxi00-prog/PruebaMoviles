package com.example.addressbook;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.example.addressbook.data.DatabaseDescription.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACT_LOADER = 0;
    private AddEditFragmentListener listener;
    private Uri contactUri;
    private boolean addingNewContact = true;
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout streetTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout zipTextInputLayout;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText streetEditText;
    private EditText cityEditText;
    private EditText stateEditText;
    private EditText zipEditText;
    private FloatingActionButton saveContactFAB;
    private View coordinatorLayout;

    private char[] numGua = new char[]{};

    public interface AddEditFragmentListener {
        void onAddEditCompleted(Uri contactUri);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
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

        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);
        nameTextInputLayout = view.findViewById(R.id.nameTextInputLayout);
        phoneTextInputLayout = view.findViewById(R.id.phoneTextInputLayout);
        emailTextInputLayout = view.findViewById(R.id.emailTextInputLayout);
        streetTextInputLayout = view.findViewById(R.id.streetTextInputLayout);
        cityTextInputLayout = view.findViewById(R.id.cityTextInputLayout);
        stateTextInputLayout = view.findViewById(R.id.stateTextInputLayout);
        zipTextInputLayout = view.findViewById(R.id.zipTextInputLayout);

        nameEditText = nameTextInputLayout.getEditText();
        phoneEditText = phoneTextInputLayout.getEditText();
        emailEditText = emailTextInputLayout.getEditText();
        streetEditText = streetTextInputLayout.getEditText();
        cityEditText = cityTextInputLayout.getEditText();
        stateEditText = stateTextInputLayout.getEditText();
        zipEditText = zipTextInputLayout.getEditText();

        saveContactFAB = view.findViewById(R.id.saveFloatingActionButton);

        nameEditText.addTextChangedListener(nameChangedListener);

        saveContactFAB.setOnClickListener(saveContactButtonClicked);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        updateSaveButton();

        Bundle arguments = getArguments();
        if (arguments != null) {
            addingNewContact = false;
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        }

        if (contactUri != null) {
            LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
        }

        return view;
    }

    private final TextWatcher nameChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveButton();
        }
        @Override
        public void afterTextChanged(Editable s) { }
    };

    private void updateSaveButton() {
        if (saveContactFAB == null) return;
        String input = nameEditText.getText().toString();
        if (input.trim().length() != 0) {
            saveContactFAB.show();
        } else {
            saveContactFAB.hide();
        }
    }

    private final View.OnClickListener saveContactButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            saveContact();
        }
    };

    private void saveContact() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String zip = zipEditText.getText().toString().trim();

        if (!isValidName(name)) {
            Snackbar.make(coordinatorLayout, "Nombre es obligatorio", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!isValidEmail(email)) {
            Snackbar.make(coordinatorLayout, "Email no válido", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!isValidPhone(phone)) {
            Snackbar.make(coordinatorLayout, "Teléfono debe tener 10 numeros", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!isValidZip(zip)) {
            Snackbar.make(coordinatorLayout, "ZIP debe tener 5 dígitos", Snackbar.LENGTH_LONG).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(Contact.COLUMN_NAME, name);
        values.put(Contact.COLUMN_PHONE, numGua.toString() );
        values.put(Contact.COLUMN_EMAIL, email);
        values.put(Contact.COLUMN_STREET, streetEditText.getText().toString().trim());
        values.put(Contact.COLUMN_CITY, cityEditText.getText().toString().trim());
        values.put(Contact.COLUMN_STATE, stateEditText.getText().toString().trim());
        values.put(Contact.COLUMN_ZIP, zip);

        if (addingNewContact) {
            Uri newUri = getActivity().getContentResolver().insert(Contact.CONTENT_URI, values);
            if (newUri != null) {
                Snackbar.make(coordinatorLayout, R.string.contact_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newUri);
            } else {
                Snackbar.make(coordinatorLayout, R.string.contact_not_added, Snackbar.LENGTH_LONG).show();
            }
        } else {
            int updatedRows = getActivity().getContentResolver().update(contactUri, values, null, null);
            if (updatedRows > 0) {
                Snackbar.make(coordinatorLayout, R.string.contact_updated, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(contactUri);
            } else {
                Snackbar.make(coordinatorLayout, R.string.contact_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
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
            nameEditText.setText(data.getString(data.getColumnIndex(Contact.COLUMN_NAME)));
            phoneEditText.setText(data.getString(data.getColumnIndex(Contact.COLUMN_PHONE)));
            emailEditText.setText(data.getString(data.getColumnIndex(Contact.COLUMN_EMAIL)));
            streetEditText.setText(data.getString(data.getColumnIndex(Contact.COLUMN_STREET)));
            cityEditText.setText(data.getString(data.getColumnIndex(Contact.COLUMN_CITY)));
            stateEditText.setText(data.getString(data.getColumnIndex(Contact.COLUMN_STATE)));
            zipEditText.setText(data.getString(data.getColumnIndex(Contact.COLUMN_ZIP)));
            updateSaveButton();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { }

    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        char[] aux = phone.toCharArray();
        char [] aux2 = new char[]{};
        int siguiente = 0;
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        } else {
            for(int i= 0; i <= aux.length; i++){
                if (aux[i] != 0 || aux[i] != 1 || aux[i] != 2 || aux[i] != 3 ||
                        aux[i] != 4 || aux[i] != 5 || aux[i] != 6 || aux[i] != 7 ||
                        aux[i] != 8 || aux[i] != 9) {

                } else {
                    aux2[siguiente] = (aux[i]);
                    siguiente++;
                }
            }
        }
        if(aux2.length != 9){
            return false;

        } else {
            numGua = aux2;
            return true;
        }
    }

    private boolean isValidZip(String zip) {
        if (zip == null || zip.trim().isEmpty()) {
            return false;
        }
        return zip.matches("\\d{5}");
    }

}