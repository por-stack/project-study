
public class LogisticEquipment {
	String name; 
	int anzahl; 
	
	public LogisticEquipment(String name, int anzahl) {
		this.name = name; 
		this.anzahl = anzahl; 
	}
	
	public void anzahlSteigern() {
		anzahl =+ 1; 
	}
}
