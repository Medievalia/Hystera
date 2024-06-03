package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class EditarPerfil extends AppCompatActivity {
    private EditText edit_nome, edit_email, edit_celular, edit_metodo;
    String[] mensagens = {"Preencha todos os campos", "Alteração realizada com sucesso!"};
    String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        ImageButton ajuda = findViewById(R.id.help_button);
        ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarPerfil.this, AjudaDuvidas.class);
                startActivity(intent);
            }
        });

        ImageButton voltar = findViewById(R.id.voltar_button);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarPerfil.this, Perfil.class);
                startActivity(intent);
            }
        });

        final EditText campo_celular = (EditText) findViewById(R.id.txt_num);
        campo_celular.addTextChangedListener(Mask.insert("(##)#####-####", campo_celular));

        AppCompatButton salvar = findViewById(R.id.btn_pefil_salvar);
        IniciarComponentes();

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = edit_nome.getText().toString();
                String email = edit_email.getText().toString();
                String celular = edit_celular.getText().toString();

                if (nome.isEmpty() || email.isEmpty() || celular.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    salvarDados(v);
                }
            }
        });
    }

    private void salvarDados(View v) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> usuarios = new HashMap<>();

        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String celular = edit_celular.getText().toString();
        String metodo = edit_metodo.getText().toString();

        usuarios.put("nome", nome);
        usuarios.put("email", email);
        usuarios.put("celular", celular);
        usuarios.put("metodo", metodo);

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        // Atualizar o documento com o novo campo usando merge para preservar os dados existentes
        documentReference.set(usuarios, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Ação de sucesso ao salvar os dados
                        Log.d("TAG", "Perfil Alterado com Sucesso!");
                        Intent intent = new Intent(EditarPerfil.this, LinhaDoTempo.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@android.support.annotation.NonNull Exception e) {
                        // Lidar com falhas ao salvar os dados
                        Log.e("TAG", "Erro ao salvar as alterações", e);
                    }
                });
    }

    private void IniciarComponentes() {
        edit_nome = findViewById(R.id.txt_nome);
        edit_email = findViewById(R.id.txt_email);
        edit_celular = findViewById(R.id.txt_num);
        edit_metodo = findViewById(R.id.txt_metodo);
    }

    public void onImageButtonClick(View view) {
        // Este método é chamado quando o ImageButton é clicado
        Intent intent = new Intent(EditarPerfil.this, Perfil.class);
        startActivity(intent);
    }

}
