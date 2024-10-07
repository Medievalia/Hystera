package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MethodUse extends AppCompatActivity {
    private String userID;
    private String metodo;
    private static final Logger logger = LoggerUtils.configurarLogger(MethodUse.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_4);

        ImageButton botao1 = findViewById(R.id.botao1);
        ImageButton botao2 = findViewById(R.id.botao2);
        ImageButton botao3 = findViewById(R.id.botao3);
        ImageButton botao4 = findViewById(R.id.botao4);
        ImageButton botao5 = findViewById(R.id.botao5);
        AppCompatButton botao6 = findViewById(R.id.botao6);
        AppCompatButton btn_n_utilizo = findViewById(R.id.btn_n_utilizo);
        ImageButton voltar = findViewById(R.id.back_button);

        botao1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = "Preservativo";
                salvarMetodo(metodo);
            }
        });
        botao2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = "Pílula";
                salvarMetodo(metodo);
            }
        });
        botao3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = "DIU";
                salvarMetodo(metodo);
            }
        });
        botao4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = "Injeção";
                salvarMetodo(metodo);
            }
        });
        botao5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = "Implante Hormonal";
                salvarMetodo(metodo);
            }
        });
        botao6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = "Outros";
                salvarMetodo(metodo);
            }
        });
        btn_n_utilizo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo = "Não utiliza";
                salvarMetodo(metodo);
            }
        });
    }

    private void salvarMetodo(String metodo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put("Method", metodo);

        if(metodo.equals("Não utiliza")){
            dadosParaAtualizar.put("UseMethod", true);
        } else {
            dadosParaAtualizar.put("UseMethod", false);
        }

        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "ID do usuário não encontrado!");
        }

        if (metodo.equals("Pílula")) {
            dadosParaAtualizar.put("Pill", true);
        } else {
            dadosParaAtualizar.put("Pill", false);
        }

        DocumentReference documentReference = db.collection("Usuarios").document(userID);

        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Método anticoncepcional salvo com sucesso!");
                        logger.log(Level.INFO, "Método anticoncepcional salvo com sucesso! " + userID);
                        Intent intent = new Intent(MethodUse.this, DUM.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Erro ao salvar o método anticoncepcional. ", e);
                        logger.log(Level.SEVERE, "Erro ao salvar o método anticoncepcional para o usuário: " + userID + " " + e);
                    }
                });
    }
}
