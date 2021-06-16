package com.example.qctestingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Token extends AppCompatActivity {
    EditText token;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        setupVariables();
    }

    private void setupVariables() {
        token = (EditText) findViewById(R.id.token_number);
        //TODO When done/enter key on android keyboard pressed login method called
        token.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            login();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        btn = (Button) findViewById(R.id.submit_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    public void login() {
        String tk=token.getText().toString();
        MyDbHelper myDbHelper=new MyDbHelper(Token.this,MyDbHelper.DB_NAME,null,1);
        String empname=myDbHelper.getEmployee(tk);
        if (!empname.equals("")) {
            Log.e("emp is",empname);
            SharedPreferences preferences=getSharedPreferences("userpref",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("user",empname);
            editor.apply();
            Intent i = new Intent(getApplicationContext(), Main_page.class);
            startActivity(i);
            Toast.makeText(Token.this, "Successfully login", Toast.LENGTH_SHORT).show();
        } else {
            ServerJson serverJson=new ServerJson(this);
            serverJson.getEmpInfo(token.getText().toString());
        }
    }
}