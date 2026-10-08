package modele;

public class Case {
    private Position position;
    private int sable;
    private TypeCase type;
    private boolean exploree;
    private Piece pieceContenue;
    private Piece pieceIndice;

    public Case(Position position, TypeCase type) {
        this.position = position;
        this.sable = 0;
        this.type = type;
        this.exploree = false;
    }

    public int getSable() { return sable; }
    public TypeCase getType() { return type; }
    public boolean isExploree() { return exploree; }
    public Position getPosition() { return position; }

    /** Appelé quand le vent déplace la tuile : la case garde sa position à jour. */
    public void setPosition(Position position) { this.position = position; }

    public boolean estBloquee() {
        return sable >= 2 || type == TypeCase.OEIL;
    }

    public void ajouterSable() { sable++; }

    public void enleverSable() {
        if (sable > 0) sable--;
    }

    public void explorer() {
        if (!exploree && sable == 0) exploree = true;
    }

    @Override
    public String toString() {
        return "Case " + position + " [" + type + "] sable=" + sable
                + (exploree ? " (explorée)" : "");
    }
    public Piece getPieceContenue() { return pieceContenue; }
    public void setPieceContenue(Piece p) { this.pieceContenue = p; }
    public Piece getPieceIndice() { return pieceIndice; }
    public void setPieceIndice(Piece p) { this.pieceIndice = p; }
}