package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.fatecsaocaetano.hystera.R;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
    private Context context;
    private List<Noticia> noticias;

    // Construtor do adaptador, recebe o contexto e a lista de notícias
    public CarouselAdapter(Context context, List<Noticia> noticias) {
        this.context = context;
        this.noticias = noticias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout de cada item do carrossel
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item, parent, false);
        return new ViewHolder(view); // Retorna o ViewHolder com a visão inflada
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Noticia noticia = noticias.get(position); // Obtém a notícia na posição atual
        holder.titulo.setText(noticia.getTitulo()); // Define o título da notícia
        holder.imagem.setImageResource(noticia.getImagemResId()); // Define a imagem da notícia
        holder.itemView.setOnClickListener(v -> {
            // Ao clicar no item, abre a URL da notícia em um navegador
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(noticia.getUrl()));
            context.startActivity(intent); // Inicia a Activity do navegador
        });
    }

    @Override
    public int getItemCount() {
        return noticias.size(); // Retorna o número de itens na lista
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo; // TextView para o título da notícia
        ImageView imagem; // ImageView para a imagem da notícia

        // Construtor do ViewHolder, inicializa as views
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.news_title);  // Atualizado para corresponder ao ID do layout
            imagem = itemView.findViewById(R.id.news_image);  // Atualizado para corresponder ao ID do layout
        }
    }
}
