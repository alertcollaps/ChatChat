package com.example.chatchat;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.client.ClientLoader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private static Button button_send;
    private static EditText user_chat;
    private static EditText send_mess;
    private static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_send = findViewById(R.id.button_send);
        user_chat = findViewById(R.id.user_chat);
        send_mess = findViewById(R.id.send_mess);
        mainActivity = MainActivity.this;

        ClientLoader.setmA(mainActivity);

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);



        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (send_mess.getText().toString().trim().equals("")) {
                    poppupWindow(R.string.send_empty_message_error);
                    return;
                }

                String error = ClientLoader.readChat(send_mess.getText().toString());
                if (!error.equals("")){
                    poppupWindow(R.string.connection_closed);
                }
            }
        });


    }
    public void setText(String text){
        user_chat.append(text);
    }
    public static void poppupWindow(int message){
        Toast.makeText(mainActivity, message, Toast.LENGTH_SHORT).show();
    }

}