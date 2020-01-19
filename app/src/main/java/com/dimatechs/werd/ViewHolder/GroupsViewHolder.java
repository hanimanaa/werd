package com.dimatechs.werd.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Interface.ItemClickListner;
import com.dimatechs.werd.R;

public class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtGroupNum,txtGroupName;
    public ImageView lockImageView;
    public ItemClickListner listner;

    public GroupsViewHolder(View itemView)
    {
        super(itemView);

        txtGroupNum = (TextView) itemView.findViewById(R.id.group_num);
        txtGroupName = (TextView) itemView.findViewById(R.id.group_name);
        lockImageView = (ImageView) itemView.findViewById(R.id.lock_item);
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
