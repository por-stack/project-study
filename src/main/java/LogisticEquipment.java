
public class LogisticEquipment {
	// make sure, that the equipment is always in the same order as a list
	private String name;
	int anzahl;
	private int dimension;

	public LogisticEquipment(String name, int anzahl, int dimension) {
		this.name = name;
		this.anzahl = anzahl;
		this.dimension = dimension;

	}

	public int getDimension() {
		return dimension;
	}

	public String getName() {
		return name;
	}

	public int getAnzahl() {
		return anzahl;
	}

	public void anzahlSteigern() {
		this.anzahl = this.anzahl + 1;
	}

	@Override
	public String toString() {
		return "[" + name + " : " + anzahl + "]";
	}

}
