/**
 *
 * LstViewAdapter manages the row elements in the PublicProfileFragment
 * @author  San Kho Lin (829463), Bingfeng Liu (639187), Yixin Chen(522819)
 * @version 1.0
 * @since   2017-09-15
 */
package seda.baseapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import seda.baseapp.R;


public class LstViewAdapter extends ArrayAdapter<String> {
    int groupid;
    String[] item_list;
    ArrayList<String> desc;
    Context context;
    public LstViewAdapter(Context context, int vg, int id, String[] item_list){
        super(context,vg, id, item_list);
        this.context=context;
        groupid=vg;
        this.item_list=item_list;

    }
    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        public TextView textid;
        public TextView textstarttime;
//        public TextView textduration;
        public TextView textsocre;

    }

    public View getView(int position, View convertView, ViewGroup parent) {


        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if(rowView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView= inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textid= (TextView) rowView.findViewById(R.id.txtid);
            viewHolder.textstarttime= (TextView) rowView.findViewById(R.id.txtstarttime);
//            viewHolder.textduration= (TextView) rowView.findViewById(R.id.txtduration);
            viewHolder.textsocre=  (TextView) rowView.findViewById(R.id.txtscore);
            rowView.setTag(viewHolder);

        }
        // Set text to each TextView of ListView item
        String[] items=item_list[position].split("__");
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textid.setText(items[0]);
        holder.textstarttime.setText(items[1]);
//        holder.textduration.setText(items[2]);
        holder.textsocre.setText(items[2]);
        return rowView;
    }

}
