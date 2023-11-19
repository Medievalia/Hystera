package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Notificacoes extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);

        ImageButton anota = findViewById(R.id.anota);
        anota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notificacoes.this, Anotacoes.class);
                startActivity(intent);
            }
        });

        ImageButton calendario = findViewById(R.id.calendario);
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notificacoes.this, Calendario.class);
                startActivity(intent);
            }
        });

        ImageButton utero = findViewById(R.id.utero);
        utero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notificacoes.this, Informacao.class);
                startActivity(intent);
            }
        });



        ImageButton menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notificacoes.this, Notificacoes.class);
                startActivity(intent);
            }
        });

        ImageButton seekbar1 = findViewById(R.id.seekbar);
        seekbar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notificacoes.this, LinhaDoTempo.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
