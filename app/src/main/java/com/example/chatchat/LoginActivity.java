package com.example.chatchat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.client.ClientLoader;
import com.example.client.packet.OPacket;
import com.example.client.packet.PacketManager;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private static Button button_login;
    private static EditText text_name;
    public static boolean connectSuccess = false;
    public static boolean keySuccess = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button_login = findViewById(R.id.login_btn);
        text_name = findViewById(R.id.text_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text_name.getText().toString().trim().equals("")){
                    Toast.makeText(LoginActivity.this, R.string.send_empty_message_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                ClientLoader.setNickname(text_name.getText().toString().trim());
                if (!connectSuccess) {
                    ClientLoader client = new ClientLoader();
                    client.start();
                    connectCheck();
                }
                keyCheck();
            }
        });

    }
    private void connectCheck(){
        Thread connectCheck = new Thread(){
            @Override
            public void run(){
                int i = 0;
                while (!connectSuccess){
                    i++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if (i >= 9){
                        break;
                    }
                }
                if (!connectSuccess) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, R.string.connection_not_established, Toast.LENGTH_SHORT).show();
                        }
                        });
                    //Toast.makeText(LoginActivity.this, R.string.connection_not_established, Toast.LENGTH_SHORT).show();
                    return;
                }
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, R.string.connection_established, Toast.LENGTH_SHORT).show();
                    }
                });
                //Toast.makeText(LoginActivity.this, R.string.connection_established, Toast.LENGTH_SHORT).show();
            }
        };
        connectCheck.start();
    }

    private void keyCheck(){
        Thread keyCheck = new Thread(){
            @Override
            public void run(){
                int i = 0;
                while (!keySuccess){
                    i++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if (i >= 9){
                        break;
                    }
                }
                if (keySuccess){
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, R.string.session_key_get, Toast.LENGTH_SHORT).show();
                            ClientLoader.handle();
                            finish();
                        }
                    });
                }
            }
        };
        keyCheck.start();
    }
}
