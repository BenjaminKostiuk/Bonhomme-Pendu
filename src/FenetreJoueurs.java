import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FenetreJoueurs extends JFrame {
	
	private JTextArea area;
	
	private JTextField nomField = new JTextField(15);
	private JTextField prenomField = new JTextField(15);
	
	private JButton confirmer = new JButton("Ok");
	private Joueur joueur;
	
	private static ObjectInputStream input;
	private static ObjectOutputStream output;
	private static ArrayList<Joueur> listeJoueurs = new ArrayList<Joueur>(); //Liste qui contient les informations des joueurs gagnants
	
	public FenetreJoueurs() {
		super("Login");
		setSize(540, 80);
		setLayout(new FlowLayout(10));
		add(new JLabel("Nom :"));
		add(nomField);
		add(new JLabel("Prenom : "));
		add(prenomField);
		add(confirmer);
		
		confirmer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(nomField.getText().equals("") || prenomField.getText().equals("")) //Vérifie si l'utilisateur a entré son nom et prénom
						throw new Exception("Informations ne sont pas entrés.");
				
					joueur = new Joueur(nomField.getText(), prenomField.getText());
					
					//Crée la fenêtre du jeu Bonhomme Pendu
					BonhommePendu frame = new BonhommePendu(joueur);
					frame.setVisible(true);
					frame.setResizable(false);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setLocationRelativeTo(null);
					
					fermer();
				}
				catch(NullPointerException e) {	//Change this to french
					JOptionPane.showMessageDialog(null, "Erreur à retrouver les fichiers musique", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
	}
	
	public FenetreJoueurs(boolean b) {	//Contructeur de la fenêtre du classement des joueurs 
		super("Liste de joueurs");
		setSize(400, 500);
		area = new JTextArea("Meilleurs scores: \n");
		add(area);
		sortList();
		for(Joueur joueur : listeJoueurs) {
			area.setText(area.getText() + joueur);
		}
	}
	
	
	public static void openFile() {		
		try {
			input = new ObjectInputStream(Files.newInputStream(Paths.get("listeJoueurs.txt")));
		}
		catch(IOException ioException) {
			System.out.println(ioException.getMessage());
			JOptionPane.showMessageDialog(null, "Erreur à ouvrir le fichier", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void readFile(Joueur j, boolean gagne) {	//Lecture du fichier
		listeJoueurs.removeAll(listeJoueurs);
		if(gagne)
			listeJoueurs.add(j);
		
		try {
			while(true) {
		
				Joueur joueur = (Joueur) input.readObject();	//Ajoute à la liste de joueurs
				listeJoueurs.add(joueur);
			}
		}
		catch (EOFException endOfFileException) {} 
	    catch (ClassNotFoundException classNotFoundException) {
	       System.err.println("Objet invalide.");
	    } 
	    catch (IOException ioException) {
	       System.err.println("Erreur de lecture à partir du fichier");
	    }
		catch (NullPointerException e) {
			System.err.println("Aucun object dans la liste");
		}
		
	}
	
	public static void closeInput() {
		try {
			if(input != null)
				input.close();
		}
		catch (IOException ioException) {
			System.err.println("Erreur à fermer le fichier.");
	         System.exit(1);
		}
	}
	
	public static void writeJoueurs() {	//Écrit les joueurs dans le fichier sous forme XML
		
		try {
			output = new ObjectOutputStream(Files.newOutputStream(Paths.get("listeJoueurs.txt")));
		}
		catch(IOException ioException) {
			System.err.println("Erreur à ouvrir le fichier. fin du programme.");
	        System.exit(1); 
		}
		for(Joueur j : listeJoueurs) {
			try {
				output.writeObject(j);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(output != null) {
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void sortList() {	//Trie la liste de joueurs selon leurs scores
		Collections.sort(listeJoueurs, new Comparator<Joueur>() {
			@Override
			public int compare(Joueur j1, Joueur j2) {
				if(j1.getScore() > j2.getScore())
					return -1;
				if(j1.getScore() < j2.getScore())
					return 1;
				return 0;
			}
			
		});
	}
	
	public void fermer() {
		super.dispose();
	}
	
	
}
