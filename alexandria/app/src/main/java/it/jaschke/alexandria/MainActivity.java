package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.jaschke.alexandria.api.Callback;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;
    public static final String BOOK_DETAIL_TAG = "book_detail";
    public static final String SCAN_FRAG_TAG = "scan_frag";
    public static final String LIST_FRAG_TAG = "list_frag";
    public static final String ABOUT_US_TAG = "about_us";
    public static final String TWO_PANE = "two_pane";
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;
    private boolean isTablet = false;
    private boolean twoPane = false;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.right_container) != null) {
            twoPane = true;
        }


        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        /**
         * Error Case: We were creating new fragment everytime and adding it to the backstack. This
         * can be less userfriendly as the user has to press back button as many times as he has used
         * the Navigation drawer.
         *
         * Now we are not creating fragments everytime. Instead first we are checking if the fragment
         * is already available in the FragmentManager and reuse it.
         */
        FragmentManager fragmentManager = getSupportFragmentManager();
        String tag = getTag(position);
        Bundle args = new Bundle();
        args.putBoolean(TWO_PANE, twoPane);
        Fragment nextFragment = fragmentManager.findFragmentByTag(tag);

        if (nextFragment == null) {
            nextFragment = getFragment(position);
            nextFragment.setArguments(args);
        }

        fragmentManager.beginTransaction().replace(R.id.container, nextFragment, tag).commit();

        if (twoPane) {
            if (position == 0) {
                findViewById(R.id.right_container).setVisibility(View.VISIBLE);
                findViewById(R.id.divider).setVisibility(View.VISIBLE);

            } else {
                findViewById(R.id.right_container).setVisibility(View.GONE);
                findViewById(R.id.divider).setVisibility(View.GONE);
            }
        }


    }

    private Fragment getFragment(int position) {
        Fragment fragment;
        switch (position) {
            default:
            case 0:
                fragment = new ListOfBooks();
                break;
            case 1:
                fragment = new AddBook();
                break;
            case 2:
                fragment = new About();
                break;

        }
        return fragment;

    }

    private String getTag(int position) {
        String tag = LIST_FRAG_TAG;
        switch (position) {
            case 1:
                tag = SCAN_FRAG_TAG;
                break;

            case 2:
                tag = ABOUT_US_TAG;
                break;
        }
        return tag;
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelected(int position, String ean) {
        if (twoPane) {
            Bundle args = new Bundle();
            args.putString(BookDetail.EAN_KEY, ean);
            args.putInt(BookDetail.POSITION, position);
            args.putBoolean(BookDetail.TWO_PANE, twoPane);

            BookDetail fragment = new BookDetail();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.right_container, fragment).commitAllowingStateLoss();
        } else {
            Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
            intent.putExtra(BookDetail.EAN_KEY, ean);
            intent.putExtra(BookDetail.POSITION, position);
            intent.putExtra(BookDetail.TWO_PANE, twoPane);
            startActivity(intent);
        }

    }

    @Override
    public void onItemDeleted() {
        if(twoPane){
            ListOfBooks fragement = (ListOfBooks) getSupportFragmentManager().findFragmentByTag(LIST_FRAG_TAG);
            if(fragement!=null){
                fragement.restartLoader();
            }
        }
    }

    @Override
    public void updateFragment() {

    }

    @Override
    public void onFragmentAttached(Fragment fragment) {
        if(fragment instanceof  ListOfBooks){
            ((ListOfBooks) fragment).restartLoader();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListOfBooks  fragment = (ListOfBooks) getSupportFragmentManager().findFragmentByTag(LIST_FRAG_TAG);
        if(fragment != null){
            fragment.restartLoader();
        }
    }
}