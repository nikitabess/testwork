package com.bessmertniyy.nikita.testwork.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bessmertniyy.nikita.testwork.R;
import com.bessmertniyy.nikita.testwork.db.adapters.LinkTableAdapter;
import com.bessmertniyy.nikita.testwork.db.contentprovider.LinkDatabaseContentProvider;
import com.bessmertniyy.nikita.testwork.db.tables.LinkTable;

public class LauncherActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int sortingType = 0;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    String BROADCAST_ACTION = " com.bessmertniyy.nikita.applicationb.UPDATE_DB";

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(getHistoryFragment() != null) {
                getHistoryFragment().loadDataFromDB(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION);
        registerReceiver(receiver, filter);
//        if(!isInitialLoading && getHistoryFragment() != null) {
//            getHistoryFragment().loadDataFromDB(null);
//        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_sort_history){
            changeSortingType();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeSortingType(){

        switch (sortingType){
            case 0 :
                sortingType ++;
                getHistoryFragment().loadDataFromDB(LinkTable.TABLE_SORT_ORDER_BY_STATUS);
                Toast.makeText(this, getString(R.string.launcher_activity_sort_toggled_status), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                sortingType++;
                getHistoryFragment().loadDataFromDB(LinkTable.TABLE_SORT_ORDER_BY_DATE);
                Toast.makeText(this, getString(R.string.launcher_activity_sort_toggled_date_added), Toast.LENGTH_SHORT).show();
                break;
            case 2:
                sortingType = 0;
                getHistoryFragment().loadDataFromDB(null);
                Toast.makeText(this, getString(R.string.launcher_activity_sort_toggled_off), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private PlaceholderFragment getHistoryFragment(){
        return (PlaceholderFragment)mSectionsPagerAdapter.getItem(1);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final int    ARG_FIRST_TAB      = 1;
        private ListView linksListView;
        private boolean isInitialLoading = true;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onResume() {
            super.onResume();
                if(!isInitialLoading) {
                    loadDataFromDB(null);
                }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            if (this.getArguments().getInt(ARG_SECTION_NUMBER, 0) == ARG_FIRST_TAB) {
                rootView = inflater.inflate(R.layout.fragment_launcher_first_tab, container, false);

                final EditText urlInputEditText = (EditText) rootView.findViewById(R.id.url_input_edit_text);
                Button launchAppB = (Button) rootView.findViewById(R.id.url_submit_button);

                launchAppB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!TextUtils.isEmpty(urlInputEditText.getText().toString())) {

                            openLinkImageViewer(urlInputEditText.getText().toString());

                        } else {

                            Toast.makeText(getContext(), getString(R.string.launcher_fragment_first_tab_no_url_entered), Toast.LENGTH_LONG).show();

                        }

                    }
                });


            } else {

                rootView = inflater.inflate(R.layout.fragment_launcher_second_tab, container, false);

              linksListView = (ListView) rootView.findViewById(R.id.links_list_view);

              loadDataFromDB(null);
              isInitialLoading = false;

            }

            return rootView;
        }

        public void loadDataFromDB(String orderBy){
            String[] projection = {LinkTable.TABLE_LINK_COLUMN_ID, LinkTable.TABLE_LINK_COLUMN_URL, LinkTable.TABLE_LINK_COLUMN_ADD_DATE, LinkTable.TABLE_LINK_COLUMN_STATUS};

            final Cursor cursor = getContext().getContentResolver().query(LinkDatabaseContentProvider.URI_CONTENT, projection, null , null, orderBy);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    LinkTableAdapter linkTableAdapter = new LinkTableAdapter(getContext(), cursor, 0);

                    linksListView.setAdapter(linkTableAdapter);

                }
            });
        }

        private void openLinkImageViewer(String URL){
            Intent openAppB = new Intent("com.bessmertniyy.nikita.applicationb.LINK_VIEW_ACTIVITY");
            openAppB.putExtra("linkURL", URL);
            openAppB.putExtra("isForSave", true);
            startActivity(openAppB);
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        PlaceholderFragment firstTab = PlaceholderFragment.newInstance(1);
        PlaceholderFragment secondTab = PlaceholderFragment.newInstance(2);

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return firstTab;
                case 1:
                    return secondTab;
                default: PlaceholderFragment.newInstance(position+1);

            }

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.launcher_activity_first_section);
                case 1:
                    return getString(R.string.launcher_activity_second_section);
            }
            return null;
        }
    }
}
