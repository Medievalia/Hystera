package br.edu.fatecsaocaetano.hystera.navegabilidade;

public class Noticia {
    private String titulo;
    private String url;
    private int imagemResId;  // Agora estamos usando o ID do recurso da imagem

    public Noticia(String titulo, String url, int imagemResId) {
        this.titulo = titulo;
        this.url = url;
        this.imagemResId = imagemResId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrl() {
        return url;
    }

    public int getImagemResId() {
        return imagemResId;
    }
}
