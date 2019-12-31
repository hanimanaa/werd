package com.dimatechs.werd;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dimatechs.werd.Model.Groups;
import com.dimatechs.werd.Model.UsersGroups;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.dimatechs.werd.ViewHolder.UsersGroupViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import io.paperdb.Paper;

public class UsersGroupActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView txtWell;
    private FloatingActionButton addBtn;
    private DatabaseReference RootRef;
    private String groupN="",phone="",id;
    private String fullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_group);


        Paper.init(this);
        fullName = Paper.book().read("fullName");

        recyclerView=(RecyclerView)findViewById(R.id.usersGroup_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtWell=(TextView)findViewById(R.id.TvWell);
        addBtn=(FloatingActionButton) findViewById(R.id.usersGroup_add_btn);

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

        txtWell.setText(" السلام عليكم " + fullName + "\n الرجاء اختيار المجموعه او التسجيل لمجموعه جديدة");

        final DatabaseReference ListRef= FirebaseDatabase.getInstance().getReference().child("UsersGroups");

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
                            holder.txtGroupNum.setText("رقم المجموعه : " + model.getGroupNum());
                            holder.txtGroupName.setText("اسم المجموعه : " + model.getGroupName());
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(UsersGroupActivity.this);
                                builder.setTitle("الورد اليومي");
                                builder.setMessage("هل اتممت قراءة الجزء ؟");
                                builder.setCancelable(true);
                                builder.setPositiveButton("نعم بحمد الله", new HandleAlertDialogListener());
                                builder.setNegativeButton("للاسف لا", new HandleAlertDialogListener());
                                AlertDialog dialog=builder.create();
                                dialog.show();
                            }
                        });

                        holder.txtmore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(UsersGroupActivity.this, GroupMainActivity.class);
                                intent.putExtra("groupNum",model.getGroupNum());
                                intent.putExtra("IsAdmin",model.getAdmin());
                                intent.putExtra("groupName",model.getGroupName());
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

    private void UpdateStatus(String id,String done)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UsersGroups");
        ref.child(id).child("done").setValue(done);
    }

    private void GetGroupNameFromFireBase(final String groupNum)
    {
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Groups").child(groupNum).exists())
                {
                    Groups groupData =dataSnapshot.child("Groups").child(groupNum).getValue(Groups.class);
                    groupN = "اسم المجموعه: " + groupData.getGroupName().toString();
                    Toast.makeText(UsersGroupActivity.this,groupN, Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(UsersGroupActivity.this, "no group", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d("hani", "onOptionsItemSelected");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Register) {
            Toast.makeText(this, "you selected הרשמה", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            return true;
         } else if (id == R.id.action_Admin) {
            Toast.makeText(this, "you selectedמנהל הוספת קבוצה ", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),AddGroupAdminActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_Settings) {
            Toast.makeText(this, " עדכון פרטים", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_Exit) {
            Toast.makeText(this, "you selected יציאה", Toast.LENGTH_LONG).show();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return true;
        }
        return true;
    }


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


}

