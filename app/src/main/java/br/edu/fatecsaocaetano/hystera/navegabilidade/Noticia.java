package br.edu.fatecsaocaetano.hystera.navegabilidade;

public class Noticia {
    private String titulo;
    private String url;
    private int imagemResId;

    // Construtor
    public Noticia(String titulo, String url, int imagemResId) {
        this.titulo = titulo;
        this.url = url;
        this.imagemResId = imagemResId;
    }

    // Getters
    public String getTitulo() {
        return titulo;
    }

    public String getUrl() {
        return url;
    }

    public int getImagemResId() {
        return imagemResId;  // Método que retorna o ID da imagem
    }
}
