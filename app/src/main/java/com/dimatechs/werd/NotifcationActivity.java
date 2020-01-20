package com.dimatechs.werd;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dimatechs.werd.Model.Users;
import com.dimatechs.werd.notifications.APIService;
import com.dimatechs.werd.notifications.Client;
import com.dimatechs.werd.notifications.Data;
import com.dimatechs.werd.notifications.Response;
import com.dimatechs.werd.notifications.Sender;
import com.dimatechs.werd.notifications.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class NotifcationActivity extends AppCompatActivity {

    EditText messageEt;
    Button sendBtn;

    APIService apiService;
    boolean notify = false;
    String myUid="0523856567",hisUid="2017";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifcation);

        messageEt=(EditText)findViewById(R.id.messageEt);
        sendBtn=(Button) findViewById(R.id.sendBtn);

        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    notify = true;
                    String message =messageEt.getText().toString().trim();
                    if(TextUtils.isEmpty(message))
                        Toast.makeText(NotifcationActivity.this, "empty message", Toast.LENGTH_SHORT).show();
                    else
                        sendMessage(message);
            }
        });
    }

    private void sendMessage(final String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(hashMap);

        messageEt.setText("");

       final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
       database.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Users user = dataSnapshot.getValue(Users.class);
               if(notify) {
                   sendNotification(hisUid, user.getName(), message);
               }
               notify=false;


           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void sendNotification(final String hisUid,final String name, final String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query =allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Token token =ds.getValue(Token.class);
                    Data data = new Data(myUid,name+":"+message,"New Message",hisUid,R.drawable.ic_person_green);

                    Sender sender =new Sender(data,token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(NotifcationActivity.this, " nnn  ", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
