import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.sound.sampled.*;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import java.security.SecureRandom;

//Projet final pour ICS4U
//Jeu de bonhomme pendu par Benjamin Kostiuk and Vyvy Ngo

/* 
 * Ce projet utilise le concept des charactères et des positions de ces charactères dans l'alphabet
 * On assume que 'a' à la position 0, 'b' = 1, etc. 
 * Plusieurs fois le charactère est échangé avec sa position dans l'alphabet
 */ 
public class BonhommePendu extends JFrame implements KeyListener {

	private String mot;		//Mot choisi aléatoirement du fichier texte
	//Contient la URLs des images utilisées
	private String[] paths = {"A.png", "B.png", "C.png", "D.png", "E.png", "F.png", "G.png", "H.png", "I.png",
			"J.png", "K.png", "L.png", "M.png", "N.png", "O.png", "P.png", "Q.png", "R.png", "S.png", "T.png",
			"U.png", "V.png", "W.png", "X.png", "Y.png", "Z.png", "RedA.png", "RedB.png", "RedC.png", "RedD.png",
			"RedE.png", "RedF.png", "RedG.png", "RedH.png", "RedI.png", "RedJ.png", "RedK.png", "RedL.png", "RedM.png",
			"RedN.png", "RedO.png", "RedP.png", "RedQ.png", "RedR.png", "RedS.png", "RedT.png", "RedU.png", "RedV.png",
			"RedW.png", "RedX.png", "RedY.png", "RedZ.png", "tete.png", "corps.png", "brasDroit.png", "brasGauche.png",
			"jambeDroit.png", "jambeGauche.png"};	
	private ImagePlaceholder[] positions = new ImagePlaceholder[32];	//Contient les objets ImagePlaceHolder pour chaque image
	private boolean[] estChoisi = new boolean[26]; 						//Tableau qui contient un boolean pour chaque lettre, une lettre choisi devient true
	private boolean activerCle;						//Boolean pour activer les touches du clavier						
	
	private JMenuBar menuBar;									//Barre menu
	private JMenu menu, subMenu, menuRegles, menuClassement;	//Options menu dans menuBar
	private JMenuItem quitter = new JMenuItem("Quitter");				//Boutons pour le menu
	private JMenuItem afficherRegles = new JMenuItem("Afficher Règles");		
	private JMenuItem voirClassement = new JMenuItem("Voir List de Joueurs");
	private JRadioButtonMenuItem radioMenuFacile = new JRadioButtonMenuItem("Facile");
	private JRadioButtonMenuItem radioMenuMoyen = new JRadioButtonMenuItem("Moyen");
	private JRadioButtonMenuItem radioMenuDifficile = new JRadioButtonMenuItem("Difficile");
	
	private static Scanner input;	//Scanner pour lire le fichier texte des mots
	private Joueur joueur;			//Contient l'information du joueur qui joue au moment
	
	private final static int ARRIERE_PLAN = 1;
	private final static int LETTRE = 2;
	private final static int GAGNER = 3;
	
