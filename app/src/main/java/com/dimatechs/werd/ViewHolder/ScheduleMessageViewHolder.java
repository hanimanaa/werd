package com.dimatechs.werd.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Interface.ItemClickListner;
import com.dimatechs.werd.R;

public class ScheduleMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtTime,txtReceiver,txtMessage,txtRequestCode;
    public ItemClickListner listner;

    public ScheduleMessageViewHolder(View itemView)
    {
        super(itemView);

        txtTime = (TextView) itemView.findViewById(R.id.tv_time_schedule);
        txtReceiver = (TextView) itemView.findViewById(R.id.tv_receiver_schedule);
        txtMessage= (TextView) itemView.findViewById(R.id.tv_message_schedule);
        txtRequestCode= (TextView) itemView.findViewById(R.id.tv_message_RequestCode);

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
