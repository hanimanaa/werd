package com.dimatechs.werd;

import android.content.Intent;
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
import android.widget.Toast;

import com.dimatechs.werd.Model.Groups;
import com.dimatechs.werd.Model.Users;
import com.dimatechs.werd.Model.UsersGroups;
import com.dimatechs.werd.Prevalent.Prevalent;
import com.dimatechs.werd.ViewHolder.WerdViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button RegisterAccountBtn;
    private RecyclerView recyclerView;
    private DatabaseReference RootRef,UserRef;
    private String groupNum;
    private String phone;


    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("hani", "onCreate");

        //Paper.init(this);
        //groupNum = Paper.book().read("groupnum");
        groupNum=getIntent().getStringExtra("groupNum");
        Toast.makeText(this, groupNum, Toast.LENGTH_SHORT).show();


        recyclerView=findViewById(R.id.recycler_menu);
      //  recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("hani", "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("yzan", "start");


        RootRef = FirebaseDatabase.getInstance().getReference().child("UsersGroups");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Query query =RootRef.orderByChild("groupNum").equalTo(groupNum);


        FirebaseRecyclerOptions<UsersGroups> options =
                            new FirebaseRecyclerOptions.Builder<UsersGroups>()
                                    .setQuery(query,UsersGroups.class)
                                    .build();



                    FirebaseRecyclerAdapter<UsersGroups, WerdViewHolder> adapter =
                            new FirebaseRecyclerAdapter<UsersGroups, WerdViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull WerdViewHolder holder, int position, @NonNull final UsersGroups model) {

                                    Log.d("yzan", "onBindViewHolder");

                                    holder.txtName.setText(model.getUserName());
                                  //  holder.txtName.setText(UserRef.child(model.getUserPhone()).child("name").getKey().);

                                    holder.txtPhone.setText(model.getUserPhone());
                                    holder.txtNum.setText(" جزء رقم : " + model.getPartNum());

                                    if(model.getDone().equals("done")) {
                                            holder.imageView.setImageResource(R.drawable.ic_person_green);
                                          }


                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Toast.makeText(MainActivity.this, "غير متوفر حاليا", Toast.LENGTH_SHORT).show();
                                            // Intent intent = new Intent(HomeActivity.this,ProductDetailsActivity.class);
                                            //   intent.putExtra("pid",model.getPid());
                                            //    startActivity(intent);
                                            //test
                                        }
                                    });
                                }

                                @Override
                                public WerdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    Log.d("hani", "onCreateViewHolder");
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
        Log.d("hani", "onOptionsItemSelected");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Register) {
            Toast.makeText(this, "you selected הרשמה", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_Update) {
            Toast.makeText(this, "you selected עדכון קבטצה", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),AddGroupsToUserActivity.class);
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


}


