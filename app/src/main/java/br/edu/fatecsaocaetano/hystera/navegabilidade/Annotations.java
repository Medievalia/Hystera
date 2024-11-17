package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
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

import java.util.HashSet;
import java.util.Set;

import br.edu.fatecsaocaetano.hystera.R;

public class Annotations extends AppCompatActivity {
    private static final String tag = "AnnotationsClass"; // Tag para logging
    private GridLayout gridLayout;
    private FirebaseFirestore db;
    private Set<String> loadedNoteIds = new HashSet<>(); // Para controlar notas carregadas e evitar duplicação

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
                    // Não iniciamos a atividade duas vezes, removemos a linha repetida
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

        db = FirebaseFirestore.getInstance(); // Inicializando Firestore
        gridLayout = findViewById(R.id.gridLayout);

        // Verifica se o usuário está autenticado antes de continuar
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.e(tag, "Usuário não autenticado. Encerrando a Activity.");
            return; // Ou use uma Intent para a tela de login
        }

        Log.d(tag, "Carregando notas existentes.");
        // Carrega as notas existentes do Firestore
        loadExistingNotes();
    }

    private void loadExistingNotes() {
        // Limpa o GridLayout antes de carregar novas notas
        gridLayout.removeAllViews();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference notesCollection = db.collection("Usuarios").document(userId).collection("Notes");

        notesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                Log.d(tag, "Notas carregadas com sucesso. Total de notas: " + querySnapshot.size());
                if (querySnapshot != null) {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String noteId = document.getId();

                        // Verifica se a nota já foi carregada para evitar duplicação
                        if (loadedNoteIds.contains(noteId)) {
                            Log.d(tag, "Nota duplicada detectada, ignorando. ID: " + noteId);
                            continue; // Ignora essa nota
                        }

                        // Marca a nota como carregada
                        loadedNoteIds.add(noteId);

                        // Obtendo os dados da nota
                        String title = document.getString("title");
                        String description = document.getString("description");

                        // Cria o cartão da nota
                        createNoteCard(noteId, title, description);
                    }
                }
            } else {
                Log.e(tag, "Erro ao carregar notas: " + task.getException().getMessage());
            }
        });
    }

    private void createNoteCard(String noteId, String title, String description) {
        // Cria um novo MaterialCardView programaticamente
        MaterialCardView novoCartao = new MaterialCardView(this);

        // Defina o tamanho fixo do cartão
        int cardWidth = (int) getResources().getDimension(R.dimen.card_width);
        int cardHeight = (int) getResources().getDimension(R.dimen.card_height);

        // Copiar LayoutParams do botão existente
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(8, 8, 8, 8);
        params.width = cardWidth;
        params.height = cardHeight;
        novoCartao.setLayoutParams(params);

        // Definir a elevação do cartão
        novoCartao.setCardElevation(4);

        // Definir a cor de fundo como branco
        novoCartao.setCardBackgroundColor(getResources().getColor(android.R.color.white));

        // Cria um LinearLayout para organizar título e descrição
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(16, 16, 16, 16); // Adiciona algum preenchimento

        // Adicionar um TextView para o título
        TextView titleView = new TextView(this);
        titleView.setText(title != null ? title : "Título da Nota"); // Usar o título real ou um padrão
        titleView.setTextColor(0xFF212021); // Usando cor direta
        titleView.setTextSize(16); // Tamanho da fonte do título

        // Adicionar um TextView para a descrição
        TextView descriptionView = new TextView(this);
        descriptionView.setText(description != null ? description : "Descrição da Nota...");
        descriptionView.setTextColor(0xFF212021); // Cor da descrição
        descriptionView.setTextSize(14); // Tamanho da fonte da descrição

        // Adicionar o título e a descrição ao layout do conteúdo
        contentLayout.addView(titleView);
        contentLayout.addView(descriptionView);

        // Adicionar o layout de conteúdo ao cartão
        novoCartao.addView(contentLayout);

        // Adiciona o cartão ao GridLayout
        gridLayout.addView(novoCartao);
    }
}
