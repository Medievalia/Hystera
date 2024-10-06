package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Esqueceu2 extends AppCompatActivity {
    private Button btn_seguinte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_senha2);

        IniciarComponentes();

        btn_seguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Esqueceu2.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void IniciarComponentes() {
        btn_seguinte = findViewById(R.id.btn_seguinte);
    }
}
