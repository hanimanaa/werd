package com.dimatechs.werd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FirstLogActivity extends AppCompatActivity {

    private Button FirstLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_log);

        FirstLogBtn = (Button) findViewById(R.id.firstLog_btn);
        FirstLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FirstLogActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
