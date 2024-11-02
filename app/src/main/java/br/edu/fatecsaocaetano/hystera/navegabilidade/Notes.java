package br.edu.fatecsaocaetano.hystera.navegabilidade;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import java.util.Date;

public class Notes {
    private String userID; // Para armazenar o ID do usuário
    private String description; // Descrição da anotação
    private Date annotationDate; // Data da anotação
    private String title; // Título da anotação
    private FirebaseFirestore db; // Instância do Firestore

    // Construtor padrão
    public Notes() {
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.db = FirebaseFirestore.getInstance(); // Inicializando o Firestore
    }

    // Novo construtor
    public Notes(String userID, String title, String description, Date annotationDate) {
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.annotationDate = annotationDate;
        this.db = FirebaseFirestore.getInstance();
    }

    public void editNoteInFirestore(String noteId) {
        DocumentReference noteRef = db.collection("Usuarios").document(userID).collection("Notes").document(noteId);
        noteRef.set(this)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Nota editada com sucesso!");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Erro ao editar a nota: " + e.getMessage());
                });
    }

    // Método para salvar a nota
    public void saveNote() {
        // Criando um novo documento na coleção "Notes"
        DocumentReference noteRef = db.collection("Usuarios").document(userID).collection("UserNotes").document();
        noteRef.set(this)
                .addOnSuccessListener(aVoid -> {
                    // A anotação foi salva com sucesso
                    System.out.println("Nota salva com sucesso!");
                })
                .addOnFailureListener(e -> {
                    // Ocorreu um erro ao salvar a anotação
                    System.err.println("Erro ao salvar a nota: " + e.getMessage());
                });
    }

    // Métodos para editar e deletar notas
    public String editNote(String description, Date annotationDate, String title) {
        // Lógica para editar a nota
        return "Nota editada com sucesso!";
    }

    public void deleteNote() {
        // Lógica para deletar a nota
    }

    // Getters e Setters (se necessário)
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
