package vue;

import modele.Desert;
import controleur.Controleur;

import javax.swing.*;
import java.awt.*;

public class VueAccueil extends JFrame {

    public VueAccueil() {
        setTitle("Le Desert Interdit");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal avec image de fond
        JPanel fond = new JPanel(new BorderLayout(10, 10)) {
            private Image bg = chargerFond();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null)
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                // Overlay sombre
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        fond.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        setContentPane(fond);

        // Titre
        JLabel titre = new JLabel("Le Desert Interdit", SwingConstants.CENTER);
        titre.setFont(new Font("Serif", Font.BOLD, 34));
        titre.setForeground(new Color(255, 210, 80));
        fond.add(titre, BorderLayout.NORTH);

        // Panel central
        JPanel centre = new JPanel();
        centre.setOpaque(false);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));

        // Sous-titre
        JLabel sousTitre = new JLabel("Luttez ensemble pour votre survie !", SwingConstants.CENTER);
        sousTitre.setFont(new Font("Serif", Font.ITALIC, 15));
        sousTitre.setForeground(new Color(240, 200, 140));
        sousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(sousTitre);
        centre.add(Box.createVerticalStrut(30));

        // Nombre de joueurs
        JLabel labelNb = creerLabel("Nombre de joueurs :");
        labelNb.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(labelNb);
        centre.add(Box.createVerticalStrut(8));

        JSpinner spinnerJoueurs = new JSpinner(new SpinnerNumberModel(2, 2, 5, 1));
        spinnerJoueurs.setMaximumSize(new Dimension(200, 35));
        spinnerJoueurs.setFont(new Font("Serif", Font.BOLD, 16));
        spinnerJoueurs.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(spinnerJoueurs);
        centre.add(Box.createVerticalStrut(20));

        // Difficulté
        JLabel labelDiff = creerLabel("Difficulte :");
        labelDiff.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(labelDiff);
        centre.add(Box.createVerticalStrut(8));

        String[] difficultes = {"Novice", "Normal", "Elite", "Legendaire"};
        JComboBox<String> comboDiff = new JComboBox<>(difficultes);
        comboDiff.setMaximumSize(new Dimension(200, 35));
        comboDiff.setFont(new Font("Serif", Font.BOLD, 14));
        comboDiff.setAlignmentX(Component.CENTER_ALIGNMENT);
        centre.add(comboDiff);
        centre.add(Box.createVerticalStrut(40));

        // Bouton commencer
        JButton commencer = new JButton("Commencer la partie");
        commencer.setFont(new Font("Serif", Font.BOLD, 17));
        commencer.setBackground(new Color(120, 70, 10));
        commencer.setForeground(new Color(255, 210, 80));
        commencer.setFocusPainted(false);
        commencer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 150, 50), 2),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        commencer.setAlignmentX(Component.CENTER_ALIGNMENT);
        commencer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                commencer.setBackground(new Color(160, 100, 20));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                commencer.setBackground(new Color(120, 70, 10));
            }
        });
        centre.add(commencer);

        fond.add(centre, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("Un jeu de Matt Leacock", SwingConstants.CENTER);
        footer.setFont(new Font("Serif", Font.ITALIC, 12));
        footer.setForeground(new Color(180, 150, 100));
        fond.add(footer, BorderLayout.SOUTH);

        // Action bouton
        commencer.addActionListener(e -> {
            int nbJoueurs = (int) spinnerJoueurs.getValue();
            double niveau;
            switch (comboDiff.getSelectedIndex()) {
                case 1: niveau = 3.0; break;
                case 2: niveau = 4.0; break;
                case 3: niveau = 5.0; break;
                default: niveau = 2.0; break;
            }
            lancerJeu(nbJoueurs, niveau);
        });
    }

    private JLabel creerLabel(String texte) {
        JLabel l = new JLabel(texte, SwingConstants.CENTER);
        l.setFont(new Font("Serif", Font.BOLD, 14));
        l.setForeground(new Color(255, 220, 150));
        return l;
    }

    private Image chargerFond() {
        try {
            java.net.URL url = getClass().getClassLoader().getResource("data/desert.jpg");
            if (url != null) return new ImageIcon(url).getImage();
            url = getClass().getClassLoader().getResource("data/desert.png");
            if (url != null) return new ImageIcon(url).getImage();
        } catch (Exception e) {
            System.out.println("Image de fond non trouvee");
        }
        return null;
    }

    private void lancerJeu(int nbJoueurs, double niveauTempete) {
        Desert desert = new Desert(nbJoueurs, niveauTempete);
        VueDesert vue = new VueDesert(desert);
        Controleur control = new Controleur(desert, vue);

        vue.haut.addActionListener(control);
        vue.bas.addActionListener(control);
        vue.gauche.addActionListener(control);
        vue.droite.addActionListener(control);
        vue.explorer.addActionListener(control);
        vue.finTour.addActionListener(control);
        vue.ramasser.addActionListener(control);
        vue.donner.addActionListener(control);

        vue.setControleur(control);
        vue.setAccueil(this);

        this.setVisible(false);
        vue.setVisible(true);
    }
}