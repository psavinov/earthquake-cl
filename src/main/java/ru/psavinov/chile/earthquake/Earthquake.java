package ru.psavinov.chile.earthquake;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Earthquake entity class
 * 
 * @author Pavel Savinov // savinovpa@gmail.com
 *
 */
public class Earthquake implements Comparable<Earthquake> {

	private Region region;
	private Date dateTime;
	private Double magnitude;
	private ScaleType scaleType;
	private Double longitude;
	private Double latitude;
	private Double depth;

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Double getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(Double magnitude) {
		this.magnitude = magnitude;
	}

	public ScaleType getScaleType() {
		return scaleType;
	}

	public void setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getDepth() {
		return depth;
	}

	public void setDepth(Double depth) {
		this.depth = depth;
	}

	public int compareTo(Earthquake o) {
		return o.getDateTime().compareTo(this.getDateTime());
	}
	
	@Override
	public int hashCode() {
		return new BigDecimal(this.getDateTime().getTime()).intValue();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Earthquake) {
			Earthquake e = (Earthquake) obj;
			if (e.getDateTime().getTime() == this.getDateTime().getTime() &&
					this.getRegion().equals(e.getRegion()) &&
					this.getMagnitude().doubleValue() == e.getMagnitude().doubleValue()){
				return true;
			}
		}
		return false;
	}	
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(EarthquakeBase.dateFormat.format(getDateTime()));
		builder.append(" ").append(getRegion().name()).append(" ").append(getMagnitude())
		.append(getScaleType().name()).append(" ").append(getDepth()).append(" lng: ").append(getLongitude())
		.append(" lat: ").append(getLatitude());
		return builder.toString();
	}

}
