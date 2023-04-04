package uk.ac.wlv.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHONE = 2;
    private static final int PERMISSIONS_REQUEST_READ_CONTACT = 3;
    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private Button deleteButton;
    private Button reportButton;
    private Button suspectButton;
    private Button reportPhoneButton;
    private CheckBox solvedCheckBox;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        this.crime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(this.crime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        this.titleField = v.findViewById(R.id.crime_title);
        this.titleField.setText(this.crime.getTitle());
        this.titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        this.dateButton = v.findViewById(R.id.crime_date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        this.dateButton.setText(dateFormat.format(this.crime.getDate()));
        this.dateButton.setOnClickListener(view -> {
            FragmentManager manager = getFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            dialog.show(manager, DIALOG_DATE);
        });

        this.deleteButton = v.findViewById(R.id.crime_delete_button);
        this.deleteButton.setOnClickListener(view -> {
            CrimeLab.deleteCrime(this.crime);
        });

        this.solvedCheckBox = v.findViewById(R.id.crime_solved);
        this.solvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> this.crime.setSolved(isChecked));
        this.solvedCheckBox.setChecked(this.crime.isSolved());

        this.reportButton = v.findViewById(R.id.crime_report);
        this.reportButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, this.getCrimeReport());
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
            intent = Intent.createChooser(intent, getString(R.string.send_report));
            startActivity(intent);
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        this.suspectButton = v.findViewById(R.id.crime_suspect);
        this.suspectButton.setOnClickListener(view -> startActivityForResult(pickContact, REQUEST_CONTACT));

        if (this.crime.getSuspect() != null) {
            this.suspectButton.setText(this.crime.getSuspect());
        }

        this.reportPhoneButton = v.findViewById(R.id.crime_report_phone);
        this.reportPhoneButton.setOnClickListener(view -> startActivityForResult(pickContact, REQUEST_PHONE));

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            this.crime.setDate(date);
            this.dateButton.setText(this.crime.getDate().toString());
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor cursor = getActivity()
                    .getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                if (cursor.getCount() == 0) {
                    return;
                }

                cursor.moveToFirst();
                String suspect = cursor.getString(0);

                this.crime.setSuspect(suspect);
                this.suspectButton.setText(suspect);
            } finally {
                cursor.close();
            }
        } else if (requestCode == REQUEST_PHONE && data != null) {
            if (getActivity()
                    .getApplicationContext()
                    .checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[] { Manifest.permission.READ_CONTACTS },
                        PERMISSIONS_REQUEST_READ_CONTACT
                );
            }

            Uri contactData = data.getData();
            ContentResolver contentResolver = getActivity().getContentResolver();
            Cursor cursor = contentResolver.query(contactData, null, null, null, null);
            try {
                if (cursor.getCount() <= 0 || !cursor.moveToNext()) {
                    return;
                }

                int idCol = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                if (idCol <= 0) {
                    return;
                }
                String id = cursor.getString(idCol);

                int hasPhoneNumberCol = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                if (hasPhoneNumberCol <= 0) {
                    return;
                }

                Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                while (phones.moveToNext()) {
                    int phoneCol = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    if (phoneCol <= 0) {
                        return;
                    }

                    String phoneNumber = phones.getString(phoneCol);

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri call = Uri.parse("tel:" + phoneNumber);
                    intent.setData(call);
                    startActivity(intent);
                }

                phones.close();
            } finally {
                cursor.close();
            }
        }
    }

    private String getCrimeReport() {
        String solvedString;
        if (this.crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");
        String dateString = dateFormat.format(this.crime.getDate());

        String suspect = this.crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(
                R.string.crime_report,
                this.crime.getTitle(),
                dateString,
                solvedString,
                suspect
        );

        return report;
    }
}
