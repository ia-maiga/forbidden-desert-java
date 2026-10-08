package modele;

import org.junit.Test;
import static org.junit.Assert.*;

public class DesertTest {

    // Test 1 : le sable s'ajoute correctement
    @Test
    public void testAjouterSable() {
        Case c = new Case(new Position(0, 0), TypeCase.SABLE);
        assertEquals(0, c.getSable());
        c.ajouterSable();
        assertEquals(1, c.getSable());
        c.ajouterSable();
        assertEquals(2, c.getSable());
    }

    // Test 2 : une case est bloquée à partir de 2 sables
    @Test
    public void testCaseBloquee() {
        Case c = new Case(new Position(0, 0), TypeCase.SABLE);
        assertFalse(c.estBloquee());
        c.ajouterSable();
        assertFalse(c.estBloquee());
        c.ajouterSable();
        assertTrue(c.estBloquee());
    }

    // Test 3 : enlever du sable
    @Test
    public void testEnleverSable() {
        Case c = new Case(new Position(0, 0), TypeCase.SABLE);
        c.ajouterSable();
        c.ajouterSable();
        c.enleverSable();
        assertEquals(1, c.getSable());
        assertFalse(c.estBloquee());
    }

    // Test 4 : un joueur perd de l'eau
    @Test
    public void testBoire() {
        Joueur j = new Joueur(new Position(0, 0), TypeJoueur.EXPLORATEUR);
        assertEquals(5, j.getNiveauEau());
        j.boire();
        assertEquals(4, j.getNiveauEau());
    }

    // Test 5 : un joueur meurt si eau = 0
    @Test
    public void testMortSoif() {
        Joueur j = new Joueur(new Position(0, 0), TypeJoueur.EXPLORATEUR);
        for (int i = 0; i < 5; i++) j.boire();
        assertEquals(0, j.getNiveauEau());
        boolean vivant = j.boire();
        assertFalse(vivant);
    }

    // Test 6 : défaite par soif détectée par Desert
    @Test
    public void testEstPerduSoif() {
        Desert d = new Desert(1, 2.0);
        assertFalse(d.estPerduSoif());
        Joueur j = d.getJoueur(0);
        for (int i = 0; i < 5; i++) j.boire();
        assertTrue(d.estPerduSoif());
    }

    // Test 7 : défaite par tempête
    @Test
    public void testEstPerduTempete() {
        Desert d = new Desert(1, 2.0);
        assertFalse(d.estPerduTempete());
        for (int i = 0; i < 10; i++) d.getTempete().augmenter();
        assertTrue(d.estPerduTempete());
    }

    // Test 8 : défaite par sable
    @Test
    public void testEstPerduSable() {
        Desert d = new Desert(1, 2.0);
        assertFalse(d.estPerduSable());
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                for (int k = 0; k < 10; k++)
                    d.getCase(i, j).ajouterSable();
        assertTrue(d.estPerduSable());
    }

    //Test 9 : déplacement valide
    @Test
    public void testDeplacementValide() {
        Desert d = new Desert(1, 2.0);
        Joueur j = d.getJoueur(0);
        Position avant = j.getPosition();
        boolean ok = d.deplacerJoueur(Direction.SUD);
        assertTrue(ok);
        assertNotEquals(avant.getLigne(), j.getPosition().getLigne());
    }

    // Test 10 : déplacement bloqué par sable
    @Test
    public void testDeplacementBloque() {
        Desert d = new Desert(1, 2.0);
        Joueur j = d.getJoueur(0);
        // La case (0,1) est INDICE_LIGNE, on met 2 sables dessus
        d.getCase(0, 1).ajouterSable();
        d.getCase(0, 1).ajouterSable();
        boolean ok = j.seDeplacer(Direction.EST, d);
        assertFalse(ok);
    }

    // Test 11 : le compteur d'actions diminue
    @Test
    public void testCompteurActions() {
        Desert d = new Desert(1, 2.0);
        assertEquals(4, d.getActionsRestantes());
        d.deplacerJoueur(Direction.SUD);
        assertEquals(3, d.getActionsRestantes());
    }

    // Test 12 : finDeTour remet les actions à 4
    @Test
    public void testFinDeTour() {
        Desert d = new Desert(2, 2.0);
        d.deplacerJoueur(Direction.SUD);
        d.deplacerJoueur(Direction.SUD);
        d.finDeTour();
        assertEquals(4, d.getActionsRestantes());
    }

