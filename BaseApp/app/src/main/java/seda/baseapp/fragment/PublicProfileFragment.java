package seda.baseapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import seda.baseapp.R;
import seda.baseapp.adapter.LstViewAdapter;
import seda.baseapp.model.Sample;
import seda.baseapp.model.SampleDao;

/**
 * Created by alanc on 8/10/2017.
 */

public class PublicProfileFragment extends Fragment {

    LstViewAdapter adapter;
    SampleDao sampleDao = null;
    ViewGroup headerView = null;
    ListView lstview = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.public_profile, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lstview = (ListView) getActivity().findViewById(R.id.listview);
        headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.header, lstview, false);
        lstview.addHeaderView(headerView);

//        String[] items = getResources().getStringArray(R.array.list_items);
//
//        for (int i=0; i < items.length ; i++){
//            Log.wtf("I", items[i]);
//        }

        sampleDao = new SampleDao(getActivity());
        sampleDao.refreshItemsFromTable();
//        List<Sample> sampleList =
//        Sample firstSample = sampleList.get(0);
//
//        Log.wtf("I", firstSample.getId() +" "+ firstSample.getSampleType()+" "+ firstSample.getCount());

//        String item1 = "1__8/10/2017/00:00:00__10__80";
//        String[] items = {item1};


//        adapter = new LstViewAdapter(getActivity(), R.layout.rowlayout, R.id.txtid, items);
////        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
////                R.array.Planets, android.R.layout.simple_list_item_1);
//        lstview.setAdapter(adapter);
    }

    public void addItemToAdapter(List<Sample> sampleList)
    {
        String itemString[] = new String[sampleList.size()];
        String temp = null;
        for(int i = 0; i < sampleList.size(); i++)
        {
            Date startTime = new Date((Long)sampleList.get(i).getStartTime());
//            long endTime = sampleList.get(i).getEndTime();
            int eventCount = sampleList.get(i).getCount();
            int score = 70;
            if (sampleList.get(i).getSampleType() == 1) {
                score = score - eventCount*2;
            }
            else if (sampleList.get(i).getSampleType() == 2) {
                score = score + eventCount*3;
            }
            else if (sampleList.get(i).getSampleType() == 3) {
                score = score - eventCount*3;
            }

            Log.d("table", " one sample -> " + sampleList.get(i));
            temp = "" + (i+1) + "__" + startTime+ "__" + score;
            itemString[i] = temp;
//            adapter.add(temp);
        }

        adapter = new LstViewAdapter(getActivity(), R.layout.rowlayout, R.id.txtid, itemString);
//        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.Planets, android.R.layout.simple_list_item_1);
        lstview.setAdapter(adapter);
    }

}