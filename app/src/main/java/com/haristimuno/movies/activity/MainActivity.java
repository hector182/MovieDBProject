package com.haristimuno.movies.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.haristimuno.movies.cache.CacheStorageImp;
import com.haristimuno.movies.fragment.ListFragment;
import com.haristimuno.movies.R;
import com.haristimuno.movies.fragment.ContentFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String API_KEY = "fb4ec6fb7d3a59a0f5c2ffcfa2748832";
    public static int POSITION = 0; // category array position
    public static int OLD_POSITION = -1;
    public static final String CATEG_POPULAR="Popular";
    public static final String CATEG_UPCOMING="Upcoming";
    public static final String CATEG_TOP_RATE="Top Rate";
    public String category;

    FragmentManager fragmentManager = getSupportFragmentManager();
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // add activity to fragments stack
        fragmentManager.addOnBackStackChangedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        category = CATEG_POPULAR; // default category
        setTitle(category);
        if (savedInstanceState == null) {
            selectItem(POSITION);
        }
    }

    private void selectItem(int position) {
        POSITION = position;

        setTitle(category);

        if(OLD_POSITION == POSITION)
            return;

        OLD_POSITION = POSITION;

        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ListFragment.ARG_POSITION, POSITION);
        args.putString(ListFragment.ARG_CATEGORY,category);
        fragment.setArguments(args);

        ListFragment f = (ListFragment) fragmentManager.findFragmentByTag(ListFragment.LISTFRAGMENT_TAG);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // if ListFragment exists, pop all stack fragment
        if (f != null) {
            FragmentManager fm = this.getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }

            // replace view with a ListFragment
            transaction.replace(R.id.test_fragment, fragment,ListFragment.LISTFRAGMENT_TAG);
        } else { // add new ListFragment
            transaction.add(R.id.test_fragment, fragment,ListFragment.LISTFRAGMENT_TAG);
        }

        transaction.addToBackStack(ListFragment.LISTFRAGMENT_TAG);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        ContentFragment f = (ContentFragment) fragmentManager.findFragmentByTag(ContentFragment.CONTENTFRAGMENT_TAG);
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(f != null) { // if in the stack is found a PostFragment, pop it
            transaction.remove(f); // eliminar el fragment
            fragmentManager.popBackStack(ContentFragment.CONTENTFRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            transaction.commit();
        } else { // MainActivity, then exit
            this.finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_popular) {
            category = CATEG_POPULAR;
            selectItem(0);
        } else if (id == R.id.nav_top_rate) {
            category = CATEG_TOP_RATE;
            selectItem(1);
        } else if (id == R.id.nav_upcoming) {
            category = CATEG_UPCOMING;
            selectItem(2);
        } else if (id == R.id.nav_clear_cache) {
            deleteCache();
            //selectItem(3);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackStackChanged() {

    }

    /**
     * delete local movies categories
     */
    public void deleteCache() {
        CacheStorageImp cache = new CacheStorageImp(getApplicationContext());
        cache.clear(CATEG_POPULAR);
        cache.clear(CATEG_TOP_RATE);
        cache.clear(CATEG_UPCOMING);
        View view = this.getWindow().getDecorView().findViewById(android.R.id.content);

        Snackbar.make(view, "Cache has been deleted", Snackbar.LENGTH_LONG)
                .show();

    }
}