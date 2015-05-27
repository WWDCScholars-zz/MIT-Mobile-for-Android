package edu.mit.mitmobile2.maps.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.model.MITMapPlace;

public class MapSearchResultAdapter extends BaseAdapter {

    private class ViewHolder {
        TextView title;
        TextView subtitle;
        ImageView info;
    }

    private Context context;
    private List<MITMapPlace> places;

    public MapSearchResultAdapter(Context context, List<MITMapPlace> places) {
        this.context = context;
        this.places = places;
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public MITMapPlace getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;

        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.map_search_list_row, null);

            holder.title = (TextView) view.findViewById(R.id.map_search_result_title);
            holder.subtitle = (TextView) view.findViewById(R.id.map_search_result_subtitle);
            holder.info = (ImageView) view.findViewById(R.id.info_icon);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        MITMapPlace place = getItem(position);
        holder.title.setText((position + 1) + ". Building " + place.getBuildingNumber());
        holder.subtitle.setText(place.getName());
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Go to place detail view
            }
        });

        return view;
    }

}
