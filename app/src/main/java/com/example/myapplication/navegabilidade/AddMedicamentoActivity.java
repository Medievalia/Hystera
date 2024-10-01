package com.example.myapplication.navegabilidade;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Calendar;

public class AddMedicamentoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1; // Constante para a seleção de imagem
    private TextInputEditText inputHora, inputNome, inputDosagem, inputInstrucoes, inputPosologia;
    // Campo de data
    private TextInputEditText inputDataInicio = findViewById(R.id.edit_data_inicio), inputHoraInicio; // Novos campos
    private ImageView imageViewSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicamento);

        // Inicialização dos campos de entrada
        TextInputLayout inputHoraLayout = findViewById(R.id.input_posologia);
        inputHora = findViewById(R.id.edit_hora);
        inputNome = findViewById(R.id.input_nome_medicacao);
        inputDosagem = findViewById(R.id.input_dosagem);
        inputInstrucoes = findViewById(R.id.input_descricao);
        inputPosologia = findViewById(R.id.input_posologia);

        inputHoraInicio = findViewById(R.id.edit_hora_inicio); // Campo de hora

        MaterialButton backButton = findViewById(R.id.back_button); // Botão Voltar
        MaterialButton buttonSalvar = findViewById(R.id.button_salvar); // Botão Salvar

        // Configurar o TimePicker para o campo de hora
        inputHoraLayout.setEndIconOnClickListener(v -> showTimePicker());

        // Ação do botão de data de início
        inputDataInicio.setOnClickListener(v -> showDatePicker());

        // Ação do botão Voltar
        backButton.setOnClickListener(v -> finish()); // Finaliza a Activity e volta à anterior

        // Ação do botão Salvar
        buttonSalvar.setOnClickListener(v -> {
            String nome = inputNome.getText().toString();
            String dosagem = inputDosagem.getText().toString();
            String instrucoes = inputInstrucoes.getText().toString();
            String posologia = inputPosologia.getText().toString();
            String hora = inputHora.getText().toString();
            String dataInicio = inputDataInicio.getText().toString();
            String horaInicio = inputHoraInicio.getText().toString();

            // Aqui você pode salvar os dados no banco de dados ou na lista de medicamentos

            // Voltar para a Activity anterior
            finish();
        });
    }

    private void showDatePicker() {
        // Obtendo a data atual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Criando o DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Formatar a data selecionada
                    String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    inputDataInicio.setText(formattedDate); // Definindo a data selecionada no campo
                }, year, month, day);

        // Exibir o DatePickerDialog
        datePickerDialog.show();
    }

    private void showTimePicker() {
        // Obtendo a hora atual
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Criando o TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    // Formatar a hora selecionada
                    String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    inputHora.setText(formattedTime); // Definindo a hora selecionada no campo
                }, hour, minute, true);

        // Exibir o TimePickerDialog
        timePickerDialog.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imageViewSelected.setVisibility(View.VISIBLE); // Tornar a ImageView visível
            try {
                // Carregar a imagem na ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageViewSelected.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
