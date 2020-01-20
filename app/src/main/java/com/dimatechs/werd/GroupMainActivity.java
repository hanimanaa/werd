package com.dimatechs.werd;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Model.UsersGroups;
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
    private String groupNum,groupname,IsAdmin,stDialogDone="no",stDialogAdmin="no";
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
        groupname=getIntent().getStringExtra("groupName");
        IsAdmin=getIntent().getStringExtra("IsAdmin");




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
                                                                        MakeToast( "تحذير","يجب ان يكون مدير واحد للمجموعه على الاقل !!!",R.drawable.error1);
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
                                            else{
                                                Toast.makeText(GroupMainActivity.this, "غير متاح", Toast.LENGTH_SHORT).show();
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
                                    MakeToast("الانتساب للمجموعه","تم اغلاق الانتساب للمجموعه",R.drawable.ok);
                                } else {
                                    MakeToast("حفظ","لم يتم الحفظ",R.drawable.error1);
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
                                    MakeToast("الانتساب للمجموعه","تم فتح الانتساب للمجموعه",R.drawable.ok);
                                } else {
                                    MakeToast("حفظ","لم يتم الحفظ",R.drawable.error1);
                                }
                            }
                        });
            }

        }
        else if (id == R.id.action_Delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupMainActivity.this);
            builder.setTitle("تحذير");
            builder.setIcon(R.drawable.ic_report_problem);
            builder.setMessage("سوف تقوم بحذف المجموعة. لن يكون هنالك امكانية لاستعادة البيانات !!!");
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
        else if (id == R.id.action_Update_Group){
            Intent intent=new Intent(GroupMainActivity.this,AddGroupAdminActivity.class);
            intent.putExtra("groupNum",groupNum);
            intent.putExtra("groupName",groupname);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_Notification){
            Intent intent=new Intent(GroupMainActivity.this, NotifcationActivity.class);
            startActivity(intent);
            return true;
        }

        else if (id == R.id.action_Exit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupMainActivity.this);
            builder.setTitle("تحذير");
            builder.setIcon(R.drawable.ic_report_problem);
            builder.setMessage("سوف تقوم بالخروج من البرنامج !!!");
            builder.setCancelable(true);
            builder.setPositiveButton("انا موافق",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(GroupMainActivity.this,LoginActivity.class);
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
                            MakeToast("حفظ","تم الحفظ بنجاح",R.drawable.ok);
                        }
                        else
                        {
                            dialog.dismiss();
                            MakeToast("حفظ","للاسف لم يتم الحفظ !!!",R.drawable.error1);
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
                    if(oldPart!=0) {
                        if (oldPart == 30)
                            newPart = 1;
                        else
                            newPart = oldPart + 1;
                    }else{
                        newPart = oldPart;
                    }
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
                                                MakeToast("تحديث","تم التحديث بنجاح",R.drawable.ok);
                                            }
                                            else
                                            {
                                                MakeToast("تحديث","للاسف لم يتم التحديث !!!",R.drawable.error1);
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
                    if(oldPart!=0) {
                        if (oldPart == 1)
                            newPart = 30;
                        else
                            newPart = oldPart - 1;
                    }else{
                        newPart = oldPart;
                    }
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
                                                MakeToast("تحديث","تم التحديث بنجاح",R.drawable.ok);
                                            }
                                            else
                                            {
                                                MakeToast("تحديث","للاسف لم يتم التحديث !!!",R.drawable.error1);

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
}






