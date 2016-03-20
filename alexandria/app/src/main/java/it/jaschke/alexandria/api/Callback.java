package it.jaschke.alexandria.api;

import android.support.v4.app.Fragment;

/**
 * Created by saj on 25/01/15.
 */
public interface Callback {
    public void onItemSelected(int position, String ean);

    public void onItemDeleted();

    public void updateFragment();

    public void onFragmentAttached(Fragment fragment);
}
