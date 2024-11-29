package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import br.edu.fatecsaocaetano.hystera.R;

public class Informations extends AppCompatActivity {

    private CarouselAdapter firstAdapter;
    private CarouselAdapter secondAdapter;
    private CarouselAdapter thirdAdapter;
    private List<Noticia> allFirstCarouselItems = new ArrayList<>();
    private List<Noticia> allSecondCarouselItems = new ArrayList<>();
    private List<Noticia> allThirdCarouselItems = new ArrayList<>();
    private ViewPager2 firstCarousel;
    private ViewPager2 secondCarousel;
    private ViewPager2 thirdCarousel;
    private LinearLayout carouselsLayout;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);

        firestore = FirebaseFirestore.getInstance();

        firstCarousel = findViewById(R.id.viewPager);
        secondCarousel = findViewById(R.id.viewPager2);
        thirdCarousel = findViewById(R.id.viewPager3);
        carouselsLayout = findViewById(R.id.carousels_layout);

        // Configurar carrosséis vazios inicialmente
        firstAdapter = new CarouselAdapter(this, allFirstCarouselItems);
        secondAdapter = new CarouselAdapter(this, allSecondCarouselItems);
        thirdAdapter = new CarouselAdapter(this, allThirdCarouselItems);
        firstCarousel.setAdapter(firstAdapter);
        secondCarousel.setAdapter(secondAdapter);
        thirdCarousel.setAdapter(thirdAdapter);

        // Carregar dados do Firestore
        loadCarouselData();

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

        // Navegação e menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationHelper menuHelper = new BottomNavigationHelper();
        menuHelper.setNavigationFocus(bottomNavigationView, R.id.nav_utero);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_anota) {
                    startActivity(new Intent(Informations.this, Notes.class));
                    startActivity(new Intent(Informations.this, Annotations.class));
                    return true;
                } else if (id == R.id.nav_calendario) {
                    startActivity(new Intent(Informations.this, CalendarCycle.class));
                    return true;
                } else if (id == R.id.nav_utero) {
                    startActivity(new Intent(Informations.this, Informations.class));
                    return true;
                } else if (id == R.id.nav_seekbar) {
                    startActivity(new Intent(Informations.this, TimeLine.class));
                    return true;
                } else if (id == R.id.nav_medicacao) {
                    startActivity(new Intent(Informations.this, Medicine.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void loadCarouselData() {
        firestore.collection("Informacoes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allFirstCarouselItems.clear();
                    allSecondCarouselItems.clear();
                    allThirdCarouselItems.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String tipo = document.getString("type");
                        String titulo = document.getString("title");
                        String url = document.getString("link");
                        String cor = document.getString("cor");
                        String imagem = document.getString("uimageinfo");

                        int imagemResId = this.getResources().getIdentifier(imagem, "drawable", this.getPackageName());

                        Noticia noticia = new Noticia(titulo, url, imagemResId, cor); // Ajuste os parâmetros conforme necessário

                        switch (tipo) {
                            case "Ciclo":
                                allFirstCarouselItems.add(noticia);
                                break;
                            case "Doenças":
                                allSecondCarouselItems.add(noticia);
                                break;
                            case "Métodos Contraceptivos":
                                allThirdCarouselItems.add(noticia);
                                break;
                        }
                    }

                    // Atualizar adaptadores
                    firstAdapter.updateItems(allFirstCarouselItems);
                    secondAdapter.updateItems(allSecondCarouselItems);
                    thirdAdapter.updateItems(allThirdCarouselItems);
                })
                .addOnFailureListener(e -> {
                    // Lidar com erros de leitura
                    e.printStackTrace();
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
}