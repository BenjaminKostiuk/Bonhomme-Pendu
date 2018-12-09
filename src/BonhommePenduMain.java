import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
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


public class BonhommePenduMain extends JFrame implements KeyListener {

	private String mot;		//Randomly chosen word from the list	
	private String[] paths = {"A.png", "B.png", "C.png", "D.png", "E.png", "F.png", "G.png", "H.png", "I.png",
			"J.png", "K.png", "L.png", "M.png", "N.png", "O.png", "P.png", "Q.png", "R.png", "S.png", "T.png",
			"U.png", "V.png", "W.png", "X.png", "Y.png", "Z.png", "RedA.png", "RedB.png", "RedC.png", "RedD.png",
			"RedE.png", "RedF.png", "RedG.png", "RedH.png", "RedI.png", "RedJ.png", "RedK.png", "RedL.png", "RedM.png",
			"RedN.png", "RedO.png", "RedP.png", "RedQ.png", "RedR.png", "RedS.png", "RedT.png", "RedU.png", "RedV.png",
			"RedW.png", "RedX.png", "RedY.png", "RedZ.png", "tete.png", "corps.png", "brasDroit.png", "brasGauche.png",
			"jambeDroit.png", "jambeGauche.png"};
	private ImagePlaceholder[] positions = new ImagePlaceholder[32];	//Positions for images
	private boolean[] estChoisi = new boolean[26]; //Array to know which letters to show when chosen
	private boolean activerCle;
	
	private JMenuBar menuBar;
	private JMenu menu, subMenu, menuRegles, menuClassement;
	private JMenuItem nouveauJeu = new JMenuItem("Nouveau Jeu");
	private JMenuItem quitter = new JMenuItem("Quitter");
	private JMenuItem voirRegles = new JMenuItem("Voir Règles");
	private JMenuItem voirClassement = new JMenuItem("Voir List de Joueurs");
	private JRadioButtonMenuItem radioMenuFacile = new JRadioButtonMenuItem("Facile");
	private JRadioButtonMenuItem radioMenuMoyen = new JRadioButtonMenuItem("Moyen");
	private JRadioButtonMenuItem radioMenuDifficile = new JRadioButtonMenuItem("Difficile");
	
	private static Scanner input;	//Scanner for input
	private Joueur joueur;
	
	private final static int ARRIERE_PLAN = 1;
	private final static int LETTRE = 2;
	private final static int GAGNER = 3;
	
