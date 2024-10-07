package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateAccount extends AppCompatActivity {

    private static final Logger logger = LoggerUtils.configurarLogger(CreateAccount.class.getName());
    private EditText edit_nome, edit_email, edit_celular, edit_senha, edit_repetir_senha;
    private Button cadastrar;
    private MaterialCheckBox checkboxTerms;
    String[] mensagens = {"Preencha todos os campos!", "Cadastro realizado com sucesso!", "Para prosseguir é necessário aceitar os Termos de Uso"};
    String userID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criacao_conta);

        final EditText campo_celular = findViewById(R.id.edit_celular);
        campo_celular.addTextChangedListener(Mask.insert("(##)#####-####", campo_celular));

        iniciarComponentes();

        AppCompatButton cadastrar = findViewById(R.id.cadastrar);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos(v)) {
                    cadastrarUsuario(v);
                }
            }
        });
    }

    private boolean validarCampos(View v) {
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String celular = edit_celular.getText().toString();
        String senha = edit_senha.getText().toString();
        String rSenha = edit_repetir_senha.getText().toString();

        if (!checkboxTerms.isChecked()) {
            mostrarSnackbar(v, mensagens[2]);
            return false;
        }

        if (nome.isEmpty() || email.isEmpty() || celular.isEmpty() || senha.isEmpty() || rSenha.isEmpty()) {
            mostrarSnackbar(v, mensagens[0]);
            return false;
        }

        if (!validarSenha(senha)) {
            mostrarSnackbar(v, "A senha deve ter no mínimo 8 caracteres, incluindo letras maiúsculas, minúsculas, números e especiais.");
            return false;
        }

        if (!senha.equals(rSenha)) {
            mostrarSnackbar(v, "Senhas divergentes");
            logger.log(Level.SEVERE, "Senha divergente para tentativa de criação de usuário: " + email);
            return false;
        }

        return true; // Todos os campos são válidos
    }

    private void cadastrarUsuario(View v) {
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    salvarDadosUsuario();
                    mostrarSnackbar(v, mensagens[1]);
                    Intent intent = new Intent(CreateAccount.this, ReasonForUse.class);
                    startActivity(intent);
                    finish();
                } else {
                    String erro = obterMensagemErro(task.getException(), email);
                    mostrarSnackbar(v, erro);
                }
            }
        });
    }

    private String obterMensagemErro(Exception e, String email) {
        String erro;
        try {
            throw e;
        } catch (FirebaseAuthWeakPasswordException ex) {
            erro = "Digite uma senha com no mínimo 8 caracteres";
            logger.log(Level.SEVERE, "Senha inserida com caracteres incorretos para o email: " + email + ", " + ex);
        } catch (FirebaseAuthUserCollisionException ex) {
            erro = "Conta já cadastrada anteriormente";
            logger.log(Level.SEVERE, "Conta já cadastrada anteriormente: " + email + ", " + ex);
        } catch (FirebaseAuthInvalidCredentialsException ex) {
            erro = "E-mail inválido";
            logger.log(Level.SEVERE, "E-mail inválido: " + email + ", " + ex);
        } catch (Exception ex) {
            erro = "Erro ao cadastrar usuário";
            logger.log(Level.SEVERE, "Erro ao cadastrar usuário: " + email + ", " + ex);
        }
        return erro;
    }

    private void salvarDadosUsuario() {
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String celular = edit_celular.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("Name", nome);
        usuarios.put("Email", email);
        usuarios.put("Phone", celular);

        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            logger.log(Level.INFO, "ID do usuário não encontrado para o e-mail: " + email);
        }

        DocumentReference documentReference = db.collection("Usuarios").document(userID);
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db", "Sucesso ao salvar os dados!");
                logger.log(Level.INFO, "Sucesso ao salvar os dados do usuário: " + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db error", "Erro ao salvar os dados " + e);
                logger.log(Level.SEVERE, "Erro ao salvar os dados " + e);
            }
        });
    }

    private boolean validarSenha(String senha) {
        if (senha.length() < 8) {
            return false;
        } else if (!senha.matches(".*[A-Z].*")) {
            return false;
        } else if (!senha.matches(".*[a-z].*")) {
            return false;
        } else if (!senha.matches(".*\\d.*")) {
            return false;
        } else if (!senha.matches(".*[!@#\\$%\\^&\\*\\(\\)_\\+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }
        return true;
    }

    private void mostrarSnackbar(View v, String mensagem) {
        Snackbar snackbar = Snackbar.make(v, mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private void iniciarComponentes() {
        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_celular = findViewById(R.id.edit_celular);
        edit_senha = findViewById(R.id.edit_senha);
        edit_repetir_senha = findViewById(R.id.edit_repetir_senha);
        cadastrar = findViewById(R.id.cadastrar);
        checkboxTerms = findViewById(R.id.checkbox_terms);
    }
}
