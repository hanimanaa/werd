package com.dimatechs.werd.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Interface.ItemClickListner;
import com.dimatechs.werd.R;

public class WerdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtName,txtPhone,txtNum;
    public ImageView doneImageView,adminImageView;
    public ItemClickListner listner;

    public WerdViewHolder(View itemView)
    {
        super(itemView);

        txtName = (TextView) itemView.findViewById(R.id.name_item);
        txtPhone = (TextView) itemView.findViewById(R.id.phone_item);
        txtNum= (TextView) itemView.findViewById(R.id.num_item);
        doneImageView = (ImageView) itemView.findViewById(R.id.done_item);
        adminImageView = (ImageView) itemView.findViewById(R.id.admin_item);


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
