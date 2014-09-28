package ru.psavinov.chile.earthquake.prediction;

import java.util.Date;

import ru.psavinov.chile.earthquake.Earthquake;
import ru.psavinov.chile.earthquake.EarthquakeBase;
import ru.psavinov.chile.earthquake.Region;
import ru.psavinov.chile.earthquake.exception.PredictionException;

/**
 * Earthquake prediction class
 * 
 * Provide earthquakes prediction based on history data and simple math methods
 * 
 * @author Pavel Savinov // savinovpa@gmail.com
 * 
 */
public class EarthquakePredictor {

	/**
	 * Predict next possible earthquake using Holt-Winters predictor, with
	 * specified earthquakes base and region
	 * 
	 * @param base
	 *            Base of earthquakes
	 * @param r
	 *            Region
	 * 
	 * @return Possible earthquake prediction
	 * 
	 * @throws PredictionException
	 *             In case of incorrect parameters or prediction error
	 */
	public static EarthquakePrediction getPredictionByRegion(
			EarthquakeBase base, Region r) {
		Series s = new Series();
		for (Earthquake q : base.getByRegion(r)) {
			SeriesItem i = new SeriesItem(q.getMagnitude(), q.getDateTime());
			s.getItems().add(i);
		}
		
		s.getItems().add(new SeriesItem(0, new Date()));

		if (s.getItems().size() > 0) {
			HoltWintersPredictor p = new HoltWintersPredictor(s, 0.7, 0.7, 0.7,
					10);

			SeriesItem pi;
			try {
				pi = p.predictNext();
			} catch (PredictionException e) {
				return null;
			}

			if (pi != null) {
				EarthquakePrediction prediction = new EarthquakePrediction();
				prediction.setMagnitude(pi.getValue());
				prediction.setDateTime(pi.getDate());
				return prediction;
			}
		}

		System.out.println("Could not predict earthquake with specified base for region: " + r.getDisplayName());
		
		return null;

	}

}
