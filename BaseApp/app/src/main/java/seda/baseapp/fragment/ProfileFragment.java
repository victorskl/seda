package seda.baseapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import seda.baseapp.R;

/**
 * Created by liubingfeng on 30/09/2017.
 */

public class ProfileFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
//        return inflater.inflate(layoutId, container, false);
        return inflater.inflate(R.layout.profile, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


    }

}
