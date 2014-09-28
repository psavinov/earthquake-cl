package ru.psavinov.chile.earthquake.prediction;

import java.util.Date;

/**
 * Possible earthquake description class
 * 
 * @author Pavel Savinov // savinovpa@gmail.com
 * 
 */
public class EarthquakePrediction {

	private double magnitude;
	private Date dateTime;

	public double getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

}
