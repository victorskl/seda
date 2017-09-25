package seda.baseapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liubingfeng on 24/09/2017.
 */
public class GeneralContentFragment extends Fragment
{
    private int layoutId = 0;

    public void setLayoutId(int layoutId)
    {
        this.layoutId = layoutId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
        return inflater.inflate(layoutId, container, false);
    }
}
