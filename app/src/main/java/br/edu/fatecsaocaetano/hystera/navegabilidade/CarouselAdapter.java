package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import br.edu.fatecsaocaetano.hystera.R;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private final Context context;
    private List<Noticia> noticias; // Lista original de notícias
    private List<Noticia> noticiasFiltradas; // Lista filtrada de notícias

    // Construtor do adaptador, recebe o contexto e a lista de notícias
    public CarouselAdapter(Context context, List<Noticia> noticias) {
        this.context = context;
        this.noticias = noticias;
        this.noticiasFiltradas = new ArrayList<>(noticias); // Inicializa a lista filtrada
    }

    @Override
    public CarouselViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Infla o layout do item do carrossel
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarouselViewHolder holder, int position) {
        // Obtém o item da lista de notícias
        Noticia noticia = noticiasFiltradas.get(position);

        // Configura o título da notícia
        holder.title.setText(noticia.getTitulo());

        // Carrega a imagem usando Glide
        Glide.with(context)
                .load(noticia.getImagemResId()) // URL ou ID do recurso
                .into(holder.imageView);

        // Define a cor do fundo com base na cor da notícia
        int color = getColorForPosition(noticia.getCor()); // Passa a cor como argumento

        // Cria um fundo com bordas arredondadas
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(16); // Arredondamento dos cantos
        drawable.setColor(ContextCompat.getColor(context, color)); // Cor de fundo
        drawable.setStroke(2, ContextCompat.getColor(context, R.color.black)); // Borda preta de 2dp
        holder.itemView.setBackground(drawable);

        // Adiciona clique no item para abrir a URL no navegador
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(noticia.getUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return noticiasFiltradas.size();
    }

    // Método para atualizar os itens com base no filtro de pesquisa
    public void updateItems(List<Noticia> newNoticias) {
        noticiasFiltradas.clear();  // Limpa a lista de notícias filtradas
        noticiasFiltradas.addAll(newNoticias); // Adiciona as novas notícias filtradas
        notifyDataSetChanged(); // Notifica o adaptador para atualizar a exibição
    }

    // Define cores diferentes com base na cor fornecida como argumento
    private int getColorForPosition(String cor) {
        switch (cor) {
            case "azul":
                return R.color.azul_calcinha;  // Exemplo de cor
            case "roxo":
                return R.color.purple_white;
            case "verde":
                return R.color.rocho_calcinha;
            default:
                return R.color.purple_mid;
        }
    }

    // Classe ViewHolder para vincular os elementos do layout
    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;

        public CarouselViewHolder(View itemView) {
            super(itemView);
            // Vincula o título e a imagem do layout
            title = itemView.findViewById(R.id.news_title);
            imageView = itemView.findViewById(R.id.news_image);
        }
    }
}
