package com.dimatechs.werd;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.dimatechs.werd.Model.Groups;
import com.dimatechs.werd.Model.UsersGroups;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.dimatechs.werd.ViewHolder.GroupsViewHolder;
import com.dimatechs.werd.ViewHolder.UsersGroupViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GroupsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText search_bar;
    private RecyclerView.LayoutManager layoutManager;
    private String groupN="",phone;
    private ProgressDialog loadingBar;

    private String saveCurrentDate,saveCurrentTime;
    private String RandomKey;
    private DatabaseReference ListRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        search_bar=(EditText)findViewById(R.id.search_bar);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_groups);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        loadingBar=new ProgressDialog(this);

        ListRef= FirebaseDatabase.getInstance().getReference().child("Groups");

        // Search a group
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String st=String.valueOf(search_bar.getText());
                Query query = ListRef.orderByChild("groupName")
                        .startAt(st)
                        .endAt(st+'\uf8ff');
                fillRecyclerView(query);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        fillRecyclerView(ListRef);
    }

    private void fillRecyclerView(Query query) {

        FirebaseRecyclerOptions<Groups> options=
                new FirebaseRecyclerOptions.Builder<Groups>()
                        .setQuery(query,Groups.class)
                        .build();

        FirebaseRecyclerAdapter<Groups, GroupsViewHolder> adapter=
                new FirebaseRecyclerAdapter<Groups, GroupsViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final GroupsViewHolder holder, int position, @NonNull final Groups model)
                    {
                        holder.txtGroupNum.setText("رقم المجموعه : " + model.getGroupNum());
                        holder.txtGroupName.setText("اسم المجموعه : " + model.getGroupName());


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                groupN=model.getGroupNum();
                                phone=Prevalent.currentOnlineUser.getPhone();

                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupsActivity.this);
                                builder.setTitle("انتساب الى مجموعة");
                                builder.setMessage("هل تريد الانتساب ؟");
                                builder.setCancelable(true);
                                builder.setPositiveButton("نعم من فضلك", new GroupsActivity.HandleAlertDialogListener());
                                builder.setNegativeButton("كلا شكرا", new GroupsActivity.HandleAlertDialogListener());
                                AlertDialog dialog=builder.create();
                                dialog.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.group_items_layout,parent,false);
                        GroupsViewHolder holder = new GroupsViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private final class  HandleAlertDialogListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==-1)
            {
                loadingBar.setTitle("انتساب الى مجموعه");
                loadingBar.setMessage("انتظر");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                ValidateUser(groupN,phone);
            }
            else
            {
                Toast.makeText(GroupsActivity.this, "لا", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void ValidateUser(final String group,final String phone)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference().child("UsersGroups");
        Query query = RootRef.orderByChild("groupNum").equalTo(group);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Toast.makeText(GroupsActivity.this, "المجموعه موجوده", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                {
                    AddGroup(groupN);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    private void AddGroup(final String group)
    {
        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        RandomKey=saveCurrentDate+saveCurrentTime;

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("Groups").child(group).exists())
                {
                    // dataSnapshot.child("Groups").child(group).child("groupName").getValue(String.class);
                    HashMap<String,Object> groupdataMap = new HashMap<>();
                    groupdataMap.put("id",RandomKey);
                    groupdataMap.put("groupNum",group);
                    groupdataMap.put("groupName",dataSnapshot.child("Groups").child(group).child("groupName").getValue(String.class));
                    groupdataMap.put("partNum","0");
                    groupdataMap.put("done","no");
                    groupdataMap.put("userName",dataSnapshot.child("Users").child(Prevalent.currentOnlineUser.getPhone()).child("name").getValue(String.class));
                    groupdataMap.put("userPhone",Prevalent.currentOnlineUser.getPhone());
                    groupdataMap.put("admin","no");


                    RootRef.child("UsersGroups").child(RandomKey)
                            .updateChildren(groupdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(GroupsActivity.this,"بارك الله فيك", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent=new Intent(GroupsActivity.this,UsersGroupActivity.class);
                                        startActivity(intent);

                                    }
                                    else
                                    {
                                        Toast.makeText(GroupsActivity.this, "שגיאת רשת", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(GroupsActivity.this, "المجموعه غير موجودة", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }


}