	public BonhommePenduMain(Joueur j) {	//Basic window
		super("BonhommePendu");
		setBackground(Color.WHITE);
		setSize(1200, 700);
		try {
			this.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("backdrop.jpg")))));	//backdrop is the name of image for background
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		jouerSon(true, ARRIERE_PLAN);
		joueur = j;
		activerCle = true;
		newGame();
		
		menuBar = new JMenuBar();
		menu = new JMenu("Options");
		menuBar.add(menu);
		menu.add(nouveauJeu);
		subMenu = new JMenu("Changer la difficulta");
		menu.add(subMenu);
		ButtonGroup group = new ButtonGroup();
		group.add(radioMenuFacile);
		subMenu.add(radioMenuFacile);
		radioMenuFacile.setSelected(true);
		group.add(radioMenuMoyen);
		subMenu.add(radioMenuMoyen);
		group.add(radioMenuDifficile);
		subMenu.add(radioMenuDifficile);
		menu.add(quitter);
		menuRegles = new JMenu("Ragles");
		menuBar.add(menuRegles);
		menuRegles.add(voirRegles);
		menuClassement = new JMenu("Classement");
		menuBar.add(menuClassement);
		menuClassement.add(voirClassement);
		
		this.setJMenuBar(menuBar);
		
		setImagePositions();
		
		nouveauJeu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				activerCle = true;
				newGame();
			}
			
		});
		
		
		quitter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		
		voirRegles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				FenetreRegles frame = new FenetreRegles();
				frame.setVisible(true);
				frame.setResizable(false);
				frame.setLocation(40, 70);
				
			}
			
		});
		
		voirClassement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				FenetreJoueurs.openFile();
				FenetreJoueurs.readFile(joueur, false);
				FenetreJoueurs.closeInput();
				
				FenetreJoueurs liste = new FenetreJoueurs(true);
				liste.setVisible(true);
				liste.setLocation(800, 200);
				liste.setResizable(false);
			}
			
		});
		
		this.addKeyListener(this);
	}

	public static void openFile() {
		try {
			input = new Scanner(Paths.get("wordList.txt"));
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Erreur à ouvrir le fichier", "Erreur", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
	
	public static String readRecords(int difficulte) {
		ArrayList<String> tousMots = new ArrayList<String>();
		SecureRandom rdm = new SecureRandom();		
		
		try {
			while(input.hasNextLine()) {
				String mot = input.nextLine();
				if(difficulte == 0 && mot.length() > 6) {
					tousMots.add(mot);
				}
				else if(difficulte == 1 && mot.length() <= 6 && mot.length() >= 5) {
					tousMots.add(mot);
				}
				else if(difficulte == 2 && mot.length() < 5) {
					tousMots.add(mot);
				}
			}
			
			return tousMots.get(rdm.nextInt(tousMots.size()));
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
	
	public static void closeFile() {
		if (input != null) {
			input.close();
		}
	}
	
	public Integer[] getCharPositions(char c) {
		ArrayList<Integer> positions = new ArrayList<Integer>();
		
		for(int i = 0; i < mot.length(); i ++) {
			if(c == mot.charAt(i)) {
				positions.add(i);
			}
		}
		Integer[] lettres = positions.toArray(new Integer[positions.size()]);
		return lettres;
	}
	
	public void setImagePositions() {
		int startX = 610;
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

		//For bonhomme
		positions[26] = new ImagePlaceholder(387,240);
		positions[27] = new ImagePlaceholder(415,308);
		positions[28] = new ImagePlaceholder(422,310);
		positions[29] = new ImagePlaceholder(374,319);
		positions[30] = new ImagePlaceholder(418,420);
		positions[31] = new ImagePlaceholder(380,422);
		}
	}
	
	private boolean containsLetter(int index) { //Varifie si la lettre est contenu dans le mot
		char x = findChar(index);
		for(int i = 0; i < mot.length(); i++) {
			if(x == mot.charAt(i))
				return true;
		}
		return false;
	}
	
	private char findChar(int x) {	//Given a integer between 0-25 determine the letter 0 = 'a'
		char c = 'a';
		for(int y = 0; y < x; y++) {
			c++;
		}
		return c;
	}
	
	public int findInt(char a) throws ExceptionLettre {
		char x = 'a';
		for(int i = 0; i < 26; i++) {
			if(a == x) {
				return i;
			}
			else if (i == 25){
				throw new ExceptionLettre(a);
			}
			x++;
		}
		return 'z';
	}
	
	public void endGame(int hangman) {
		char[] lettres = mot.toCharArray();
		if(hangman == 6) {
			jouerSon(true, GAGNER);
			JOptionPane.showMessageDialog(null, "Bonhomme pendu! Jouez encore?", "Jeu perdu", JOptionPane.INFORMATION_MESSAGE);
			newGame();
			activerCle = false;
		}
		else {
			boolean test = true;
			for(char c : lettres) {
				try {
					if(!estChoisi[findInt(c)])
						test = false;
				} catch (ExceptionLettre e) {
					e.printStackTrace();
				}
			}
			if(test) {
				jouerSon(false, GAGNER);
				int difficulty = 0;
				if(radioMenuFacile.isSelected())
					difficulty = 0;
				else if(radioMenuMoyen.isSelected())
					difficulty = 1;
				else if(radioMenuDifficile.isSelected())
					difficulty = 2;
				
				joueur.setScore(hangman, difficulty, lettres.length);
				FenetreJoueurs.openFile();
				FenetreJoueurs.readFile(joueur, true);
				FenetreJoueurs.closeInput();
				FenetreJoueurs.writeJoueurs();
				
				JOptionPane.showMessageDialog(null, "Jeu Gagné! Jouez encore?", "Jeu gagnae", JOptionPane.INFORMATION_MESSAGE);
				newGame();
				activerCle = false;
			}
		}
		
	}

	public void newGame() {
		int difficulty = 0;
		for(int x = 0; x < estChoisi.length; x++) {
			estChoisi[x] = false;
		}
		if(radioMenuFacile.isSelected())
			difficulty = 0;
		else if(radioMenuMoyen.isSelected())
			difficulty = 1;
		else if(radioMenuDifficile.isSelected())
			difficulty = 2;

		openFile();
		mot = readRecords(difficulty);
		closeFile();
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;	//Creates a 2D graphics object
		
		int start = 600 - (90 * mot.length() - 30) / 2;
		int piecesBonhomme = 0;
		
		//Paint used letters
		for(int x = 0; x < estChoisi.length; x++) {
			if(estChoisi[x]) {
				if(containsLetter(x))
				{
					try {
						g2.drawImage(ImageIO.read(new File("lettres/" + paths[x])), positions[x].getX(),  positions[x].getY(), 49, 55, Color.WHITE, null);
						for(int i : getCharPositions(findChar(x))) {
							g2.drawImage(ImageIO.read(new File("lettres/" + paths[x])), start + 90 * i, 590, 49, 55, Color.WHITE, null);	//Depends on method getCharPositions
						}
						
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				else {
					try {
						g2.drawImage(ImageIO.read(new File("lettres/" + paths[x + 26])), positions[x].getX(), positions[x].getY(), 49, 55, Color.WHITE, null);
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
					piecesBonhomme++;
				}
			}
			
		}
		
		//Paint dashes
		g2.setColor(Color.BLACK);
		for(int x = 0; x < mot.length(); x++) {
			g2.drawLine(start, 650, start + 60, 650);
			start += 60 + 30;
		}
		
		
		//Paint hangman
		for(int x = 52; x < 52 + piecesBonhomme; x++) {
			try {
				g2.drawImage(ImageIO.read(new File("bonhomme/" + paths[x])), positions[x - 26].getX(), positions[x - 26].getY(), Color.BLACK, null);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		endGame(piecesBonhomme);
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
	public void keyTyped(KeyEvent e) {
		if(activerCle) {
			char lettre = e.getKeyChar();
			try {
				estChoisi[findInt(lettre)] = true;
				jouerSon(containsLetter(findInt(lettre)), LETTRE); //Varifie si la lettre choisi est vrai pour jouer le son
			}
			catch(ExceptionLettre exceptionLettre) {
				JOptionPane.showMessageDialog(null, exceptionLettre, "Erreur", JOptionPane.ERROR_MESSAGE);
			}
			repaint();
		}
	}

	public void jouerSon(boolean gagner, int choix) {
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
			
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(son);
			music.open(audioIn);
			if(choix == 1)
				music.loop(Clip.LOOP_CONTINUOUSLY);
			else
				music.start();
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
		FenetreJoueurs login = new FenetreJoueurs();
		login.setVisible(true);
		login.setResizable(true);
		login.setLocationRelativeTo(null);
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}

