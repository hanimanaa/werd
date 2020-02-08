package com.dimatechs.werd.ViewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Interface.ItemClickListner;
import com.dimatechs.werd.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtName,txtPhone,txtNum;
    public ImageView doneImageView;
    public CheckBox chBox;
    public ItemClickListner listner;

    public NotificationViewHolder(View itemView)
    {
        super(itemView);

        txtName = (TextView) itemView.findViewById(R.id.name_item);
        txtPhone = (TextView) itemView.findViewById(R.id.phone_item);
        txtNum= (TextView) itemView.findViewById(R.id.num_item);
        doneImageView = (ImageView) itemView.findViewById(R.id.done_item);
        chBox = (CheckBox) itemView.findViewById(R.id.chBox);


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
