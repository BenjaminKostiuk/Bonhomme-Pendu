import java.io.Serializable;

//Cette classe crée des objects Joueur représentants les joueurs du jeu
public class Joueur implements Serializable {
	private String nom;
	private String prenom;
	private int score;
	
	public Joueur(String nom, String prenom) {
		this.nom = nom;
		this.prenom = prenom;
		score = 0;
	}
	
	public void setScore(int piecesBonhomme, int diff, int nombreLettres) {	
		//Ajoute au joueur son score lorsqu'il gagne la partie
		this.score = nombreLettres * (diff * 2 + 1) - piecesBonhomme + 5;
	}
	
	public int getScore() {
		return score;
	}
	
	@Override 
	public String toString() {
		return String.format("%-15s\t%-15s\t%-7s  %d%n", prenom, nom, "Score:", score);
	}
}
