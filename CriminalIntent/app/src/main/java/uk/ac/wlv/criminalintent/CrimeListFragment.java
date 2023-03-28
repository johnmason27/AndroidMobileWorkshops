package uk.ac.wlv.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class CrimeListFragment extends Fragment {
    private RecyclerView crimeRecyclerView;
    private CrimeAdapter adapter;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        this.crimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        this.crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        this.updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (this.adapter == null) {
            this.adapter = new CrimeAdapter(crimes);
            this.crimeRecyclerView.setAdapter(this.adapter);
        } else {
            this.adapter.setCrimes(crimes);
            this.adapter.notifyDataSetChanged();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleTextView;
        private TextView dateTextView;
        private CheckBox solvedCheckBox;
        private Crime crime;

        public CrimeHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.titleTextView = view.findViewById(R.id.list_item_crime_title_text_view);
            this.dateTextView = view.findViewById(R.id.list_item_crime_date_text_view);
            this.solvedCheckBox = view.findViewById(R.id.list_item_crime_solved_check_box);
            this.solvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                this.crime.setSolved(isChecked);
                CrimeLab.updateCrime(this.crime);
            });
        }

        @Override
        public void onClick(View view) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), this.crime.getId());
            startActivity(intent);
        }

        public void bindCrime(Crime crime) {
            this.crime = crime;
            this.titleTextView.setText(this.crime.getTitle());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
            this.dateTextView.setText(dateFormat.format(this.crime.getDate()));
            this.solvedCheckBox.setChecked(this.crime.isSolved());
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> crimes;

        public void setCrimes(List<Crime> crimes) {
            this.crimes = crimes;
        }

        public CrimeAdapter(List<Crime> crimes) {
            this.crimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);

            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = this.crimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return this.crimes.size();
        }
    }
}
