package vue;

import modele.Desert;
import modele.Joueur;
import modele.TypeCase;
import controleur.Controleur;
import modele.Case;
import modele.TypeJoueur;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class VueDesert extends JFrame implements Observer {
    private Desert desert;
    private JButton[][] boutons;
    private JLabel labelEtat;
    private JLabel labelTour;
    private Controleur controleur;
    private VueAccueil accueil;
    private ImageIcon imgDesert;
    private ImageIcon imgOasis;
    private ImageIcon imgMirage;
    private ImageIcon imgTunnel;
    private ImageIcon imgIndiceLigne;
    private ImageIcon imgIndiceColonne;
    private ImageIcon imgCrash;
    private ImageIcon imgPiste;
    private JPanel panneauInfo;
    private JLabel labelEau;
    private JLabel labelPieces;

    public JButton haut     = new JButton("↑");
    public JButton bas      = new JButton("↓");
    public JButton gauche   = new JButton("←");
    public JButton droite   = new JButton("→");
    public JButton explorer = new JButton("Explorer");
    public JButton finTour  = new JButton("Fin de tour");
    public JButton ramasser = new JButton("Ramasser");
    public JButton donner   = new JButton("Donner eau");

    public JButton getBouton(int i, int j) { return boutons[i][j]; }

    private static final Color[] COULEURS_JOUEURS = {
            new Color(70, 130, 255),
            new Color(220, 80, 80),
            new Color(80, 180, 80),
            new Color(200, 140, 0),
            new Color(150, 60, 200)
    };

    private static final int TAILLE_CASE = 95;

    public VueDesert(Desert desert) {
        this.desert = desert;
        setTitle("Le Desert Interdit");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(4, 4));

        chargerImages();

        // NORD
        JPanel nord = new JPanel(new BorderLayout(0, 4));
        nord.setBackground(new Color(50, 30, 10));
        nord.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        JLabel titre = new JLabel("Le Desert Interdit", SwingConstants.CENTER);
        titre.setFont(new Font("Serif", Font.BOLD, 22));
        titre.setForeground(new Color(255, 210, 100));
        nord.add(titre, BorderLayout.NORTH);

        labelTour = new JLabel("Joueur 1 - Actions : 4", SwingConstants.CENTER);
        labelTour.setFont(new Font("Serif", Font.BOLD, 14));
        labelTour.setForeground(COULEURS_JOUEURS[0]);
        nord.add(labelTour, BorderLayout.CENTER);

        JPanel boutonsBarre = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        boutonsBarre.setBackground(new Color(50, 30, 10));
        for (JButton b : new JButton[]{gauche, haut, bas, droite, explorer, ramasser, donner}) {
            styliserBouton(b);
            boutonsBarre.add(b);
        }
        nord.add(boutonsBarre, BorderLayout.SOUTH);
        add(nord, BorderLayout.NORTH);

        // CENTRE
        JPanel grille = new JPanel(new GridLayout(5, 5, 4, 4));
        grille.setBackground(new Color(80, 50, 20));
        grille.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 130, 50), 3),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        boutons = new JButton[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                boutons[i][j] = creerBoutonCase();
                grille.add(boutons[i][j]);
            }
        }
        add(grille, BorderLayout.CENTER);

        // SUD
        JPanel sud = new JPanel(new BorderLayout(4, 4));
        sud.setBackground(new Color(50, 30, 10));
        sud.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

// Ligne stats principale
        labelEtat = new JLabel("", SwingConstants.LEFT);
        labelEtat.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelEtat.setForeground(new Color(240, 220, 170));

// Eau de tous les joueurs
        labelEau = new JLabel("", SwingConstants.LEFT);
        labelEau.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelEau.setForeground(new Color(150, 210, 255));

