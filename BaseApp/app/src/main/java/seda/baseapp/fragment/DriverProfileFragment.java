/**
 *
 * DriverProfileFragment hosts many profile page in sliding window.
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */

package seda.baseapp.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import seda.baseapp.MainActivity;
import seda.baseapp.R;

/**
 * Created by liubingfeng on 30/09/2017.
 */

public class DriverProfileFragment extends Fragment
{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
//        return inflater.inflate(layoutId, container, false);
        return inflater.inflate(R.layout.driver_profile, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);


//        this action bar only in compact activity

//       toolbar is the one one top menue

        toolbar.setTitle("Driver Profiles");

        ((MainActivity)getActivity()).setSupportActionBar(toolbar);

        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);

//        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    //    Tabs


    /**
     * This is the method used to added initial pages.
     * @return void
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());


        String[] tabNames = {"Bing", "Regina", "Alan", "Victor", "Sam", "Tim", "Kim", "Lee", "James", "Vim"};

        ProfileFragment profileFragment = null;

        for(String oneTabName:tabNames)
        {
            profileFragment = new ProfileFragment();

            adapter.addFrag(profileFragment, oneTabName);
        }

        viewPager.setAdapter(adapter);
    }

    /**
     * ViewPagerAdapter is used in DriverProfileFragment as sliding elements adpater
     */
    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
