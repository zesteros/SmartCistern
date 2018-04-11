package itl.angelo.smartcistern.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

import itl.angelo.smartcistern.fragments.ConnectFragment;
import itl.angelo.smartcistern.fragments.DataFragment;
import itl.angelo.smartcistern.fragments.ManualModeFragment;
import itl.angelo.smartcistern.fragments.MonitorFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private FragmentManager mFragmentManager;
    private ArrayList mPages;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        this.mFragmentManager = fm;
        mPages = new ArrayList<Page>();
    }

    @Override
    public Fragment getItem(int position) {
        /*switch (position) {
            case 0:
                return new ConnectFragment();
            case 1:
                return new MonitorFragment();
            case 2:
                return new DataFragment();
            case 3:
                return new ManualModeFragment();
        }
        return new ConnectFragment();*/
        return ((Page) mPages.get(position)).getPage();
    }

    public void replacePage(int position, Page page) {
        mPages.remove(position);
        mPages.add(position, page);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return mPages.size();
    }

    public void addPage(Page page) {
        if (!mPages.contains(page)) mPages.add(page);
        notifyDataSetChanged();
    }

    public void removePage(Page page) {
        mPages.remove(page);
        notifyDataSetChanged();
    }

    public void removePage(int position) {
        mPages.remove(position);
        notifyDataSetChanged();
    }


    public Page getPage(int position) {
        return (Page) mPages.get(position);
    }

    public boolean containsPage(Page page) {
        return mPages.contains(page);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /*switch (position){
            case 0 : return "Conexión";
            case 1 : return "Monitoreo";
            case 2 : return "Datos";
            case 3 : return "Modo Manual";
        }
        return "Conexión";*/
        return ((Page) mPages.get(position)).getTitle();
    }
}