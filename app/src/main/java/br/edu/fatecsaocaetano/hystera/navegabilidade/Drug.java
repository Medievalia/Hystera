package br.edu.fatecsaocaetano.hystera.navegabilidade;

import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Drug {

    private static final String tag = "DrugClass";
    private String userID;
    private String drugName;
    private String drugDescription;
    private Timestamp drugDate;
    private String drugAmount;
    private int trigger;
    private String id;
    private boolean sound;
    private boolean notification;
    private boolean vibration;

    public Drug(){}

    public Drug(String drugName, String drugDescription, String drugAmount, Timestamp drugDate, int trigger, String userID, boolean notification, boolean sound, boolean vibration) {
        this.drugName = drugName;
        this.drugDate = drugDate;
        this.trigger = trigger;
        this.userID = userID;
        this.drugAmount = drugAmount;
        this.drugDescription = drugDescription;
        this.id = gerarIdDrugs(drugName);
        this.sound = sound;
        this.notification = notification;
        this.vibration = vibration;
    }

    private String gerarIdDrugs(String drugName) {
        Timestamp now = Timestamp.now();
        SimpleDateFormat sdf = new SimpleDateFormat("MMyyyy", Locale.getDefault());
        String baseId = sdf.format(now.toDate());
        String timestamp = String.valueOf(now.getSeconds());
        String formattedName = drugName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return baseId + "-" + timestamp + "_" + formattedName;
    }

    public void salvarDrugNoFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference cycleRef = db.collection("Usuarios").document(userID)
                .collection("Medicines").document(id);

        cycleRef.set(this)
                .addOnSuccessListener(aVoid -> Log.i(tag, "Medicamento salvo com sucesso! " + userID))
                .addOnFailureListener(e -> Log.e(tag,"Erro ao salvar medicamento. " + e));
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public Timestamp getDrugDate() {
        return drugDate;
    }

    public void setDrugDate(Timestamp drugDate) {
        this.drugDate = drugDate;
    }

    public int getTrigger() {
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    public String getDrugDescription() { return drugDescription; }

    public void setDrugDescription(String drugDescription) { this.drugDescription = drugDescription; }

    public String getDrugAmount() {
        return drugAmount;
    }

    public void setDrugAmount(String drugQuant) {
        this.drugAmount = drugQuant;
    }
}
