package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import br.edu.fatecsaocaetano.hystera.R;

public class CycleInformation extends AppCompatActivity  {

    private MaterialButton voltarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cycle_information);

        voltarButton = findViewById(R.id.back_button);
        voltarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
