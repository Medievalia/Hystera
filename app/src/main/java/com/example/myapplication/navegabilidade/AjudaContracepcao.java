package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class AjudaContracepcao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda3);



        ImageButton back_button =  findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjudaContracepcao.this, AjudaDuvidas.class);
                startActivity(intent);
            }
        });
    }

    public void cima1(View view) {
        Intent intent = new Intent(AjudaContracepcao.this, AjudaDuvidas.class);
        startActivity(intent);
    }
}
