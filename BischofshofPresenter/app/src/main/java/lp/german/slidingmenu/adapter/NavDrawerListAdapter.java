package lp.german.slidingmenu.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lp.german.bischofshofpresenter.app.R;
import lp.german.slidingmenu.model.NavDrawerItem;

/**
 * Created by paullichtenberger on 24.06.14.
 */
public class NavDrawerListAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private String mMarke;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems, String marke){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.mMarke = marke;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.btn_frame);

        if(mMarke.equals("pref_bischofshof")){
            imageView.setImageResource(R.drawable.btn_frame);
        }else{
            imageView.setImageResource(R.drawable.btn_frame_wb);
        }

        //txtCount.setText("5");
        txtTitle.setText(navDrawerItems.get(position).getTitle());

        // displaying count
        // check whether it set visible or not
        if(navDrawerItems.get(position).getCounterVisibility()){
            txtCount.setText(navDrawerItems.get(position).getCount());
        }else{
            // hide the counter view
            txtCount.setVisibility(View.GONE);
        }

        return convertView;
    }

}
