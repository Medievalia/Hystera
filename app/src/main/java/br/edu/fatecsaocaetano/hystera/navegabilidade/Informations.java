package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import br.edu.fatecsaocaetano.hystera.R;

public class Informations extends AppCompatActivity {

    private CarouselAdapter firstAdapter;
    private CarouselAdapter secondAdapter;
    private CarouselAdapter thirdAdapter;
    private List<Noticia> allFirstCarouselItems;
    private List<Noticia> allSecondCarouselItems;
    private List<Noticia> allThirdCarouselItems;

    private ViewPager2 firstCarousel;
    private ViewPager2 secondCarousel;
    private ViewPager2 thirdCarousel;
    private LinearLayout carouselsLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);

        firstCarousel = findViewById(R.id.viewPager);
        secondCarousel = findViewById(R.id.viewPager2);
        thirdCarousel = findViewById(R.id.viewPager3);
        carouselsLayout = findViewById(R.id.carousels_layout);

        // Carrossel 1: Ciclos Menstruais
        allFirstCarouselItems = getDataForFirstCarousel();
        firstAdapter = new CarouselAdapter(this, allFirstCarouselItems);
        firstCarousel.setAdapter(firstAdapter);

        // Carrossel 2: Doenças
        allSecondCarouselItems = getDataForSecondCarousel();
        secondAdapter = new CarouselAdapter(this, allSecondCarouselItems);
        secondCarousel.setAdapter(secondAdapter);

        // Carrossel 3: Métodos Contraceptivos
        allThirdCarouselItems = getDataForThirdCarousel();
        thirdAdapter = new CarouselAdapter(this, allThirdCarouselItems);
        thirdCarousel.setAdapter(thirdAdapter);

        EditText searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterCarousels(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void filterCarousels(String query) {
        List<Noticia> filteredFirstItems = filterItems(query, allFirstCarouselItems);
        List<Noticia> filteredSecondItems = filterItems(query, allSecondCarouselItems);
        List<Noticia> filteredThirdItems = filterItems(query, allThirdCarouselItems);

        firstAdapter.updateItems(filteredFirstItems);
        secondAdapter.updateItems(filteredSecondItems);
        thirdAdapter.updateItems(filteredThirdItems);

        // Exibe os carrosséis de acordo com a visibilidade dos itens filtrados
        firstCarousel.setVisibility(filteredFirstItems.isEmpty() ? ViewPager2.GONE : ViewPager2.VISIBLE);
        secondCarousel.setVisibility(filteredSecondItems.isEmpty() ? ViewPager2.GONE : ViewPager2.VISIBLE);
        thirdCarousel.setVisibility(filteredThirdItems.isEmpty() ? ViewPager2.GONE : ViewPager2.VISIBLE);
    }

    private List<Noticia> filterItems(String query, List<Noticia> items) {
        List<Noticia> filteredItems = new ArrayList<>();
        for (Noticia item : items) {
            if (item.getTitulo().toLowerCase().contains(query.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    private List<Noticia> getDataForFirstCarousel() {
        List<Noticia> noticias = new ArrayList<>();
        //Noticias sobre o ciclo
        noticias.add(new Noticia("Ciclo menstrual explicado", "https://mundoeducacao.uol.com.br/sexualidade/ciclo-menstrual.htm", R.drawable.prototipo_tcc_1_3, "verde"));
        noticias.add(new Noticia("Coletor menstrual ou absorvente: qual o melhor?", "https://cinge.com.br/2022/08/08/coletor-menstrual-ou-absorvente-qual-o-melhor/", R.drawable.prototipo_tcc_1_2, "roxo"));
        noticias.add(new Noticia("Cuidados com a Saúde Menstrual", "https://www.who.int/news-room/fact-sheets/detail/menstrual-health", R.drawable.prototipo_tcc_2_1, "azul"));
        noticias.add(new Noticia("Absorvente ecológico vs descartável: 5 diferenças", "https://www.pantys.com.br/blogs/pantys/absorvente-ecologico-vs-descartavel-5-diferencas", R.drawable.prototipo_tcc_1_4, "roxo"));
        return noticias;
    }

    private List<Noticia> getDataForSecondCarousel() {
        List<Noticia> noticias = new ArrayList<>();
        //Noticias sobre doenças
        noticias.add(new Noticia("Síndrome dos Ovários Policísticos (SOP)", "https://www.who.int/news-room/fact-sheets/detail/polycystic-ovary-syndrome", R.drawable.prototipo_tcc_2_1, "roxo"));
        noticias.add(new Noticia("O que é endometriose e como tratar?", "https://bvsms.saude.gov.br/endometriose/#:~:text=A%20endometriose%20%C3%A9%20uma%20doen%C3%A7a,geral%2C%20devem%20ser%20retiradas%20cirurgicamente.", R.drawable.prototipo_tcc_2_1, "verde"));
        noticias.add(new Noticia("Menopausa e Saúde da Mulher", "https://www.who.int/news-room/fact-sheets/detail/menopause", R.drawable.prototipo_tcc_2_1, "azul"));
        return noticias;
    }

    private List<Noticia> getDataForThirdCarousel() {
        List<Noticia> noticias = new ArrayList<>();
        //Noticias sobre o metodos contraceptivos
        noticias.add(new Noticia("Métodos Contraceptivos: Tipos e Eficácia", "https://www.who.int/news-room/fact-sheets/detail/contraception", R.drawable.prototipo_tcc_1_5, "verde"));
        noticias.add(new Noticia("Pílula anticoncepcional: benefícios e cuidados", "https://www.istockphoto.com/photo/birth-control-pills-and-syringe-on-a-blue-background-contraception-methods-gm1188483140-338235349", R.drawable.prototipo_tcc_1_5, "azul"));
        noticias.add(new Noticia("Preservativo: Como usar corretamente", "https://www.saude.gov.br/saude-de-a-z/contraceptivos", R.drawable.prototipo_tcc_2_1, "roxo"));
        noticias.add(new Noticia("Implantes hormonais: O que você precisa saber", "https://www.mayoclinic.org/healthy-lifestyle/birth-control/experts/faq/faq-20058361", R.drawable.vacina, "verde"));
        noticias.add(new Noticia("Método de esterilização feminina", "https://www.who.int/news-room/fact-sheets/detail/sterilization", R.drawable.prototipo_tcc_2_1, "azul"));
        noticias.add(new Noticia("Pílula do dia seguinte: tudo o que você precisa saber", "https://hilab.com.br/blog/pilula-dia-seguinte/", R.drawable.prototipo_tcc_1_5, "azul"));
        return noticias;
    }
}
