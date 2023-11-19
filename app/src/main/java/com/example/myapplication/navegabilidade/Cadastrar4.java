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
import com.google.firebase.firestore.DocumentSnapshot;
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
    int diasCiclo;
    String usuarioId;
    int diasSangramento;
    private Button diasMenstruaisButton;
    private TextView diasTextView;
    private CircularSeekBar seekBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_3);
        ImageButton voltar = findViewById(R.id.back_button);

        seekBar = findViewById(R.id.seekbar);
        diasTextView = findViewById(R.id.Dias);
        diasMenstruaisButton = findViewById(R.id.diasmens_button);
        diasMenstruaisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exibir mensagem com o número de dias menstruais
                String mensagem = "Dias menstruais: " + diasSangramento;
                Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                int dias = (int) ((progress / 100) * 50);
                diasCiclo = dias;

                if (dias >= 45 && !longoMensagemMostrada) {
                    Toast.makeText(Cadastrar4.this, "Ciclo muito longo, é importante consultar um especialista", Toast.LENGTH_LONG).show();
                    longoMensagemMostrada = true;
                    diasTextView.setText(dias + " dias");
                } else if (dias < 14 && !curtoMensagemMostrada) {
                    Toast.makeText(Cadastrar4.this, "Ciclo muito curto, é importante consultar um especialista", Toast.LENGTH_LONG).show();
                    curtoMensagemMostrada = true;
                    diasTextView.setText(dias + " dias");
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

                String textoDiasCiclo = diasTextView.getText().toString();
                String textoDiasMenstruais = diasMenstruaisButton.getText().toString();

                int diasDoCiclo = extrairApenasDigitos(textoDiasCiclo);
                int diasMenstruais = extrairApenasDigitos(textoDiasMenstruais);

                salvarDadosCiclo(diasDoCiclo, diasMenstruais);
            }
        });
    }

    private void salvarDadosCiclo(int diasDoCiclo, int diasMenstruais) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put("diasSangramento", diasMenstruais);
        dadosParaAtualizar.put("diasCiclo", diasDoCiclo);

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Dias do ciclo atualizados com sucesso!");
                        Intent intent = new Intent(Cadastrar4.this, LinhaDoTempo.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Erro ao atualizar os dias.", e);
                    }
                });
    }

    public void onImageButtonClick(View view) {
        Intent intent = new Intent(Cadastrar4.this, Cadastrar3.class);
        startActivity(intent);
    }

    public void aumentarDiasMenstruais(View view) {
        if (diasSangramento < 10) {
            diasSangramento++;
            // Atualizar o texto no TextView
            diasMenstruaisButton.setText(diasSangramento + " dias");
        }
    }

    public void diminuirDiasMenstruais(View view) {
        if (diasSangramento > 1) {
            diasSangramento--;
            // Atualizar o texto no TextView
            diasMenstruaisButton.setText(diasSangramento + " dias");
        }
    }

    private int extrairApenasDigitos(String texto) {
        String apenasDigitos = texto.replaceAll("[^0-9]", "");
        return Integer.parseInt(apenasDigitos);
    }
}
