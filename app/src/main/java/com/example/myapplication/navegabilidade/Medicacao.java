package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Medicacao extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicacao);
        // Inicializa o FAB
        FloatingActionButton fab_add_medicamento = findViewById(R.id.fab_add_medicamento); //  ID

        // Adiciona o listener de clique
        fab_add_medicamento.setOnClickListener(v -> {
            Intent intent = new Intent(Medicacao.this, AddMedicamentoActivity.class); // Use Medicacao.this
            startActivity(intent);
        });
    }
}
