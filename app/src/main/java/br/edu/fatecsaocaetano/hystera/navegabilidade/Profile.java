package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profile extends AppCompatActivity {

    private final String tag = "ProfileClass";
    private MaterialButton voltarButton;
    private TextView nomeUsuario;
    private TextView emailUsuario;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        iniciarComponente();
        carregandoBotoesAppCompatImageButton(R.id.btn_pefil_editar, EditProfile.class);
        carregandoBotoesAppCompatImageButton(R.id.btn_ciclo_editar, FirstCycle.class);
        carregandoBotoesAppCompatImageButton(R.id.btn_alterar_senha, ResetPassword.class);
        carregandoBotoesAppCompatImageButton(R.id.btn_logoff, Logout.class);
    }
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            Log.e(tag, "ID do usuário não encontrado!");
            return;
        }

        voltarButton = findViewById(R.id.voltar_button);
        voltarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, TimeLine.class);
                startActivity(intent);
                finish(); // Finaliza a Activity atual
            }
        });

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference documentReference = db.collection("Usuarios").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    nomeUsuario.setText(documentSnapshot.getString("Name"));
                    emailUsuario.setText(email);
                    Log.i(tag, "Carregando email e nome do usuário: " + emailUsuario + ", " + nomeUsuario);
                }
            }
        });
    }
    private void iniciarComponente(){
        nomeUsuario = findViewById(R.id.txt_nome);
        emailUsuario = findViewById(R.id.txtEmail);
    }

    private void carregandoBotoesAppCompatImageButton(int buttonId, Class<?> targetActivity) {
        MaterialButton button = findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, targetActivity);
                startActivity(intent);
                finish();
            }
        });
    }
}
