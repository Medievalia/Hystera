package com.example.myapplication.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Cadastrar2 extends AppCompatActivity {
    String usuarioId;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_2);

        ImageButton voltar = findViewById(R.id.back_button);
        AppCompatButton seguinte =  findViewById(R.id.btn_seguinte);

        CalendarView calendarView = findViewById(R.id.calendarView);


        seguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalvarDados(calendarView);
                Intent intent = new Intent(Cadastrar2.this, Cadastrar3.class);
                startActivity(intent);
            }
        });
    }

    private void SalvarDados(CalendarView calendarView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dadosParaAtualizar = new HashMap<>();

        dadosParaAtualizar.put("UltimaMenstruacao", GetDataSelecionada(calendarView));

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

        // Atualizar o documento com o novo campo usando merge para preservar os dados existentes
        documentReference.set(dadosParaAtualizar, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Ação de sucesso ao salvar os dados
                        Log.d("TAG", "Escolha salva com sucesso!");

                        // Após salvar a escolha, você pode navegar para a próxima tela se necessário
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Lidar com falhas ao salvar os dados
                        Log.e("TAG", "Erro ao salvar a escolha.", e);
                    }
                });
    }
    public String GetDataSelecionada(CalendarView calendarView) {
        final Calendar selectedCalendar = Calendar.getInstance();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedCalendar.set(year, month, dayOfMonth);
            }
        });

        // Criar um objeto SimpleDateFormat para formatar a data
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Obter a data formatada no formato desejado (dia/mês/ano)
        return dateFormat.format(selectedCalendar.getTime());
    }


    //botão voltar
    public void onImageButtonClick(View view) {
        // Este método é chamado quando o ImageButton é clicado
        Intent intent = new Intent(Cadastrar2.this, Cadastrar.class);
        startActivity(intent);
    }
}

