package com.dimatechs.werd;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Model.Groups;
import com.dimatechs.werd.Model.Messages;
import com.dimatechs.werd.Model.UsersGroups;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.dimatechs.werd.ViewHolder.MessageViewHolder;
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

import java.util.ArrayList;

import io.paperdb.Paper;

public class MessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference RootRef;
    private FloatingActionButton btnSend;
    private ArrayList<UsersGroups> checkedUsers;
    private EditText edmessage;
    private String senderUserID,groupNum,groupname,IsAdmin;
    private DatabaseReference MessagesRef,GroupsRef;
    private RadioButton rbNotSelect, rbSelectAll, rbRead, rbNotRead;
    private String phone;

    FirebaseRecyclerAdapter<Messages, MessageViewHolder> adapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        MessagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        Paper.init(this);
        phone = Prevalent.currentOnlineUser.getPhone();

        recyclerView = findViewById(R.id.recycler_Messages);
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
       // ((LinearLayoutManager) layoutManager).setReverseLayout(true);
       ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {       
            getMenuInflater().inflate(R.menu.message_menu, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all_message) {
            MessagesRef.child(phone).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                MakeToast("حذف", "تم حذف كافة الرسالة !!!  ", R.drawable.error1);
                            }
                        }
                    });
            return true;
        }
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();

        Query query =MessagesRef.child(phone);

        FirebaseRecyclerOptions<Messages> options =
                new FirebaseRecyclerOptions.Builder<Messages>()
                        .setQuery(query, Messages.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Messages, MessageViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final MessageViewHolder holder,final int position, @NonNull final Messages model) {
                        if(model.getFrom().equals(phone))
                        {
                            holder.message_background.setBackgroundColor(Color.parseColor("#f2e2cd"));
                        }
                        else
                        {
                            holder.message_background.setBackgroundColor(Color.parseColor("#ffffff"));
                        }
                        holder.txtName.setText(model.getSenderName());
                        holder.txtTime.setText(model.getDate() + "\n"+model.getTime());
                        holder.txtMessage.setText(model.getBody());
                        holder.txtGroupName.setText(model.getGroupName());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(MessagesActivity.this,messageDetailsActivity.class);
                                intent.putExtra("SenderName",model.getSenderName());
                                intent.putExtra("Date",model.getDate() + "\n"+model.getTime());
                                intent.putExtra("Message",model.getBody());
                                intent.putExtra("GroupName",model.getGroupName());
                                startActivity(intent);
                            } 
                        });

                    }

                    @NonNull
                    @Override
                    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_item_layout, parent, false);
                        MessageViewHolder holder = new MessageViewHolder(view);
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
        //getRef(direction).getKey();
    }
    
}
