package ru.psavinov.chile.earthquake;

import ru.psavinov.chile.earthquake.exception.UnknownRegionException;


/**
 * Regions of Chile enumeration
 * 
 * @author Pavel Savinov // savinovpa@gmail.com
 *
 */
public enum Region {

	Tarapaca("Tarapacá", 1),
	Antofagasta("Antogagasta", 2),
	Atacama("Atacama", 3),
	Coquimbo("Coquimbo", 4),
	Valparaiso("Valparaiso", 5),
	OHiggins("O'Higgins",6),
	Maule("Maule", 7),
	Biobio("Bio-Bio",8,"Bio-Bio"),
	Araucania("Araucania" ,9,"La Araucania"),
	LosLagos("Los Lagos", 10,"Los Lagos"),
	Aysen("Aisen", 11,"Aisen"),
	Magallanes("Magallanes", 12, "Antarctica Chilena"),
	Metropolitana("Región Metropolitana", 13),
	LosRios("Los Rios", 14,"Los Rios"),
	Arica_y_Parinacota("Arica y Parinacota", 15,"Arica","Parinacota");
	
	Region(String ufName, int num, String... names) {
		setDisplayName(ufName);
		setNumber(num);
		setNames(names);
	}
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	private int number;
	private String[] names;
	private String displayName;
	
	public static Region getRegion(String string) throws UnknownRegionException {
		
		string = string.toUpperCase().trim();
		for (Region r : Region.values()) {
			if (string.contains(r.name().toUpperCase())) {
				return r;
			}
			if (r.getNames() != null && r.getNames().length > 0) {
				for (String name : r.getNames()) {
					if (string.contains(name.toUpperCase())) {
						return r;
					}					
				}
			}
		}
		
		throw new UnknownRegionException(string);
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}
