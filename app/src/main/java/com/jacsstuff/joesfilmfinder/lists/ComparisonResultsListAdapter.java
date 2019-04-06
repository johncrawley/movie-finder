package com.jacsstuff.joesfilmfinder.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.jacsstuff.joesfilmfinder.R;
import com.jacsstuff.joesfilmfinder.results.ResultLink;

import java.util.List;

/**
 * Created by John on 02/04/2018.
 * Helps fill a ListView with data
 */
public class ComparisonResultsListAdapter extends ArrayAdapter<ResultLink> {

    private Context context;
    private int itemLayoutId;
    private List<ResultLink> items;

    public ComparisonResultsListAdapter(Context context, int itemLayoutId, List<ResultLink> items){
        super(context, itemLayoutId, items);
        this.context = context;
        this.itemLayoutId = itemLayoutId;
        this.items = items;
    }


    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.itemLayoutId, null);
        }
        ResultLink resultLink = items.get(position);
        assignTextToView(convertView, resultLink.getRolesAsString(), R.id.rolesText);
        return convertView;
    }

    private void assignTextToView(View parentView, String text, int textViewId){
        TextView view = parentView.findViewById(textViewId);
        if(view != null){
            view.setText(text);
        }
    }


}
