package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import br.edu.fatecsaocaetano.hystera.R;

public class Informations extends AppCompatActivity {

    private CarouselAdapter firstAdapter;
    private CarouselAdapter secondAdapter;
    private List<Noticia> allFirstCarouselItems;
    private List<Noticia> allSecondCarouselItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);

        // Configuração do botão de perfil para redirecionar para a Activity Profile
        findViewById(R.id.button_perfil).setOnClickListener(v -> {
            Intent intent = new Intent(Informations.this, Profile.class);
            startActivity(intent); // Abre a Activity Profile
        });

        // Configuração do primeiro carrossel de notícias
        ViewPager2 firstCarousel = findViewById(R.id.viewPager);
        allFirstCarouselItems = getDataForFirstCarousel();
        firstAdapter = new CarouselAdapter(this, allFirstCarouselItems);
        firstCarousel.setAdapter(firstAdapter); // Associa o adaptador ao carrossel

        // Configuração do segundo carrossel de notícias
        ViewPager2 secondCarousel = findViewById(R.id.viewPager2);
        allSecondCarouselItems = getDataForSecondCarousel();
        secondAdapter = new CarouselAdapter(this, allSecondCarouselItems);
        secondCarousel.setAdapter(secondAdapter); // Associa o adaptador ao segundo carrossel

        // Encontrando o EditText de pesquisa e adicionando o TextWatcher
        EditText searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Não precisamos usar esse método
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filtra os itens enquanto o texto é alterado
                filterCarousels(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Não precisamos usar esse método
            }
        });
    }

    /**
     * Função para filtrar os carrosséis com base no texto de pesquisa
     */
    private void filterCarousels(String query) {
        List<Noticia> filteredFirstItems = filterItems(query, allFirstCarouselItems);
        List<Noticia> filteredSecondItems = filterItems(query, allSecondCarouselItems);

        // Atualiza os adaptadores com os itens filtrados
        firstAdapter.updateItems(filteredFirstItems);
        secondAdapter.updateItems(filteredSecondItems);
    }

    /**
     * Função que filtra os itens com base no título
     */
    private List<Noticia> filterItems(String query, List<Noticia> items) {
        List<Noticia> filteredItems = new ArrayList<>();
        for (Noticia item : items) {
            if (item.getTitulo().toLowerCase().contains(query.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    /**
     * Retorna os dados para o primeiro carrossel de notícias.
     */
    private List<Noticia> getDataForFirstCarousel() {
        List<Noticia> noticias = new ArrayList<>();
        noticias.add(new Noticia(
                "Coletor menstrual ou absorvente: qual o melhor?", // Título da notícia
                "https://cinge.com.br/2022/08/08/coletor-menstrual-ou-absorvente-qual-o-melhor/", // URL da notícia
                R.drawable.prototipo_tcc_2_1 // ID da imagem
        ));
        noticias.add(new Noticia(
                "Ciclo menstrual explicado",
                "https://mundoeducacao.uol.com.br/sexualidade/ciclo-menstrual.htm",
                R.drawable.prototipo_tcc_2_1
        ));
        noticias.add(new Noticia(
                "Absorvente ecológico vs descartável: 5 diferenças",
                "https://www.pantys.com.br/blogs/pantys/absorvente-ecologico-vs-descartavel-5-diferencas",
                R.drawable.prototipo_tcc_2_1
        ));
        return noticias; // Retorna a lista de notícias para o primeiro carrossel
    }

    /**
     * Retorna os dados para o segundo carrossel de notícias.
     */
    private List<Noticia> getDataForSecondCarousel() {
        List<Noticia> noticias = new ArrayList<>();
        noticias.add(new Noticia(
                "Pílula do dia seguinte: tudo o que você precisa saber",
                "https://hilab.com.br/blog/pilula-dia-seguinte/",
                R.drawable.prototipo_tcc_2_1 // ID da imagem
        ));
        noticias.add(new Noticia(
                "O que é endometriose e como tratar?",
                "https://bvsms.saude.gov.br/endometriose/#:~:text=A%20endometriose%20%C3%A9%20uma%20doen%C3%A7a,geral%2C%20devem%20ser%20retiradas%20cirurgicamente.",
                R.drawable.prototipo_tcc_2_1
        ));
        return noticias; // Retorna a lista de notícias para o segundo carrossel
    }
}
