package seda.baseapp.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import seda.baseapp.MainActivity;
import seda.baseapp.R;

/**
 * Created by liubingfeng on 24/09/2017.
 */
public class AboutUsFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
//        Log.d("test", "layoutId " + getArguments().getInt("layout"));
//        return inflater.inflate(layoutId, container, false);
        return inflater.inflate(R.layout.about_us, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

//        Button startBluetoothButton = (Button)getActivity().findViewById(R.id.startBluetoothButton);

//        Button asServerButton = (Button)getActivity().findViewById(R.id.asServerButton);
//
//
//
//        Button asClientButton = (Button)getActivity().findViewById(R.id.asClientButton);
//
//        final TextView displayBluetoothTextView = (TextView)getActivity().findViewById(R.id.displayBluetoothTextView);
//
//
//
//
//        asServerButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Log.wtf("bingfengappservice", "server start");
//                ((MainActivity) getActivity()).startServerThread(displayBluetoothTextView);
//            }
//        });
//
//        asClientButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Log.wtf("bingfengappservice", "client start");
//
//                ((MainActivity)getActivity()).startClientThread("SmartisanBing", displayBluetoothTextView);
//            }
//        });





    }
}
