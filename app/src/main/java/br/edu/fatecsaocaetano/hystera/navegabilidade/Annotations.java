package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import androidx.appcompat.app.AppCompatActivity;
import br.edu.fatecsaocaetano.hystera.R;

public class Annotations extends AppCompatActivity {
    GridLayout gridLayout;
    MaterialCardView cardReferencia; // Para armazenar o cartão de referência
    MaterialButton buttonAdd; // Declaração do botão de adicionar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacao);

        gridLayout = findViewById(R.id.gridLayout);
        buttonAdd = findViewById(R.id.button_add); // Inicialização do botão de adicionar
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarNovoQuadradoBranco();
            }
        });

        // Referência ao cartão existente (nota1)
        cardReferencia = findViewById(R.id.nota1);
    }

    private void adicionarNovoQuadradoBranco() {
        // Cria um novo MaterialCardView programaticamente
        MaterialCardView novoCartao = new MaterialCardView(this);

        // Copiar LayoutParams do cartão existente
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(cardReferencia.getLayoutParams());
        params.setMargins(8, 8, 8, 8);
        novoCartao.setLayoutParams(params);

        // Definir a elevação igual à do cartão referência
        novoCartao.setCardElevation(cardReferencia.getCardElevation());

        // Definir o raio igual ao do cartão referência
        float radius = getResources().getDimension(R.dimen.card_corner_radius); // Certifique-se de que esse recurso existe
        novoCartao.setRadius(radius); // Definindo o raio do cartão

        // Definir a cor de fundo como branco
        novoCartao.setCardBackgroundColor(getResources().getColor(android.R.color.white));

        // Encontrar o índice do botão "+" e adicionar o novo cartão antes dele
        int index = gridLayout.indexOfChild(buttonAdd);
        gridLayout.addView(novoCartao, index);
    }
}
