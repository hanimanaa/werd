package com.dimatechs.werd.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Interface.ItemClickListner;
import com.dimatechs.werd.R;

public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtName,txtTime,txtMessage,txtGroupName;
    public ItemClickListner listner;

    public MessageViewHolder(View itemView)
    {
        super(itemView);

        txtName = (TextView) itemView.findViewById(R.id.tv_sender_name);
        txtTime = (TextView) itemView.findViewById(R.id.tv_time);
        txtMessage= (TextView) itemView.findViewById(R.id.tv_message);
        txtGroupName = (TextView) itemView.findViewById(R.id.tv_group_name);



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
