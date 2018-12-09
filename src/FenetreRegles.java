import java.io.IOException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class FenetreRegles extends JFrame {
	
	private JTextArea area;
	private static Scanner input;
	
	public FenetreRegles() {
		super("Règles");
		setSize(450, 200);
		area = new JTextArea();
		add(area);
		ouvrirFichier();
		lireFichier();
		fermerFichier();
		
	}
	
	public static void ouvrirFichier() {
		try
	      {
	         input = new Scanner(Paths.get("regles.txt")); 
	      } 
	      catch (IOException ioException)
	      {
	         System.err.println("Erreur dans l'ouverture du fichier.");
	         System.exit(1);
	      } 
	}
	
	public void lireFichier() {
		 try 
	      {
	         while (input.hasNext()) // parcourir tous le fichier
	         {
	        	 area.setText(area.getText() + input.nextLine() + "\n");
	         }
	      } 
	      catch (NoSuchElementException elementException)
	      {
	         System.err.println("Le fichier est mal écrit.");
	      } 
	      catch (IllegalStateException stateException)
	      {
	         System.err.println("Erreur à lire à partir du fichier.");
	      } 
	}
	
	public static void fermerFichier() {
		if (input != null)
	         input.close();
	}
		
}
