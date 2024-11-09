package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.edu.fatecsaocaetano.hystera.R;

public class EditAnnotationActivity extends AppCompatActivity {

    private EditText tituloEditText;
    private EditText anotacaoEditText;
    private TextView dataCriacaoTextView;
    private Button btnSalvar;
    private Button btnExcluir;
    private String noteId; // ID da nota
    private FirebaseFirestore db; // Instância do Firestore
    private Notes note; // Instância da nota

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_annotation);

        tituloEditText = findViewById(R.id.titulo);
        anotacaoEditText = findViewById(R.id.anotacao);
        dataCriacaoTextView = findViewById(R.id.dataCriacao);
        btnSalvar = findViewById(R.id.btn_salvar);
        btnExcluir = findViewById(R.id.excluir);

        // Inicializa o Firestore
        db = FirebaseFirestore.getInstance();

        // Receber dados da Intent
        Intent intent = getIntent();
        noteId = intent.getStringExtra("NOTE_ID");

        // Referência para o documento da nota
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference noteRef = db.collection("Usuarios").document(userId).collection("Notes").document(noteId);

        // Buscar a nota no Firestore
        noteRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Carregar os dados da nota
                String titulo = documentSnapshot.getString("title");
                String anotacao = documentSnapshot.getString("description");
                Date annotationDate = documentSnapshot.getDate("annotationDate");

                // Se a data de criação não for nula, formate e exiba
                if (annotationDate != null) {
                    String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(annotationDate);
                    dataCriacaoTextView.setText(formattedDate); // Exibe a data de criação
                }

                // Definir os valores nos campos
                tituloEditText.setText(titulo);
                anotacaoEditText.setText(anotacao);

                // Criar uma instância da nota
                note = new Notes(userId, titulo, anotacao, annotationDate);
            }
        }).addOnFailureListener(e -> {
            // Em caso de erro ao buscar a nota
            System.err.println("Erro ao buscar a nota: " + e.getMessage());
        });

        // Configurar os listeners para os botões
        btnSalvar.setOnClickListener(v -> {
            // Captura os dados atualizados
            String updatedTitle = tituloEditText.getText().toString();
            String updatedDescription = anotacaoEditText.getText().toString();
            Date updatedDate = new Date(); // Atualiza com a data atual ao salvar

            // Atualiza os campos na instância de Notes
            note.setTitle(updatedTitle);
            note.setDescription(updatedDescription);
            note.setAnnotationDate(updatedDate);

            // Edita a nota no Firestore
            editNoteInFirestore(noteId, updatedTitle, updatedDescription, updatedDate);
            // Fecha a Activity após o sucesso
            finish();
        });

        btnExcluir.setOnClickListener(v -> {
            // Lógica para excluir a anotação
            deleteNoteFromFirestore(noteId);
            finish(); // Finaliza a Activity
        });

        // Botão voltar (se necessário)
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }

    private void editNoteInFirestore(String noteId, String title, String description, Date annotationDate) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference noteRef = db.collection("Usuarios").document(userId).collection("Notes").document(noteId);

        noteRef.update("title", title, "description", description, "annotationDate", annotationDate)
                .addOnSuccessListener(aVoid -> System.out.println("Nota atualizada com sucesso!"))
                .addOnFailureListener(e -> System.err.println("Erro ao atualizar a nota: " + e.getMessage()));
    }

    private void deleteNoteFromFirestore(String noteId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference noteRef = db.collection("Usuarios").document(userId).collection("Notes").document(noteId);

        noteRef.delete()
                .addOnSuccessListener(aVoid -> System.out.println("Nota excluída com sucesso!"))
                .addOnFailureListener(e -> System.err.println("Erro ao excluir a nota: " + e.getMessage()));
    }
}