    // Test 13 : donner eau entre joueurs
    @Test
    public void testDonnerEau() {
        Joueur j1 = new Joueur(new Position(0, 0), TypeJoueur.EXPLORATEUR);
        Joueur j2 = new Joueur(new Position(0, 0), TypeJoueur.ALPINISTE);
        for (int i = 0; i < 3; i++) j2.boire();
        assertEquals(2, j2.getNiveauEau());
        j1.donnerEau(j2);
        assertEquals(3, j2.getNiveauEau());
        assertEquals(4, j1.getNiveauEau());
    }

    // Test 14 : boireEau ne dépasse pas le max
    @Test
    public void testBoireEauMax() {
        Joueur j = new Joueur(new Position(0, 0), TypeJoueur.EXPLORATEUR);
        j.boireEau(10);
        assertEquals(5, j.getNiveauEau());
    }

    // Test 15 : ramasser une pièce
    @Test
    public void testRamasserPiece() {
        Desert d = new Desert(1, 2.0);
        d.getCase(1, 1).setPieceContenue(Piece.HELICE);
        assertEquals(0, d.getPiecesRamassees().size());
        d.getCase(1, 1).getPieceContenue();
        d.ajouterPieceRamassee(Piece.HELICE);
        d.getCase(1, 1).setPieceContenue(null);
        assertEquals(1, d.getPiecesRamassees().size());
    }

    // ---- Tests ajoutés après correction de bugs ----

    // Test 16 : après des tempêtes, chaque case connaît sa vraie position
    // (avant correction, une tuile déplacée par le vent gardait l'ancienne,
    // et estGagne() vérifiait la mauvaise case pour la piste de décollage)
    @Test
    public void testPositionsCoherentesApresTempete() {
        Desert d = new Desert(2, 5.0);
        for (int tour = 0; tour < 30; tour++) d.actionDesert();
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) {
                Position p = d.getCase(i, j).getPosition();
                assertEquals(i, p.getLigne());
                assertEquals(j, p.getColonne());
            }
    }

    // Test 17 : donner de l'eau ne fait jamais passer les actions en négatif
    @Test
    public void testDonnerEauRespecteLesActions() {
        Desert d = new Desert(2, 2.0);           // les 2 joueurs partent sur (0,0)
        d.getJoueur(1).boire(); d.getJoueur(1).boire();
        d.getJoueur(1).boire(); d.getJoueur(1).boire();
        for (int k = 0; k < 4; k++) assertTrue(d.donnerEau());
        assertEquals(0, d.getActionsRestantes());
        assertFalse(d.donnerEau());
        assertEquals(0, d.getActionsRestantes());
    }

    // Test 18 : l'eau est donnée à un seul joueur, pas à tous ceux de la case
    @Test
    public void testDonnerEauUnSeulReceveur() {
        Desert d = new Desert(3, 2.0);
        d.getJoueur(1).boire();
        d.getJoueur(2).boire();
        assertTrue(d.donnerEau());
        assertEquals(4, d.getJoueur(0).getNiveauEau());
        assertEquals(9, d.getJoueur(1).getNiveauEau() + d.getJoueur(2).getNiveauEau());
        assertEquals(3, d.getActionsRestantes());
    }

    // Test 19 : gourde vide -> pas de don et pas d'action consommée
    @Test
    public void testDonnerEauGourdeVide() {
        Desert d = new Desert(2, 2.0);
        for (int k = 0; k < 5; k++) d.getJoueur(0).boire();
        assertFalse(d.donnerEau());
        assertEquals(4, d.getActionsRestantes());
    }

    // Test 20 : explorer une oasis donne de l'eau, sans aucune fenêtre
    // (le test tourne sans écran : le modèle ne doit pas utiliser Swing)
    @Test
    public void testExplorerOasis() {
        Desert d = new Desert(1, 2.0);
        Joueur j = d.getJoueur(0);
        for (int i = 0; i < 5; i++)
            for (int k = 0; k < 5; k++)
                if (d.getCase(i, k).getType() == TypeCase.OASIS) {
                    while (d.getCase(i, k).getSable() > 0) d.getCase(i, k).enleverSable();
                    j.setPosition(new Position(i, k));
                    j.boire(); j.boire();
                    String message = d.explorerZone();
                    assertNotNull(message);
                    assertTrue(message.startsWith("Oasis"));
                    assertEquals(5, j.getNiveauEau());
                    assertTrue(d.getCase(i, k).isExploree());
                    assertEquals(3, d.getActionsRestantes());
                    return;
                }
        fail("aucune oasis sur la grille");
    }
}
