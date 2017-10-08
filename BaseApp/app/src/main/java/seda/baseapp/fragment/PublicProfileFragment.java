package seda.baseapp.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import seda.baseapp.R;
import seda.baseapp.adapter.LstViewAdapter;

/**
 * Created by alanc on 8/10/2017.
 */

public class PublicProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.public_profile, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView lstview =(ListView)getActivity().findViewById(R.id.listview);
        ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.header, lstview,false);
        lstview.addHeaderView(headerView);

        String[] items = getResources().getStringArray(R.array.list_items);

        for (int i=0; i < items.length ; i++){
            Log.wtf("I", items[i]);
        }



        LstViewAdapter adapter=new LstViewAdapter(getActivity(),R.layout.rowlayout,R.id.txtname,items);
//        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.Planets, android.R.layout.simple_list_item_1);
        lstview.setAdapter(adapter);
    }

}