	public BonhommePendu(Joueur j) {	//Constructeur de la fenêtre du jeu
		super("BonhommePendu");
		setBackground(Color.WHITE);
		setSize(1200, 700);
		//Ajoute l'image de l'arrière plan
		try {
			this.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("backdrop.jpg")))));	
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		jouerSon(true, ARRIERE_PLAN);	//Joue la musique en arrière-plan
		joueur = j;						//Donne la valeur au joueur
		activerCle = true;				
		newGame(0);						//Commence un nouveau jeu avec la difficulté facile
		
		menuBar = new JMenuBar();		//Crée la barre menu
		menu = new JMenu("Options");
		menuBar.add(menu);
		subMenu = new JMenu("Nouveau Jeu");
		menu.add(subMenu);
		
		ButtonGroup group = new ButtonGroup();	//Crée un buttonGroup pour gérer les radioButtons 
		group.add(radioMenuFacile);				//Ajoute les options pour la difficulte, 
		subMenu.add(radioMenuFacile);			//Facile, Moyen ou difficile
		radioMenuFacile.setSelected(true);
		group.add(radioMenuMoyen);
		subMenu.add(radioMenuMoyen);
		group.add(radioMenuDifficile);
		subMenu.add(radioMenuDifficile);
		
		menu.add(quitter);						//Ajoute les options quitter, afficher règles et
		menuRegles = new JMenu("Règles");		//classement au menu
		menuBar.add(menuRegles);
		menuRegles.add(afficherRegles);
		menuClassement = new JMenu("Classement");
		menuBar.add(menuClassement);
		menuClassement.add(voirClassement);
		
		this.setJMenuBar(menuBar);
		
		setImagePositions();		//Ceci créera et placera les positions de images
		
		radioMenuButtonHandler handler = new radioMenuButtonHandler();	//Crée un handler pour le radioBoutons
		
		radioMenuFacile.addActionListener(handler);		//Ajoute le handler au radioBoutons comme ActionListener
		radioMenuMoyen.addActionListener(handler);
		radioMenuDifficile.addActionListener(handler);
		
		quitter.addActionListener(new ActionListener() {	//Classe anonyme pour le bouton quitter

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		
		afficherRegles.addActionListener(new ActionListener() {	//Classe anonyme pour le bouton afficherRègles

			@Override
			public void actionPerformed(ActionEvent arg0) {
				FenetreRegles frame = new FenetreRegles();		//Crée une fenêtre pour afficher les règles
				frame.setVisible(true);
				frame.setResizable(false);
				frame.setLocation(40, 70);
			}
			
		});
		
		voirClassement.addActionListener(new ActionListener() {		//Classe anonyme pour le classement

			@Override
			public void actionPerformed(ActionEvent e) {
				
				FenetreJoueurs.openFile();					//Lit les scores et les ajoute à la liste de joueurs
				FenetreJoueurs.readFile(joueur, false);
				FenetreJoueurs.closeInput();
				
				FenetreJoueurs liste = new FenetreJoueurs(true);	//Affiche la liste des top scores
				liste.setVisible(true);	
				liste.setLocation(800, 200);
				liste.setResizable(false);
			}
			
		});
		
		this.addKeyListener(this);	//Ajoute cette classe comme keyListener
	}
	
	private class radioMenuButtonHandler implements ActionListener {	//Classe privée pour gérer les radioBoutons du menu

		@Override
		public void actionPerformed(ActionEvent e) {
			
			JRadioButtonMenuItem radioButtonMenu = (JRadioButtonMenuItem) e.getSource();
			
			int diff = 0;
			if(radioButtonMenu.getText().equals("Facile"))
				diff = 0;
			else if(radioButtonMenu.getText().equals("Moyen"))
				diff = 1;
			else if(radioButtonMenu.getText().equals("Difficile"))
				diff = 2;
			
			activerCle = true;
			newGame(diff);
			
		}
		
	}

	public static void openFile() {		//Ouvre le fichier texte qui contient les mots
		try {
			input = new Scanner(Paths.get("listeMots.txt"));
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Erreur à ouvrir le fichier", "Erreur", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
	
	public static String readRecords(int difficulte) {			//Lis les mots du fichier et retourne un mot choisi aléatoirement
		ArrayList<String> mots = new ArrayList<String>();
		SecureRandom rdm = new SecureRandom();		
		
		try {
			while(input.hasNextLine()) {		//Ajoute un mot à la liste s'ils appartiennent à la difficulte
				String mot = input.nextLine();
				if(difficulte == 0 && mot.length() > 6) {	//Mots facile ont une longeur supérieur à 6 
					mots.add(mot);
				}
				else if(difficulte == 1 && mot.length() <= 6 && mot.length() >= 5) { //Mots de difficulté moyenne ont une longeur entre 6 et 5 
					mots.add(mot);
				}
				else if(difficulte == 2 && mot.length() < 5) { //Mots difficile ont une longeur inférieur à 5
					mots.add(mot);
				}
			}
			
			return mots.get(rdm.nextInt(mots.size()));		//Retourne un mot aléatoirement de la liste de mots correspondants
		}
		catch(NoSuchElementException e) {
			JOptionPane.showMessageDialog(null, "Erreur : Le fichier est mal ecrit.", "Erreur", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		catch(IllegalStateException e) {
			JOptionPane.showMessageDialog(null, "Erreur : Ne peut pas lire à partir du fichier.", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}
	
	public static void closeFile() {	//Ferme le fichier text
		if (input != null) {
			input.close();
		}
	}
	
	public Integer[] getCharPositions(char c) {		//Retourne un tableau avec les positions de la lettre choisi dans le mot
		ArrayList<Integer> positions = new ArrayList<Integer>();
		
		for(int i = 0; i < mot.length(); i ++) {
			if(c == mot.charAt(i)) {
				positions.add(i);
			}
		}
		Integer[] lettres = positions.toArray(new Integer[positions.size()]);
		return lettres;
	}
	
	public void setImagePositions() {	//Crée les objects ImagePlaceholder avec la position abs de chaque image
		int startX = 610;
		//Pour les images des lettres
		for(int x = 0; x < 6; x++) {
			positions[x] = new ImagePlaceholder(startX, 70);
			startX += 80;
		}
		startX = 570;
		for(int x = 6; x < 13; x++) {
			positions[x] = new ImagePlaceholder(startX, 150);
			startX += 80;
		}
		startX = 610;
		for(int x = 13; x < 19; x++) {
			positions[x] = new ImagePlaceholder(startX, 230);
			startX += 80;
		}
		startX = 570;
		for(int x = 19; x < 26; x++) {
			positions[x] = new ImagePlaceholder(startX, 310);
			startX += 80;
			
		//Pour les images des pièces du bonhomme
		positions[26] = new ImagePlaceholder(387,240);
		positions[27] = new ImagePlaceholder(415,308);
		positions[28] = new ImagePlaceholder(422,310);
		positions[29] = new ImagePlaceholder(374,319);
		positions[30] = new ImagePlaceholder(418,420);
		positions[31] = new ImagePlaceholder(380,422);
		}
	}
	
	private boolean containsLetter(int index) { //Vérifie si la lettre passé est contenu dans le mot
		char x = findChar(index);
		for(int i = 0; i < mot.length(); i++) {
			if(x == mot.charAt(i))
				return true;
		}
		return false;
	}
	
	private char findChar(int x) {	//Retourne le charactère associée avec l'index de la lettre dans l'alphabet
		char c = 'a';				// 'a' ayant la position de 0, 'b' de 1, etc. 
		for(int y = 0; y < x; y++) {
			c++;
		}
		return c;
	}
	
	public int findInt(char a) throws ExceptionLettre {	//Retourne l'index associée avec un charactère
		char x = 'a';
		for(int i = 0; i < 26; i++) {
			if(a == x) {
				return i;
			}
			else if (i == 25){
				throw new ExceptionLettre(a);	//Si le charactère n'est pas contenu dans l'alphabet lance une exception ExceptionLettre
			}
			x++;
		}
		return 'z';
	}
	
	public void endGame(int hangman) {		//Verifie si la partie est terminé et si le jeu est gagné ou perdu
		char[] lettres = mot.toCharArray();
		
		if(hangman == 6) {		//Si perdu
			jouerSon(true, GAGNER);	//Jouer le son pour une partie gagné
			JOptionPane.showMessageDialog(null, "Bonhomme pendu! Jouez encore?", "Jeu perdu", JOptionPane.INFORMATION_MESSAGE);
			activerCle = false;		//Désactive l'option pour entrer plus de lettres
		}
		else {					
			boolean test = true;
			for(char c : lettres) {		//Détermine si tous les lettres ont étés choisies
				try {
					if(!estChoisi[findInt(c)])
						test = false;
				} catch (ExceptionLettre e) {
					e.printStackTrace();
				}
			}
			if(test) {	//Si gagné
				jouerSon(false, GAGNER);
				int difficulte = 0;
				if(radioMenuFacile.isSelected())
					difficulte = 0;
				else if(radioMenuMoyen.isSelected())
					difficulte = 1;
				else if(radioMenuDifficile.isSelected())
					difficulte = 2;
				
				joueur.setScore(hangman, difficulte, lettres.length);	//Donne au joueur son score gagnant
				FenetreJoueurs.openFile();								//Ajoute le joueur et son score au fichier gardant le classement
				FenetreJoueurs.readFile(joueur, true);
				FenetreJoueurs.closeInput();
				FenetreJoueurs.writeJoueurs();
				
				JOptionPane.showMessageDialog(null, "Jeu Gagné! Jouez encore?", "Jeu gagnée", JOptionPane.INFORMATION_MESSAGE);
				activerCle = false;
			}
		}
		
	}

	public void newGame(int difficulte) {		//Initialize un nouveau jeu
		for(int x = 0; x < estChoisi.length; x++) {
			estChoisi[x] = false;		//Remets les lettres choisi à false
		}
		openFile();
		mot = readRecords(difficulte);	//Choisi un nouveau mot du fichier texte
		closeFile();
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;	//Crée un object Graphics 2D
		
		int start = 600 - (90 * mot.length() - 30) / 2;	//Donne la position abs de la première lettre
		int piecesBonhomme = 0;
		
		//Dessine les lettres
		for(int x = 0; x < estChoisi.length; x++) {
			if(estChoisi[x]) {
				if(containsLetter(x))	//Si la lettre choisi est contenu dans le mot
				{
					try {
						//Dessine les lettres dans la section des lettres choisies
						g2.drawImage(ImageIO.read(new File("lettres/" + paths[x])), positions[x].getX(),  positions[x].getY(), 49, 55, Color.WHITE, null);
						//Dessine les lettres sur les traits du mot
						for(int i : getCharPositions(findChar(x))) {
							g2.drawImage(ImageIO.read(new File("lettres/" + paths[x])), start + 90 * i, 590, 49, 55, Color.WHITE, null);	//Depends on method getCharPositions
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				else {				//Si la lettre choisi n'est pas contenu dans le mot
					try {
						//Dessine les lettres rouges d'une mauvaise lettre
						g2.drawImage(ImageIO.read(new File("lettres/" + paths[x + 26])), positions[x].getX(), positions[x].getY(), 49, 55, Color.WHITE, null);
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
					piecesBonhomme++;	//Ajoute un des pièces du bonhomme pendu pour chaque mauvaise lettre
				}
			}
		}
		
		//Dessine les traits pour chaque lettre dans le mot
		g2.setColor(Color.BLACK);
		for(int x = 0; x < mot.length(); x++) {
			g2.drawLine(start, 650, start + 60, 650);
			start += 60 + 30;
		}
		
		//Dessine les membres du bonhomme pendu
		for(int x = 52; x < 52 + piecesBonhomme; x++) {
			try {
				g2.drawImage(ImageIO.read(new File("bonhomme/" + paths[x])), positions[x - 26].getX(), positions[x - 26].getY(), Color.BLACK, null);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		endGame(piecesBonhomme);	//Vérifie si le partie est finie
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent e) {	//Méthode KeyListener 
		if(activerCle) {
			char lettre = e.getKeyChar();
			try {
				estChoisi[findInt(lettre)] = true;	//Mets l'index de la lettre à true 
				jouerSon(containsLetter(findInt(lettre)), LETTRE); 	//Joue le son assosié avec la lettre
			}
			catch(ExceptionLettre exceptionLettre) {
				JOptionPane.showMessageDialog(null, exceptionLettre, "Erreur", JOptionPane.ERROR_MESSAGE);	//Message erreur pour une lettre invalide
			}
			repaint();
		}
	}

	public void jouerSon(boolean gagner, int choix) {	//Méthode qui choisi quel ficher .wav joueur
		try {
			URL son = null;
			Clip music = AudioSystem.getClip();
			
			if(choix == 1)
				son = this.getClass().getClassLoader().getResource("musicArrierePlan.wav");
			else if (choix == 2) {
				if(gagner) 
					son = this.getClass().getClassLoader().getResource("bonneLettre.wav");
				else 
					son = this.getClass().getClassLoader().getResource("mauvaiseLettre.wav");
			}
			else if (choix == 3) {
				if(gagner) 
					son = this.getClass().getClassLoader().getResource("musicPerdu.wav");
				else
					son = this.getClass().getClassLoader().getResource("musicGagner.wav");
			}
			
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(son);	//Produit une copy du fichier
			music.open(audioIn);												//Ouvre le fichier
			if(choix == 1)
				music.loop(Clip.LOOP_CONTINUOUSLY);								//Recommence la chanson quand elle termine
			else
				music.start();													//Joue le son une seule fois
		}
		catch (UnsupportedAudioFileException e) {
	        e.printStackTrace();
	    } 
	    catch (IOException e) {
	    	e.printStackTrace();
	    } 
	    catch (LineUnavailableException e) {
	    	e.printStackTrace();
	    }
	}
	
	public static void main(String[] args) {		
		FenetreJoueurs login = new FenetreJoueurs();	//Crée la fenêtre pour le login du joueur
		login.setVisible(true);
		login.setResizable(true);
		login.setLocationRelativeTo(null);
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}

