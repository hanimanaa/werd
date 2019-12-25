package com.dimatechs.werd.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimatechs.werd.Interface.ItemClickListner;
import com.dimatechs.werd.R;

public class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtGroupNum,txtGroupName;
    public ItemClickListner listner;

    public GroupsViewHolder(View itemView)
    {
        super(itemView);

        txtGroupNum = (TextView) itemView.findViewById(R.id.group_num);
        txtGroupName = (TextView) itemView.findViewById(R.id.group_name);
    }

    public void setItemClickListner(ItemClickListner listner )
    {
        this.listner=listner;
    }


    @Override
    public void onClick(View view)
    {
        listner.onClick(view,getAdapterPosition(),false);
    }
}
