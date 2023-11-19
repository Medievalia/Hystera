package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Informacao extends AppCompatActivity {

    String link1 = "pudim.com.br";
    String link2 = "pudim.com.br";
    String link3 = "pudim.com.br";
    String link4 = "pudim.com.br";
    String link5 = "pudim.com.br";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacao);

        Button coletor;
        Button monitorar;
        Button reutilizavel;
        Button pilula;
        Button endrometriose;

        coletor = findViewById(R.id.coletor);
        coletor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link1));

                // Verificar se existe um aplicativo para abrir o link antes de iniciar o Intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Caso não haja nenhum aplicativo para abrir o link, você pode exibir uma mensagem ou tratar de outra maneira
                    Toast.makeText(getApplicationContext(), "Nenhum aplicativo disponível para abrir o link.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        monitorar = findViewById(R.id.monitorar);
        monitorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link2));

                // Verificar se existe um aplicativo para abrir o link antes de iniciar o Intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Caso não haja nenhum aplicativo para abrir o link, você pode exibir uma mensagem ou tratar de outra maneira
                    Toast.makeText(getApplicationContext(), "Nenhum aplicativo disponível para abrir o link.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reutilizavel = findViewById(R.id.reutilizavel);
        reutilizavel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link3));

                // Verificar se existe um aplicativo para abrir o link antes de iniciar o Intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Caso não haja nenhum aplicativo para abrir o link, você pode exibir uma mensagem ou tratar de outra maneira
                    Toast.makeText(getApplicationContext(), "Nenhum aplicativo disponível para abrir o link.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pilula = findViewById(R.id.pilula);
        pilula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link4));

                // Verificar se existe um aplicativo para abrir o link antes de iniciar o Intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Caso não haja nenhum aplicativo para abrir o link, você pode exibir uma mensagem ou tratar de outra maneira
                    Toast.makeText(getApplicationContext(), "Nenhum aplicativo disponível para abrir o link.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        endrometriose = findViewById(R.id.endrometriose);
        endrometriose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link5));

                // Verificar se existe um aplicativo para abrir o link antes de iniciar o Intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Caso não haja nenhum aplicativo para abrir o link, você pode exibir uma mensagem ou tratar de outra maneira
                    Toast.makeText(getApplicationContext(), "Nenhum aplicativo disponível para abrir o link.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
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
