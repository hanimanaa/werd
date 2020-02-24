package com.dimatechs.werd;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.BroadcastReceiver.ScheduleNotification;
import com.dimatechs.werd.Model.ScheduleMessages;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.dimatechs.werd.ViewHolder.ScheduleMessageViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class ScheduleMessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference ScheduleMessagesRef,messageCodeRef;
    private FloatingActionButton btnAddSceduleMessage;
    private String fullName,groupNum,groupname,senderUserID,requestCode,receiver="all";
    private Dialog dialog;
    private TextView tvTime,no_notification_text;
    private Button SaveBtn;
    private EditText etMessage;
    private RadioButton rbSelectAll, rbRead, rbNotRead;
    private AlarmManager AutoAlarmManager;
    private PendingIntent pendingIntent;
    private int h,m;
    private int x;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_messages);

        Paper.init(this);
        fullName = Paper.book().read("fullName");

        ScheduleMessagesRef = FirebaseDatabase.getInstance().getReference().child("ScheduleMessages");

        groupNum=Paper.book().read(Prevalent.GroupNum);
        groupname=Paper.book().read(Prevalent.GroupName);

        no_notification_text = findViewById(R.id.no_notification_text);
        ImageSpan imageSpan = new ImageSpan(this, R.drawable.ic_add_circle);
        SpannableString spannableString = new SpannableString(no_notification_text.getText());
        int start = 35;
        int end = 36;
        int flag = 0;
        spannableString.setSpan(imageSpan, start, end, flag);

        no_notification_text.setText(spannableString);

        recyclerView = findViewById(R.id.recycler_ScheduleMessages);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        senderUserID = Prevalent.currentOnlineUser.getPhone();

        btnAddSceduleMessage = (FloatingActionButton) findViewById(R.id.send_btn);

        btnAddSceduleMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ScheduleMessagesActivity.this);
                dialog.setContentView(R.layout.dialog_add_schedule_messages);
                dialog.setTitle("تحديث");
                dialog.setCancelable(true);

                rbSelectAll = (RadioButton) dialog.findViewById(R.id.RB_selectAllS);
                rbRead = (RadioButton) dialog.findViewById(R.id.RB_readS);
                rbNotRead = (RadioButton) dialog.findViewById(R.id.RB_notReadS);

                rbSelectAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        receiver="all";
                    }
                });

                rbRead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        receiver="read";
                    }
                });

                rbNotRead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        receiver="notRead";
                    }
                });


                etMessage = (EditText) dialog.findViewById(R.id.etDialogMessageS);


                final Calendar myCalender = Calendar.getInstance();
              //  int hour = myCalender.get(Calendar.HOUR_OF_DAY);
              //  int minute = myCalender.get(Calendar.MINUTE);

                tvTime = (TextView) dialog.findViewById(R.id.tvTimeS);
               // tvTime.setText(""+hour+":"+minute);

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                tvTime.setText(sdf.format(myCalender.getTime()));



                tvTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        final Calendar myCalender = Calendar.getInstance();
                        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                        int minute = myCalender.get(Calendar.MINUTE);
                        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
                                myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                myCalender.set(Calendar.MINUTE, minute);

                                h=hourOfDay;
                                m=minute;

                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                tvTime.setText(sdf.format(myCalender.getTime()));
                            }
                        };
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleMessagesActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                        timePickerDialog.setTitle("اختر التوقيت :");
                        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        timePickerDialog.show();
                    }
                });

                SaveBtn = (Button) dialog.findViewById(R.id.btnDialogScheduleAdd);
                SaveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (TextUtils.isEmpty(etMessage.getText().toString())) {
                            etMessage.setError("ادخل الاسم اذا سمحت");
                        }
                        else
                            {
                            messageCodeRef = FirebaseDatabase.getInstance().getReference().child("ScheduleMessagesCode");
                            messageCodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        //new ScheduleMessagesCode
                                        requestCode = (dataSnapshot.getValue().toString());
                                        x = Integer.parseInt(requestCode) + 1;
                                        messageCodeRef.setValue(x);

                                        HashMap<String, Object> Map = new HashMap<>();
                                        Map.put("sendTime", tvTime.getText().toString());
                                        Map.put("body", etMessage.getText().toString());
                                        Map.put("receiver", receiver);
                                        Map.put("requestCode", requestCode);
                                        Map.put("groupNum", groupNum);


                                        ScheduleMessagesRef.child(senderUserID).push()
                                                .setValue(Map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            AutoAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                                            final Calendar calendar = Calendar.getInstance();

                                                            final Intent ScheduleIntent = new Intent(ScheduleMessagesActivity.this, ScheduleNotification.class);
                                                            ScheduleIntent.putExtra("groupNum", groupNum);
                                                            ScheduleIntent.putExtra("groupName", groupname);
                                                            ScheduleIntent.putExtra("senderUserID", senderUserID);
                                                            ScheduleIntent.putExtra("fullName", fullName);
                                                            ScheduleIntent.putExtra("body", etMessage.getText().toString());
                                                            ScheduleIntent.putExtra("receiver", receiver);

                                                            calendar.set(Calendar.HOUR_OF_DAY, h);
                                                            calendar.set(Calendar.MINUTE, m);

                                                            pendingIntent = PendingIntent.getBroadcast(ScheduleMessagesActivity.this, Integer.parseInt(requestCode), ScheduleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                            AutoAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                                            Log.d("sc",String.valueOf(x-1));

                                                            loadNotification();
                                                            dialog.dismiss();

                                                            IsEmptyRecyclerView();
                                                            MakeToast("اضافة","تم اضافة الاشعار بنجاح",R.drawable.ok);
                                                        }
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check if recycler view is empty
        IsEmptyRecyclerView();

    }

    private void loadNotification() {

        Query query =ScheduleMessagesRef.child(senderUserID).orderByChild("groupNum").equalTo(groupNum);


        FirebaseRecyclerOptions<ScheduleMessages> options =
                new FirebaseRecyclerOptions.Builder<ScheduleMessages>()
                        .setQuery(query, ScheduleMessages.class)
                        .build();

        FirebaseRecyclerAdapter<ScheduleMessages, ScheduleMessageViewHolder> adapter =
                new FirebaseRecyclerAdapter<ScheduleMessages, ScheduleMessageViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final ScheduleMessageViewHolder holder, final int position, @NonNull final ScheduleMessages model) {
                        holder.txtTime.setText(model.getSendTime());
                        holder.txtMessage.setText(model.getBody());
                        holder.txtRequestCode.setText(model.getRequestCode());
                        if(model.getReceiver().equals("all"))
                        {
                            holder.txtReceiver.setText("لكل المجموعة");
                        }
                        else
                        {
                            if (model.getReceiver().equals("read"))
                            {
                                holder.txtReceiver.setText("قرا الورد");
                            }
                            else
                            {
                                holder.txtReceiver.setText("لم يقرا الورد");
                            }
                        }
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleMessagesActivity.this);
                                builder.setTitle("تحذير");
                                builder.setIcon(R.drawable.ic_red_forever_black_24dp);
                                builder.setMessage("هل تريد حذف الاشعار ؟");
                                builder.setCancelable(true);
                                builder.setPositiveButton("نعم",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {


                                                int req =Integer.parseInt(holder.txtRequestCode.getText().toString());
                                                Log.d("sc",String.valueOf(req));


                                                Intent in = new Intent(ScheduleMessagesActivity.this, ScheduleNotification.class);
                                                PendingIntent pi = PendingIntent.getBroadcast(ScheduleMessagesActivity.this, req, in, PendingIntent.FLAG_CANCEL_CURRENT);
                                                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

                                                if (pi != null) {
                                                    am.cancel(pi);
                                                    ScheduleMessagesRef.child(senderUserID).child(getRef(position).getKey()).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        IsEmptyRecyclerView();
                                                                        MakeToast("حذف", "تم حذف الاشعار !!!  ", R.drawable.error1);
                                                                    }
                                                                }
                                                            });
                                                }else
                                                {
                                                    Toast.makeText(ScheduleMessagesActivity.this, "null : "+req, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                );
                                builder.setNegativeButton("لا",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                            }
                                        }

                                );
                                AlertDialog alertDialog=builder.create();
                                alertDialog.show();
                                return false;
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ScheduleMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_messages_item_layout, parent, false);
                        ScheduleMessageViewHolder holder = new ScheduleMessageViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void MakeToast(String headerText, String message,int image)
    {
        // MakeToast("hi","jhvgfxfhg",R.drawable.warning1);
        // image=R.id.toast_image
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);

        LayoutInflater ly=getLayoutInflater();
        View v=ly.inflate(R.layout.toast,(ViewGroup)findViewById(R.id.line1));
        TextView txt1=(TextView)v.findViewById(R.id.toast_text1);
        TextView txt2=(TextView)v.findViewById(R.id.toast_text2);
        ImageView img =(ImageView)v.findViewById(R.id.toast_image);
        txt1.setText(headerText);
        txt2.setText(message);
        img.setImageResource(image);

        toast.setView((v));
        toast.show();

    }

    private void IsEmptyRecyclerView()
    {
        ScheduleMessagesRef.child(senderUserID).orderByChild("groupNum").equalTo(groupNum).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            no_notification_text.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            loadNotification();
                        }
                        else
                        {
                            no_notification_text.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

}
