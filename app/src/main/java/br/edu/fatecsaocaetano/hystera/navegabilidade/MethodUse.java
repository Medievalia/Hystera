package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

public class MethodUse extends AppCompatActivity {

    private boolean methoduse = false;
    private boolean usepill = false;
    private String tag = "MethodUseClass";
    private String userID;

    private enum Metodo {
        PRESERVATIVO, PILULA, DIU, INJECAO, IMPLANTE_HORMONAL, OUTROS, NAO_UTILIZA
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_4);

        // Referências aos botões
        ShapeableImageView[] imageButtons = {
                findViewById(R.id.botao1), findViewById(R.id.botao2),
                findViewById(R.id.botao3), findViewById(R.id.botao4),
                findViewById(R.id.botao5)
        };

        // Lista de métodos correspondentes aos botões
        Metodo[] metodos = {
                Metodo.PRESERVATIVO, Metodo.PILULA,
                Metodo.DIU, Metodo.INJECAO,
                Metodo.IMPLANTE_HORMONAL
        };

        AppCompatButton botao6 = findViewById(R.id.botao6);
        AppCompatButton btn_n_utilizo = findViewById(R.id.btn_n_utilizo);

        AppCompatButton voltar = findViewById(R.id.back_button);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MethodUse.this, ReasonForUse.class);
                startActivity(intent);
                finish();
            }
        });

        // Configurando o clique de forma genérica
        for (int i = 0; i < imageButtons.length; i++) {
            final Metodo metodo = metodos[i];
            imageButtons[i].setOnClickListener(v -> salvarMetodo(metodo));
        }

        // Clique para o botão 'Outros'
        botao6.setOnClickListener(v -> salvarMetodo(Metodo.OUTROS));

        // Clique para o botão 'Não utiliza'
        btn_n_utilizo.setOnClickListener(v -> salvarMetodo(Metodo.NAO_UTILIZA));
    }

    private void salvarMetodo(Metodo metodo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dadosParaAtualizar = new HashMap<>();
        dadosParaAtualizar.put("Method", metodo);

        if(!metodo.toString().equals("NAO_UTILIZA")){
            methoduse = true;
        }

        if (metodo.toString().equals("PILULA")) {
            usepill = true;
        }

        dadosParaAtualizar.put("UseMethod", methoduse);
        dadosParaAtualizar.put("Pill", usepill);

        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            Log.e(tag, "ID do usuário não encontrado!");
        }

        DocumentReference documentReference = db.collection("Usuarios").document(userID);

        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(tag, "Método anticoncepcional salvo com sucesso!" + userID);
                        Intent intent = new Intent(MethodUse.this, DUM.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(tag, "Erro ao salvar o método anticoncepcional. " + userID + e);
                    }
                });
    }
}
