package ru.psavinov.chile.earthquake.prediction;

import java.util.Date;

public class SeriesItem implements Comparable<SeriesItem> {
	
	private Date date;
	private double value;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public SeriesItem(double predict, Date time) {
		setValue(predict);
		setDate(time);
	}

	public int compareTo(SeriesItem o) {
		return this.getDate().compareTo(o.getDate());
	}

}
