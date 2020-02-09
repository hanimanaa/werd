package com.dimatechs.werd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dimatechs.werd.Model.Messages;
import com.dimatechs.werd.Model.UsersGroups;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.dimatechs.werd.ViewHolder.MessageViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    private DatabaseReference MessagesRef;
    private RadioButton rbNotSelect, rbSelectAll, rbRead, rbNotRead;
    private String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        MessagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        Paper.init(this);
        phone = Prevalent.currentOnlineUser.getPhone();

        recyclerView = findViewById(R.id.recycler_Messages);
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }


    @Override
    protected void onStart() {
        super.onStart();

        Query query =MessagesRef.child(phone);

        FirebaseRecyclerOptions<Messages> options =
                new FirebaseRecyclerOptions.Builder<Messages>()
                        .setQuery(query, Messages.class)
                        .build();

        FirebaseRecyclerAdapter<Messages, MessageViewHolder> adapter =
                new FirebaseRecyclerAdapter<Messages, MessageViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final MessageViewHolder holder, int position, @NonNull final Messages model) {
                        holder.txtName.setText(model.getSenderName());
                        holder.txtTime.setText(model.getDate() + "\n"+model.getTime());
                        holder.txtMessage.setText(model.getBody());
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
}
