package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import br.edu.fatecsaocaetano.hystera.R;

public class Ajuda extends AppCompatActivity {

    private MaterialButton backButton;
    private LinearLayout pergunta1, pergunta2, pergunta3;
    private TextView txtAnswer1, txtAnswer2, txtAnswer3;
    private ImageView arrowIcon1, arrowIcon2, arrowIcon3; // Adicionando referências para os ícones de seta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda); // Altere o nome para o nome do seu layout

        // Inicialização dos elementos
        backButton = findViewById(R.id.back_button);
        pergunta1 = findViewById(R.id.pergunta1);
        pergunta2 = findViewById(R.id.pergunta2);
        pergunta3 = findViewById(R.id.pergunta3);
        txtAnswer1 = findViewById(R.id.txt_answer1);
        txtAnswer2 = findViewById(R.id.txt_answer2);
        txtAnswer3 = findViewById(R.id.txt_answer3);

        // Referências para os ícones de seta
        arrowIcon1 = findViewById(R.id.arrow_icon1); // Supondo que você tenha um ID correspondente
        arrowIcon2 = findViewById(R.id.arrow_icon2); // Supondo que você tenha um ID correspondente
        arrowIcon3 = findViewById(R.id.arrow_icon3); // Supondo que você tenha um ID correspondente

        // Definindo os ouvintes para cada pergunta
        setUpQuestionClickListeners();

        // Ouvinte para o botão Voltar
        backButton.setOnClickListener(v -> finish());
    }

    private void setUpQuestionClickListeners() {
        // Ouvintes para os ícones de seta
        arrowIcon1.setOnClickListener(v -> toggleAnswer(txtAnswer1));
        arrowIcon2.setOnClickListener(v -> toggleAnswer(txtAnswer2));
        arrowIcon3.setOnClickListener(v -> toggleAnswer(txtAnswer3));

        // Ouvintes para os layouts das perguntas, caso você queira que o clique em qualquer lugar da pergunta também funcione
        pergunta1.setOnClickListener(v -> toggleAnswer(txtAnswer1));
        pergunta2.setOnClickListener(v -> toggleAnswer(txtAnswer2));
        pergunta3.setOnClickListener(v -> toggleAnswer(txtAnswer3));
    }

    private void toggleAnswer(TextView answerTextView) {
        if (answerTextView.getVisibility() == View.VISIBLE) {
            answerTextView.setVisibility(View.GONE);
        } else {
            answerTextView.setVisibility(View.VISIBLE);
        }
    }
}
