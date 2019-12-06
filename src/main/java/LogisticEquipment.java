
public class LogisticEquipment {
	//make sure, that the equipment is always in the same order as a list
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