// Pièces ramassées
        labelPieces = new JLabel("", SwingConstants.LEFT);
        labelPieces.setFont(new Font("Monospaced", Font.PLAIN, 12));
        labelPieces.setForeground(new Color(255, 215, 0));

        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        statsPanel.setOpaque(false);
        statsPanel.add(labelEtat);
        statsPanel.add(labelEau);
        statsPanel.add(labelPieces);

        styliserBouton(finTour);
        finTour.setBackground(new Color(120, 60, 10));

        sud.add(statsPanel, BorderLayout.CENTER);
        sud.add(finTour, BorderLayout.EAST);
        add(sud, BorderLayout.SOUTH);

        // Clavier
        setFocusable(true);
        requestFocusInWindow();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                boutons[i][j].addKeyListener(new java.awt.event.KeyAdapter() {
                    @Override
                    public void keyPressed(java.awt.event.KeyEvent e) {
                        switch (e.getKeyCode()) {
                            case java.awt.event.KeyEvent.VK_UP:
                            case java.awt.event.KeyEvent.VK_Z:
                                desert.deplacerJoueur(modele.Direction.NORD);
                                rafraichir();
                                break;
                            case java.awt.event.KeyEvent.VK_DOWN:
                            case java.awt.event.KeyEvent.VK_S:
                                desert.deplacerJoueur(modele.Direction.SUD);
                                rafraichir();
                                break;
                            case java.awt.event.KeyEvent.VK_LEFT:
                            case java.awt.event.KeyEvent.VK_Q:
                                desert.deplacerJoueur(modele.Direction.OUEST);
                                rafraichir();
                                break;
                            case java.awt.event.KeyEvent.VK_RIGHT:
                            case java.awt.event.KeyEvent.VK_D:
                                desert.deplacerJoueur(modele.Direction.EST);
                                rafraichir();
                                break;
                            case java.awt.event.KeyEvent.VK_E:
                                String msg = desert.explorerZone();
                                rafraichir();
                                afficherMessage(msg);
                                break;
                            case java.awt.event.KeyEvent.VK_ENTER:
                                desert.finDeTour();
                                rafraichir();
                                verifierFinPartie();
                                if (desert.estGagne()) {
                                    JOptionPane.showMessageDialog(null,
                                            "VICTOIRE ! Vous vous envolez !",
                                            "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
                                    VueDesert.this.dispose();
                                    new VueAccueil().setVisible(true);
                                }
                                break;
                            case java.awt.event.KeyEvent.VK_A:
                                desert.deplacerJoueurDiagonal(-1, -1);
                                rafraichir();
                                break;
                            case java.awt.event.KeyEvent.VK_W:
                                desert.deplacerJoueurDiagonal(-1, 1);
                                rafraichir();
                                break;
                            case java.awt.event.KeyEvent.VK_X:
                                desert.deplacerJoueurDiagonal(1, -1);
                                rafraichir();
                                break;
                            case java.awt.event.KeyEvent.VK_C:
                                desert.deplacerJoueurDiagonal(1, 1);
                                rafraichir();
                                break;
                        }
                    }
                });
            }
        }
        requestFocusInWindow();

        // Clics cases
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                final int li = i, co = j;
                boutons[i][j].addActionListener(e -> {
                    if (controleur != null) controleur.caseCliquee(li, co);
                });
            }
        }

        pack();
        setLocationRelativeTo(null);
        rafraichir();
    }

    // Chargement images
    private void chargerImages() {
        imgDesert        = chargerImage("desert.png");
        imgOasis         = chargerImage("Oasis.jpg");
        imgMirage        = chargerImage("mirage.png");
        imgTunnel        = chargerImage("tunnel.png");
        imgIndiceLigne   = chargerImage("indice_ligne.png");
        imgIndiceColonne = chargerImage("indice_colone.png");
        imgCrash         = chargerImage("crash.jpg");
        imgPiste         = chargerImage("piste.png");
    }

    private ImageIcon chargerImage(String nom) {
        try {
            java.net.URL url = getClass().getClassLoader().getResource("data/" + nom);
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(TAILLE_CASE, TAILLE_CASE, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.out.println("Image non trouvee : " + nom);
        }
        return null;
    }

    private JButton creerBoutonCase() {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Les pions sont dessinés par-dessus dans rafraichir via setIcon
                // On override juste pour garder l'opacité
            }
        };
        btn.setPreferredSize(new Dimension(TAILLE_CASE, TAILLE_CASE));
        btn.setFont(new Font("Serif", Font.BOLD, 11));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        return btn;
    }

    private void styliserBouton(JButton btn) {
        btn.setFont(new Font("Serif", Font.BOLD, 13));
        btn.setBackground(new Color(90, 55, 15));
        btn.setForeground(new Color(255, 210, 100));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 130, 50), 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
    }

    // Crée l'icône composite : image de fond + pions + sable
    private ImageIcon creerIconeCase(ImageIcon fond, List<Integer> indexJoueurs,
                                     int indexActuel, int sable, boolean bloquee,
                                     String labelCase, String labelPiece) {
        BufferedImage img = new BufferedImage(TAILLE_CASE, TAILLE_CASE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fond
        if (fond != null) {
            g.drawImage(fond.getImage(), 0, 0, TAILLE_CASE, TAILLE_CASE, null);
        } else {
            g.setColor(new Color(200, 160, 80));
            g.fillRect(0, 0, TAILLE_CASE, TAILLE_CASE);
        }

        // Overlay sable (assombrit la case)
        if (sable >= 2) {
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, TAILLE_CASE, TAILLE_CASE);
        } else if (sable == 1) {
            g.setColor(new Color(180, 120, 0, 60));
            g.fillRect(0, 0, TAILLE_CASE, TAILLE_CASE);
        }

        // Compteur sable en haut à gauche
        if (sable > 0) {
            g.setColor(sable >= 2 ? new Color(255, 80, 80) : new Color(255, 200, 0));
            g.setFont(new Font("Serif", Font.BOLD, 14));
            g.drawString(sable + "", 5, 18);
            // Petits carrés de sable
            for (int k = 0; k < Math.min(sable, 5); k++) {
                g.setColor(new Color(210, 160, 50));
                g.fillRect(5 + k * 10, 22, 8, 8);
                g.setColor(new Color(150, 100, 20));
                g.drawRect(5 + k * 10, 22, 8, 8);
            }
        }

        // Label de la case (en bas)
        if (labelCase != null && !labelCase.isEmpty()) {
            g.setColor(new Color(0, 0, 0, 140));
            g.fillRoundRect(2, TAILLE_CASE - 22, TAILLE_CASE - 4, 20, 6, 6);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Serif", Font.BOLD, 11));
            FontMetrics fm = g.getFontMetrics();
            int tx = (TAILLE_CASE - fm.stringWidth(labelCase)) / 2;
            g.drawString(labelCase, tx, TAILLE_CASE - 7);
        }

        // Pièce (bordure dorée + label)
        if (labelPiece != null && !labelPiece.isEmpty()) {
            g.setColor(new Color(255, 215, 0, 180));
            g.fillRoundRect(20, 30, TAILLE_CASE - 40, 30, 8, 8);
            g.setColor(new Color(100, 60, 0));
            g.setFont(new Font("Serif", Font.BOLD, 11));
            FontMetrics fm = g.getFontMetrics();
            int tx = (TAILLE_CASE - fm.stringWidth(labelPiece)) / 2;
            g.drawString(labelPiece, tx, 52);
        }

        // Pions des joueurs (cercles colorés)
        if (!indexJoueurs.isEmpty()) {
            int nb = indexJoueurs.size();
            int rayon = 14;
            int espacement = Math.min(22, (TAILLE_CASE - 10) / nb);
            int startX = (TAILLE_CASE - (nb - 1) * espacement - rayon * 2) / 2;

            for (int k = 0; k < nb; k++) {
                int idx = indexJoueurs.get(k);
                Color c = COULEURS_JOUEURS[idx % COULEURS_JOUEURS.length];
                int px = startX + k * espacement;
                int py = 30;

                // Ombre du pion
                g.setColor(new Color(0, 0, 0, 80));
                g.fillOval(px + 2, py + 2, rayon * 2, rayon * 2);

                // Corps du pion
                g.setColor(c);
                g.fillOval(px, py, rayon * 2, rayon * 2);

                // Reflet
                g.setColor(c.brighter().brighter());
                g.fillOval(px + 4, py + 3, rayon / 2, rayon / 2);

                // Contour
                g.setColor(idx == indexActuel ? Color.WHITE : c.darker());
                g.setStroke(new BasicStroke(idx == indexActuel ? 2.5f : 1.5f));
                g.drawOval(px, py, rayon * 2, rayon * 2);

                // Numéro du joueur
                g.setColor(Color.WHITE);
                g.setFont(new Font("Serif", Font.BOLD, 11));
                g.drawString(String.valueOf(idx + 1), px + rayon - 3, py + rayon + 4);
            }
        }

        g.dispose();
        return new ImageIcon(img);
    }

    // Rafraichissement
    @Override
    public void rafraichir() {
        List<Joueur> joueurs = desert.getJoueurs();
        int oL = desert.getOeil().getLigne();
        int oC = desert.getOeil().getColonne();
        int jL = desert.getJoueurActuel().getPosition().getLigne();
        int jC = desert.getJoueurActuel().getPosition().getColonne();
        int idxActuel = desert.getIndexJoueurActuel();

        // PARTIE 1 : mise à jour de chaque case de la grille
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                JButton btn = boutons[i][j];
                btn.setText("");
                btn.setBorder(BorderFactory.createLineBorder(new Color(160, 110, 40), 1));

                // Case spéciale : oeil de la tempête
                if (i == oL && j == oC) {
                    BufferedImage img = new BufferedImage(TAILLE_CASE, TAILLE_CASE, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = img.createGraphics();
                    g.setColor(new Color(30, 10, 5));
                    g.fillRect(0, 0, TAILLE_CASE, TAILLE_CASE);
                    g.setColor(new Color(180, 50, 50, 150));
                    for (int r = 40; r > 5; r -= 10) {
                        g.setStroke(new BasicStroke(3));
                        g.drawOval(TAILLE_CASE/2 - r, TAILLE_CASE/2 - r, r*2, r*2);
                    }
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Serif", Font.BOLD, 12));
                    g.drawString("Tempete", 15, TAILLE_CASE/2 + 5);
                    g.dispose();
                    btn.setIcon(new ImageIcon(img));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(200, 50, 50), 2));
                    continue;
                }

                // Infos de la case
                int sable = desert.getCase(i, j).getSable();
                TypeCase type = desert.getCase(i, j).getType();
                boolean exploree = desert.getCase(i, j).isExploree();

                // Choisir l'image de fond et le label selon le type
                ImageIcon fond;
                String labelCase;
                switch (type) {
                    case CRASH:
                        fond = imgCrash;
                        labelCase = "Crash";
                        break;
                    case PISTE_DECOLLAGE:
                        fond = imgPiste;
                        labelCase = "Piste";
                        break;
                    case OASIS:
                        fond = exploree ? imgOasis : imgDesert;
                        labelCase = exploree ? "Oasis" : "?";
                        break;
                    case MIRAGE:
                        fond = exploree ? imgMirage : imgDesert;
                        labelCase = exploree ? "Mirage" : "?";
                        break;
                    case TUNNEL:
                        fond = imgTunnel;
                        labelCase = exploree ? "Tunnel" : "Tunnel";
                        break;
                    case INDICE_LIGNE:
                        fond = exploree ? imgIndiceLigne : imgDesert;
                        labelCase = exploree ? "Ind.L" : "?";
                        break;
                    case INDICE_COLONNE:
                        fond = exploree ? imgIndiceColonne : imgDesert;
                        labelCase = exploree ? "Ind.C" : "?";
                        break;
                    default:
                        fond = imgDesert;
                        labelCase = "";
                        break;
                }

                // Pièce posée sur cette case (affichage doré)
                String labelPiece = "";
                if (desert.getCase(i, j).getPieceContenue() != null) {
                    labelPiece = desert.getCase(i, j).getPieceContenue().name();
                }

                // Joueurs présents sur cette case
                List<Integer> joueursIci = new java.util.ArrayList<>();
                for (int k = 0; k < joueurs.size(); k++) {
                    if (joueurs.get(k).getPosition().getLigne() == i
                            && joueurs.get(k).getPosition().getColonne() == j) {
                        joueursIci.add(k);
                    }
                }

                // Dessiner l'icône composite (fond + sable + pions + labels)
                btn.setIcon(creerIconeCase(fond, joueursIci, idxActuel,
                        sable, sable >= 2, labelCase, labelPiece));

                // Bordures colorées sur les cases adjacentes au joueur actif
                // Vert = déplaçable, Orange = creusable, Rouge = bloqué
                int dist = Math.abs(jL - i) + Math.abs(jC - j);
                if (dist == 1) {
                    if (sable >= 2 || type == TypeCase.OEIL) {
                        btn.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    } else if (sable == 1) {
                        btn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
                    } else {
                        btn.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                    }
                }

                // Bordure dorée si une pièce est sur cette case
                if (!labelPiece.isEmpty()) {
                    btn.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3));
                }
            }
        }

        // ── PARTIE 2 : mise à jour des labels du bas ──

        // Label du joueur actif (avec détection enlisement)
        int idx = desert.getIndexJoueurActuel();
        Case caseActuelle = desert.getCase(
                desert.getJoueurActuel().getPosition().getLigne(),
                desert.getJoueurActuel().getPosition().getColonne()
        );
        if (caseActuelle.getSable() >= 2 && (desert.getJoueur(idx)).getType() != TypeJoueur.ALPINISTE) {
            labelTour.setText("Joueur " + (idx + 1) + " ENLISE ! Creusez d'abord !");
            labelTour.setForeground(Color.RED);
        } else {
            String pouvoir = "";
            switch (desert.getJoueurActuel().getType()) {
                case ARCHEOLOGUE: pouvoir = " [Creuse x2]"; break;
                case EXPLORATEUR: pouvoir = " [Diag: A/W/X/C]"; break;
                default: break;
            }
            labelTour.setText("Joueur " + (idx + 1)
                    + " (" + desert.getJoueurActuel().getType() + ")" + pouvoir
                    + " - Actions : " + desert.getActionsRestantes() + "/4");
            labelTour.setForeground(COULEURS_JOUEURS[idx % COULEURS_JOUEURS.length]);
        }

        // Stats générales (sable + tempête)
        labelEtat.setText("  Sable: " + desert.compterSableTotal() + "/43"
                + "  |  Tempete: " + desert.getTempete().getNiveau() + "/7");

        // Eau de TOUS les joueurs
        StringBuilder eauStr = new StringBuilder("  Eau : ");
        for (int k = 0; k < joueurs.size(); k++) {
            eauStr.append("J").append(k + 1).append(":")
                    .append(joueurs.get(k).getNiveauEau());
            if (k < joueurs.size() - 1) eauStr.append(" | ");
        }
        labelEau.setText(eauStr.toString());

        // Pièces ramassées
        List<modele.Piece> pieces = desert.getPiecesRamassees();
        StringBuilder pieceStr = new StringBuilder("  Pieces : ");
        if (pieces.isEmpty()) {
            pieceStr.append("aucune");
        } else {
            for (modele.Piece piece : pieces) {
                pieceStr.append(piece.name()).append("  ");
            }
        }
        pieceStr.append("(").append(pieces.size()).append("/4)");
        labelPieces.setText(pieceStr.toString());
    }

    public void verifierFinPartie() {
        if (desert.estPerdu()) {
            String raison = desert.estPerduSoif()    ? "Un joueur est mort de soif !"
                    : desert.estPerduTempete() ? "La tempete est trop violente !"
                      : "Le desert est totalement ensable !";
            JOptionPane.showMessageDialog(this,
                    "DEFAITE\n\n" + raison, "Fin de partie", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            if (accueil != null) {
                accueil = new VueAccueil();
                accueil.setVisible(true);
            }
        }
    }

    /** Affiche un message renvoyé par le modèle (s'il y en a un). */
    public void afficherMessage(String message) {
        if (message != null)
            JOptionPane.showMessageDialog(this, message, "Le Desert Interdit",
                    JOptionPane.INFORMATION_MESSAGE);
    }

    public void setControleur(Controleur c) {
        this.controleur = c;
    }
    public void setAccueil(VueAccueil a) {
        this.accueil = a;
    }
}