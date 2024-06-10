package com.example.myapplication.navegabilidade.Ajuda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Ajuda1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda1);

        ImageButton back_button =  findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ajuda1.this, Ajuda.class);
                startActivity(intent);
            }
        });
    }

    public void cima1(View view) {
        Intent intent = new Intent(Ajuda1.this, Ajuda.class);
        startActivity(intent);
    }
}
