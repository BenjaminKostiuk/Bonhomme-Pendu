
public class ExceptionLettre extends Exception {
	
	private char lettre;
	
	ExceptionLettre(char lettre){
		this.lettre = lettre;
	}
	
	@Override
	public String toString() {
		return "ExceptionLettre:  \"" + lettre + "\"  n'est pas une lettre de l'alphabet";
	}
}
