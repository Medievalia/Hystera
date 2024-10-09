package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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

    private final String tag = "LinhaDoTempoClass";
    private String userID;
    private Timestamp today;
    private CircularSeekBar seekBar;
    private CircularSeekBar seekBarPeriod;
    private CircularSeekBar seekBarNextBleeding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linha_tempo);
        seekBar = findViewById(R.id.seekbar);
        seekBarPeriod = findViewById(R.id.seekbar_period);
        seekBarNextBleeding = findViewById(R.id.seekbar_next);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            Log.e(tag, "ID do usuário não encontrado!");
            return;
        }

        db.collection("Usuarios")
                .document(userID)
                .collection("Ciclos")
                .orderBy("startDate", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot currentCycle = queryDocumentSnapshots.getDocuments().get(0);
                        Cycle ultimoCiclo = currentCycle.toObject(Cycle.class);
                        Map<String, String> informacoesCiclo = ultimoCiclo.obterInformacoesDoCicloAtual();

                        int duracaoCiclo = ultimoCiclo.getDuration();
                        int diasMenstruacao = ultimoCiclo.getBleeding();

                        String nomeFase = informacoesCiclo.get("faseAtual");
                        String diaDoCiclo = informacoesCiclo.get("diaDoCiclo");

                        Map<String, Integer> diasRestantes = ultimoCiclo.calcularDiasRestantes();
                        int diasProximaMenstruacao = diasRestantes.get("diasProximaMenstruacao");
                        int diasProximaFase = diasRestantes.get("diasProximaFase");

                        float progressoSeekBarDiaDoCiclo = (Float.parseFloat(diaDoCiclo) / duracaoCiclo) * 100;

                        int diasFaltandoParaProximaFase = diasProximaFase;
                        int duracaoFaseAtual = 0;

                        for (Map.Entry<String, Map<String, Timestamp>> entry : ultimoCiclo.getFases().entrySet()) {
                            String fase = entry.getKey();
                            Map<String, Timestamp> datasFase = entry.getValue();
                            Timestamp inicioFase = datasFase.get("inicio");
                            Timestamp fimFase = datasFase.get("fim");

                            if (fase.equals(nomeFase)) {
                                duracaoFaseAtual = (int) ((fimFase.toDate().getTime() - inicioFase.toDate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
                                break;
                            }
                        }

                        float progressoSeekBarProximaFase = ((duracaoFaseAtual - diasFaltandoParaProximaFase) / (float) duracaoFaseAtual) * 100;
                        float progressoSeekBarProximoSangramento = ((duracaoCiclo - diasProximaMenstruacao) / (float) duracaoCiclo) * 100;

                        seekBar.setProgress(progressoSeekBarDiaDoCiclo);
                        seekBarPeriod.setProgress(progressoSeekBarProximaFase);
                        seekBarNextBleeding.setProgress(progressoSeekBarProximoSangramento);

                        Log.i(tag, "Último ciclo recuperado: " + ultimoCiclo.getId());
                    } else {
                        Log.e(tag, "Nenhum ciclo encontrado.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(tag, "Erro ao recuperar o último ciclo. ", e);
                });
    }

        // ImageButton inicioMenst = findViewById(R.id.inicioMenst);
        //inicioMenst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Criação do diálogo de confirmação
//                AlertDialog.Builder builder = new AlertDialog.Builder(LinhaDoTempo.this);
//                builder.setTitle("Atualizar data de menstruação");
//                builder.setMessage("Deseja atualizar a data da última menstruação para hoje?");
//                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        atualizarDataUltimaMenstruacao(totoday);
//
//                    }
//                });
//                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });

//        Button mediaCiclo = findViewById(R.id.mediaCiclo);
//        mediaCiclo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                DocumentReference docRef = db.collection("Usuarios").document(usuarioId);
//
//                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            String penultimaMenstruacaoinfo = documentSnapshot.getString("PenultimaMenstruacao");
//                            String ultimaMenstruacaoinfo = documentSnapshot.getString("UltimaMenstruacao");
//                            String proximaMenstruacaoinfo = documentSnapshot.getString("ProximaMenstruacao");
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(LinhaDoTempo.this);
//                            builder.setTitle("Informações de Ciclo Menstrual");
//
//                            String mensagem = "Penúltima Menstruação: " + penultimaMenstruacaoinfo + "\n" +
//                                    "Última Menstruação: " + ultimaMenstruacaoinfo + "\n" +
//                                    "Próxima Menstruação: " + proximaMenstruacaoinfo;
//
//                            builder.setMessage(mensagem);
//
//                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    dialog.dismiss(); // Fecha a caixa de mensagem ao clicar em OK
//                                }
//                            });
//
//                            AlertDialog dialog = builder.create();
//                            dialog.show();
//                        } else {
//                            Log.d("TAG", "O documento não existe");
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("TAG", "Falha ao obter informações do Firestore", e);
//                    }
//                });
//            }
//        });
//    }

//    private void atualizarSeekBar(String newProx) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        Calendar calendar = Calendar.getInstance();
//
//        try {
//            Date proximaMenstruacaoDate = dateFormat.parse(newProx);
//            Date hojeDate = dateFormat.parse(today);
//
//            long diferencaMilissegundos = proximaMenstruacaoDate.getTime() - hojeDate.getTime();
//            long diferencaDias = diferencaMilissegundos / (1000 * 60 * 60 * 24); // Converte para dias
//
//            if (diferencaDias >= 0) {
//                int progresso = (int) (100 - diferencaDias);
//                seekBar.setProgress(progresso);
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                Map<String, Object> usuarios = new HashMap<>();
//
//                String diasProxima = String.valueOf(diferencaDias);
//                usuarios.put("diasProxima", diasProxima);
//
//                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                DocumentReference documentReference = db.collection("Usuarios").document(userID);
//                documentReference.set(usuarios, SetOptions.merge())
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // Ação de sucesso ao salvar os dados
//                                Log.d("TAG", "");
//                            }
//                        });
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }


    private void configurarBotao(int botaoId, Class<?> destino) {
        ImageButton botao = findViewById(botaoId);
        botao.setOnClickListener(v -> {
            Intent intent = new Intent(LinhaDoTempo.this, destino);
            startActivity(intent);
//        configurarBotao(R.id.button_perfil, Perfil.class);
//        configurarBotao(R.id.button_grafico, Graph.class);
        });
    }
}
