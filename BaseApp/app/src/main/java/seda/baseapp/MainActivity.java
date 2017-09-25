package seda.baseapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import seda.baseapp.adapter.NavigationItemAdapter;
import seda.baseapp.fragment.GeneralContentFragment;
import seda.baseapp.fragment.ToDoFragment;
import seda.baseapp.todo.ToDoActivity;

public class MainActivity extends AppCompatActivity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Log.i("SEDA Base App", "Initialized...");
//
//        Button sedaBtn = findViewById(R.id.sedaBtn);
//        sedaBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
//                Log.i("SEDA Base App", "Go to ToDo");
//                startActivity(intent);
//            }
//        });
//    }

    private String[] navigationItemsNames = null;
    private DrawerLayout drawerLayout = null;
    private ListView drawerList = null;
    private String curFragmentName = "";


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationItemsNames = getResources().getStringArray(R.array.navigation_bar_item_names);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //could change it to other list view use it here first
        drawerList = (ListView) findViewById(R.id.left_drawer);
        //
        NavigationItemAdapter navigationItemAdapter = new NavigationItemAdapter(this,R.layout.navigation_list_item);


        // Set the adapter for the list view
        drawerList.setAdapter(navigationItemAdapter);

        for (String oneNaviItem:navigationItemsNames)
        {
            navigationItemAdapter.add(oneNaviItem);
        }


        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GeneralContentFragment generalContentFragment = new GeneralContentFragment();
        generalContentFragment.setLayoutId(R.layout.about_us);

        curFragmentName = getString(R.string.about_us);

        fragmentTransaction.add(R.id.content_frame, generalContentFragment, getString(R.string.about_us));
        fragmentTransaction.commit();



    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //position is a index value, which refers to the element in mPlanetTitles
            selectItem(position, (String)view.getTag());
            Log.d("bingappservice", "navigation item clicked, position: " + position + ", tag: " +view.getTag());
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position, String itemTagName) {

        FragmentManager fragmentManager = getFragmentManager();

        //fragment replace will destroy the fragment
        //use hide instead
        //change fragment in activity_main to the right layout
        //https://stackoverflow.com/questions/22713128/how-can-i-switch-between-two-fragments-without-recreating-the-fragments-each-ti
        if(itemTagName.equals(getString(R.string.to_do_item)) && !itemTagName.equals(curFragmentName))
        {
            ToDoFragment fragment = new ToDoFragment();

            toggleFragment(fragmentManager, fragment, itemTagName, getString(R.string.to_do_item), position);

        }
        else if (itemTagName.equals(getString(R.string.about_us)) && !itemTagName.equals(curFragmentName))
        {
            GeneralContentFragment fragment = new GeneralContentFragment();

            //set layout id
            fragment.setLayoutId(R.layout.about_us);
            toggleFragment(fragmentManager, fragment, itemTagName, getString(R.string.to_do_item), position);

        }

        drawerList.setItemChecked(position, true);
//        setTitle(navigationItemsNames[position]);
        drawerLayout.closeDrawer(drawerList);

    }

    public void toggleFragment(FragmentManager fragmentManager, Fragment fragment, String curFragmentTagName, String startingFragmentName, int currentFragmentTagNameIndex)
    {
        //start new fragment
        if (fragmentManager.findFragmentByTag(curFragmentTagName) != null)
        {
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(curFragmentTagName)).commit();
        }
        else
        {
            fragmentManager.beginTransaction()
                    .add(R.id.content_frame, fragment, getString(R.string.to_do_item))
                    .commit();
        }

        //hide old fragment
        if (fragmentManager.findFragmentByTag(curFragmentName) != null)
        {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(curFragmentName)).commit();
        }

        curFragmentName = navigationItemsNames[currentFragmentTagNameIndex];

    }






}
