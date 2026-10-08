package modele;

import java.util.Random;

public class Tempete {
    private double niveau;
    private Random random;

    public Tempete(double niveauInitial) {
        this.niveau = niveauInitial;
        this.random = new Random();
    }

    public Tempete() {
        this(2.0);
    }

    public double getNiveau() { return niveau; }

    public void augmenter() {
        niveau += 0.5;
        System.out.println("Tempête augmente ! Niveau = " + niveau);
    }

    public int tirerAction() {
        int r = random.nextInt(10);
        if (r < 7) return 0;  // vent 70%
        else if (r < 9) return 1; // chaleur 20%
        else return 2;         // déchaînement 10%
    }

    // Force du vent : 1, 2, 3
    public int tirerForce() {
        return random.nextInt(3) + 1;
    }

    // Direction aléatoire pour le vent
    public Direction tirerDirection() {
        Direction[] dirs = Direction.values();
        return dirs[random.nextInt(dirs.length)];
    }
}