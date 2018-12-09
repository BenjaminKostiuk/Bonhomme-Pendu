import java.io.Serializable;

public class Joueur implements Serializable {
	private String nom;
	private String prenom;
	private int score;
	
	public Joueur(String nom, String prenom) {
		this.nom = nom;
		this.prenom = prenom;
	}
	
	public void setScore(int piecesBonhomme, int diff, int nombreLettres) {
		this.score = nombreLettres * (diff * 2 + 1) - piecesBonhomme + 5;
	}
	
	public int getScore() {
		return score;
	}
	
	
	@Override 
	public String toString() {
		return String.format("%-15s%-8s\t%-7s  %d%n", prenom, nom, "Score:", score);
	}
}
