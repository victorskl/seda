package seda.baseapp.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import seda.baseapp.MainActivity;
import seda.baseapp.R;

/**
 * Created by liubingfeng on 24/09/2017.
 */
public class AboutUsFragment extends Fragment
{

    private boolean shining = true;
    private AnimationDrawable animation = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
//        Log.d("test", "layoutId " + getArguments().getInt("layout"));
//        return inflater.inflate(layoutId, container, false);
        return inflater.inflate(R.layout.about_us, container, false);


    }

    public void setAnimation(boolean isShow)
    {

        //        not showing
        if(isShow && !shining && animation != null)
        {
            animation.start();
            shining = true;
            return;
        }
//        is showing
        if(!isShow && shining & animation != null)
        {
            animation.stop();
//                  back to first frame
            animation.selectDrawable(0);

            shining = false;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        animation = new AnimationDrawable();
        animation.addFrame(getResources().getDrawable(R.drawable.device5_no_light), 200);
        animation.addFrame(getResources().getDrawable(R.drawable.device5_star_light), 300);
//        animation.addFrame(getResources().getDrawable(R.drawable.image3), 300);
        animation.setOneShot(false);

        ImageView imageAnim =  (ImageView) getActivity().findViewById(R.id.sedaDevice_imageView);
        imageAnim.setBackground(animation);

        animation.selectDrawable(0);

        // start the animation!
//        animation.start();




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
