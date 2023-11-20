package com.example.myapplication.navegabilidade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Notificacoes extends AppCompatActivity {

    private TextView notifica;

    long diasProxima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacoes);
        proximaMestruacao();

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

    public void proximaMestruacao() {
        diasProxima = getDiasProxima();
        String notificaProxima = "Faltam " + diasProxima + " dias para a próxima menstruação";
        notifica = findViewById(R.id.notifica);
        notifica.setText(notificaProxima);
    }

    public long getDiasProxima() {
        return diasProxima;
    }

    public void setDiasProxima(long diasProxima) {
        this.diasProxima = diasProxima;
    }
}
