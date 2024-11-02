package br.edu.fatecsaocaetano.hystera.navegabilidade;

import java.util.Date;

public class Notes {
    private String userID; // User ID como uma String
    private String description;
    private Date annotationDate;
    private String title;

    // Construtor
    public Notes(String userID, String description, Date annotationDate, String title) {
        this.userID = userID;
        this.description = description;
        this.annotationDate = annotationDate;
        this.title = title;
    }

    // Métodos
    public String saveNote(String userID, String description, Date annotationDate, String title) {
        // Lógica para salvar a nota
        return "Nota salva com sucesso"; // Placeholder
    }

    public String editNote(String userID, String description, Date annotationDate, String title) {
        // Lógica para editar a nota
        return "Nota editada com sucesso"; // Placeholder
    }

    public void deleteNote(String userID, Notes note) {
        // Lógica para deletar a nota
    }

    // Getters e Setters (opcionais, mas geralmente recomendados)
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getAnnotationDate() {
        return annotationDate;
    }

    public void setAnnotationDate(Date annotationDate) {
        this.annotationDate = annotationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
