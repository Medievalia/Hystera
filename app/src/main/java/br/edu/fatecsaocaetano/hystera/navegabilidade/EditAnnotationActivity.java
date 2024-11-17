package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import br.edu.fatecsaocaetano.hystera.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditAnnotationActivity extends AppCompatActivity {
    private static final String tag = "EditAnnotationActivity"; // Tag para logging
    private FirebaseFirestore db;
    private EditText titleEditText, descriptionEditText;
    private TextView dataCriacaoTextView;
    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_annotation);

        db = FirebaseFirestore.getInstance();

        // Recebe o ID da nota passada pela Intent
        noteId = getIntent().getStringExtra("noteId");

        // Inicializa os campos de edição
        titleEditText = findViewById(R.id.titulo);
        descriptionEditText = findViewById(R.id.anotacao);
        dataCriacaoTextView = findViewById(R.id.dataCriacao);

        if (noteId != null) {
            // Carrega a nota para edição
            loadNoteForEditing(noteId);
        }

        // Configura o botão de salvar
        Button saveButton = findViewById(R.id.btn_salvar);
        saveButton.setOnClickListener(v -> saveNote());

        // Configura o botão de excluir
        Button deleteButton = findViewById(R.id.excluir); // Assumindo que o ID é 'excluir' no layout
        deleteButton.setOnClickListener(v -> deleteNote());
    }

    private void loadNoteForEditing(String noteId) {
        // Pegue o ID do usuário autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(tag, "Carregando dados para a nota com ID: " + noteId + " para o usuário com ID: " + userId);

        db.collection("Usuarios")
                .document(userId) // Usando o ID do usuário atual
                .collection("Notes")
                .document(noteId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        String title = document.getString("title");
                        String description = document.getString("description");

                        // Verifica se a data de anotação está presente e é do tipo Timestamp
                        Object annotationDate = document.get("annotationDate");

                        if (annotationDate instanceof com.google.firebase.Timestamp) {
                            com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) annotationDate;
                            Date date = timestamp.toDate();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Formato "DD/MM/YYYY"
                            String formattedDate = dateFormat.format(date);
                            dataCriacaoTextView.setText("Data de Criação: " + formattedDate);
                            Log.d(tag, "Data de criação: " + formattedDate); // Log para verificar se a data foi recuperada corretamente
                        } else {
                            // Se o campo annotationDate não for do tipo Timestamp ou estiver ausente
                            Log.e(tag, "annotationDate não é um Timestamp válido.");
                            dataCriacaoTextView.setText("Data de Criação: Data inválida");
                        }

                        // Preenche os campos com os dados da nota
                        titleEditText.setText(title);
                        descriptionEditText.setText(description);
                    } else {
                        Log.e(tag, "Erro ao carregar a nota: " + task.getException().getMessage());
                    }
                });
    }

    private void saveNote() {
        String newTitle = titleEditText.getText().toString();
        String newDescription = descriptionEditText.getText().toString();

        // Pegue o ID do usuário autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Usuarios")
                .document(userId) // Usando o ID do usuário atual
                .collection("Notes")
                .document(noteId)
                .update("title", newTitle, "description", newDescription)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(tag, "Nota atualizada com sucesso");
                        finish(); // Fecha a atividade e volta
                    } else {
                        Log.e(tag, "Erro ao atualizar nota: " + task.getException().getMessage());
                    }
                });
    }

    private void deleteNote() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Usuarios")
                .document(userId)
                .collection("Notes")
                .document(noteId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(tag, "Nota excluída com sucesso.");
                        // Define o resultado de volta para a Activity anterior (Annotations)
                        setResult(RESULT_OK);
                        finish();  // Fecha a atividade e volta
                    } else {
                        Log.e(tag, "Erro ao excluir nota: " + task.getException().getMessage());
                    }
                });
    }


}
