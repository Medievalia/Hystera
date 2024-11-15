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

import java.util.List;

import br.edu.fatecsaocaetano.hystera.R;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private final Context context;
    private final List<Noticia> noticias;

    public CarouselAdapter(Context context, List<Noticia> noticias) {
        this.context = context;
        this.noticias = noticias;
    }

    @Override
    public CarouselViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarouselViewHolder holder, int position) {
        Noticia noticia = noticias.get(position);
        holder.title.setText(noticia.getTitulo());

        // Carregar a imagem usando Glide
        Glide.with(context)
                .load(noticia.getImagemResId()) // Carregando imagem pelo ID do recurso
                .into(holder.imageView);

        // Aplique cores diferentes com base na posição
        int color = getColorForPosition(position);
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, color));

        // Definir o fundo com bordas arredondadas diretamente no código
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(16); // Defina o raio da borda
        drawable.setColor(ContextCompat.getColor(context, color)); // Cor de fundo
        drawable.setStroke(2, ContextCompat.getColor(context, R.color.black)); // Cor e largura da borda
        holder.itemView.setBackground(drawable);  // Aplica no item

        // Configura o clique no item para abrir a URL no navegador
        holder.itemView.setOnClickListener(v -> {
            // Abre a URL no navegador
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(noticia.getUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return noticias.size();
    }

    private int getColorForPosition(int position) {
        // Defina cores diferentes para cada posição
        switch (position % 4) {
            case 0:
                return R.color.azul_calcinha;  // Exemplo de cor
            case 1:
                return R.color.purple_white;
            case 2:
                return R.color.rocho_calcinha;
            default:
                return R.color.purple_mid;
        }
    }

    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;

        public CarouselViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.news_title);
            imageView = itemView.findViewById(R.id.news_image);
        }
    }
}
