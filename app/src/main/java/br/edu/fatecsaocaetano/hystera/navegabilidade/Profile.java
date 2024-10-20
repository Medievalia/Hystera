package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Profile extends AppCompatActivity {

    private final String tag = "ProfileClass";
    private MaterialButton voltarButton;
    private MaterialButton changeImageButton;
    private TextView nomeUsuario;
    private TextView emailUsuario;
    private ImageView fotoPerfil;
    private String userID;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        iniciarComponente();
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

        voltarButton.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, TimeLine.class);
            startActivity(intent);
            finish();
        });

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DocumentReference documentReference = db.collection("Usuarios").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && !isFinishing()) { // Verifica se a atividade ainda está ativa
                    nomeUsuario.setText(documentSnapshot.getString("Name"));
                    emailUsuario.setText(email);
                    String base64Image = documentSnapshot.getString("ProfileImage");
                    if (base64Image != null) {
                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Glide.with(Profile.this).load(decodedString).into(fotoPerfil);
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
        changeImageButton = findViewById(R.id.btn_changeImage);
    }

    private void configurarBotoes() {
        changeImageButton.setOnClickListener(v -> openGallery());
        carregandoBotoesAppCompatImageButton(R.id.btn_pefil_editar, EditProfile.class);
        carregandoBotoesAppCompatImageButton(R.id.btn_ciclo_editar, FirstCycle.class);
        carregandoBotoesAppCompatImageButton(R.id.btn_alterar_senha, ResetPassword.class);
        carregandoBotoesAppCompatImageButton(R.id.btn_logoff, Logout.class);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToFirestore();
        }
    }

    private void uploadImageToFirestore() {
        if (imageUri != null) {
            Log.d(tag, "Iniciando upload da imagem: " + imageUri.toString());

            changeImageButton.setEnabled(false);

            // Converter a imagem em uma string Base64
            String base64Image = convertImageUriToBase64(imageUri);
            if (base64Image != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("Usuarios").document(userID);

                // Atualizar a string Base64 no Firestore
                userRef.update("ProfileImage", base64Image)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(tag, "Imagem salva com sucesso!");
                            showToast("Imagem atualizada com sucesso!");
                            changeImageButton.setEnabled(true);

                            // Atualizar a imagem na visualização imediatamente após a atualização do Firestore
                            // Convertendo de Base64 para Bitmap para o Glide
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Glide.with(Profile.this).load(decodedByte).into(fotoPerfil);
                        })
                        .addOnFailureListener(e -> {
                            Log.e(tag, "Erro ao salvar imagem", e);
                            changeImageButton.setEnabled(true);
                        });
            } else {
                Log.e(tag, "Falha na conversão da imagem para Base64.");
            }
        } else {
            Log.e(tag, "A URI da imagem está nula.");
        }
    }


    private String convertImageUriToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Reduzir o tamanho da imagem
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // Ajuste a qualidade aqui (0-100)
            byte[] bytes = baos.toByteArray();

            // Verifica se o tamanho é menor que 1MB
            if (bytes.length > 1048576) { // 1MB em bytes
                Log.e(tag, "Imagem ainda é maior que 1MB após compressão.");
                showToast("É necessário uma imagem mais leve.");
                return null; // Retorna null se a imagem não couber
            }

            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e(tag, "Erro ao converter a imagem para Base64", e);
            return null;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void carregandoBotoesAppCompatImageButton(int buttonId, Class<?> targetActivity) {
        MaterialButton button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, targetActivity);
            startActivity(intent);
            finish();
        });
    }
}
