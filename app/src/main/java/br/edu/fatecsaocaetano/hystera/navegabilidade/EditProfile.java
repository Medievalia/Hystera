package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import br.edu.fatecsaocaetano.hystera.R;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    private EditText editNome, editEmail, editCelular;
    private Spinner spinnerMetodoContraceptivo;
    private ImageView imgFotoPerfil;
    private String[] mensagens = {"Preencha todos os campos", "Alteração realizada com sucesso!"};
    private String usuarioId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private MaterialButton changeImageButton;
    private boolean methoduse = false;
    private boolean usepill = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        inicializarComponentes();

        carregarDadosUsuario(); // Carregar dados do usuário ao abrir a tela
        configurarBotaoAjuda();
        configurarBotaoVoltar();
        configurarSpinner();
        configurarBotaoSalvar();
        configurarBotaoChangeImage();
        carregarFotoPerfil();
    }

    private void inicializarComponentes() {
        editNome = findViewById(R.id.edit_nome);
        editEmail = findViewById(R.id.edit_email);
        editCelular = findViewById(R.id.edit_celular);
        spinnerMetodoContraceptivo = findViewById(R.id.spinner_metodo_contraceptivo);
        imgFotoPerfil = findViewById(R.id.img_foto_perfil);
        changeImageButton = findViewById(R.id.btn_changeImage);
    }

    private void configurarBotaoAjuda() {
        MaterialButton ajuda = findViewById(R.id.help_button);
        ajuda.setOnClickListener(v -> startActivity(new Intent(EditProfile.this, Help.class)));
    }

    private void configurarBotaoVoltar() {
        MaterialButton voltar = findViewById(R.id.voltar_button);
        voltar.setOnClickListener(v -> startActivity(new Intent(EditProfile.this, Profile.class)));
    }

    private void configurarSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.metodos_contraceptivos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodoContraceptivo.setAdapter(adapter);

        spinnerMetodoContraceptivo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String metodoSelecionado = parent.getItemAtPosition(position).toString();
                Log.d("Spinner Selection", "Método selecionado: " + metodoSelecionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não faz nada
            }
        });
    }

    private void configurarBotaoSalvar() {
        AppCompatButton salvar = findViewById(R.id.btn_enviar);
        salvar.setOnClickListener(v -> {
            String nome = editNome.getText().toString();
            String email = editEmail.getText().toString();
            String celular = editCelular.getText().toString();
            String metodo = spinnerMetodoContraceptivo.getSelectedItem().toString();

            if (nome.isEmpty() || email.isEmpty() || celular.isEmpty() || metodo.isEmpty()) {
                Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            } else {
                salvarDados(v, nome, email, celular, converterMetodoParaSalvar(metodo));
            }
        });
    }

    private void configurarBotaoChangeImage() {
        changeImageButton.setOnClickListener(v -> openGallery());
    }

    private void carregarDadosUsuario() {
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                editNome.setText(documentSnapshot.getString("Name"));
                editEmail.setText(documentSnapshot.getString("Email"));
                editCelular.setText(documentSnapshot.getString("Phone"));
                String metodo = documentSnapshot.getString("Method");

                // Converte o método armazenado para o formato exibido no Spinner
                String metodoParaExibir = converterMetodoParaExibir(metodo);

                // Define o método contraceptivo no Spinner
                if (metodoParaExibir != null) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                            R.array.metodos_contraceptivos, android.R.layout.simple_spinner_item);
                    spinnerMetodoContraceptivo.setAdapter(adapter);
                    int position = adapter.getPosition(metodoParaExibir);
                    spinnerMetodoContraceptivo.setSelection(position);
                }
            }
        }).addOnFailureListener(e -> Log.e("EditProfile", "Erro ao carregar os dados do usuário", e));
    }

    private void carregarFotoPerfil() {
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String base64Image = documentSnapshot.getString("UserImage");
                if (base64Image != null) {
                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Glide.with(EditProfile.this).load(decodedByte).into(imgFotoPerfil);
                }
            }
        }).addOnFailureListener(e -> Log.e("EditProfile", "Erro ao carregar a foto do perfil", e));
    }

    private void salvarDados(View v, String nome, String email, String celular, String metodo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("Name", nome);
        usuarios.put("Email", email);
        usuarios.put("Phone", celular);
        usuarios.put("Method", metodo);

        if(!metodo.toString().equals("NAO_UTILIZA")){
            methoduse = true;
        }

        if (metodo.toString().equals("PILULA")) {
            usepill = true;
        }
        usuarios.put("UseMethod", methoduse);
        usuarios.put("Pill", usepill);

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        documentReference.set(usuarios, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("TAG", "Perfil Alterado com Sucesso!");
                    Intent intent = new Intent(EditProfile.this, TimeLine.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Log.e("TAG", "Erro ao salvar as alterações", e));
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
            Log.d("EditProfile", "Iniciando upload da imagem: " + imageUri.toString());

            // Converter a imagem em uma string Base64
            String base64Image = convertImageUriToBase64(imageUri);
            if (base64Image != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("Usuarios").document(usuarioId);

                // Atualizar a string Base64 no Firestore
                userRef.update("UserImage", base64Image)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("EditProfile", "Imagem salva com sucesso!");
                            showToast("Imagem atualizada com sucesso!");

                            // Atualizar a imagem na visualização imediatamente após a atualização do Firestore
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            Glide.with(EditProfile.this).load(decodedByte).into(imgFotoPerfil);
                        })
                        .addOnFailureListener(e -> Log.e("EditProfile", "Erro ao atualizar a imagem", e));
            }
        }
    }

    private String convertImageUriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e("EditProfile", "Erro ao converter a imagem para Base64", e);
            return null;
        }
    }

    private String converterMetodoParaSalvar(String metodo) {
        switch (metodo) {
            case "PRESERVATIVO":
                return "PRESERVATIVO";
            case "PÍLULA":
                return "PILULA";
            case "DIU":
                return "DIU";
            case "INJEÇÃO":
                return "INJECAO";
            case "IMPLANTE HORMONAL":
                return "IMPLANTE_HORMONAL";
            case "OUTROS":
                return "OUTROS";
            case "NÃO UTILIZA":
                return "NAO_UTILIZA"; //
            default:
                return null;
        }
    }

    private String converterMetodoParaExibir(String metodo) {
        switch (metodo) {
            case "PRESERVATIVO":
                return "PRESERVATIVO";
            case "PÍLULA":
                return "PILULA";
            case "DIU":
                return "DIU";
            case "INJECAO":
                return "INJEÇÃO";
            case "IMPLANTE_HORMONAL":
                return "IMPLANTE HORMONAL"; // Observe o espaço
            case "OUTROS":
                return "OUTROS";
            case "NAO_UTILIZA":
                return "NÃO UTILIZA"; // Observe o espaço
            default:
                return null; // Retorna nulo se o método não for reconhecido
        }
    }

    private void showToast(String message) {
        Toast.makeText(EditProfile.this, message, Toast.LENGTH_SHORT).show();
    }
}
