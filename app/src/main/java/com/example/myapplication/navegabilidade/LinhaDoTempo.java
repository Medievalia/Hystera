package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class LinhaDoTempo extends AppCompatActivity {
    FirebaseFirestore db;
    String usuarioId;
    String proximaMenstruacao;
    String today;
    CircularSeekBar seekBar;
    int diasDoCiclo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linha_tempo);

        seekBar = findViewById(R.id.seekbar);

        db = FirebaseFirestore.getInstance();
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ImageButton btnPerfil = findViewById(R.id.button_perfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinhaDoTempo.this, Perfil.class);
                startActivity(intent);
            }
        });

        ImageButton anota = findViewById(R.id.anota);
        anota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinhaDoTempo.this, Anotacoes.class);
                startActivity(intent);
            }
        });

        ImageButton calendario = findViewById(R.id.calendario);
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinhaDoTempo.this, Calendario.class);
                startActivity(intent);
            }
        });

        ImageButton utero = findViewById(R.id.utero);
        utero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinhaDoTempo.this, Informacao.class);
                startActivity(intent);
            }
        });

        ImageButton menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinhaDoTempo.this, Notificacoes.class);
                startActivity(intent);
            }
        });

        ImageButton inicioMenst = findViewById(R.id.inicioMenst);
        inicioMenst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinhaDoTempo.this, Cadastrar3.class);
                startActivity(intent);
            }
        });

        Button mediaCiclo = findViewById(R.id.mediaCiclo);
        mediaCiclo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinhaDoTempo.this, Cadastrar4.class);
                startActivity(intent);
            }
        });

        buscarDataUltimaMenstruacao();
    }

    private void buscarDataUltimaMenstruacao() {
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String ultimaMenstruacao = documentSnapshot.getString("UltimaMenstruacao");
                            diasDoCiclo = documentSnapshot.getLong("diasCiclo").intValue();
                            String stringDiasDoCiclo = Integer.toString(diasDoCiclo);

                            proximaMenstruacao = calcularProximaMenstruacao(ultimaMenstruacao, diasDoCiclo);
                            today = calcularDataDeHoje();
                            String dia = calcularDiaDoCiclo(ultimaMenstruacao, today);

                            String faseDoCiclo = determinarFaseDoCiclo(ultimaMenstruacao, proximaMenstruacao, today);
                            Button buttonFaseCiclo = findViewById(R.id.faseCiclo);
                            buttonFaseCiclo.setText(faseDoCiclo + "\n" + dia);

                            TextView textViewProximaMenstruacao = findViewById(R.id.proximaMenst);
                            TextView textViewDiasCiclo = findViewById(R.id.mediaCiclo);

                            String dataProximaMenstruacao = calcularProximaMenstruacao(ultimaMenstruacao, diasDoCiclo);

                            textViewProximaMenstruacao.setText("Próxima: " + dataProximaMenstruacao);
                            textViewDiasCiclo.setText(stringDiasDoCiclo + " dias");

                            if (proximaMenstruacao.compareTo(today) < 0) {
                                atualizarDataUltimaMenstruacao(proximaMenstruacao);
                            } else {
                                atualizarSeekBar();
                            }
                        } else {
                            Log.d("TAG", "Documento não encontrado");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Erro ao buscar dados", e);
                    }
                });
    }

    private void atualizarDataUltimaMenstruacao(String novaData) {
        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put("UltimaMenstruacao", novaData);

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        documentReference.update(dadosParaAtualizar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        buscarDataUltimaMenstruacao();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Erro ao atualizar a última menstruação", e);
                    }
                });
    }

    private void atualizarSeekBar() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            Date proximaMenstruacaoDate = dateFormat.parse(proximaMenstruacao);
            Date hojeDate = dateFormat.parse(today);

            long diferencaMilissegundos = proximaMenstruacaoDate.getTime() - hojeDate.getTime();
            long diferencaDias = diferencaMilissegundos / (1000 * 60 * 60 * 24); // Converte para dias

            if (diferencaDias >= 0) {
                int progresso = (int) (100 - diferencaDias);
                seekBar.setProgress(progresso);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> usuarios = new HashMap<>();

                String diasProxima = String.valueOf(diferencaDias);
                usuarios.put("diasProxima", diasProxima);

                usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
                documentReference.set(usuarios, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Ação de sucesso ao salvar os dados
                                Log.d("TAG", "");
                            }
                        });
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String calcularProximaMenstruacao(String ultimaMenstruacao, int diasDoCiclo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            Date dataUltimaMenstruacao = dateFormat.parse(ultimaMenstruacao);

            calendar.setTime(dataUltimaMenstruacao);
            calendar.add(Calendar.DAY_OF_MONTH, diasDoCiclo);

            return dateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "Erro ao calcular a data";
        }
    }

    private String calcularDataDeHoje() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        Date dataDeHoje = calendar.getTime();
        return dateFormat.format(dataDeHoje);
    }

    private String determinarFaseDoCiclo(String ultimaMenstruacao, String proximaMenstruacao, String today) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date ultimaMenstruacaoDate = dateFormat.parse(ultimaMenstruacao);
            Date proximaMenstruacaoDate = dateFormat.parse(proximaMenstruacao);
            Date todayDate = dateFormat.parse(today);

            Calendar cal = Calendar.getInstance();
            cal.setTime(ultimaMenstruacaoDate);
            cal.add(Calendar.DAY_OF_MONTH, diasDoCiclo - 14);
            Date diaPeriodoFertil = cal.getTime();

            cal.setTime(diaPeriodoFertil);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            Date umDiaAntesPeriodoFertil = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 3);
            Date umDiaDepoisPeriodoFertil = cal.getTime();

            if (todayDate.after(umDiaAntesPeriodoFertil) && todayDate.before(umDiaDepoisPeriodoFertil)) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> usuarios = new HashMap<>();
                String periodo = "Período Fértil";
                usuarios.put("periodo", periodo);
                usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
                documentReference.set(usuarios, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Ação de sucesso ao salvar os dados
                                Log.d("TAG", "");
                            }
                        });
                return "Período Fértil";
            } else if (todayDate.after(umDiaDepoisPeriodoFertil) && todayDate.before(proximaMenstruacaoDate)) {
                return "Fase Lútea";
            } else {
                return "Fase Folicular";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Erro ao determinar a fase do ciclo";
        }
    }

    private String calcularDiaDoCiclo(String ultimaMenstruacao, String today) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date ultimaMenstruacaoDate = dateFormat.parse(ultimaMenstruacao);
            Date todayDate = dateFormat.parse(today);

            long diferencaMilissegundos = todayDate.getTime() - ultimaMenstruacaoDate.getTime();
            long diferencaDias = diferencaMilissegundos / (1000 * 60 * 60 * 24);

            int diaDoCiclo = (int) diferencaDias + 1;

            String sufixo = "º";

            return diaDoCiclo + sufixo + " dia do ciclo";
        } catch (ParseException e) {
            e.printStackTrace();
            return "Erro ao calcular o dia do ciclo";
        }
    }
}
