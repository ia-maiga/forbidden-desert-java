package modele;

import java.util.ArrayList;
import java.util.List;

public class Desert {
    private Case[][] grille;
    private Tempete tempete;
    private Position oeil;
    private List<Joueur> joueurs;
    private int actionsRestantes;
    private int indexJoueurActuel;
    private java.util.Map<Piece, Position> indicesLigne = new java.util.HashMap<>();
    private java.util.Map<Piece, Position> indicesColonne = new java.util.HashMap<>();
    private java.util.List<Piece> piecesRamassees = new ArrayList<>();

    private static final int SABLE_MAX = 43;
    private static final double TEMPETE_MAX = 7.0;

    public Desert(int nbJoueurs, double niveauTempete) {
        grille = new Case[5][5];
        tempete = new Tempete(niveauTempete);
        oeil = new Position(2, 2);
        joueurs = new ArrayList<>();
        actionsRestantes = 4;
        indexJoueurActuel = 0;

        initialiserGrille();
        initialiserSable();

        TypeJoueur[] roles = TypeJoueur.values();
        for (int i = 0; i < nbJoueurs; i++) {
            joueurs.add(new Joueur(new Position(0, 0), roles[i]));
        }
    }

    public Desert() {
        this(3, 2.0);
    }

    private void initialiserGrille() {
        // Cases fixes
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                grille[i][j] = new Case(new Position(i, j), TypeCase.SABLE);

        grille[2][2] = new Case(new Position(2, 2), TypeCase.OEIL);
        grille[0][0] = new Case(new Position(0, 0), TypeCase.CRASH);

        // Cases à placer aléatoirement
        List<TypeCase> types = new ArrayList<>();
        types.add(TypeCase.PISTE_DECOLLAGE);
        types.add(TypeCase.OASIS);
        types.add(TypeCase.OASIS);
        types.add(TypeCase.MIRAGE);
        types.add(TypeCase.TUNNEL);
        types.add(TypeCase.TUNNEL);
        types.add(TypeCase.TUNNEL);
        // 4 indices ligne + 4 indices colonne = 8
        types.add(TypeCase.INDICE_LIGNE);
        types.add(TypeCase.INDICE_LIGNE);
        types.add(TypeCase.INDICE_LIGNE);
        types.add(TypeCase.INDICE_LIGNE);
        types.add(TypeCase.INDICE_COLONNE);
        types.add(TypeCase.INDICE_COLONNE);
        types.add(TypeCase.INDICE_COLONNE);
        types.add(TypeCase.INDICE_COLONNE);
        // Reste = sable normal : 25 - 1(oeil) - 1(crash) - 15(spéciales) = 8
        for (int k = 0; k < 8; k++) types.add(TypeCase.SABLE);

        java.util.Collections.shuffle(types);

        // Placer sur la grille en évitant oeil et crash
        int idx = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if ((i == 2 && j == 2) || (i == 0 && j == 0)) continue;
                grille[i][j] = new Case(new Position(i, j), types.get(idx++));
            }
        }

        assignerIndices();
    }

    private void assignerIndices() {
        // Collecte exactement 4 cases INDICE_LIGNE et 4 INDICE_COLONNE
        List<Case> lignes   = new ArrayList<>();
        List<Case> colonnes = new ArrayList<>();

        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                TypeCase t = grille[i][j].getType();
                if (t == TypeCase.INDICE_LIGNE)   lignes.add(grille[i][j]);
                if (t == TypeCase.INDICE_COLONNE) colonnes.add(grille[i][j]);
            }

        // Mélanger les deux listes indépendamment
        java.util.Collections.shuffle(lignes);
        java.util.Collections.shuffle(colonnes);

        // Assigner : pièce k → lignes[k] + colonnes[k]
        // Garantit que chaque pièce a exactement 1 indice ligne ET 1 indice colonne
        Piece[] pieces = Piece.values(); // Les pieces
        for (int k = 0; k < 4; k++) {
            lignes.get(k).setPieceIndice(pieces[k]);
            colonnes.get(k).setPieceIndice(pieces[k]);
        }
    }
    private void initialiserSable() {
        int[][] positions = {
                {0,2},{1,1},{1,3},{2,0},{2,4},{3,1},{3,3},{4,2}
        };
        for (int[] p : positions)
            grille[p[0]][p[1]].ajouterSable();
    }
    public Case getCase(int i, int j) { return grille[i][j]; }
    public Position getOeil() { return oeil; }
    public Joueur getJoueur(int i) { return joueurs.get(i); }
    public List<Joueur> getJoueurs() { return joueurs; }
    public Tempete getTempete() { return tempete; }

    public int compterSableTotal() {
        int total = 0;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                total += grille[i][j].getSable();
        return total;
    }

    public void actionDesert() {
        int nbActions = (int) tempete.getNiveau();
        boolean chaleurDejaJouee = false;
        for (int k = 0; k < nbActions; k++) {
            int action = tempete.tirerAction();
            if (action == 1 && chaleurDejaJouee) {

                vent(tempete.tirerDirection(), tempete.tirerForce());
            } else {
                switch (action) {
                    case 0: vent(tempete.tirerDirection(), tempete.tirerForce()); break;
                    case 1: chaleur(); chaleurDejaJouee = true; break;
                    case 2: tempete.augmenter(); break;
                }
            }
        }
    }

    private void vent(Direction dir, int force) {
        System.out.println("Vent " + dir + " force " + force);

        for (int f = 0; f < force; f++) {

            int li = oeil.getLigne();
            int ci = oeil.getColonne();

            int srcL = li;
            int srcC = ci;

            switch (dir) {
                case NORD: srcL = li + 1; break;
                case SUD:  srcL = li - 1; break;
                case EST:  srcC = ci - 1; break;
                case OUEST: srcC = ci + 1; break;
            }

            // hors grille → stop
            if (srcL < 0 || srcL >= 5 || srcC < 0 || srcC >= 5) {
                System.out.println("Vent bloqué");
                continue;
            }

            // récupérer la tuile
            Case tuile = grille[srcL][srcC];

            // déplacer la tuile vers l'œil
            grille[li][ci] = tuile;

            // nouvelle position de l'œil
            grille[srcL][srcC] = new Case(new Position(srcL, srcC), TypeCase.OEIL);
            oeil = new Position(srcL, srcC);

            // la tuile connaît sa nouvelle position (utile pour estGagne)
            tuile.setPosition(new Position(li, ci));

            // ajouter sable
            tuile.ajouterSable();
            // La pièce suit la tuile
            // (pieceContenue reste sur la tuile, elle se déplace avec elle — rien à faire
            // car c'est l'objet Case lui-même qui est déplacé dans la grille)

            // déplacer les joueurs sur cette case
            for (Joueur j : joueurs) {
                if (j.getPosition().getLigne() == srcL &&
                        j.getPosition().getColonne() == srcC) {

                    j.setPosition(new Position(li, ci));
                }
            }
        }
    }

    private void chaleur() {
        System.out.println("Vague de chaleur !");
        for (Joueur j : joueurs) {
            Case caseJoueur = getCase(
                    j.getPosition().getLigne(),
                    j.getPosition().getColonne()
            );
            if (caseJoueur.getType() == TypeCase.TUNNEL && caseJoueur.isExploree()) {
                System.out.println(j + " est protégé dans un tunnel.");
            } else if (j.getNiveauEau() > 0) {
                // Ne boire que si encore vivant
                j.boire();
            }
        }
    }

    public boolean estPerduSable()   { return compterSableTotal() > SABLE_MAX; }
    public boolean estPerduTempete() { return tempete.getNiveau() >= TEMPETE_MAX; }
    public boolean estPerduSoif()    {
        return joueurs.stream().anyMatch(j -> j.getNiveauEau() <= 0);
    }
    public boolean estPerdu() {
        return estPerduSable() || estPerduTempete() || estPerduSoif();
    }

    public void afficherDesert() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Case c = grille[i][j];
                if (c.getType() == TypeCase.OEIL)
                    System.out.print("X ");
                else
                    System.out.print(c.getSable() + " ");
            }
            System.out.println();
        }
        System.out.println("Sable total : " + compterSableTotal());
        System.out.println("Niveau tempête : " + tempete.getNiveau());
    }
    public Joueur getJoueurActuel() {
        return joueurs.get(indexJoueurActuel);
    }
    public int getIndexJoueurActuel() {
        return indexJoueurActuel;
    }
    public int getActionsRestantes() {
        return actionsRestantes;
    }
    public boolean peutJouer() {
        return actionsRestantes > 0;
    }
    private void depenseAction() {
        int p =  actionsRestantes;
        this.actionsRestantes = p-1;
    }
    public void finDeTour() {
        actionDesert();
        indexJoueurActuel = (indexJoueurActuel + 1) % joueurs.size();
        actionsRestantes = 4;
    }
    public boolean deplacerJoueur(Direction direction) {
        if (!peutJouer()) return false;

        Joueur j = getJoueurActuel();
        Case actuelle = getCase(j.getPosition().getLigne(), j.getPosition().getColonne());

        // Joueur enlisé : doit creuser d'abord
        if (actuelle.getSable() >= 2 && j.getType() != TypeJoueur.ALPINISTE) {
            System.out.println("Joueur enlisé ! Creusez d'abord.");
            return false;
        }

        boolean ok = j.seDeplacer(direction, this);
        if (ok) depenseAction();
        return ok;
    }
    /**
     * Le joueur actif explore sa case.
     * @return un message à afficher (indice, oasis, mirage), ou null.
     * Le modèle ne fait aucun affichage : c'est la vue qui montre le message.
     */
    public String explorerZone() {
        if (!peutJouer()) return null;
        Joueur joueur = getJoueurActuel();
        Position p = joueur.getPosition();
        Case c = getCase(p.getLigne(), p.getColonne());
        if (!joueur.explorer(this)) return null;
        depenseAction();

        switch (c.getType()) {
            case INDICE_LIGNE:
            case INDICE_COLONNE:
                return enregistrerIndice(c);
            case OASIS:
                int nb = 0;
                for (Joueur j : joueurs) {
                    if (j.getPosition().getLigne() == p.getLigne()
                            && j.getPosition().getColonne() == p.getColonne()) {
                        j.boireEau(2);
                        nb++;
                    }
                }
                return "Oasis trouvée !\n" + nb + " joueur(s) gagnent +2 eau !";
            case MIRAGE:
                return "Mirage...\nAucune eau !";
            default:
                return null;
        }
    }
    public String enregistrerIndice(Case c) {
        Piece p = c.getPieceIndice();
        if (p == null) return null;

        Position posActuelle = null;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (grille[i][j] == c)
                    posActuelle = new Position(i, j);

        if (posActuelle == null) return null;

        if (c.getType() == TypeCase.INDICE_LIGNE) {
            indicesLigne.put(p, posActuelle);
        } else if (c.getType() == TypeCase.INDICE_COLONNE) {
            indicesColonne.put(p, posActuelle);
        }

        if (indicesLigne.containsKey(p) && indicesColonne.containsKey(p)) {
            int ligne   = indicesLigne.get(p).getLigne();
            int colonne = indicesColonne.get(p).getColonne();
            grille[ligne][colonne].setPieceContenue(p);
            return "Piece " + p.name() + " localisee en (" + ligne + "," + colonne + ") !";
        }
        return null;
    }

    public void ajouterPieceRamassee(Piece p) {
        if (!piecesRamassees.contains(p)) piecesRamassees.add(p);
    }

    public List<Piece> getPiecesRamassees() { return piecesRamassees; }

    public void ramasserPiece() {
        if (!peutJouer()) return;
        boolean ok = getJoueurActuel().ramasserPiece(this);
        if (ok) depenseAction();
    }

    public boolean estGagne() {
        if (piecesRamassees.size() < 4) return false;
        Case pisteDecollage = null;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (grille[i][j].getType() == TypeCase.PISTE_DECOLLAGE)
                    pisteDecollage = grille[i][j];
        if (pisteDecollage == null || pisteDecollage.estBloquee()) return false;
        int pl = pisteDecollage.getPosition().getLigne();
        int pc = pisteDecollage.getPosition().getColonne();
        for (Joueur j : joueurs)
            if (j.getPosition().getLigne() != pl || j.getPosition().getColonne() != pc)
                return false;
        return true;
    }
    /**
     * Le joueur actif donne 1 eau à UN autre joueur sur la même case.
     * Coûte une action, seulement si le don a vraiment eu lieu.
     */
    public boolean donnerEau() {
        if (!peutJouer()) return false;
        Joueur j1 = getJoueurActuel();

        for (Joueur j2 : joueurs) {
            if (j2 != j1 &&
                    j2.getPosition().getLigne() == j1.getPosition().getLigne() &&
                    j2.getPosition().getColonne() == j1.getPosition().getColonne()) {

                if (j1.donnerEau(j2)) {
                    depenseAction();
                    return true;
                }
                return false;   // gourde vide
            }
        }
        return false;           // personne sur la case
    }
    public Position getAutreTunnel(Position posActuelle) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Case c = grille[i][j];
                if (c.getType() == TypeCase.TUNNEL
                        && c.isExploree()
                        && (i != posActuelle.getLigne() || j != posActuelle.getColonne())
                        && !c.estBloquee()) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }
    public boolean creuserCase(int i, int j) {
        if (!peutJouer()) return false;

        Joueur jActuel = getJoueurActuel();
        int li = jActuel.getPosition().getLigne();
        int co = jActuel.getPosition().getColonne();

        int distance = Math.abs(li - i) + Math.abs(co - j);
        if (distance > 1) return false;

        Case c = getCase(i, j);

        if (c.getType() == TypeCase.OEIL) return false;

        if (c.getSable() > 0) {
            c.enleverSable();
            // Archéologue enlève 2 sables en 1 action
            if (getJoueurActuel().getType() == TypeJoueur.ARCHEOLOGUE && c.getSable() > 0) {
                c.enleverSable();
            }
            depenseAction();
            return true;
        }

        return false;
    }
    public boolean deplacerJoueurDiagonal(int dLigne, int dColonne) {
        if (!peutJouer()) return false;
        Joueur j = getJoueurActuel();
        if (j.getType() != TypeJoueur.EXPLORATEUR) return false;
        Case actuelle = getCase(j.getPosition().getLigne(), j.getPosition().getColonne());
        if (actuelle.getSable() >= 2) return false;
        boolean ok = j.seDeplacerDiagonal(dLigne, dColonne, this);
        if (ok) depenseAction();
        return ok;
    }
}