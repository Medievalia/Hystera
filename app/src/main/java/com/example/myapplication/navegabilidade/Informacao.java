package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Informacao extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacao);






        ImageButton anota = findViewById(R.id.anota);
        anota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Informacao.this, Anotacoes.class);
                startActivity(intent);
            }
        });

        ImageButton calendario = findViewById(R.id.calendario);
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Informacao.this, Calendario.class);
                startActivity(intent);
            }
        });

        ImageButton menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Informacao.this, Notificacoes.class);
                startActivity(intent);
            }
        });

        ImageButton seekbar = findViewById(R.id.seekbar);
        seekbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Informacao.this, LinhaDoTempo.class);
                startActivity(intent);
            }
        });
    }
}
