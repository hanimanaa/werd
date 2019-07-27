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

import com.dimatechs.werd.Model.Users;
import com.dimatechs.werd.ViewHolder.WerdViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button RegisterAccountBtn;
    private RecyclerView recyclerView;
    private DatabaseReference UsersRef;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("hani", "onCreate");


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


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
        Log.d("hani", "onStart");
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(UsersRef, Users.class)
                        .build();


        FirebaseRecyclerAdapter<Users, WerdViewHolder> adapter =
                new FirebaseRecyclerAdapter<Users, WerdViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull WerdViewHolder holder, int position, @NonNull final Users model) {
                        holder.txtName.setText(model.getName());
                        holder.txtPhone.setText(model.getPhone());
                        holder.txtNum.setText(" جزء رقم : " + model.getNum());


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


