package uk.ac.wlv.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
    private static final String EXTRA_CRIME_ID = "uk.ac.wlv.criminalintent.crime_id";
    private ViewPager viewPager;
    private List<Crime> crimes;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        this.viewPager = findViewById(R.id.activity_crime_pager_view_pager);
        this.crimes = CrimeLab.get(this).getCrimes();

        FragmentManager manager = getSupportFragmentManager();
        this.viewPager.setAdapter(new FragmentStatePagerAdapter(manager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Crime crime = crimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return crimes.size();
            }
        });

        int crimeIndex = this.findCrimeIndex(crimeId);
        this.viewPager.setCurrentItem(crimeIndex);
    }

    private int findCrimeIndex(UUID crimeId) {
        int index = 0;
        for (Crime crime: this.crimes) {
            if (crime.getId().equals(crimeId)) {
                break;
            }
            index++;
        }
        return index;
    }
}
