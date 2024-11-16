package br.edu.fatecsaocaetano.hystera.navegabilidade;

public class Noticia {
    private String titulo;
    private String url;
    private int imagemResId;  // Agora estamos usando o ID do recurso da imagem
    private String cor;  // Novo campo para armazenar a cor

    // Construtor atualizado para incluir a cor
    public Noticia(String titulo, String url, int imagemResId, String cor) {
        this.titulo = titulo;
        this.url = url;
        this.imagemResId = imagemResId;
        this.cor = cor;  // Atribui a cor ao campo
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

    public String getCor() {
        return cor;
    }
}
