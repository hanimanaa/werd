package com.dimatechs.werd;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dimatechs.werd.Model.Groups;
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
    private DatabaseReference RootRef,GroupRef;
    private String groupNum,IsAdmin,stDialogDone="no",stDialogAdmin="no";
    private Dialog dialog;
    private ImageView dialogDone,dialogAdmin;
    private EditText etDialogPartNum;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);

        RootRef = FirebaseDatabase.getInstance().getReference().child("UsersGroups");
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");



        //Paper.init(this);
        groupNum=getIntent().getStringExtra("groupNum");
        IsAdmin=getIntent().getStringExtra("IsAdmin");


        Toast.makeText(this, groupNum, Toast.LENGTH_SHORT).show();


        recyclerView=findViewById(R.id.recycler_menu);
      //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(IsAdmin.equals("yes")) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
            return true;
        }else
            return false;
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
                                            if (IsAdmin.equals("yes")) {
                                                dialog = new Dialog(GroupMainActivity.this);
                                                dialog.setContentView(R.layout.dialog_edit_group_user);
                                                dialog.setTitle("تحديث");
                                                dialog.setCancelable(true);
                                                etDialogPartNum = (EditText) dialog.findViewById(R.id.etDialogPartNum);
                                                etDialogPartNum.setText(model.getPartNum());

                                                // done
                                                dialogDone = (ImageView) dialog.findViewById(R.id.dialogDone);
                                                if (model.getDone().equals("done")) {
                                                    dialogDone.setImageResource(R.drawable.ic_person_green);
                                                    stDialogDone = "done";
                                                } else {
                                                    dialogDone.setImageResource(R.drawable.ic_person_red);
                                                    stDialogDone = "no";
                                                }

                                                dialogDone.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (stDialogDone.equals("done")) {
                                                            dialogDone.setImageResource(R.drawable.ic_person_red);
                                                            stDialogDone = "no";
                                                        } else {
                                                            dialogDone.setImageResource(R.drawable.ic_person_green);
                                                            stDialogDone = "done";
                                                        }
                                                    }
                                                });

                                                // admin
                                                dialogAdmin = (ImageView) dialog.findViewById(R.id.dialogAdmin);
                                                if (model.getAdmin().equals("yes")) {
                                                    dialogAdmin.setImageResource(R.drawable.ic_person_pin_circle_24dp);
                                                    stDialogAdmin = "yes";
                                                } else {
                                                    dialogAdmin.setImageResource(R.drawable.ic_person_pin_circle_black_24dp);
                                                    stDialogAdmin = "no";
                                                }

                                                dialogAdmin.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (stDialogAdmin.equals("yes")) {
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
                                                        } else {
                                                            dialogAdmin.setImageResource(R.drawable.ic_person_pin_circle_24dp);
                                                            stDialogAdmin = "yes";
                                                        }
                                                    }
                                                });

                                                SaveBtn = (Button) dialog.findViewById(R.id.btnDialogSave);
                                                SaveBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        SaveInfoToDatabase(model.getId(), etDialogPartNum.getText().toString(), stDialogDone, stDialogAdmin);
                                                    }
                                                });
                                                dialog.show();
                                            }
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final Menu Fmenu=menu;
        GroupRef.orderByChild("groupNum").equalTo(groupNum).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    String lock = areaSnapshot.child("locked").getValue(String.class);
                    if (lock.equals("no")) {
                        Fmenu.findItem(R.id.action_Close).setTitle("اغلاق الانتساب للمجموعه");
                    } else {
                        Fmenu.findItem(R.id.action_Close).setTitle("فتح الانتساب للمجموعه");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Asc) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupMainActivity.this);
            builder.setTitle("تحذير");
            builder.setIcon(R.drawable.ic_report_problem);
            builder.setMessage("سوف تقوم بتحديث الاجزاء لكل الاعضاء !!!");
            builder.setCancelable(true);
            builder.setPositiveButton("انا موافق",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AscUpdate();
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
        else if (id == R.id.action_Desc) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupMainActivity.this);
            builder.setTitle("تحذير");
            builder.setIcon(R.drawable.ic_report_problem);
            builder.setMessage("سوف تقوم بتحديث الاجزاء لكل الاعضاء !!!");
            builder.setCancelable(true);
            builder.setPositiveButton("انا موافق",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DescUpdate();
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
        else if (id == R.id.action_Close) {
            if(item.getTitle().equals("اغلاق الانتساب للمجموعه")) {
                item.setTitle("فتح الانتساب للمجموعه");
                HashMap<String, Object> productMap = new HashMap<>();
                productMap.put("locked", "yes");

                GroupRef.child(groupNum).updateChildren(productMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(GroupMainActivity.this, "تم اغلاق المجموعه", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(GroupMainActivity.this, "لم يتم الحفظ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                item.setTitle("اغلاق الانتساب للمجموعه");
                HashMap<String, Object> productMap = new HashMap<>();
                productMap.put("locked", "no");

                GroupRef.child(groupNum).updateChildren(productMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(GroupMainActivity.this, "تم فتح الانتساب للمجموعه", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(GroupMainActivity.this, "لم يتم الحفظ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }
        else if (id == R.id.action_Delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupMainActivity.this);
            builder.setTitle("تحذير");
            builder.setIcon(R.drawable.ic_report_problem);
            builder.setMessage("سوف تقوم بحذف المجموعة !!!");
            builder.setCancelable(true);
            builder.setPositiveButton("انا موافق",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //delete group and user group
                            GroupRef.child(groupNum).removeValue();
                            RootRef.orderByChild("groupNum").equalTo(groupNum).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postsnapshot :dataSnapshot.getChildren()) {
                                        postsnapshot.getRef().removeValue();
                                    }
                                    Intent intent=new Intent(GroupMainActivity.this,UsersGroupActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
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

    private void AscUpdate() {
        RootRef.orderByChild("groupNum").equalTo(groupNum).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<UsersGroups> usersGroups = new ArrayList<>();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    UsersGroups ug = areaSnapshot.getValue(UsersGroups.class);
                    usersGroups.add(ug);
                }

                for (int i = 0; i < usersGroups.size(); i++) {
                    int oldPart =Integer.parseInt(usersGroups.get(i).getPartNum());
                    int newPart;
                    if(oldPart==30)
                        newPart=1;
                    else
                        newPart=oldPart+1;
                    usersGroups.get(i).setPartNum(String.valueOf(newPart));
                }

                for (int i = 0; i < usersGroups.size(); i++) {

                    final int finalI = i;
                    RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            HashMap<String,Object> groupdataMap = new HashMap<>();
                            groupdataMap.put("id",usersGroups.get(finalI).getId());
                            groupdataMap.put("groupNum",usersGroups.get(finalI).getGroupNum());
                            groupdataMap.put("groupName",usersGroups.get(finalI).getGroupName());
                            groupdataMap.put("partNum",usersGroups.get(finalI).getPartNum());
                            groupdataMap.put("done","no");
                            groupdataMap.put("userName",usersGroups.get(finalI).getUserName());
                            groupdataMap.put("userPhone",usersGroups.get(finalI).getUserPhone());
                            groupdataMap.put("admin",usersGroups.get(finalI).getAdmin());
                            groupdataMap.put("groupNumPhone",usersGroups.get(finalI).getGroupNum()+"_"+usersGroups.get(finalI).getUserPhone());

                            RootRef.child(usersGroups.get(finalI).getId())
                                    .updateChildren(groupdataMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(GroupMainActivity.this, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(GroupMainActivity.this, "للاسف لم يتم التحديث !!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void DescUpdate() {
        RootRef.orderByChild("groupNum").equalTo(groupNum).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<UsersGroups> usersGroups = new ArrayList<>();
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    UsersGroups ug = areaSnapshot.getValue(UsersGroups.class);
                    usersGroups.add(ug);
                }

                for (int i = 0; i < usersGroups.size(); i++) {
                    int oldPart =Integer.parseInt(usersGroups.get(i).getPartNum());
                    int newPart;
                    if(oldPart==1)
                        newPart=30;
                    else
                        newPart=oldPart-1;
                    usersGroups.get(i).setPartNum(String.valueOf(newPart));
                }

                for (int i = 0; i < usersGroups.size(); i++) {

                    final int finalI = i;
                    RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            HashMap<String,Object> groupdataMap = new HashMap<>();
                            groupdataMap.put("id",usersGroups.get(finalI).getId());
                            groupdataMap.put("groupNum",usersGroups.get(finalI).getGroupNum());
                            groupdataMap.put("groupName",usersGroups.get(finalI).getGroupName());
                            groupdataMap.put("partNum",usersGroups.get(finalI).getPartNum());
                            groupdataMap.put("done","no");
                            groupdataMap.put("userName",usersGroups.get(finalI).getUserName());
                            groupdataMap.put("userPhone",usersGroups.get(finalI).getUserPhone());
                            groupdataMap.put("admin",usersGroups.get(finalI).getAdmin());
                            groupdataMap.put("groupNumPhone",usersGroups.get(finalI).getGroupNum()+"_"+usersGroups.get(finalI).getUserPhone());

                            RootRef.child(usersGroups.get(finalI).getId())
                                    .updateChildren(groupdataMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(GroupMainActivity.this, "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(GroupMainActivity.this, "للاسف لم يتم التحديث !!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

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


