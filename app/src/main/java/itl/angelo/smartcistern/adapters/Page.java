package itl.angelo.smartcistern.adapters;

import android.app.Fragment;
import android.preference.SwitchPreference;
import android.widget.TextView;

/**
 * Created by Angelo on 29/04/2017.
 */

public class Page {
    private Fragment page;
    private String title;

    public Page(Fragment fragment, String title){
        this.page = fragment;
        this.title = title;
    }

    public Fragment getPage() {
        return page;
    }

    public void setPage(Fragment page) {
        this.page = page;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
