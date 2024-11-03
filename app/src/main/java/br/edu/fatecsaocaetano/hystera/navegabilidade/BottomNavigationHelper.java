package br.edu.fatecsaocaetano.hystera.navegabilidade;

import com.google.android.material.bottomnavigation.BottomNavigationView;
public class BottomNavigationHelper {

    // Método para definir o foco no item correto do BottomNavigationView
    public static void setNavigationFocus(BottomNavigationView navigationView, int itemId) {
        if (navigationView != null) {
            navigationView.setSelectedItemId(itemId);
        }
    }
}