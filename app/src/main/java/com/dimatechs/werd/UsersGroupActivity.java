package com.dimatechs.werd;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Model.UsersGroups;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.dimatechs.werd.ViewHolder.UsersGroupViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.paperdb.Paper;

public class UsersGroupActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView txtWell;
    private FloatingActionButton addBtn,messageBtn;
    private String groupN="",phone="",id;
    private String fullName;
    private String CurrentDate;
    private TextView no_notification_text;
    private DatabaseReference ListRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_group);

        ListRef= FirebaseDatabase.getInstance().getReference().child("UsersGroups");

        Paper.init(this);
        fullName = Paper.book().read("fullName");

        Calendar calendar=Calendar.getInstance();

        Locale locale = new Locale( "ar" , "JO" ) ;
        SimpleDateFormat currentDate = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.FULL,locale);
        CurrentDate=currentDate.format(calendar.getTime());

       // SimpleDateFormat currentDate = new SimpleDateFormat("d/MMM/yyyy");
      //  CurrentDate=currentDate.format(calendar.getTime());

        no_notification_text = findViewById(R.id.no_notification_text);
        ImageSpan imageSpan = new ImageSpan(this, R.drawable.ic_add_circle);
        SpannableString spannableString = new SpannableString(no_notification_text.getText());
        int start = 35;
        int end = 36;
        int flag = 0;
        spannableString.setSpan(imageSpan, start, end, flag);

        no_notification_text.setText(spannableString);
        recyclerView=(RecyclerView)findViewById(R.id.usersGroup_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtWell=(TextView)findViewById(R.id.TvWell);
        addBtn=(FloatingActionButton) findViewById(R.id.usersGroup_add_btn);
        messageBtn=(FloatingActionButton) findViewById(R.id.usersGroup_messages_btn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]
                        {
                                "انشاء مجموعه جديدة",
                                "انتساب الى مجموعة"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(UsersGroupActivity.this);
                builder.setTitle("امكانيات : ");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                             Intent intent = new Intent(getApplicationContext(),AddGroupAdminActivity.class);
                             startActivity(intent);
                        }
                        if (i == 1) {
                            Intent intent = new Intent(getApplicationContext(),GroupsActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                builder.show();

            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MessagesActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        txtWell.setText(CurrentDate+ "\n \n" + " السلام عليكم "+fullName +"");
        Log.d("yzan","start");
        //check if recycler view is empty
        IsEmptyRecyclerView();


    }


/*
    private void UpdateStatus(String id,String done)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UsersGroups");
        ref.child(id).child("done").setValue(done);
    }

*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_Settings) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
            return true;
        } else  if (id == R.id.action_Messages) {
            Intent intent = new Intent(getApplicationContext(),MessagesActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_Exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UsersGroupActivity.this);
            builder.setTitle("تحذير");
            builder.setIcon(R.drawable.ic_report_problem);
            builder.setMessage("سوف تقوم بالخروج من البرنامج !!!");
            builder.setCancelable(true);
            builder.setPositiveButton("انا موافق",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(UsersGroupActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
            );
            builder.setNegativeButton("الغاء",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }

            );
            AlertDialog dialog=builder.create();
            dialog.show();

            return true;
        }
        return true;
    }

/*
    private final class  HandleAlertDialogListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==-1)
            {
               Toast.makeText(UsersGroupActivity.this,"بارك الله فيك", Toast.LENGTH_SHORT).show();
               UpdateStatus(id,"done");
            }
            else
            {
                Toast.makeText(UsersGroupActivity.this, "لا", Toast.LENGTH_SHORT).show();
                UpdateStatus(id,"no");
            }
        }
    }

 */
private void IsEmptyRecyclerView()
{
    ListRef.orderByChild("userPhone").equalTo(Prevalent.currentOnlineUser.getPhone()).
            addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        no_notification_text.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        Log.d("yzan","exists");

                        loadRecyclerView();
                    }
                    else
                    {
                        no_notification_text.setVisibility(View.VISIBLE);

                        Log.d("yzan","not exists");

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

}
    private void loadRecyclerView() {

        Log.d("yzan","load");

        Query query =ListRef.orderByChild("userPhone").equalTo(Prevalent.currentOnlineUser.getPhone());
        FirebaseRecyclerOptions<UsersGroups> options=
                new FirebaseRecyclerOptions.Builder<UsersGroups>()
                        .setQuery(query,UsersGroups.class)
                        .build();

        FirebaseRecyclerAdapter<UsersGroups, UsersGroupViewHolder> adapter=
                new FirebaseRecyclerAdapter<UsersGroups, UsersGroupViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final UsersGroupViewHolder holder, int position, @NonNull final UsersGroups model)
                    {
                        //holder.txtGroupNum.setText("رقم المجموعه : " + model.getGroupNum());
                        holder.txtGroupName.setText("مجموعة" +"\n"+ model.getGroupName());
                        holder.txtPartNum.setText("جزء رقم : " + model.getPartNum());

                        if (model.getDone().equals("done")) {
                            holder.imageView.setImageResource(R.drawable.ic_person_green);
                        }


                        holder.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                groupN=model.getGroupNum();
                                phone=Prevalent.currentOnlineUser.getPhone();
                                id=model.getId();

                                if(model.getDone().equals("done"))
                                {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UsersGroups");
                                    ref.child(id).child("done").setValue("no");
                                }
                                else
                                {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UsersGroups");
                                    ref.child(id).child("done").setValue("done");
                                    Toast.makeText(UsersGroupActivity.this,"بارك الله فيك", Toast.LENGTH_SHORT).show();
                                }

                                /*
                                AlertDialog.Builder builder = new AlertDialog.Builder(UsersGroupActivity.this);
                                builder.setTitle("الورد اليومي");
                                builder.setMessage("هل اتممت قراءة الجزء ؟");
                                builder.setCancelable(true);
                                builder.setPositiveButton("نعم بحمد الله", new HandleAlertDialogListener());
                                builder.setNegativeButton("للاسف لا", new HandleAlertDialogListener());
                                AlertDialog dialog=builder.create();
                                dialog.show();
                                */


                            }
                        });

                        holder.txtmore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(UsersGroupActivity.this, GroupMainActivity.class);
                                // intent.putExtra("groupNum",model.getGroupNum());
                                intent.putExtra("IsAdmin",model.getAdmin());
                                Paper.book().write(Prevalent.GroupNum, model.getGroupNum());
                                Paper.book().write(Prevalent.GroupName, model.getGroupName());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public UsersGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_group_items_layout,parent,false);
                        UsersGroupViewHolder holder = new UsersGroupViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

}

