/**
 *
 * SedaStatusFragment used to show SEDA device status page, but now it is integrated in About us page.
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */
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


public class SedaStatusFragment extends Fragment
{

    /**
     * This is the method used to inflate the view.
     * @return void
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * Inflate the layout for this fragment
         */
//        Log.d("test", "layoutId " + getArguments().getInt("layout"));
//        return inflater.inflate(layoutId, container, false);
        return inflater.inflate(R.layout.seda_status, container, false);


    }

    /**
     * This is the method used to set up the animation of SEDA devision (green light blinking)
     * @return void
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        AnimationDrawable animation = new AnimationDrawable();
        animation.addFrame(getResources().getDrawable(R.drawable.device5_no_light), 200);
        animation.addFrame(getResources().getDrawable(R.drawable.device5_star_light), 300);
//        animation.addFrame(getResources().getDrawable(R.drawable.image3), 300);
        animation.setOneShot(false);

        ImageView imageAnim =  (ImageView) getActivity().findViewById(R.id.sedaDevice_imageView);
        imageAnim.setBackground(animation);

        // start the animation!
        animation.start();

//        Button startBluetoothButton = (Button)getActivity().findViewById(R.id.startBluetoothButton);

//        ImageView sedaImage = getActivity().findViewById(R.id.sedaDevice_imageView);










    }
}
