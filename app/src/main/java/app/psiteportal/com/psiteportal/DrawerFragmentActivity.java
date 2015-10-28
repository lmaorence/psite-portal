package app.psiteportal.com.psiteportal;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Lawrence on 9/19/2015.
 */

import java.util.ArrayList;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import app.psiteportal.com.model.NavDrawerItem;
import app.psiteportal.com.utils.NavDrawerListAdapter;

public class DrawerFragmentActivity extends AppCompatActivity{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    String user_pid, user_fname, user_lname, user_gender, user_contact, user_email,
            user_address, user_institution, user_points, user_qr, user_usertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_fragment_layout);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            user_pid = extras.getString("pid");
            user_fname = extras.getString("firstname");
            user_lname = extras.getString("lastname");
            user_gender = extras.getString("gender");
            user_contact = extras.getString("contact");
            user_email = extras.getString("email");
            user_address = extras.getString("address");
            user_institution = extras.getString("institution");
            user_points = extras.getString("points");
            user_qr = extras.getString("qr_id");
            user_usertype = extras.getString("usertype");
        }

        mTitle = mDrawerTitle = getTitle();

        if(user_usertype.equals("officer")) {
            // load slide menu items
            navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
            // nav drawer icons from resources
            navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        }else{
            // load slide menu items
            navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_member);
            // nav drawer icons from resources
            navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons_member);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array

        if(user_usertype.equals("officer")){
            // Your Account
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
            // Officer Panel
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
            // Liquidation
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
            // Seminars
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
            //Take Picture
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
            // Election
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        }else{
            // Your Account
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
            // Seminars
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
            //Take Picture
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
            // Election
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

        }

            Log.e("USER TYPE!!!!", user_usertype);

        // Recycle the typed arrays
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);


        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
            displayViewMember(0);
        }

    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            if(user_usertype.equals("officer")) {
                displayView(position);
            }else{
                displayViewMember(position);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void displayViewMember(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = userDetails();
                break;
            case 2:
                fragment = seminarsFragment();
                break;
            case 3:
                fragment = new CaptureImageFragment();
                break;
            case 4:
                fragment = electionFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("DrawerMainAdapter", "Error in creating fragment");
        }
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = userDetails();
                break;
            case 2:
                fragment = new OfficerPanelFragment();
                break;
            case 3:
                fragment = new LiquidationFragment();
                break;
            case 4:
                fragment = seminarsFragment();
                break;
            case 5:
                fragment = new CaptureImageFragment();
                break;
            case 6:
                fragment = electionFragment();
                break;
            default:
                break;
        }


        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("DrawerMainAdapter", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private Fragment userDetails(){
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("user_fname", user_fname);
        bundle.putString("user_lname", user_lname);
        bundle.putString("user_institution", user_institution);
        bundle.putString("user_address", user_address);
        bundle.putString("user_email", user_email);
        bundle.putString("user_points", user_points);
        bundle.putString("user_qr", user_qr);
        bundle.putString("user_usertype", user_usertype);

        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(bundle);

        return userProfileFragment;
    }


    Fragment seminarsFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);

        SeminarsFragment seminarsFragment = new SeminarsFragment();
        seminarsFragment.setArguments(bundle);

        return seminarsFragment;
    }


    Fragment electionFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("user_pid", user_pid);
        bundle.putString("usertype", user_usertype);

        ElectionFragment electionFragment = new ElectionFragment();
        electionFragment.setArguments(bundle);

        return electionFragment;
    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DrawerFragmentActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


}

