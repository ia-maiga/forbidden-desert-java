package controleur;


import modele.Desert;
import modele.Direction;
import modele.TypeCase;
import vue.Observer;
import vue.VueDesert;
import vue.VueAccueil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controleur implements ActionListener {
    //private JPanel controles;

    Desert desert;
    //Direction d;
    //JButton bouton;
    VueDesert vueDesert;

    public Controleur(Desert desert, VueDesert vueDes) {
        this.desert = desert;
        this.vueDesert = vueDes;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object p = e.getSource();

        if (p == vueDesert.haut) {
            desert.deplacerJoueur(Direction.NORD);
            vueDesert.rafraichir();
        } else if (p == vueDesert.bas) {
            desert.deplacerJoueur(Direction.SUD);
            vueDesert.rafraichir();
        } else if (p == vueDesert.droite) {
            desert.deplacerJoueur(Direction.EST);
            vueDesert.rafraichir();
        } else if (p == vueDesert.gauche) {
            desert.deplacerJoueur(Direction.OUEST);
            vueDesert.rafraichir();
        } else if (p == vueDesert.explorer) {
            String message = desert.explorerZone();
            vueDesert.rafraichir();
            vueDesert.afficherMessage(message);
        } else if (p == vueDesert.ramasser) {
            desert.ramasserPiece();
            vueDesert.rafraichir();
            if (desert.estGagne()) {
                JOptionPane.showMessageDialog(vueDesert,
                        "VICTOIRE ! Vous vous envolez !", "Fin de partie",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (p == vueDesert.donner) {
            desert.donnerEau();
            vueDesert.rafraichir();
        } else if (p == vueDesert.finTour) {
            desert.finDeTour();
            vueDesert.rafraichir();
            vueDesert.verifierFinPartie();
            if (desert.estGagne()) {
                JOptionPane.showMessageDialog(vueDesert,
                        "VICTOIRE ! Vous vous envolez !",
                        "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
                vueDesert.dispose();
                new VueAccueil().setVisible(true);
            }
        }
    }

    public void caseCliquee(int i, int j) {
        if (!desert.peutJouer()) return;

        int li = desert.getJoueurActuel().getPosition().getLigne();
        int co = desert.getJoueurActuel().getPosition().getColonne();

        int distLigne   = i - li;
        int distColonne = j - co;
        int distance    = Math.abs(distLigne) + Math.abs(distColonne);

        if (distance == 0) return;

        // Diagonal pour Explorateur
        if (Math.abs(distLigne) == 1 && Math.abs(distColonne) == 1) {
            if (desert.deplacerJoueurDiagonal(distLigne, distColonne)) {
                vueDesert.rafraichir();
            }
            return;
        }

        if (distance > 1) return;

        // Creuser
        if (desert.getCase(i, j).getSable() > 0) {
            if (desert.creuserCase(i, j)) {
                vueDesert.rafraichir();
            }
            return;
        }

        // Déplacement orthogonal
        Direction d = null;
        if (distLigne == -1)      d = Direction.NORD;
        else if (distLigne == 1)  d = Direction.SUD;
        else if (distColonne == 1) d = Direction.EST;
        else if (distColonne == -1) d = Direction.OUEST;

        if (d != null) {
            desert.deplacerJoueur(d);
            vueDesert.rafraichir();
        }
    }
}
