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
    String userID;
    String totoday = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    CircularSeekBar seekBar;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_linha_tempo);
//
//        seekBar = findViewById(R.id.seekbar);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        ImageButton btnPerfil = findViewById(R.id.button_perfil);
//        btnPerfil.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LinhaDoTempo.this, Perfil.class);
//                startActivity(intent);
//            }
//        });
//
//        ImageButton anota = findViewById(R.id.anota);
//        anota.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LinhaDoTempo.this, Anotacoes.class);
//                startActivity(intent);
//            }
//        });
//
//        ImageButton calendario = findViewById(R.id.calendario);
//        calendario.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LinhaDoTempo.this, Calendario.class);
//                startActivity(intent);
//            }
//        });
//
//        ImageButton utero = findViewById(R.id.utero);
//        utero.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LinhaDoTempo.this, Informacao.class);
//                startActivity(intent);
//            }
//        });
//
//        ImageButton menu = findViewById(R.id.menu);
//        menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LinhaDoTempo.this, Notificacoes.class);
//                startActivity(intent);
//            }
//        });
//
//        ImageButton inicioMenst = findViewById(R.id.inicioMenst);
//        inicioMenst.setOnClickListener(new View.OnClickListener() {
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
//
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
//
//        Button buttonFaseCiclo = findViewById(R.id.faseCiclo);
//        buttonFaseCiclo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                DocumentReference docRef = db.collection("Usuarios").document(usuarioId);
//
//                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            String ultimaMenstruacao = documentSnapshot.getString("UltimaMenstruacao");
//                            String proximaMenstruacao = documentSnapshot.getString("ProximaMenstruacao");
//                            long ciclolong = documentSnapshot.getLong("diasCiclo");
//                            int attcicloint = (int) ciclolong;
//                            long sangramento = documentSnapshot.getLong("diasSangramento");
//                            int sangramentoint = (int) sangramento;
//
//                            String faseCiclo = determinarFaseDoCiclo(ultimaMenstruacao, proximaMenstruacao, totoday, sangramentoint, attcicloint);
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(LinhaDoTempo.this);
//                            builder.setTitle("Fase do Ciclo Menstrual");
//                            String mensagem = "";
//
//                            switch (faseCiclo) {
//                                case "Fase Folicular":
//                                    mensagem = "Nesta fase, a preparação do útero inicia com o rompimento do revestimento uterino. O corpo se prepara para liberar um óvulo.";
//                                    break;
//                                case "Período Fértil":
//                                    mensagem = "Durante essa fase, a ovulação ocorre e há maior chance de engravidar. É o período mais fértil do ciclo.";
//                                    break;
//                                case "Fase Lútea":
//                                    mensagem = "Neste momento, caso não ocorra a fecundação, o corpo se prepara para o próximo ciclo menstrual. Podem ocorrer sintomas como inchaço ou alterações de humor.";
//                                    break;
//                                case "Período Menstrual":
//                                    mensagem = "Se o óvulo não é fecundado, o corpo se prepara para um novo ciclo. A concentração de hormônios diminui, levando ao sangramento menstrual.";
//                                    break;
//                                default:
//                                    mensagem = "Fase do ciclo não identificada.";
//                                    break;
//                            }
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
//
//        validacaoDatas();
//    }
//
//    private void validacaoDatas() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
//        documentReference.get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            String ultimaMenstruacao = documentSnapshot.getString("UltimaMenstruacao");
//                            String proximaMenstruacao = documentSnapshot.getString("ProximaMenstruacao");
//                            long sangramento = documentSnapshot.getLong("diasSangramento");
//                            int sangramentoint = (int) sangramento;
//                            long ciclolong = documentSnapshot.getLong("diasCiclo");
//                            int cicloint = (int) ciclolong;
//
//                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                            Date today = new Date();
//
//                            try {
//                                Date proximaMenstruacaoDate = dateFormat.parse(proximaMenstruacao);
//
//                                if (proximaMenstruacaoDate != null && proximaMenstruacaoDate.compareTo(today) < 0) {
//                                    int intervalo = calcularIntervaloUltimaMenstruacao(proximaMenstruacao, ultimaMenstruacao);
//                                    String newProxima = calcularProximaMenstruacao(dateFormat.format(today), intervalo);
//                                    atualizarDatasMenstruacao(proximaMenstruacao, newProxima, ultimaMenstruacao, intervalo);
//                                    atualizarSeekBar(newProxima);
//                                    atualizaCampos(proximaMenstruacao, newProxima, intervalo, dateFormat.format(today), sangramentoint);
//                                } else {
//                                    atualizarSeekBar(proximaMenstruacao);
//                                    atualizaCampos(ultimaMenstruacao, proximaMenstruacao, cicloint, totoday, sangramentoint);
//                                }
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            Log.d("TAG", "Documento não encontrado");
//                        }
//                    }
//                });
//    }
//
//    private void atualizaCampos(String last, String next, int daysCicle, String newhoje, int diasSangramento) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
//        documentReference.get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            String stringDiasDoCiclo = Integer.toString(daysCicle);
//                            String diacicliquinho = calcularDiaDoCiclo(last, newhoje);
//
//                            String faseDoCiclo = determinarFaseDoCiclo(last, next, totoday, diasSangramento, daysCicle);
//                            Button buttonFaseCiclo = findViewById(R.id.faseCiclo);
//                            buttonFaseCiclo.setText(faseDoCiclo + "\n" + diacicliquinho);
//
//                            TextView textViewProximaMenstruacao = findViewById(R.id.proximaMenst);
//                            TextView textViewDiasCiclo = findViewById(R.id.mediaCiclo);
//
//                            textViewProximaMenstruacao.setText("Próxima: " + next);
//                            textViewDiasCiclo.setText(stringDiasDoCiclo + " dias");
//
//                        } else {
//                            Log.d("TAG", "Documento não encontrado");
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("TAG", "Erro ao buscar dados", e);
//                    }
//                });
//    }
//
//    private void atualizarDataUltimaMenstruacao (String variavelHoje) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
//        documentReference.get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            String penultimaMenstruacaoValida = documentSnapshot.getString("UltimaMenstruacao");
//                            String proximaqueviraultima = variavelHoje;
//                            long sangramento = documentSnapshot.getLong("diasSangramento");
//                            int sangramentoint = (int) sangramento;
//
//                            int intervaloNovo = calcularIntervaloUltimaMenstruacao(variavelHoje, penultimaMenstruacaoValida);
//                            String newToProxima = calcularProximaMenstruacao(variavelHoje, intervaloNovo);
//                            atualizarDatasMenstruacao(proximaqueviraultima, newToProxima, penultimaMenstruacaoValida, intervaloNovo);
//                            atualizarSeekBar(newToProxima);
//                            atualizaCampos(proximaqueviraultima, newToProxima, intervaloNovo, variavelHoje, sangramentoint);
//
//                        } else {
//                            Log.d("TAG", "Documento não encontrado");
//                        }
//                    }
//                });
//    }
//
//    private void atualizarDatasMenstruacao(String novaDataUltima, String novaDataProxima, String penultimaData, int intervalo) {
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map<String, Object> dadosParaAtualizar = new HashMap<>();
//        dadosParaAtualizar.put("UltimaMenstruacao", novaDataUltima);
//        dadosParaAtualizar.put("ProximaMenstruacao", novaDataProxima);
//        dadosParaAtualizar.put("PenultimaMenstruacao", penultimaData);
//        dadosParaAtualizar.put("diasCiclo", intervalo);
//
//        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
//
//        documentReference.update(dadosParaAtualizar)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.e("TAG", "Datas da menstruação salvas com sucesso");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("TAG", "Erro ao atualizar a última menstruação", e);
//                    }
//                });
//    }
//
//    private void atualizarSeekBar(String newProx) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        Calendar calendar = Calendar.getInstance();
//
//        try {
//            Date proximaMenstruacaoDate = dateFormat.parse(newProx);
//            Date hojeDate = dateFormat.parse(totoday);
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
//                usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
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
//
//    private String determinarFaseDoCiclo(String ultimaMenstruacao, String proximaMenstruacao, String today, int diasSangramento, int diasCiclo) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//
//        try {
//            Date ultimaMenstruacaoDate = dateFormat.parse(ultimaMenstruacao);
//            Date proximaMenstruacaoDate = dateFormat.parse(proximaMenstruacao);
//            Date hojeDate = dateFormat.parse(today);
//
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(ultimaMenstruacaoDate);
//
//            // Calcula o fim do período menstrual
//            cal.add(Calendar.DAY_OF_MONTH, diasSangramento);
//            Date fimSangramento = cal.getTime();
//
//            cal.setTime(fimSangramento);
//            cal.add(Calendar.DAY_OF_MONTH, 1); // Um dia após o término do sangramento
//            Date inicioFaseFolicular = cal.getTime();
//
//            // Calcula o início da fase fértil (dois dias antes do início da fase lútea)
//            cal.setTime(ultimaMenstruacaoDate);
//            cal.add(Calendar.DAY_OF_MONTH, (diasCiclo / 2) - 3); // Dois dias antes do início da fase lútea
//            Date inicioFaseFertil = cal.getTime();
//
//            // Calcula o início da fase lútea (meio do ciclo)
//            cal.setTime(ultimaMenstruacaoDate);
//            cal.add(Calendar.DAY_OF_MONTH, diasCiclo / 2); // Meio do ciclo
//            Date inicioFaseLutea = cal.getTime();
//
//            if (hojeDate.after(ultimaMenstruacaoDate) && hojeDate.before(inicioFaseFolicular)) {
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                Map<String, Object> usuarios = new HashMap<>();
//                String periodo = "Período Menstrual";
//                usuarios.put("periodo", periodo);
//                usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
//                documentReference.set(usuarios, SetOptions.merge())
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // Ação de sucesso ao salvar os dados
//                                Log.d("TAG", "");
//                            }
//                        });
//                return "Período Menstrual";
//            } else if (hojeDate.equals(ultimaMenstruacaoDate)) {
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                Map<String, Object> usuarios = new HashMap<>();
//                String periodo = "Período Menstrual";
//                usuarios.put("periodo", periodo);
//                usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
//                documentReference.set(usuarios, SetOptions.merge())
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // Ação de sucesso ao salvar os dados
//                                Log.d("TAG", "");
//                            }
//                        });
//                return "Período Menstrual";
//            } else if (hojeDate.after(fimSangramento) && hojeDate.before(inicioFaseFertil)) {
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                Map<String, Object> usuarios = new HashMap<>();
//                String periodo = "Fase Folicular";
//                usuarios.put("periodo", periodo);
//                usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
//                documentReference.set(usuarios, SetOptions.merge())
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // Ação de sucesso ao salvar os dados
//                                Log.d("TAG", "");
//                            }
//                        });
//                return "Fase Folicular";
//            } else if (hojeDate.after(inicioFaseFertil) && hojeDate.before(inicioFaseLutea)) {
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                Map<String, Object> usuarios = new HashMap<>();
//                String periodo = "Período Fértil";
//                usuarios.put("periodo", periodo);
//                usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
//                documentReference.set(usuarios, SetOptions.merge())
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // Ação de sucesso ao salvar os dados
//                                Log.d("TAG", "");
//                            }
//                        });
//                return "Período Fértil";
//            } else if (hojeDate.after(inicioFaseLutea) && hojeDate.before(proximaMenstruacaoDate)) {
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                Map<String, Object> usuarios = new HashMap<>();
//                String periodo = "Fase Lútea";
//                usuarios.put("periodo", periodo);
//                usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
//                documentReference.set(usuarios, SetOptions.merge())
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // Ação de sucesso ao salvar os dados
//                                Log.d("TAG", "");
//                            }
//                        });
//                return "Fase Lútea";
//            } else {
//                return "Fora do ciclo";
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return "Erro ao determinar a fase do ciclo";
//        }
//    }
//
//    private String calcularDiaDoCiclo(String ultimaMenstruacao, String today) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//
//        try {
//            Date ultimaMenstruacaoDate = dateFormat.parse(ultimaMenstruacao);
//            Date todayDate = dateFormat.parse(today);
//
//            long diferencaMilissegundos = todayDate.getTime() - ultimaMenstruacaoDate.getTime();
//            long diferencaDias = diferencaMilissegundos / (1000 * 60 * 60 * 24);
//
//            int diaDoCiclo = (int) diferencaDias + 1;
//
//            String sufixo = "º";
//
//            return diaDoCiclo + sufixo + " dia do ciclo";
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return "Erro ao calcular o dia do ciclo";
//            }
//        }
//
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
//
//    private int calcularIntervaloUltimaMenstruacao(String ultimaMenstruacao, String penultimaMenstruacao) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//
//        try {
//            // Obtendo a data penultima
//            Date dataPenultimaMenstruacao = dateFormat.parse(penultimaMenstruacao);
//
//            // Obtendo a data da última menstruação
//            Date dataUltimaMenstruacao = dateFormat.parse(ultimaMenstruacao);
//
//            // Calculando o intervalo em milissegundos
//            long intervaloMilissegundos = dataUltimaMenstruacao.getTime() - dataPenultimaMenstruacao.getTime();
//
//            // Convertendo o intervalo de milissegundos para dias
//            int intervaloDias = (int) (intervaloMilissegundos / (1000 * 60 * 60 * 24));
//
//            return intervaloDias;
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return -1; // Retorna um valor inválido se houver erro na conversão das datas
//        }
//    }
}
