package itl.angelo.smartcistern.activities;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.adapters.Page;
import itl.angelo.smartcistern.adapters.SectionsPagerAdapter;
import itl.angelo.smartcistern.fragments.MonitorFragment;
import itl.angelo.smartcistern.fragments.ConnectFragment;
import itl.angelo.smartcistern.fragments.DataFragment;
import itl.angelo.smartcistern.fragments.ManualModeFragment;
import itl.angelo.smartcistern.util.connection.GlobalData;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    public SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public ViewPager mViewPager;
    public SharedPreferences prefs;
    public String[] mTitles;
    public Page[] mPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), this);
        mTitles = getResources().getStringArray(R.array.sections_titles);
        mPages = getPages();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Set up the ViewPager with the sections adapter.
        //addInitialPages();
        mSectionsPagerAdapter.addPage(mPages[0]);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //checkManualMode();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * @return the pages of pager adapter
     */
    public Page[] getPages() {
        Page[] page = new Page[mTitles.length];
        Fragment[] fragments = getFragments();
        for (int i = 0; i < page.length; i++)
            page[i] = new Page(fragments[i], mTitles[i]);
        return page;
    }

    /**
     * @return the fragments of pager adapter
     */
    public Fragment[] getFragments() {
        return new Fragment[]{
                new ConnectFragment(),
                new MonitorFragment(),
                new DataFragment(),
                new ManualModeFragment(),
        };
    }

    /**
     * initialize the ui
     */
    public void addInitialPages() {
        for (int i = 1; i < mPages.length - 1; i++)
            if (!mSectionsPagerAdapter.containsPage(mPages[i]))
                mSectionsPagerAdapter.addPage(mPages[i]);
    }

    /**
     * initialize the ui
     */
    public void removeInitialPages() {
        for (int i = 1; i < mPages.length - 1; i++)
            if (mSectionsPagerAdapter.containsPage(mPages[i]))
                mSectionsPagerAdapter.removePage(mPages[i]);
    }

    /**
     * add manual mode if is activated or remove it
     */
    public void checkManualMode() {
        boolean manualMode = prefs.getBoolean(getString(R.string.manual_mode_key), false);
        if (manualMode)
            if (GlobalData.getInstance().isConnected()) addManualMode();
            else removeManualMode();
        else if (!manualMode) removeManualMode();
    }

    public void addManualMode() {
        Page manualPage = mPages[mPages.length - 1];
        if (!mSectionsPagerAdapter.containsPage(manualPage))
            mSectionsPagerAdapter.addPage(manualPage);
    }

    public void removeManualMode() {
        Page manualPage = mPages[mPages.length - 1];
        if (mSectionsPagerAdapter.containsPage(manualPage))
            mSectionsPagerAdapter.removePage(manualPage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkManualMode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
