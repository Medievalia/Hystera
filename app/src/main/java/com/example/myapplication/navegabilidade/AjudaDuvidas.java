package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;

public class AjudaDuvidas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda);

        ImageButton baixo1 =  findViewById(R.id.baixo1); // Como alterar a minha senha
        baixo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjudaDuvidas.this, AjudaSenha.class);
                startActivity(intent);
            }
        });

        ImageButton baixo2 =  findViewById(R.id.baixo2); // Dados do ciclo incorretos
        baixo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjudaDuvidas.this, AjudaCiclo.class);
                startActivity(intent);
            }
        });

        ImageButton baixo3 =  findViewById(R.id.baixo3); // Método Contraceptivo
        baixo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjudaDuvidas.this, AjudaContracepcao.class);
                startActivity(intent);
            }
        });

        ImageButton back_button =  findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AjudaDuvidas.this, Perfil.class);
                startActivity(intent);
            }
        });
    }

}
