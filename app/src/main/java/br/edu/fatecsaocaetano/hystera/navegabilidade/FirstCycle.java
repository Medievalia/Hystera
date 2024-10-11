package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class FirstCycle extends AppCompatActivity {

    private final String tag = "FirstCycleClass";
    private boolean longoMensagem = false;
    private boolean curtoMensagem = false;
    private String userID;
    private boolean natural = true;
    private int diasSangramento = 5;
    private MaterialButton diasMenstruaisButton;
    private MaterialTextView diasTextView;
    private CircularSeekBar seekBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_3);
        seekBar = findViewById(R.id.seekbar);
        float progressoInicial = (28f / 50f) * 100;
        seekBar.setProgress(progressoInicial);
        diasTextView = findViewById(R.id.Dias);
        diasMenstruaisButton = findViewById(R.id.diasmens_button);

        diasMenstruaisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensagem = "Dias menstruais: " + diasSangramento;
                Toast.makeText(getApplicationContext(), mensagem, Toast.LENGTH_SHORT).show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                int dias = (int) ((progress / 100) * 50);
                if (dias > 38 && !longoMensagem) {
                    Toast.makeText(FirstCycle.this, "Ciclo muito longo, é importante consultar um especialista", Toast.LENGTH_LONG).show();
                    longoMensagem = true;
                    diasTextView.setText(dias + " dias");
                } else if (dias < 15 && !curtoMensagem) {
                    Toast.makeText(FirstCycle.this, "Ciclo muito curto, é importante consultar um especialista", Toast.LENGTH_LONG).show();
                    curtoMensagem = true;
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
                String textoDiasCiclo = diasTextView.getText().toString();
                int diasDoCiclo = extrairApenasDigitos(textoDiasCiclo);
                createFirstCycle(diasDoCiclo);
            }
        });

        AppCompatButton seguinte = findViewById(R.id.btn_seguinte);
        seguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoDiasCiclo = diasTextView.getText().toString();
                int diasDoCiclo = extrairApenasDigitos(textoDiasCiclo);
                createFirstCycle(diasDoCiclo);
            }
        });

        AppCompatButton voltar = findViewById(R.id.back_button);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstCycle.this, DUM.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void createFirstCycle(int diasDoCiclo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            Log.e(tag, "ID do usuário não encontrado!");
            return;
        }

        DocumentReference documentReference = db.collection("Usuarios").document(userID);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Timestamp DUM = documentSnapshot.getTimestamp("DUM");
                            String method = documentSnapshot.getString("Method");

                            if(method.equals("PILULA") || method.equals("IMPLANTE_HORMONAL") || method.equals("INJECAO")){
                                natural = false;
                            }
                            Cycle ciclo = new Cycle(DUM, diasDoCiclo, natural, diasSangramento, userID);
                            saveCycle(ciclo);

                        } else {
                            Log.e(tag, "Documento não encontrado! " + userID);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(tag, "Erro ao buscar a última menstruação. ", e);
                    }
                });
    }

    private void saveCycle(Cycle ciclo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String cicloId = ciclo.getId();

        // Referência para o ciclo do usuário
        DocumentReference cycleRef = db.collection("Usuarios")
                .document(userID)
                .collection("Ciclos")
                .document(cicloId);

        // Salvar o ciclo
        cycleRef.set(ciclo)
                .addOnSuccessListener(aVoid -> {
                    Log.i(tag, "Ciclo salvo com sucesso! " + userID);
                    Intent intent = new Intent(FirstCycle.this, TimeLine.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Log.e(tag, "Erro ao salvar ciclo! " + userID + " " + e));
    }

    public void aumentarDiasMenstruais(View view) {
        if (diasSangramento < 10) {
            diasSangramento++;
            diasMenstruaisButton.setText(diasSangramento + " dias");
        }
    }

    public void diminuirDiasMenstruais(View view) {
        if (diasSangramento > 1) {
            diasSangramento--;
            diasMenstruaisButton.setText(diasSangramento + " dias");
        }
    }

    private int extrairApenasDigitos(String texto) {
        String apenasDigitos = texto.replaceAll("[^0-9]", "");
        return Integer.parseInt(apenasDigitos);
    }
}
