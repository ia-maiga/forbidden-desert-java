import javax.swing.SwingUtilities;

import vue.VueAccueil;

/** Point d'entrée : ouvre l'écran d'accueil sur le thread graphique de Swing. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VueAccueil().setVisible(true));
    }
}
