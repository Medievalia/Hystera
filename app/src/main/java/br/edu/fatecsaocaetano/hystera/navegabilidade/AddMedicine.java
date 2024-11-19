package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.edu.fatecsaocaetano.hystera.R;

public class AddMedicine extends AppCompatActivity {

    private String userID;
    private final String tag = "AddMedicineClass";
    private boolean isSoundEnabled = false;
    private boolean isNotificationEnabled = false;
    private boolean isVibrationEnabled = false;
    private MaterialButton voltarButton;
    private MaterialButton buttonSalvar;
    private Spinner spinnerDosagem;
    private String dosagemSelecionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicamento);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userID == null) {
            Log.e(tag, "ID do usuário não encontrado!");
            return;
        }

        spinnerDosagem = findViewById(R.id.medicacao_spinner);
        buttonSalvar = findViewById(R.id.button_salvar);
        voltarButton = findViewById(R.id.back_button);
        configurarSpinner();

        voltarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(AddMedicine.this, Medicine.class);
                startActivity(intent);
            }
        });

        // Referências aos switches
        SwitchMaterial soundSwitch = findViewById(R.id.soundSwitch);
        SwitchMaterial notificationSwitch = findViewById(R.id.notificationSwitch);
        SwitchMaterial vibrationSwitch = findViewById(R.id.vibrationSwitch);

        // Obtendo os TextInputLayouts corretamente
        TextInputLayout layoutDosagem = findViewById(R.id.input_dosagem);
        TextInputLayout layoutNome = findViewById(R.id.input_nome_medicacao);
        TextInputLayout layoutDescription = findViewById(R.id.input_descricao);
        TextInputLayout layoutIntervalo = findViewById(R.id.input_posologia);
        TextInputLayout layoutDataInicio = findViewById(R.id.input_data_inicio);
        TextInputLayout layoutHoraInicio = findViewById(R.id.input_hora_inicio);

        // Obtendo os TextInputEditTexts a partir dos TextInputLayouts
        TextInputEditText editNome = layoutDosagem != null ? (TextInputEditText) layoutNome.getEditText() : null;
        TextInputEditText editDescription = layoutDescription != null ? (TextInputEditText) layoutDescription.getEditText() : null;
        TextInputEditText editDosagem = layoutDosagem != null ? (TextInputEditText) layoutDosagem.getEditText() : null;
        TextInputEditText editIntervalo = layoutIntervalo != null ? (TextInputEditText) layoutIntervalo.getEditText() : null;
        TextInputEditText editDataInicio = layoutDataInicio != null ? (TextInputEditText) layoutDataInicio.getEditText() : null;
        TextInputEditText editHoraInicio = layoutHoraInicio != null ? (TextInputEditText) layoutHoraInicio.getEditText() : null;


        // Aplicando máscaras
        if (editDosagem != null) {
            editDosagem.addTextChangedListener(Mask.insert("#####", editDosagem));
        }

        if (editIntervalo != null) {
            editIntervalo.addTextChangedListener(Mask.insert("##", editIntervalo));
        }

        if (editDataInicio != null) {
            editDataInicio.setFocusable(false);
            editDataInicio.setClickable(true);

            editDataInicio.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                            editDataInicio.setText(formattedDate);
                        }, year, month, day);
                datePickerDialog.show();
            });
        }

        if (editHoraInicio != null) {
            editHoraInicio.setFocusable(false);
            editHoraInicio.setClickable(true);

            editHoraInicio.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        (TimePicker view, int selectedHour, int selectedMinute) -> {
                            // Formatando e definindo a hora selecionada
                            String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                            editHoraInicio.setText(formattedTime);
                        }, hour, minute, true); // true para formato 24h
                timePickerDialog.show();
            });
        }

        // Listener para SoundSwitch
        if (soundSwitch != null) {
            soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d(tag, "SoundSwitch: " + isChecked);
                isSoundEnabled = soundSwitch != null && soundSwitch.isChecked();
            });
        }

        // Listener para NotificationSwitch
        if (notificationSwitch != null) {
            notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d(tag, "NotificationSwitch: " + isChecked);
                isNotificationEnabled = notificationSwitch != null && notificationSwitch.isChecked();
            });
        }

        // Listener para VibrationSwitch
        if (vibrationSwitch != null) {
            vibrationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Log.d(tag, "VibrationSwitch: " + isChecked);
                isVibrationEnabled = vibrationSwitch != null && vibrationSwitch.isChecked();
            });
        }

        buttonSalvar.setOnClickListener(v -> {
            if (editDosagem == null || editIntervalo == null || editDataInicio == null || editHoraInicio == null || editNome == null || editDescription == null) {
                Toast.makeText(this, "Um ou mais campos não foram inicializados!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtendo valores dos campos
            String drugName = editNome.getText() != null ? editNome.getText().toString().trim() : "";
            String drugDescription = editDescription.getText() != null ? editDescription.getText().toString().trim() : "";
            String drugAmount = editDosagem.getText() != null ? editDosagem.getText().toString().trim() : "";
            String intervalText = editIntervalo.getText() != null ? editIntervalo.getText().toString().trim() : "";
            String startDateText = editDataInicio.getText() != null ? editDataInicio.getText().toString().trim() : "";
            String startTimeText = editHoraInicio.getText() != null ? editHoraInicio.getText().toString().trim() : "";

            // Impedir salvar se "Unidade de Dosagem" for a opção selecionada
            if (dosagemSelecionada.equals("Unidade de Dosagem")) {
                Toast.makeText(this, "A unidade de dosagem deve ser preenchida corretamente!", Toast.LENGTH_SHORT).show();
                return;
            } else if(dosagemSelecionada.isEmpty() || drugName.isEmpty() || drugDescription.isEmpty() || drugAmount.isEmpty() || intervalText.isEmpty() || startDateText.isEmpty() || startTimeText.isEmpty()){
                Toast.makeText(this, "Não é possível cadastrar um medicamento com campos vazios!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int interval = Integer.parseInt(intervalText);
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Timestamp drugDate = new Timestamp(dateTimeFormat.parse(startDateText + " " + startTimeText));

                // Criando o objeto Drug
                Drug newDrug = new Drug(
                        drugName,
                        drugDescription,
                        drugAmount,
                        dosagemSelecionada,
                        drugDate,
                        interval,
                        userID,
                        isNotificationEnabled,
                        isSoundEnabled,
                        isVibrationEnabled
                );

                // Salvando no Firebase
                newDrug.salvarDrugNoFirebase();

                // Exibir mensagem de sucesso
                Toast.makeText(this, "Medicação salva com sucesso!", Toast.LENGTH_SHORT).show();

                // Limpar campos
                editNome.setText("");
                editDescription.setText("");
                editDosagem.setText("");
                editIntervalo.setText("");
                editDataInicio.setText("");
                editHoraInicio.setText("");
                spinnerDosagem.setSelection(0);  // Limpa o spinner e mantém a opção inicial
            } catch (Exception e) {
                Log.e(tag, "Erro ao criar ou salvar o medicamento: " + e.getMessage(), e);
            }
        });
    }

    private void configurarSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dosagem, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDosagem.setAdapter(adapter);
        spinnerDosagem.setSelection(0);

        spinnerDosagem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dosagemSelecionada = parent.getItemAtPosition(position).toString();
                if (dosagemSelecionada.equals("Unidade de Dosagem")) {
                    dosagemSelecionada = "";
                }
                Log.d(tag, "Dosagem selecionada: " + dosagemSelecionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não fazer nada
            }
        });
    }
}
