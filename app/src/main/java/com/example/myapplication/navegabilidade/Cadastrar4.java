package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import android.support.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class Cadastrar4 extends AppCompatActivity {

    private CircularSeekBar progressoCiclo;
    private boolean longoMensagemMostrada = false;
    private boolean curtoMensagemMostrada = false;
    int diasDoCiclo = 0;
    String usuarioId;
    private int diasMenstruais = 5;
    private Button diasMenstruaisButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_3);
        ImageButton voltar = findViewById(R.id.back_button);

        CircularSeekBar seekBar = findViewById(R.id.seekbar);
        TextView diasTextView = findViewById(R.id.Dias);

        diasMenstruaisButton = findViewById(R.id.diasmens_button);
        diasMenstruaisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exibir mensagem com o número de dias menstruais
                String mensagem = "Dias menstruais: " + diasMenstruais;
                Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                int dias = (int) ((progress / 100) * 50);
                diasDoCiclo = dias;

                if (dias >= 45 && !longoMensagemMostrada) {
                    Toast.makeText(Cadastrar4.this, "Ciclo muito longo, é importante consultar um especialista", Toast.LENGTH_LONG).show();
                    longoMensagemMostrada = true;
                } else if (dias < 14 && !curtoMensagemMostrada) {
                    Toast.makeText(Cadastrar4.this, "Ciclo muito curto, é importante consultar um especialista", Toast.LENGTH_LONG).show();
                    curtoMensagemMostrada = true;
                } else {
                    diasTextView.setText(dias + " dias");
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });

        AppCompatButton naoSeiButton = findViewById(R.id.nao_sei);
        naoSeiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diasTextView.setText("28 dias");
            }
        });

        AppCompatButton seguinte = findViewById(R.id.btn_seguinte);
        seguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int diasCiclo = diasDoCiclo;
                int diasSangramento = diasMenstruais;
                salvarDadosCiclo(diasCiclo, diasSangramento);
            }
        });
    }

    private void salvarDadosCiclo(int diasCiclo, int diasSangramento) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dadosParaAtualizar = new HashMap<>();

        dadosParaAtualizar.put("diasCiclo", diasCiclo);
        dadosParaAtualizar.put("diasSangramento", diasSangramento);

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Dados salvos com sucesso!");
                        Intent intent = new Intent(Cadastrar4.this, LinhaDoTempo.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Erro ao salvar os dados.", e);
                    }
                });
    }

    public void onImageButtonClick(View view) {
        Intent intent = new Intent(Cadastrar4.this, Cadastrar3.class);
        startActivity(intent);
    }

    public void aumentarDiasMenstruais(View view) {
        if (diasMenstruais < 10) {
            diasMenstruais++;
            // Atualizar o texto no Button
            diasMenstruaisButton.setText(diasMenstruais + " dias");
        }
    }

    public void diminuirDiasMenstruais(View view) {
        if (diasMenstruais > 1) {
            diasMenstruais--;
            // Atualizar o texto no Button
            diasMenstruaisButton.setText(diasMenstruais + " dias");
        }
    }
}
