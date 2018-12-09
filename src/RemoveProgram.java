import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class RemoveProgram {

	private static Scanner input;
	private static FileWriter writer;
	private static ArrayList<String> words = new ArrayList<String>();
	private static ArrayList<Mot> mots = new ArrayList<Mot>();
	private static ObjectOutputStream output;
	
	private static void read() {
		try 
		{
			input = new Scanner(Paths.get("wordList.txt"));
		}
		catch (IOException e) {
			System.err.println("Impossible d'ouvrir le fichier.");
			System.exit(1);
		}
		
		
		try {
			while(input.hasNext()) 
			{
				words.add(input.nextLine());
			}
		}
		catch (NoSuchElementException e) {
			System.err.println("Element non trouvable");
		}
		catch (IllegalStateException e) {
			System.err.println("Ne pouvait pas lire fichier");
		}
		
		input.close();
		
	}
	
	private static void remove() {
		try {
			writer = new FileWriter("wordList.txt");
		}
		catch (FileNotFoundException e) {
			 System.err.println("Le programme n'arrive pas e trouver le fichier. le programme termine.");
	         System.exit(1);
		} 
		catch (IOException e) {
			 System.err.println("Le fichier ne s'ouvre pas. le programme termine.");
	         System.exit(1);
		}
		
		for(String s : words) {
			try {
				if(contains(s))
					writer.write(String.format("%s%n", s.toLowerCase()));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
		
	}
	
	public static boolean contains(String s) {
		if(s.length() > 10)
			return false;
		for(int x = 0; x < s.length(); x++) {
			if(s.charAt(x) == 'ç' || s.charAt(x) == ' ')
				return false;
		}
		return true;
	}
	
	public static void modifyRecords() {
		
		try {
	         output = new ObjectOutputStream(Files.newOutputStream(Paths.get("wordListSerialized.txt")));
	      }
	      catch (IOException ioException)
	      {
	         System.err.println("Erreur e ouvrir le fichier. fin du programme.");
	         System.exit(1); 
	      } 
		
		for(Mot m : mots) {
			try {
				output.writeObject(m);
			}
			catch (IOException e) {
				System.err.println("Error modifying records");
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
	
	public static void addDifficulty() {
		int diff = 0;
		
		for(String s : words) {
			if(s.length() >= 10) 
				diff = 0;
			else if(s.length() >= 7) 
				diff = 1;
			else if(s.length() >= 5)
				diff = 2;
			else
				diff = 3;
				
			mots.add(new Mot(s, diff));
		}
	}
	
	
	
	
	public static void main(String[] args) {	
		read();
		//remove();
		//addDifficulty();
		//modifyRecords();
		
		

	}

}
