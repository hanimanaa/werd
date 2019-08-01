package com.dimatechs.werd.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimatechs.werd.Interface.ItemClickListner;
import com.dimatechs.werd.R;

public class UsersGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtGroupNum,txtGroupName,txtPartNum;
    public ImageView imageView;
    public ItemClickListner listner;

    public UsersGroupViewHolder(View itemView)
    {
        super(itemView);

        txtGroupNum = (TextView) itemView.findViewById(R.id.groupNum_item);
        txtGroupName = (TextView) itemView.findViewById(R.id.groupName_item);
        txtPartNum = (TextView) itemView.findViewById(R.id.partNum_item);
        imageView = (ImageView) itemView.findViewById(R.id.done_item);

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
