package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.edu.fatecsaocaetano.hystera.R;

public class Annotations extends AppCompatActivity {
    private static final String tag = "AnnotationsClass";
    private static final int EDIT_NOTE_REQUEST_CODE = 1; // Código de requisição para editar ou excluir uma nota
    private GridLayout gridLayout;
    private FirebaseFirestore db;
    private Set<String> loadedNoteIds = new HashSet<>(); // Set para controlar as notas carregadas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacao);

        // Navegação e menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper menuHelper = new BottomNavigationHelper();
        menuHelper.setNavigationFocus(bottomNavigationView, R.id.nav_anota);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(Annotations.this, CalendarCycle.class));
                    return true;
                } else if (id == R.id.nav_utero) {
                    startActivity(new Intent(Annotations.this, Informations.class));
                    return true;
                } else if (id == R.id.nav_seekbar) {
                    startActivity(new Intent(Annotations.this, TimeLine.class));
                    return true;
                } else if (id == R.id.nav_medicacao) {
                    startActivity(new Intent(Annotations.this, Medicine.class));
                    return true;
                }
                return false;
            }
        });

        db = FirebaseFirestore.getInstance();
        gridLayout = findViewById(R.id.gridLayout);
        // Verifica se o usuário está autenticado antes de continuar
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.e(tag, "Usuário não autenticado. Encerrando a Activity.");
            return;
        }

        // Carrega as notas existentes
        loadExistingNotes();

        // Adiciona um listener para o botão de adicionar anotação
        findViewById(R.id.add_anotacao).setOnClickListener(v -> createNewNoteAndEdit());
    }

    private void createNewNoteAndEdit() {
        // Cria uma nova nota vazia
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> newNote = new HashMap<>();
        newNote.put("title", "");
        newNote.put("description", "");
        newNote.put("annotationDate", new com.google.firebase.Timestamp(new Date())); // Adiciona a data de criação

        // Cria o documento da nova nota no Firestore
        db.collection("Usuarios")
                .document(userId)
                .collection("Notes")
                .add(newNote)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Depois de criar a nota, obtenha o ID da nota recém-criada
                        String noteId = task.getResult().getId();
                        // Redireciona para a tela de edição com o ID da nova nota
                        Intent intent = new Intent(Annotations.this, EditAnnotationActivity.class);
                        intent.putExtra("noteId", noteId); // Passa o ID da nova nota
                        startActivityForResult(intent, EDIT_NOTE_REQUEST_CODE); // Inicia a activity de edição com código de requisição
                    } else {
                        Log.e(tag, "Erro ao criar nova nota: " + task.getException().getMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Recarrega as notas quando uma nota for excluída ou atualizada
            loadExistingNotes();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarrega as notas sempre que a Activity for retomada (ex: ao voltar da tela de edição ou exclusão)
        loadExistingNotes();
    }

    private void loadExistingNotes() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference notesCollection = db.collection("Usuarios").document(userId).collection("Notes");

        // Limpa o grid antes de recarregar as notas
        gridLayout.removeAllViews();
        loadedNoteIds.clear(); // Limpa os IDs de notas carregadas para evitar duplicações

        // Ordena as notas por annotationDate em ordem decrescente (mais recente primeiro)
        notesCollection.orderBy("annotationDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            // Remove notas vazias antes de adicionar ao grid
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String noteId = document.getId();
                                String title = document.getString("title");
                                String description = document.getString("description");

                                // Verifica se a anotação está vazia (título e descrição vazios)
                                if (title != null && title.isEmpty() && description != null && description.isEmpty()) {
                                    // Exclui a nota vazia do Firestore
                                    db.collection("Usuarios")
                                            .document(userId)
                                            .collection("Notes")
                                            .document(noteId)
                                            .delete()
                                            .addOnCompleteListener(deleteTask -> {
                                                if (deleteTask.isSuccessful()) {
                                                    Log.d(tag, "Nota vazia excluída com sucesso.");
                                                } else {
                                                    Log.e(tag, "Erro ao excluir nota vazia: " + deleteTask.getException().getMessage());
                                                }
                                            });
                                } else {
                                    // Se a nota não for vazia e ainda não foi carregada, cria o cartão
                                    if (!loadedNoteIds.contains(noteId)) {
                                        loadedNoteIds.add(noteId);
                                        createNoteCard(noteId, title, description);
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e(tag, "Erro ao carregar notas: " + task.getException().getMessage());
                    }
                });
    }


    private void createNoteCard(String noteId, String title, String description) {
        // Criação do MaterialCardView
        MaterialCardView novoCartao = new MaterialCardView(this);

        // Estilo do cartão (dimensões, margem, canto arredondado, etc.)
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = (int) getResources().getDimension(R.dimen.card_width);
        params.height = (int) getResources().getDimension(R.dimen.card_height);
        params.setMargins(8, 8, 8, 8); // Margem semelhante ao layout XML

        novoCartao.setLayoutParams(params);
        novoCartao.setCardElevation(16); // Elevação para o sombreamento
        novoCartao.setRadius(16); // Canto arredondado
        novoCartao.setCardBackgroundColor(getResources().getColor(android.R.color.white));

        // Configuração do layout interno
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(16, 16, 16, 16);

        // Título da anotação
        TextView titleView = new TextView(this);
        titleView.setText(title != null ? title : "Título da Nota");
        titleView.setTextColor(Color.parseColor("#8A50FF"));
        titleView.setTextSize(16);
        titleView.setTypeface(ResourcesCompat.getFont(this, R.font.kurale), Typeface.BOLD);

        // Descrição da anotação
        TextView descriptionView = new TextView(this);
        descriptionView.setText(description != null ? description : "Descrição da Nota...");
        descriptionView.setTextColor(Color.parseColor("#212121"));
        descriptionView.setTextSize(14);

        // Adiciona título e descrição ao layout interno
        contentLayout.addView(titleView);
        contentLayout.addView(descriptionView);

        // Adiciona o layout interno ao cartão
        novoCartao.addView(contentLayout);

        // Configura o clique para abrir a tela de edição
        novoCartao.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditAnnotationActivity.class);
            intent.putExtra("noteId", noteId);
            startActivity(intent);
        });

        // Adiciona o cartão ao GridLayout
        gridLayout.addView(novoCartao);
    }
}
