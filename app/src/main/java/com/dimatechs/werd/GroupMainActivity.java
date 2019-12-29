package com.dimatechs.werd;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dimatechs.werd.Model.Users;
import com.dimatechs.werd.Model.UsersGroups;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.dimatechs.werd.ViewHolder.WerdViewHolder;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupMainActivity extends AppCompatActivity {

    private Button SaveBtn;
    private RecyclerView recyclerView;
    private DatabaseReference RootRef;
    private String groupNum,stDialogDone="no",stDialogAdmin="no";
    private Dialog dialog;
    private ImageView dialogDone,dialogAdmin;
    private EditText etDialogPartNum;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);

        RootRef = FirebaseDatabase.getInstance().getReference().child("UsersGroups");



        //Paper.init(this);
        groupNum=getIntent().getStringExtra("groupNum");
        Toast.makeText(this, groupNum, Toast.LENGTH_SHORT).show();


        recyclerView=findViewById(R.id.recycler_menu);
      //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query =RootRef.orderByChild("groupNum").equalTo(groupNum);

        FirebaseRecyclerOptions<UsersGroups> options =
                            new FirebaseRecyclerOptions.Builder<UsersGroups>()
                                    .setQuery(query,UsersGroups.class)
                                    .build();
        
                    FirebaseRecyclerAdapter<UsersGroups, WerdViewHolder> adapter =
                            new FirebaseRecyclerAdapter<UsersGroups, WerdViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull WerdViewHolder holder, int position, @NonNull final UsersGroups model) {
                                    holder.txtName.setText(model.getUserName());
                                  //  holder.txtName.setText(UserRef.child(model.getUserPhone()).child("name").getKey().);
                                    holder.txtPhone.setText(model.getUserPhone());
                                    holder.txtNum.setText(" جزء رقم : " + model.getPartNum());

                                    if(model.getDone().equals("done")) {
                                            holder.doneImageView.setImageResource(R.drawable.ic_person_green);
                                    }else {
                                        holder.doneImageView.setImageResource(R.drawable.ic_person_red);
                                    }

                                    if(model.getAdmin().equals("yes")) {
                                        holder.adminImageView.setImageResource(R.drawable.ic_person_pin_circle_24dp);
                                    }else{
                                        holder.adminImageView.setImageResource(R.drawable.ic_person_pin_circle_white_24dp);
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog= new Dialog(GroupMainActivity.this);
                                            dialog.setContentView(R.layout.dialog_edit_group_user);
                                            dialog.setTitle("تحديث");
                                            dialog.setCancelable(true);
                                            etDialogPartNum=(EditText)dialog.findViewById(R.id.etDialogPartNum);
                                            etDialogPartNum.setText(model.getPartNum());

                                            // done
                                            dialogDone=(ImageView)dialog.findViewById(R.id.dialogDone);
                                            if (model.getDone().equals("done")) {
                                                dialogDone.setImageResource(R.drawable.ic_person_green);
                                                stDialogDone="done";
                                                }else{
                                                dialogDone.setImageResource(R.drawable.ic_person_red);
                                                stDialogDone="no";
                                            }

                                            dialogDone.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(stDialogDone.equals("done"))
                                                    {
                                                        dialogDone.setImageResource(R.drawable.ic_person_red);
                                                        stDialogDone="no";
                                                    }
                                                    else
                                                    {
                                                        dialogDone.setImageResource(R.drawable.ic_person_green);
                                                        stDialogDone="done";
                                                    }
                                                }
                                            });

                                            // admin
                                            dialogAdmin=(ImageView)dialog.findViewById(R.id.dialogAdmin);
                                            if (model.getAdmin().equals("yes")) {
                                                dialogAdmin.setImageResource(R.drawable.ic_person_pin_circle_24dp);
                                                stDialogAdmin="yes";
                                            }else{
                                                dialogAdmin.setImageResource(R.drawable.ic_person_pin_circle_black_24dp);
                                                stDialogAdmin="no";
                                            }

                                            dialogAdmin.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(stDialogAdmin.equals("yes")) {
                                                        RootRef.orderByChild("groupNum").equalTo(groupNum).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                final List<String> admins = new ArrayList<>();
                                                                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                                                                    String admin = areaSnapshot.child("admin").getValue(String.class);
                                                                    admins.add(admin);
                                                                }

                                                                int count = 0;
                                                                for (int i = 0; i < admins.size(); i++) {
                                                                    if (admins.get(i).equals("yes")) {
                                                                        count++;
                                                                    }
                                                                }
                                                                if (count > 1) {
                                                                    dialogAdmin.setImageResource(R.drawable.ic_person_pin_circle_black_24dp);
                                                                    stDialogAdmin = "no";
                                                                } else {
                                                                    Toast.makeText(GroupMainActivity.this, "يجب ان يكون مدير واحد للمجموعه على الاقل !!!", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }


                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }

                                                    else
                                                    {
                                                        dialogAdmin.setImageResource(R.drawable.ic_person_pin_circle_24dp);
                                                        stDialogAdmin="yes";
                                                    }
                                                }
                                            });

                                            SaveBtn=(Button)dialog.findViewById(R.id.btnDialogSave);
                                            SaveBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    SaveInfoToDatabase(model.getId(),etDialogPartNum.getText().toString(),stDialogDone,stDialogAdmin);
                                                }
                                            });
                                            dialog.show();
                                        }
                                    });

                                }

                                @Override
                                public WerdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.werd_items_layout, parent, false);
                                    WerdViewHolder holder = new WerdViewHolder(view);
                                    return holder;
                                }
                            };
                    recyclerView.setAdapter(adapter);
                    adapter.startListening();

                }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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

    private void SaveInfoToDatabase(String userGroupId,String partNum,String done,String admin)
    {
        HashMap<String,Object> productMap =  new HashMap<>();
        productMap.put("partNum",partNum);
        productMap.put("done",done);
        productMap.put("admin",admin);

        RootRef.child(userGroupId).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            dialog.dismiss();
                            Toast.makeText(GroupMainActivity.this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            dialog.dismiss();
                            Toast.makeText(GroupMainActivity.this, "لم يتم الحفظ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}


