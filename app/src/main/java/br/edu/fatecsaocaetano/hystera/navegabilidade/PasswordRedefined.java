package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;

public class PasswordRedefined extends AppCompatActivity {
    private Button btn_seguinte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_senha2);

        iniciarComponentes();

        btn_seguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordRedefined.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void iniciarComponentes() {
        btn_seguinte = findViewById(R.id.btn_seguinte);
    }
}
