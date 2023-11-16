package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.R;

public class Cadastrar4 extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_4);
        ImageButton voltar = findViewById(R.id.back_button);

//        AppCompatButton seguinte =  findViewById(R.id.btn_seguinte);
//        seguinte.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Cadastrar4.this, Cadastrar5.class);
//                startActivity(intent);
//            }
//        });
    }
    public void onImageButtonClick(View view) {
        // Este método é chamado quando o ImageButton é clicado
        Intent intent = new Intent(Cadastrar4.this, Cadastrar3.class);
        startActivity(intent);
    }
}
