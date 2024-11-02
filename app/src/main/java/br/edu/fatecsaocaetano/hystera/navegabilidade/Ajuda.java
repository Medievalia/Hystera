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
    private ImageView arrowIcon1, arrowIcon2, arrowIcon3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajuda);

        // Inicialização dos elementos
        backButton = findViewById(R.id.back_button);
        pergunta1 = findViewById(R.id.pergunta1);
        pergunta2 = findViewById(R.id.pergunta2);
        pergunta3 = findViewById(R.id.pergunta3);
        txtAnswer1 = findViewById(R.id.txt_answer1);
        txtAnswer2 = findViewById(R.id.txt_answer2);
        txtAnswer3 = findViewById(R.id.txt_answer3);

        // Referências para os ícones de seta
        arrowIcon1 = findViewById(R.id.arrow_icon1);
        arrowIcon2 = findViewById(R.id.arrow_icon2);
        arrowIcon3 = findViewById(R.id.arrow_icon3);

        // Definindo os ouvintes para cada pergunta
        setUpQuestionClickListeners();

        // Ouvinte para o botão Voltar
        backButton.setOnClickListener(v -> finish());
    }

    private void setUpQuestionClickListeners() {
        arrowIcon1.setOnClickListener(v -> toggleAnswer(txtAnswer1, arrowIcon1));
        arrowIcon2.setOnClickListener(v -> toggleAnswer(txtAnswer2, arrowIcon2));
        arrowIcon3.setOnClickListener(v -> toggleAnswer(txtAnswer3, arrowIcon3));

        pergunta1.setOnClickListener(v -> toggleAnswer(txtAnswer1, arrowIcon1));
        pergunta2.setOnClickListener(v -> toggleAnswer(txtAnswer2, arrowIcon2));
        pergunta3.setOnClickListener(v -> toggleAnswer(txtAnswer3, arrowIcon3));
    }

    private void toggleAnswer(TextView answerTextView, ImageView arrowIcon) {
        if (answerTextView.getVisibility() == View.VISIBLE) {
            answerTextView.setVisibility(View.GONE);
            animateArrow(arrowIcon, 0f);
        } else {
            answerTextView.setVisibility(View.VISIBLE);
            animateArrow(arrowIcon, 180f);
        }
    }

    private void animateArrow(ImageView arrowIcon, float targetRotation) {
        arrowIcon.animate()
                .rotation(targetRotation)
                .setDuration(300)
                .start();
    }
}

