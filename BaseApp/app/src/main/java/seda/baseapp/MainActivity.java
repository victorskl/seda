package seda.baseapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import seda.baseapp.todo.ToDoActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("SEDA Base App", "Initialized...");

        Button sedaBtn = findViewById(R.id.sedaBtn);
        sedaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ToDoActivity.class);
                Log.i("SEDA Base App", "Go to ToDo");
                startActivity(intent);
            }
        });
    }
}
