package com.saggafarsyad.catinthehat;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inflate views
        Button button1 = (Button) findViewById(R.id.button_1);
        Button button2 = (Button) findViewById(R.id.button_2);
        Button button3 = (Button) findViewById(R.id.button_3);
        Button button4 = (Button) findViewById(R.id.button_4);
        Button button5 = (Button) findViewById(R.id.button_5);
        Button button6 = (Button) findViewById(R.id.button_6);
        Button button7 = (Button) findViewById(R.id.button_7);

        // Set click listener
        button1.setOnClickListener(buttonAction);
        button2.setOnClickListener(buttonAction);
        button3.setOnClickListener(buttonAction);
        button4.setOnClickListener(buttonAction);
        button5.setOnClickListener(buttonAction);
        button6.setOnClickListener(buttonAction);
        button7.setOnClickListener(buttonAction);
    }

    // Generic click listener
    private View.OnClickListener buttonAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String toastMessage = "Button \"" + ((Button) v).getText().toString() + "\" is clicked!";
            Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        }
    };
}
