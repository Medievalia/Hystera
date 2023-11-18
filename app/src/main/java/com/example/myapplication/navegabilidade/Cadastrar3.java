package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class Cadastrar3 extends AppCompatActivity {

    private CircularSeekBar progressoCiclo;
    private boolean longoMensagemMostrada = false;
    private boolean curtoMensagemMostrada = false;

    int diasDoCiclo = 0;

    String usuarioId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_3);
        ImageButton voltar = findViewById(R.id.back_button);

        CircularSeekBar seekBar = findViewById(R.id.seekbar);
        TextView diasTextView = findViewById(R.id.Dias);

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                int dias = (int) ((progress / 100) * 50);
                diasDoCiclo = dias;

                if (dias >= 45 && !longoMensagemMostrada) {
                    Toast.makeText(Cadastrar3.this, "Ciclo muito longo, é importante consultar um especialista", Toast.LENGTH_LONG).show();
                    longoMensagemMostrada = true;
                }

                else if (dias < 14 && !curtoMensagemMostrada) {
                    Toast.makeText(Cadastrar3.this, "Ciclo muito curto, é importante consultar um especialista", Toast.LENGTH_LONG).show();
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
                // Define o texto como 28 dias quando "Não sei" é clicado
                diasTextView.setText("28 dias");
            }
        });

        AppCompatButton seguinte =  findViewById(R.id.btn_seguinte);
        seguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Salvar os dados na Firestore ao passar para a próxima tela
                int dias = diasDoCiclo;
                salvarDadosCiclo(dias);
            }
        });
    }

    // Método para salvar os dados do ciclo na Firestore
    private void salvarDadosCiclo(int dias) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dadosParaAtualizar = new HashMap<>();

        dadosParaAtualizar.put("diasCiclo", dias);
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        // Atualizar o documento com o novo campo usando merge para preservar os dados existentes
        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Ação de sucesso ao salvar os dados
                        Log.d("TAG", "Dias do ciclo salvos com sucesso!");
                        Intent intent = new Intent(Cadastrar3.this, Cadastrar4.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Lidar com falhas ao salvar os dados
                        Log.e("TAG", "Erro ao salvar dias do ciclo.", e);
                    }
                });
    }

    public void onImageButtonClick(View view) {
        // Este método é chamado quando o ImageButton é clicado
        Intent intent = new Intent(Cadastrar3.this, Cadastrar2.class);
        startActivity(intent);
    }
}

