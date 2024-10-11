package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.os.Bundle;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;

public class CalendaryCycle extends AppCompatActivity {

    private CalendarView calendarView;
    private long dataInicialMillis;
    private int periodo = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);}

  /*    calendarView = findViewById(R.id.calendarView);

       // Definir um listener para o CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                long selectedMillis = cal.getTimeInMillis();

                // Verificar se o dia selecionado está dentro do intervalo marcado
                if (selectedMillis >= dataInicialMillis && selectedMillis <= dataInicialMillis + (periodo - 1) * 86400000) {
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    Toast.makeText(Calendario.this, "Data selecionada: " + selectedDate + " - Dia marcado!", Toast.LENGTH_SHORT).show();
                } else {
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    Toast.makeText(Calendario.this, "Data selecionada: " + selectedDate + " - Dia não marcado!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Chamar o método para marcar os dias desejados
        marcarDiasNoCalendarView("11/10/2023");

        ImageButton anota = findViewById(R.id.anota);
        anota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendario.this, Anotacoes.class);
                startActivity(intent);
            }
        });

        ImageButton calendario = findViewById(R.id.calendario);
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendario.this, Calendario.class);
                startActivity(intent);
            }
        });

        ImageButton utero = findViewById(R.id.utero);
        utero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendario.this, Informacao.class);
                startActivity(intent);
            }
        });



        ImageButton menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendario.this, Notificacoes.class);
                startActivity(intent);
            }
        });

        ImageButton seekbar1 = findViewById(R.id.seekbar);
        seekbar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Calendario.this, LinhaDoTempo.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void marcarDiasNoCalendarView(String dataString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date dataInicial = sdf.parse(dataString);
            dataInicialMillis = dataInicial.getTime();

            calendarView.setDate(dataInicial.getTime(), true, true); // Marcar o primeiro dia

        } catch (ParseException e) {
            e.printStackTrace();
        }
    } */


}
