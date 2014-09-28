package ru.psavinov.chile.earthquake;

/**
 * Earthquake strength scales enumeration
 * 
 * @author Pavel Savinov // savinovpa@gmail.com
 *
 */
public enum ScaleType {
	
	ML,MB,MS,MW;

	public static ScaleType getScaleType(String string) {
		
		string = string.trim().toUpperCase();
		
		if (string.equals("M") || string.equals("")) {
			string = "ML";
		}
		
		return ScaleType.valueOf(string);
	}

}
