package com.dimatechs.werd;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Model.ScheduleMessages;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.dimatechs.werd.ViewHolder.ScheduleMessageViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class ScheduleMessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference ScheduleMessagesRef;
    private FloatingActionButton btnAddSceduleMessage;
    private String fullName,groupNum,senderUserID;
    private Dialog dialog;
    private TextView tvTime;
    private Button SaveBtn;
    private EditText etMessage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_messages);

        Paper.init(this);
        fullName = Paper.book().read("fullName");

        ScheduleMessagesRef = FirebaseDatabase.getInstance().getReference().child("ScheduleMessages");
        groupNum = getIntent().getStringExtra("groupNum");

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

                etMessage = (EditText) dialog.findViewById(R.id.etDialogMessageS);

                final Calendar myCalender = Calendar.getInstance();
                int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                int minute = myCalender.get(Calendar.MINUTE);

                tvTime = (TextView) dialog.findViewById(R.id.tvTimeS);
                tvTime.setText(""+hour+":"+minute);
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

                                tvTime.setText(""+hourOfDay+":"+minute);

                            }
                        };
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleMessagesActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                        timePickerDialog.setTitle("اختر التوقيت :");
                        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        timePickerDialog.show();
                    }
                });
                //,tvTime.getText().toString()
                SaveBtn = (Button) dialog.findViewById(R.id.btnDialogScheduleAdd);
                SaveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        HashMap<String,Object> Map =  new HashMap<>();
                        Map.put("sendTime","00:22");
                        Map.put("body"," ");
                        Map.put("receiver","للجميع");
                        Map.put("requestCode","0");

                        ScheduleMessagesRef.child(senderUserID).push()
                                .setValue(Map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ScheduleMessagesActivity.this, "تم بنجاح", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });                      
                    }
                });


                dialog.show();

            }


        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query =ScheduleMessagesRef.child(senderUserID);

        FirebaseRecyclerOptions<ScheduleMessages> options =
                new FirebaseRecyclerOptions.Builder<ScheduleMessages>()
                        .setQuery(query, ScheduleMessages.class)
                        .build();

        FirebaseRecyclerAdapter<ScheduleMessages, ScheduleMessageViewHolder> adapter =
                new FirebaseRecyclerAdapter<ScheduleMessages, ScheduleMessageViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final ScheduleMessageViewHolder holder, int position, @NonNull final ScheduleMessages model) {
                        holder.txtTime.setText(model.getSendTime());
                        holder.txtReceiver.setText(model.getReceiver());
                        holder.txtMessage.setText(model.getBody());
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
}
