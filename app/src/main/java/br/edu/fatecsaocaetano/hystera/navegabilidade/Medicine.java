package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Medicine extends AppCompatActivity {

    private static final String tag = "MedicineClass";
    private MaterialButton profileButton;
    private String userID;
    private LinearLayout medicamentosLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicacao);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userID == null) {
            Log.e(tag, "ID do usuário não encontrado!");
            return;
        }

        medicamentosLayout = findViewById(R.id.scrollViewLayout);
        profileButton = findViewById(R.id.button_perfil);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Medicine.this, Profile.class);
                startActivity(intent);
            }
        });

        //navegação e menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper menuHelper = new BottomNavigationHelper();
        menuHelper.setNavigationFocus(bottomNavigationView, R.id.nav_medicacao);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    startActivity(new Intent(Medicine.this, Notes.class));
                    startActivity(new Intent(Medicine.this, Annotations.class));
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(Medicine.this, CalendarCycle.class));
                    return true;
                } else if (id == R.id.nav_utero) {
                    startActivity(new Intent(Medicine.this, Informations.class));
                    return true;
                } else if (id == R.id.nav_seekbar) {
                    startActivity(new Intent(Medicine.this, TimeLine.class));
                    return true;
                } else if (id == R.id.nav_medicacao) {
                    startActivity(new Intent(Medicine.this, Medicine.class));
                    return true;
                }
                return false;
            }
        });
        carregarMedicamentos(db);

        FloatingActionButton addMedicamentoButton = findViewById(R.id.add_medicamento);
        addMedicamentoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Medicine.this, AddMedicine.class);
                startActivity(intent);
            }
        });
    }

    private void carregarMedicamentos(FirebaseFirestore db) {
        db.collection("Usuarios")
                .document(userID)
                .collection("Medicines")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String nome = document.getString("drugName");
                                Timestamp drugDate = document.getTimestamp("drugDate");
                                boolean drugNotification = document.getBoolean("notification");
                                boolean drugSound = document.getBoolean("sound");
                                boolean drugVibration = document.getBoolean("vibration");
                                String dosagem = document.getString("drugAmount");
                                String dosageUnits = document.getString("dosageUnits");
                                long intervaloLong = document.getLong("trigger");
                                int intervalo = (int) intervaloLong;
                                String instrucoes = document.getString("drugDescription");
                                String documentId = document.getId();

                                // Criar e adicionar a view do medicamento
                                addMedicamentoView(nome, dosagem, instrucoes, intervalo, dosageUnits, documentId, drugNotification, drugSound, drugVibration, drugDate);
                            }
                        } else {
                            Log.e(tag, "Nenhuma medicação encontrada para o usuário.");
                        }
                    } else {
                        Log.e(tag, "Erro ao carregar as medicações.", task.getException());
                    }
                });
    }

    private void addMedicamentoView(String nome, String dosagem, String instrucoes, int intervalo, String dosageUnits, String documentId, boolean drugNotification, boolean drugSound, boolean drugVibration, Timestamp drugDate) {
        // Obter referência ao layout pai
        LinearLayout scrollViewLayout = findViewById(R.id.scrollViewLayout);

        // Criar o layout principal do medicamento
        LinearLayout medicamentoLayout = new LinearLayout(this);
        medicamentoLayout.setOrientation(LinearLayout.VERTICAL);
        medicamentoLayout.setPadding(16, 16, 16, 16);

        // Adiciona a imagem do medicamento
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(120) // Converte 120dp para pixels
        );
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.ic_medicamento_foreground);
        imageView.setContentDescription("Imagem do medicamento");
        medicamentoLayout.addView(imageView);

        // Nome do Medicamento
        TextView nomeTextView = new TextView(this);
        nomeTextView.setText("Medicamento: " + nome);
        nomeTextView.setTextSize(18);
        nomeTextView.setTextColor(getResources().getColor(android.R.color.black));
        nomeTextView.setTypeface(null, Typeface.BOLD);
        nomeTextView.setPadding(0, dpToPx(8), 0, 0);
        medicamentoLayout.addView(nomeTextView);

        // Dosagem do Medicamento
        TextView dosagemTextView = new TextView(this);
        dosagemTextView.setText("Dosagem: " + dosagem + " " + dosageUnits);
        dosagemTextView.setTextSize(14);
        dosagemTextView.setPadding(0, dpToPx(4), 0, 0);
        medicamentoLayout.addView(dosagemTextView);

        // Intervalo do Medicamento
        TextView intervalorTextView = new TextView(this);
        intervalorTextView.setText("Intervalo: " + intervalo + " horas");
        intervalorTextView.setTextSize(14);
        intervalorTextView.setPadding(0, dpToPx(4), 0, 0);
        medicamentoLayout.addView(intervalorTextView);

        // Instruções do Medicamento
        TextView instrucoesTextView = new TextView(this);
        instrucoesTextView.setText("Instruções: " + instrucoes);
        instrucoesTextView.setTextSize(14);
        instrucoesTextView.setPadding(0, dpToPx(4), 0, dpToPx(8));
        medicamentoLayout.addView(instrucoesTextView);

        // Botão de opções
        ImageButton opcoesButton = new ImageButton(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = Gravity.END;
        opcoesButton.setLayoutParams(buttonParams);
        opcoesButton.setImageResource(R.drawable.ic_more_vert);
        opcoesButton.setBackgroundResource(android.R.color.transparent);
        opcoesButton.setContentDescription("Mais opções");

        opcoesButton.setOnClickListener(v -> {
            Intent intent = new Intent(Medicine.this, EditMedicine.class);
            intent.putExtra("medicineName", nome);
            intent.putExtra("medicineDosage", dosagem);
            intent.putExtra("medicineDescription", instrucoes);
            intent.putExtra("medicineInterval", intervalo);
            intent.putExtra("medicineDosageUnits", dosageUnits);
            intent.putExtra("documentId", documentId);  // Passando o ID do documento
            intent.putExtra("medicineNotification", drugNotification);
            intent.putExtra("medicineSound", drugSound);
            intent.putExtra("medicineVibration", drugVibration);
            intent.putExtra("medicineTimestampDate", drugDate);

            // Iniciando a atividade
            startActivity(intent);
        });
        medicamentoLayout.addView(opcoesButton);

        // Adicionar ao layout pai
        scrollViewLayout.addView(medicamentoLayout);
    }

    // Método utilitário para converter dp para pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}