package ru.psavinov.chile.earthquake.prediction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import ru.psavinov.chile.earthquake.exception.PredictionException;

public class HoltWintersPredictor {
	
	private int window;
	public HoltWintersPredictor(Series series, double alpha, double beta,
			double gamma, int win) {
		this.avgTimeBetweenTuples = 1L;
		this.seasonLen = 2;
		setSerie(series);
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		this.window = win;
	}

	private void setSerie(Series serie) {
		series = serie;
		computeSerieInfo();
		computeSeasonLen();
	}

	private void computeSerieInfo() {
		avgValue = 0.0D;
		count = 0;
		avgTimeBetweenTuples = 0L;
		Calendar cal = Calendar.getInstance();
		SeriesItem tt = series.getFirst();
		cal.setTime(tt.getDate());
		long lastTS = cal.getTimeInMillis();
		while (tt != null) {
			avgValue += tt.getValue();
			count++;
			tt = series.getNext();
			if (tt != null) {
				cal.setTime(tt.getDate());
				avgTimeBetweenTuples += cal.getTimeInMillis() - lastTS;
				lastTS = cal.getTimeInMillis();
			}
		}
		if (count > 0) {
			avgValue /= count;
			avgTimeBetweenTuples /= count;
		}
	}

	public SeriesItem predict(Date date) throws PredictionException {
		if (count == 0) {
			return null;
		} else {
			Calendar cal2 = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
			cal2.setTime(date);
			cal.setTime(series.getLast().getDate());
			long diff = cal2.getTimeInMillis() - cal.getTimeInMillis();
			int k = (int) (diff /= avgTimeBetweenTuples);
			cal.add(14, k * (int) avgTimeBetweenTuples);
			return new SeriesItem(predict(k - 1), cal.getTime());
		}
	}

	public SeriesItem predictNext() throws PredictionException {
		if (count == 0) {
			return null;
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(series.getLast().getDate());
			cal.add(14, (int) avgTimeBetweenTuples);
			double val = predict(0);
			if (val == 0) {
				return null;
			}
			
			return new SeriesItem(val, cal.getTime());
		}
	}

	private double predict(int index) throws PredictionException {
		Double valor = new Double(0);
		double nextSeason[] = holtwinters();
		
		do {
			if (nextSeason.length == 0 || index >= nextSeason.length) {
				break;
			}
			
			valor = nextSeason[index];
			index++;
			
		} while (valor == 0 || valor.isNaN() || valor.isInfinite() || index < nextSeason.length);
		
		if (valor.isNaN() || valor.isInfinite()) {
			return 0;
		}
		
		return Math.abs(new BigDecimal(valor).setScale(1,RoundingMode.HALF_UP).doubleValue());
	}

	private double[] holtwinters() throws PredictionException {
		if (window < seasonLen * 2) {
			window = seasonLen * 2;
		}
		
		if (count < seasonLen * 2)
			throw new PredictionException("Too few data.");
		int ylen = Math.min(window, count);
		int offset1 = window >= count ? 0 : count - window;
		int offset = ylen % seasonLen;
		ylen -= offset;
		offset += offset1;
		double fc = seasonLen;
		double ybar2 = 0.0D;
		for (int i = offset + seasonLen; i < offset + seasonLen * 2; i++)
			ybar2 += y(i);

		ybar2 /= fc;
		double ybar1 = 0.0D;
		for (int i = offset; i < offset + seasonLen; i++)
			ybar1 += y(i);

		ybar1 /= fc;
		double b0 = (ybar2 - ybar1) / fc;
		double tbar = (double) ((2 + seasonLen) * seasonLen) / 2D / fc;
		double a0 = ybar1 - b0 * tbar;
		double I[] = new double[ylen];
		for (int i = 0; i < ylen; i++)
			I[i] = y(offset + i) / (a0 + (double) (i + 1) * b0);

		double S[] = new double[ylen + seasonLen];
		double sumS = 0.0D;
		for (int i = 0; i < seasonLen; i++) {
			S[i] = (I[i] + I[i + seasonLen]) / 2D;
			sumS += S[i];
		}
		
		double tS = (double) seasonLen / sumS;
		for (int i = 0; i < seasonLen; i++)
			S[i] *= tS;

		double F[] = new double[ylen + seasonLen];
		double At = a0;
		double Bt = b0;
		for (int i = 0; i < ylen; i++) {
			double Atm1 = At;
			double Btm1 = Bt;
			At = (alpha * y(offset + i)) / S[i] + (1.0D - alpha)
					* (Atm1 + Btm1);
			Bt = beta * (At - Atm1) + (1.0D - beta) * Btm1;
			S[i + seasonLen] = (gamma * y(offset + i)) / At + (1.0D - gamma)
					* S[i];
			F[i] = (a0 + b0 * (double) (i + 1)) * S[i];
		}
		
		double forecast[] = new double[seasonLen];
		for (int i = 0; i < seasonLen; i++)
			forecast[i] = (At + Bt * (double) (i + 1)) * S[ylen + i];

		return forecast;
	}

	private double y(int t) {
		SeriesItem tt = series.get(t);
		if (tt == null)
			return 0.0D;
		else
			return tt.getValue();
	}

	private double r(int k) {
		double sumTop = 0.0D;
		double sumBottom = 0.0D;
		for (int i = 0; i < count - k; i++) {
			double t_avgT = y(i) - avgValue;
			sumTop += t_avgT * (y(i + k) - avgValue);
			sumBottom += t_avgT * t_avgT;
		}

		if (sumBottom == 0.0D)
			return (0.0D / 0.0D);
		else
			return sumTop / sumBottom;
	}

	private void computeSeasonLen() {
		double maxVal = -1D;
		int maxK = 1;
		for (int i = 1; i <= count / 2; i++) {
			double corr = r(i);
			if (corr > maxVal) {
				maxVal = corr;
				maxK = i;
			}
		}

		seasonLen = maxK;
	}

	private Series series;
	private double alpha;
	private double beta;
	private double gamma;
	private int count;
	private double avgValue;
	private long avgTimeBetweenTuples;
	private int seasonLen;
}