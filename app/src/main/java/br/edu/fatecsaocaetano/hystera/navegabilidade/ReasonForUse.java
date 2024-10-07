package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
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

public class ReasonForUse extends AppCompatActivity {

    private static final Logger logger = LoggerUtils.configurarLogger(ReasonForUse.class.getName());
    private AppCompatButton monitorar;
    private AppCompatButton engravidar;
    private boolean choice;
    String userID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_1);

        monitorar = findViewById(R.id.monitorar);
        engravidar = findViewById(R.id.engravidar);

        monitorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = false;
                salvarMotivo();
            }
        });
        engravidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = true;
                salvarMotivo();
            }
        });
    }

    private void salvarMotivo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put("GetPregnant", choice);

        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "ID do usuário não encontrado!");
        }
        DocumentReference documentReference = db.collection("Usuarios").document(userID);

        // Atualizar o documento com o novo campo usando merge para preservar os dados existentes
        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Ação de sucesso ao salvar os dados
                        Log.d("TAG", "Motivo do uso do aplicativo salvo com sucesso!");
                        logger.log(Level.INFO, "Motivo do uso do aplicativo salvo com sucesso: " + userID);
                        Intent intent = new Intent(ReasonForUse.this, MethodUse.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Lidar com falhas ao salvar os dados
                        Log.e("TAG", "Erro ao salvar motivo do uso do aplicativo. ", e);
                        logger.log(Level.SEVERE, "Erro ao salvar motivo do uso do aplicativo. " + e);
                    }
                });
    }
}