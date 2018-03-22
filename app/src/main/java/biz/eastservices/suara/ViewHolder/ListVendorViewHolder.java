package biz.eastservices.suara.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import biz.eastservices.suara.R;

/**
 * Created by reale on 3/22/2018.
 */

public class ListVendorViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_name,txt_description;
    public ListVendorViewHolder(View itemView) {
        super(itemView);
        txt_name = (TextView)itemView.findViewById(R.id.txt_name);
        txt_description = (TextView)itemView.findViewById(R.id.txt_description);
    }
}
