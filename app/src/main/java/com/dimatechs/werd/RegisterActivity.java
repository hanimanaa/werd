package com.dimatechs.werd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountBtn;
    private EditText Edname,Edphone,Edpassword;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountBtn=(Button)findViewById(R.id.Add_btn);
        Edname=(EditText)findViewById(R.id.Edname);
        Edphone=(EditText)findViewById(R.id.Edphone);
        Edpassword=(EditText)findViewById(R.id.Edpassword);


        loadingBar=new ProgressDialog(this);

        CreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {
        String name =Edname.getText().toString();
        String phone =Edphone.getText().toString();
        String password =Edpassword.getText().toString();



        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "ادخل الاسم اذا سمحت . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "ادخل رقم الهاتف اذا سمحت . . .", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "ادخل كلمة السر . . .", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("انشاء حساب");
            loadingBar.setMessage("انتظر");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(name,phone,password);

        }



    }

    private void ValidatePhoneNumber(final String name,final String phone,final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.child("Users").child(phone).exists())
                {
                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("name",name);
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        //Toast.makeText(RegisterActivity.this, "تمت الاضافه بنجاح", Toast.LENGTH_SHORT).show();
                                        MakeToast("اضافة مستخدم جديد","تمت الاضافه بنجاح",R.drawable.ok);

                                        loadingBar.dismiss();
                                        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                       // Toast.makeText(RegisterActivity.this, "خطا في الشبكة", Toast.LENGTH_SHORT).show();
                                        MakeToast("تحديث","خطا في الشبكة",R.drawable.error1);

                                        loadingBar.dismiss();

                                    }
                                }
                            });
                }
                else
                {
                    //Toast.makeText(RegisterActivity.this, "المستخدم مسجل", Toast.LENGTH_SHORT).show();
                    MakeToast("خطا","للاسف المستخدم مسجل !!!",R.drawable.error1);
                    Edphone.setFocusable(true);

                    loadingBar.dismiss();
                   // Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                 //   startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

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
