package com.example.chatchat;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
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
    private static ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_send = findViewById(R.id.button_send);
        user_chat = findViewById(R.id.user_chat);
        send_mess = findViewById(R.id.send_mess);
        scrollView = findViewById(R.id.SCROLLER_ID);
        mainActivity = MainActivity.this;

        user_chat.setMovementMethod(new ScrollingMovementMethod());
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
                send_mess.setText("");


            }
        });
        user_chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int a = scrollView.getScrollY();
                int c = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                if (a == c){
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
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