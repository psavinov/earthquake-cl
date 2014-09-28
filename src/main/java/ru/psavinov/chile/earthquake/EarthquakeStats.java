package ru.psavinov.chile.earthquake;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.ChartUtilities;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;

import ru.psavinov.chile.earthquake.exception.BaseLoadException;
import ru.psavinov.chile.earthquake.prediction.EarthquakePrediction;
import ru.psavinov.chile.earthquake.prediction.EarthquakePredictor;

/**
 * Main class, static methods to build charts and calculate statistics data.
 * 
 * @author Pavel Savinov // savinovpa@gmail.com
 *
 */
public class EarthquakeStats {

	public static void main(String[] args) throws BaseLoadException, IOException {
		EarthquakeBase base = new EarthquakeBase(true);
		
		/*
		 * Create regional distribution chart
		 */
		createRegionDistribution(base, "charts/EarthquakesByRegion.png");
		System.out.println("Regional distribution - OK");
		
		/*
		 * Create magnitude distribution chart
		 */
		createMagnitudeDistribution(base, "charts/EarthquakesByMagnitude.png");
		System.out.println("Magnitude distribution - OK");
		
		/*
		 * Create month distribution chart
		 */
		createMonthDistribution(base, "charts/EarthquakesByMonth.png");
		System.out.println("Month distribution - OK");
		
		System.out.println();
		
		/*
		 * Possible earthquakes prediction, just a joke ;)
		 */
		for (Region r : Region.values()) {
			EarthquakePrediction p;
			p = EarthquakePredictor.getPredictionByRegion(base, r);
			if ( p != null) {
				System.out.println("Nearest possible earthquake in " + r.getDisplayName() + ": " + p.getDateTime() + " " + p.getMagnitude());
			}
		}
	}
	

	/**
	 * Create month distribution chart with given earthquakes base
	 * 
	 * @param base Earthquakes base
	 * @param outputFileName Output filename (PNG) to store a chart
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException in case of null or empty earthquakes base
	 */
	private static void createMonthDistribution(EarthquakeBase base,
			String outputFileName) throws IOException {
		
		if (base == null || base.getEarthquakes().size() == 0) {
			throw new IllegalArgumentException("Empty earthquakes base passed");
		}
		
		DefaultPieDataset dataset = new DefaultPieDataset();

		SimpleDateFormat format = new SimpleDateFormat("MMMM");
		Calendar c = Calendar.getInstance();
		for (int k = 0; k<=11; k++) {
			c.set(Calendar.MONTH, k);
			dataset.setValue(format.format(c.getTime()), base.getByMonth(k).size());
		}

		JFreeChart chart = ChartFactory.createPieChart3D(
				String.format("Earthquakes in Chile, distribution by month, %s - %s",
						base.getFirstYear(),base.getLastYear()),
				dataset, true, true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		
		if (outputFileName == null || outputFileName.equals("")) {
			outputFileName = "EarthquakesByRegion_".concat(String.valueOf(System.currentTimeMillis())).concat(".png");
		}
		
		ChartUtilities.saveChartAsPNG(new File(outputFileName), chart, 1024, 768);
		
	}


	/**
	 * Create magnitude distribution chart with given earthquakes base
	 * 
	 * @param base Earthquakes base
	 * @param outputFileName Output filename (PNG) to store a chart
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException in case of null or empty earthquakes bas
	 */
	private static void createMagnitudeDistribution(EarthquakeBase base,
			String outputFileName) throws IOException {
		
		if (base == null || base.getEarthquakes().size() == 0) {
			throw new IllegalArgumentException("Empty earthquakes base passed");
		}
		
		if (outputFileName == null || outputFileName.equals("")) {
			outputFileName = "EarthquakesByMagnitude_".concat(String.valueOf(System.currentTimeMillis())).concat(".png");
		}
		
		String[] categories = new String[Region.values().length];
		String[] values = new String[]{"< 3.5", "3.5 - 5", "> 5"};
		double[][] data = new double[3][Region.values().length];
		int c = 0;
		for (Region r : Region.values()) {
			categories[c] = r.getDisplayName();
			data[0][c] = base.getByMagnitudeLTRegion(r, 3.5).size();
			data[1][c] = base.getByMagnitudeLEGERegion(r, 3.5, 5).size();
			data[2][c] = base.getByMagnitudeGTRegion(r, 5).size();
			c++;
		}
		
		CategoryDataset dataset = DatasetUtilities.createCategoryDataset(values, categories, data);
		
		final JFreeChart chart = ChartFactory.createBarChart3D(
				String.format("Earthquakes in Chile, distribution by magnitude, %s - %s",
						base.getFirstYear(),base.getLastYear()), 
	            "Region",               
	            "Count",                  
	            dataset,                  
	            PlotOrientation.VERTICAL, 
	            true,                     
	            true,                     
	            false                     
	        );
		
		CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 8.0)
        );
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        
        renderer.setDrawBarOutline(false);
        
        ChartUtilities.saveChartAsPNG(new File(outputFileName), chart, 1024, 768);
		
	}


	/**
	 * Create region distribution chart with given earthquakes base
	 * 
	 * @param base Earthquakes base
	 * @param outputFileName Output filename (PNG) to store a chart
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException in case of null or empty earthquakes base
	 */
	public static void createRegionDistribution(EarthquakeBase base, String outputFileName) throws IOException {
		
		if (base == null || base.getEarthquakes().size() == 0) {
			throw new IllegalArgumentException("Empty earthquakes base passed");
		}
		
		DefaultPieDataset dataset = new DefaultPieDataset();

		for (Region r : Region.values()) {
			dataset.setValue(r.getDisplayName(), base.getByRegion(r).size());
		}

		JFreeChart chart = ChartFactory.createPieChart3D(
				String.format("Earthquakes in Chile, distribution by regions, %s - %s",
						base.getFirstYear(),base.getLastYear()),
				dataset, true, true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		
		if (outputFileName == null || outputFileName.equals("")) {
			outputFileName = "EarthquakesByRegion_".concat(String.valueOf(System.currentTimeMillis())).concat(".png");
		}
		
		ChartUtilities.saveChartAsPNG(new File(outputFileName), chart, 1024, 768);
		
	}

}
