package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.edu.fatecsaocaetano.hystera.R;
import com.bumptech.glide.Glide;
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
    private MaterialButton help;
    private TextView nomeUsuario;
    private TextView emailUsuario;
    private ImageView fotoPerfil;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        iniciarComponente();
        configurarBotaoAjuda();
        configurarBotoes();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userID == null) {
            Log.e(tag, "ID do usuário não encontrado!");
            return;
        }

        voltarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference documentReference = db.collection("Usuarios").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && !isFinishing()) { // Verifica se a atividade ainda está ativa
                    nomeUsuario.setText(documentSnapshot.getString("Name"));
                    emailUsuario.setText(email);
                    String base64Image = documentSnapshot.getString("UserImage");
                    if (base64Image != null) {
                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Glide.with(Profile.this).load(decodedString).into(fotoPerfil); // Carrega a imagem do perfil
                    }
                    Log.i(tag, "Carregando email e nome do usuário: " + emailUsuario.getText() + ", " + nomeUsuario.getText());
                }
            }
        });
    }

    private void iniciarComponente() {
        nomeUsuario = findViewById(R.id.txt_nome);
        emailUsuario = findViewById(R.id.txtEmail);
        fotoPerfil = findViewById(R.id.btn_foto);
        voltarButton = findViewById(R.id.voltar_button);
    }

    private void configurarBotoes() {
        carregandoBotoesAppCompatImageButton(R.id.btn_pefil_editar, EditProfile.class);
        carregandoBotoesAppCompatImageButton(R.id.btn_ciclo_editar, EditCycleOne.class);
        carregandoBotoesAppCompatImageButton(R.id.btn_alterar_senha, ResetPassword.class);

        // Configurando o botão para excluir conta
        MaterialButton excluirConta = findViewById(R.id.btn_excluir_conta);
        excluirConta.setOnClickListener(v -> confirmarExclusaoConta());

        // Configurando o botão para sair do aplicativo
        MaterialButton sair = findViewById(R.id.btn_logoff);
        sair.setOnClickListener(v -> realizarLogout());
    }

    private void confirmarExclusaoConta() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza de que deseja excluir sua conta? Essa ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> excluirConta())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirConta() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            auth.getCurrentUser().delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showToast("Conta excluída com sucesso.");
                            // Redirecionar para a tela de login após a exclusão
                            Intent intent = new Intent(Profile.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("Falha ao excluir a conta. Faça login novamente para confirmar.");
                        }
                    });
        }
    }

    private void realizarLogout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Sair da Conta")
                .setMessage("Tem certeza de que deseja sair? Você precisará fazer login novamente para acessar sua conta.")
                .setPositiveButton("Sair", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    showToast("Você saiu da sua conta.");
                    Intent intent = new Intent(Profile.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void carregandoBotoesAppCompatImageButton(int buttonId, Class<?> targetActivity) {
        MaterialButton button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, targetActivity);
            startActivity(intent);
        });
    }

    private void configurarBotaoAjuda() {
        MaterialButton ajuda = findViewById(R.id.help_button);
        ajuda.setOnClickListener(v -> startActivity(new Intent(Profile.this, Help.class)));
    }

}
