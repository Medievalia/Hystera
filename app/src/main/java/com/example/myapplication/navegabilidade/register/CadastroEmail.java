package com.example.myapplication.navegabilidade.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.R;
import com.example.myapplication.navegabilidade.main.MainActivity;

public class CadastroEmail extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_5);
        ImageButton voltar = findViewById(R.id.back_button);

        AppCompatButton signInEmail =  findViewById(R.id.signInEmail);
        signInEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroEmail.this, CriarConta.class);
                startActivity(intent);
            }
        });
    }
    public void onImageButtonClick(View view) {
        // Este método é chamado quando o ImageButton é clicado
        Intent intent = new Intent(CadastroEmail.this, MainActivity.class);
        startActivity(intent);
    }
}
