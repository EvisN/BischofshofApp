package lp.german.bischofshofpresenter.app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import lp.german.bischofshofpresenter.app.util.ProjektItem;
import lp.german.slidingmenu.model.NavDrawerItem;

/**
 * Created by paullichtenberger on 11.07.14.
 */
public class ProjectListAdapter extends ArrayAdapter<ProjektItem> {

    private final Context context;
    private final ArrayList<ProjektItem> projektItems;

    public ProjectListAdapter(Context context, ArrayList<ProjektItem> projektItems){
        super(context,R.layout.projekt_list_item,projektItems);
        this.context = context;
        this.projektItems = projektItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.projekt_list_item, parent, false);


        ProjektItem item = projektItems.get(position);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);

        //txtCount.setText("5");
        txtTitle.setText(item.getTitle());

        return rowView;
    }
}