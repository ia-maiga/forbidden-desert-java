package modele;

public class Joueur {
    private Position position;
    private int niveauEau;
    private static final int EAU_MAX = 5;
    private TypeJoueur type_j;

    public Joueur(Position position, TypeJoueur type) {
        this.position = position;
        this.niveauEau = EAU_MAX;
        this.type_j = type;
    }

    public Position getPosition() { return position; }
    public int getNiveauEau() { return niveauEau; }
    public TypeJoueur getType() { return type_j; }

    public boolean seDeplacer(Direction direction, Desert desert) {
        int ligne   = position.getLigne();
        int colonne = position.getColonne();
        switch (direction) {
            case NORD:  ligne--;   break;
            case SUD:   ligne++;   break;
            case EST:   colonne++; break;
            case OUEST: colonne--; break;
        }

        if (ligne < 0 || ligne >= 5 || colonne < 0 || colonne >= 5) return false;

        Case cible = desert.getCase(ligne, colonne);
        if(cible.getType() == TypeCase.OEIL) return false;
        if (cible.estBloquee() && this.getType() != TypeJoueur.ALPINISTE) return false;

        position = new Position(ligne, colonne);

        // Téléportation tunnel → tunnel (une seule fois)
        Case nouvelleCase = desert.getCase(position.getLigne(), position.getColonne());
        if (nouvelleCase.getType() == TypeCase.TUNNEL && nouvelleCase.isExploree()) {
            Position autreTunnel = desert.getAutreTunnel(position);
            if (autreTunnel != null) {
                Case caseTunnel = desert.getCase(
                        autreTunnel.getLigne(), autreTunnel.getColonne());
                if (!caseTunnel.estBloquee()) {
                    position = autreTunnel;
                    System.out.println("Teleportation vers tunnel " + autreTunnel);
                }
            } else {
                System.out.println("Pas d'autre tunnel explore.");
            }
        }

        return true;
    }
    // Pour l'explorateur
    public boolean seDeplacerDiagonal(int dLigne, int dColonne, Desert desert) {
        if (type_j != TypeJoueur.EXPLORATEUR) return false;
        int ligne   = position.getLigne()   + dLigne;
        int colonne = position.getColonne() + dColonne;
        if (ligne < 0 || ligne >= 5 || colonne < 0 || colonne >= 5) return false;
        Case cible = desert.getCase(ligne, colonne);
        if (cible.estBloquee()) return false;
        position = new Position(ligne, colonne);
        return true;
    }
    public boolean creuser(Direction d, Desert desert) {
        int i = position.getLigne();
        int j = position.getColonne();

        switch (d) {
            case NORD: i--; break;
            case SUD: i++; break;
            case EST: j++; break;
            case OUEST: j--; break;
        }

        if (i < 0 || i >= 5 || j < 0 || j >= 5) return false;

        desert.getCase(i, j).enleverSable();
        return true;
    }

    /**
     * Explore la case du joueur si elle n'est pas ensablée et pas encore
     * explorée. Les effets (indice, oasis...) sont gérés par Desert.
     */
    public boolean explorer(Desert desert) {
        Case c = desert.getCase(position.getLigne(), position.getColonne());
        if (c.getSable() == 0 && !c.isExploree()) {
            c.explorer();
            return true;
        }
        return false;
    }

    public boolean boire() {
        if (niveauEau <= 0) return false;
        niveauEau--;
        return true;
    }

    public void boireEau(int quantite) {
        niveauEau = Math.min(niveauEau + quantite, EAU_MAX);
    }

    @Override
    public String toString() {
        return "Joueur en " + position + " eau=" + niveauEau;
    }

    public boolean ramasserPiece(Desert desert) {
        int i = position.getLigne();
        int j = position.getColonne();
        Case c = desert.getCase(i, j);
        Piece p = c.getPieceContenue();
        if (p != null) {
            desert.ajouterPieceRamassee(p);
            c.setPieceContenue(null);
            System.out.println("Pièce " + p + " ramassée !");
            return true;
        }
        return false;
    }
    public void deplacerAutreJoueur(Joueur autre, Direction d, Desert des) {
        if(this.getType() != TypeJoueur.NAVIGATRICE) return;
        autre.seDeplacer(d, des);

    }
    public boolean donnerEau(Joueur autre) {
        if (this.niveauEau <= 0) return false;

        this.niveauEau--;
        autre.boireEau(1);
        return true;
    }
    public void setPosition(Position p) {
        this.position = p;
    }
}