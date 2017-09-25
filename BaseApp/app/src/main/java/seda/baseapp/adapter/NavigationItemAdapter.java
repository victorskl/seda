package seda.baseapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import seda.baseapp.R;


/**
 * Created by liubingfeng on 24/09/2017.
 */
public class NavigationItemAdapter extends ArrayAdapter<String>
{

    /**
     * Adapter context
     */
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public NavigationItemAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    /**
     * Returns the view for a specific item on the list
     */
    // when calling add will come here to set up items
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final String currentItem = getItem(position);

        Log.d("bingfengappservice", "currentItem -> " + currentItem);
        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        //each item is a view, this will set a tag name to this view so later we could use
        // getTag method to get the name
        row.setTag(currentItem);

        final TextView textView = (TextView) row.findViewById(R.id.nagigationListItemtextView);

        textView.setText(currentItem);


//        checkBox.setText(currentItem.getText());
//        checkBox.setChecked(false);
//        checkBox.setEnabled(true);
//
//        checkBox.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                if (checkBox.isChecked()) {
//                    checkBox.setEnabled(false);
//                    if (mContext instanceof ToDoActivity) {
//                        ToDoActivity activity = (ToDoActivity) mContext;
//                        activity.checkItem(currentItem);
//                    }
//                }
//            }
//        });

        return row;
    }
}
