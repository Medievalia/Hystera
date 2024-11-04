package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import br.edu.fatecsaocaetano.hystera.R;

public class Annotations extends AppCompatActivity {
    private static final String tag = "AnnotationsClass"; // Tag para logging
    private GridLayout gridLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacao);

        //navegação e menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper menuHelper = new BottomNavigationHelper();
        menuHelper.setNavigationFocus(bottomNavigationView, R.id.nav_anota);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    startActivity(new Intent(Annotations.this, Notes.class));
                    startActivity(new Intent(Annotations.this, Annotations.class));
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(Annotations.this, CalendaryCycle.class));
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

        Log.d(tag, "Verificando e criando coleção de notas.");
        // Inicia a coleção "Notes" se não existir
        verifyAndCreateNotesCollection();

        Log.d(tag, "Carregando notas existentes.");
        // Carrega as notas existentes do Firestore
        loadExistingNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza as anotações ao retornar à atividade
        Log.d(tag, "Atualizando notas existentes.");
        loadExistingNotes();
    }

    private void verifyAndCreateNotesCollection() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference notesCollection = db.collection("Usuarios").document(userId).collection("Notes");

        // Adicionando um documento vazio para garantir que a coleção seja criada
        notesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                Log.d(tag, "Coleção 'Notes' criada com sucesso!");
            } else if (task.isSuccessful()) {
                Log.d(tag, "Coleção 'Notes' já existe.");
            } else {
                Log.e(tag, "Erro ao verificar a coleção 'Notes': " + task.getException().getMessage());
            }
        });
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
                for (QueryDocumentSnapshot document : querySnapshot) {
                    String noteId = document.getId();

                    // Obtendo os dados da nota
                    String title = document.getString("title");
                    String description = document.getString("description");

                    // Verifica se o cartão já existe antes de criar
                    boolean cardExists = false;
                    for (int i = 0; i < gridLayout.getChildCount(); i++) {
                        View child = gridLayout.getChildAt(i);
                        if (child instanceof MaterialCardView) {
                            TextView existingTitleView = (TextView) ((LinearLayout) ((MaterialCardView) child).getChildAt(0)).getChildAt(0);
                            if (existingTitleView.getText().toString().equals(title)) {
                                cardExists = true;
                                break;
                            }
                        }
                    }

                    // Se o cartão não existir, cria um novo
                    if (!cardExists) {
                        createNoteCard(noteId, title, description);
                    }
                }
                // Adiciona o botão "+" como o último item após carregar todas as notas
                addButtonAdd();
            } else {
                Log.e(tag, "Erro ao carregar notas: " + task.getException().getMessage());
            }
        });
    }


    private void adicionarNovoQuadradoBranco() {
        // Gera um ID único no formato MMAAAA-DDMMSSMMM
        String noteId = generateNoteId();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference notesCollection = db.collection("Usuarios").document(userId).collection("Notes");

        // Captura a data atual para a annotationDate
        Date currentDate = new Date();

        // Cria um mapa para armazenar os dados da nova nota
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("title", "Título da Nota"); // Placeholder, você pode substituir por um EditText
        noteData.put("description", "Descrição da Nota"); // Placeholder, você pode substituir por um EditText
        noteData.put("annotationDate", currentDate); // Usando a data de criação

        // Adiciona a nova nota na coleção
        notesCollection.document(noteId).set(noteData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(tag, "Nota adicionada com sucesso! ID da nota: " + noteId);
                createNoteCard(noteId, "Título da Nota", "Descrição da Nota..."); // Cria o cartão da nota
                addButtonAdd(); // Adiciona o botão "+" novamente
            } else {
                Log.e(tag, "Erro ao adicionar a nota: " + task.getException().getMessage());
            }
        });
    }

    private String generateNoteId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMyyyy-ddmmssSSS", Locale.getDefault());
        return dateFormat.format(new Date());
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
        descriptionView.setText(description != null ? description : "Descrição da Nota..."); // Usar descrição real ou um padrão
        descriptionView.setTextColor(0xFF212021);
        descriptionView.setTextSize(12); // Tamanho da fonte da descrição
        descriptionView.setEllipsize(TextUtils.TruncateAt.END); // Adiciona "..." se o texto for longo
        descriptionView.setMaxLines(2); // Limita a 2 linhas

        // Adiciona título e descrição ao LinearLayout
        contentLayout.addView(titleView);
        contentLayout.addView(descriptionView);

        // Adiciona o LinearLayout ao cartão
        novoCartao.addView(contentLayout);

        // Adiciona um listener para abrir a view de edição da anotação
        novoCartao.setOnClickListener(v -> openEditAnnotation(noteId, title, description));

        // Adiciona o cartão à GridLayout
        gridLayout.addView(novoCartao);
    }

    private void openEditAnnotation(String noteId, String title, String description) {
        Intent intent = new Intent(this, EditAnnotationActivity.class);
        intent.putExtra("NOTE_ID", noteId); // Passa o ID da nota para a nova activity
        intent.putExtra("NOTE_TITLE", title); // Passa o título da nota
        intent.putExtra("NOTE_DESCRIPTION", description); // Passa a descrição da nota
        startActivity(intent);
    }

    private void addButtonAdd() {
        // Remove qualquer botão "+" existente antes de adicionar um novo
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child instanceof MaterialButton && ((MaterialButton) child).getText().equals("+")) {
                gridLayout.removeViewAt(i);
                break; // Sai do loop após remover o botão
            }
        }

        // Cria um novo botão "+"
        MaterialButton buttonAdd = new MaterialButton(this);

        // Definindo as dimensões do botão para serem iguais às dos cartões
        int buttonWidth = (int) getResources().getDimension(R.dimen.card_width);
        int buttonHeight = (int) getResources().getDimension(R.dimen.card_height);

        // Aplicando layout params ao botão
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(8, 8, 8, 8);
        params.width = buttonWidth; // Largura do botão igual à do cartão
        params.height = buttonHeight; // Altura do botão igual à do cartão
        buttonAdd.setLayoutParams(params);

        buttonAdd.setText("+");
        buttonAdd.setBackgroundColor(getResources().getColor(R.color.blue)); // Cor de fundo
        buttonAdd.setCornerRadius(28);
        buttonAdd.setTextColor(getResources().getColor(android.R.color.white)); // Cor do texto

        // Adiciona o listener para adicionar uma nova nota
        buttonAdd.setOnClickListener(v -> adicionarNovoQuadradoBranco());

        // Adiciona o botão à GridLayout
        gridLayout.addView(buttonAdd);
    }
}
