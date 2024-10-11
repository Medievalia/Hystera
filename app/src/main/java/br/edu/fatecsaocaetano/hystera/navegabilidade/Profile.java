package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profile extends AppCompatActivity {

    private TextView nomeUsuario;
    private TextView emailUsuario;
    private TextView numeroUsuario;
    private TextView metodoContraUsuario;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        iniciarComponente();

        AppCompatButton sair = findViewById(R.id.btn_logoff);
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Sair.class);
                startActivity(intent);
            }
        });

        AppCompatButton perfilEditar =  findViewById(R.id.btn_pefil_editar);
        perfilEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, EditarPerfil.class);
                startActivity(intent);
            }
        });

        AppCompatButton alterarSenha =  findViewById(R.id.btn_alterar_senha);
        alterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, ResetPassword.class);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference documentReference = db.collection("Usuarios").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    nomeUsuario.setText(documentSnapshot.getString("nome"));
                    emailUsuario.setText(email);
                    numeroUsuario.setText(documentSnapshot.getString("celular"));
                    metodoContraUsuario.setText(documentSnapshot.getString("metodo"));
                }
            }
        });
    }
    private void iniciarComponente(){
        nomeUsuario = findViewById(R.id.txt_nome);
        emailUsuario = findViewById(R.id.txt_email);
        numeroUsuario = findViewById(R.id.txt_num);
        metodoContraUsuario = findViewById(R.id.txt_metodo);
    }

    public void onImageButtonClick(View view) {
        // Este método é chamado quando o ImageButton é clicado
        Intent intent = new Intent(Profile.this, LinhaDoTempo.class);
        startActivity(intent);
    }
}
