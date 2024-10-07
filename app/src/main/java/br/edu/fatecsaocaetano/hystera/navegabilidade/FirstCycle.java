package br.edu.fatecsaocaetano.hystera.navegabilidade;

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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import androidx.annotation.NonNull;

import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class FirstCycle extends AppCompatActivity {

    private static final Logger logger = LoggerUtils.configurarLogger(FirstCycle.class.getName());
    private CircularSeekBar progressoCiclo;
    private boolean longoMensagemMostrada = false;
    private boolean curtoMensagemMostrada = false;
    private int duracaoCiclo;
    private String userID;
    private int diasSangramento;
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
                duracaoCiclo = dias;

                if (dias > 35 && !longoMensagemMostrada) {
                    Toast.makeText(FirstCycle.this, "Ciclo muito longo, é importante consultar um especialista", Toast.LENGTH_LONG).show();
                    longoMensagemMostrada = true;
                    diasTextView.setText(dias + " dias");
                } else if (dias < 24 && !curtoMensagemMostrada) {
                    Toast.makeText(FirstCycle.this, "Ciclo muito curto, é importante consultar um especialista", Toast.LENGTH_LONG).show();
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

                // Verificação se foi inserido um valor para os dias menstruais
                if (textoDiasMenstruais.isEmpty()) {
                    Toast.makeText(FirstCycle.this, "Insira um valor para os dias menstruais", Toast.LENGTH_SHORT).show();
                    return;
                }

                int diasMenstruais = extrairApenasDigitos(textoDiasMenstruais);
                salvarDadosCiclo(diasDoCiclo, diasMenstruais);
            }
        });
    }

    private void salvarDadosCiclo(int diasDoCiclo, int diasMenstruais) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put("Bleeding", diasMenstruais);
        dadosParaAtualizar.put("Average", diasDoCiclo);

        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "ID do usuário não encontrado!");
            return;
        }

        DocumentReference documentReference = db.collection("Usuarios").document(userID);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Timestamp DUM = documentSnapshot.getTimestamp("DUM");
                            boolean natural = documentSnapshot.getBoolean("Pill");

                            // Cria o ciclo
                            Cycle ciclo = new Cycle(DUM, diasDoCiclo, natural, diasSangramento);

                            // Salvar o ciclo no Firestore
                            saveCycle(ciclo);
                        } else {
                            Log.d("TAG", "Documento não encontrado");
                            logger.log(Level.SEVERE, "Documento não encontrado! " + userID);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Erro ao buscar a última menstruação.", e);
                        logger.log(Level.SEVERE, "Erro ao buscar a última menstruação! " + userID);
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
                    Log.d("TAG", "Ciclo salvo com sucesso!");
                    logger.log(Level.INFO, "Ciclo salvo com sucesso! " + userID);
                    Intent intent = new Intent(FirstCycle.this, LinhaDoTempo.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> logger.log(Level.INFO, "Erro ao salvar ciclo! " + userID + " " + e));
    }

    public void onImageButtonClick(View view) {
        Intent intent = new Intent(FirstCycle.this, DUM.class);
        startActivity(intent);
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

//    private String calcularProximaMenstruacao(String ultimaMenstruacao, int diasDoCiclo) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//
//        try {
//            // Convertendo a última menstruação para o formato Date
//            Date ultimaData = dateFormat.parse(ultimaMenstruacao);
//
//            // Usando um calendário para calcular a próxima menstruação
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(ultimaData);
//
//            // Adicionando a duração do ciclo à última menstruação
//            calendar.add(Calendar.DAY_OF_MONTH, diasDoCiclo);
//
//            // Convertendo a data calculada de volta para o formato String
//            return dateFormat.format(calendar.getTime());
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return "Erro ao calcular a próxima menstruação";
//        }
//    }

//    private String calcularPenultimaMenstruacao(String ultimaMenstruacao, int diasDoCiclo) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//
//        try {
//            // Convertendo a última menstruação para o formato Date
//            Date ultimaData = dateFormat.parse(ultimaMenstruacao);
//
//            // Usando um calendário para calcular a penúltima menstruação
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(ultimaData);
//
//            // Subtraindo a duração do ciclo da última menstruação
//            calendar.add(Calendar.DAY_OF_MONTH, -diasDoCiclo);
//
//            // Convertendo a data calculada de volta para o formato String
//            return dateFormat.format(calendar.getTime());
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return "Erro ao calcular a penúltima menstruação";
//        }
//    }
}
