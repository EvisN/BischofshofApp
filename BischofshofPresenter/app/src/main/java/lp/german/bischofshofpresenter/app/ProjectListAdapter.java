package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lp.german.bischofshofpresenter.app.util.ProjektItem;
import lp.german.slidingmenu.model.NavDrawerItem;

/**
 * Created by paullichtenberger on 11.07.14.
 */
public class ProjectListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ProjektItem> projektItems;
    private String mName;

    public ProjectListAdapter(Context context, ArrayList<ProjektItem> projektItems){
        this.context = context;
        this.projektItems = projektItems;
    }

    @Override
    public int getCount() {
        return projektItems.size();
    }

    @Override
    public ProjektItem getItem(int position) {
        return projektItems.get(position);
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
            convertView = mInflater.inflate(R.layout.projekt_list_item, null);
        }

        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

        //txtCount.setText("5");
        txtTitle.setText(projektItems.get(position).getTitle());

        return convertView;
    }

}