package ru.psavinov.chile.earthquake;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import ru.psavinov.chile.earthquake.exception.BaseLoadException;

/**
 * 
 * Earthquakes base class, contains utility methods to get quake sets by params
 * 
 * @author Pavel Savinov // savinovpa@gmail.com
 * 
 */
public class EarthquakeBase {

	/**
	 * Default earthquakes base from resources
	 */
	public static final String RESOURCE_BASE = "/ru/psavinov/chile/earthquake/data.csv";

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static final String EMCS_URL = "http://www.emsc-csem.org/Earthquake/?filter=yes&region=AISEN%2C+CHILE%7CANTOFAGASTA%2C+CHILE%7CARAUCANIA%2C+CHILE%7CATACAMA%2C+CHILE%7CBIO-BIO%2C+CHILE%7CCOQUIMBO%2C+CHILE%7CISLA+CHILOE%2C+LOS+LAGOS%2C+CHILE%7CLIBERTADOR+O%60HIGGINS%2C+CHILE%7CLOS+LAGOS%2C+CHILE%7CMAGALLANES%2C+CHILE%7CMAULE%2C+CHILE%7CNEAR+COAST+OF+AISEN%2C+CHILE%7COFF+COAST+OF+AISEN%2C+CHILE%7COFF+COAST+OF+ANTOFAGASTA%2C+CHILE%7COFF+COAST+OF+ARAUCANIA%2C+CHILE%7COFF+COAST+OF+ATACAMA%2C+CHILE%7COFF+COAST+OF+BIO-BIO%2C+CHILE%7COFF+COAST+OF+COQUIMBO%2C+CHILE%7COFF+COAST+OF+LOS+LAGOS%2C+CHILE%7COFF+COAST+OF+MAULE%2C+CHILE%7COFF+COAST+OF+O%60HIGGINS%2C+CHILE%7COFF+COAST+OF+TARAPACA%2C+CHILE%7COFF+COAST+OF+VALPARAISO%2C+CHILE%7COFFSHORE+ANTOFAGASTA%2C+CHILE%7COFFSHORE+ARAUCANIA%2C+CHILE%7COFFSHORE+ATACAMA%2C+CHILE%7COFFSHORE+BIO-BIO%2C+CHILE%7COFFSHORE+COQUIMBO%2C+CHILE%7COFFSHORE+LOS+LAGOS%2C+CHILE%7COFFSHORE+MAULE%2C+CHILE%7COFFSHORE+O%60HIGGINS%2C+CHILE%7COFFSHORE+TARAPACA%2C+CHILE%7COFFSHORE+VALPARAISO%2C+CHILE%7CREGION+METROPOLITANA%2C+CHILE%7CTARAPACA%2C+CHILE%7CVALPARAISO%2C+CHILE%7CWEST+CHILE+RISE&min_intens=0&max_intens=8&export=csv";

	/**
	 * Load base in constructor
	 * 
	 * @param loadRemote
	 *            Load latest earthquakes data from Web
	 * 
	 * @throws BaseLoadException
	 *             in case of missing/corrupted earthquakes base
	 */
	public EarthquakeBase(boolean loadRemote) throws BaseLoadException {
		loadBase(loadRemote);
	}

	/**
	 * Get latest earthquake from base
	 * 
	 * @return Latest earthquake
	 */
	public Earthquake getLatest() {
		List<Earthquake> list = new ArrayList<Earthquake>(getEarthquakes());
		Collections.sort(list);
		return list.get(0);
	}

	/**
	 * Get earliest earthquake from base
	 * 
	 * @return Earliest earthquake
	 */
	public Earthquake getOldest() {
		List<Earthquake> list = new ArrayList<Earthquake>(getEarthquakes());
		Collections.sort(list, new Comparator<Earthquake>() {
			public int compare(Earthquake o1, Earthquake o2) {
				return o1.getDateTime().compareTo(o2.getDateTime());
			}
		});
		return list.get(0);
	}

	/**
	 * Get a set of all earthquakes
	 * 
	 * @return All earthquakes
	 */
	public Set<Earthquake> getEarthquakes() {
		if (earthquakes == null) {
			earthquakes = new HashSet<Earthquake>();
		}
		return earthquakes;
	}

	/**
	 * Get strongest earthquake
	 * 
	 * @return Strongest earthquak
	 */
	public Earthquake getStrongest() {
		Earthquake strongest = null;

		for (Earthquake quake : getEarthquakes()) {
			if (strongest == null
					|| quake.getMagnitude() > strongest.getMagnitude()) {
				strongest = quake;
			}
		}

		return strongest;
	}

	/**
	 * Fetch all earthquakes in specified regions
	 * 
	 * @param regiones
	 *            Regions array
	 * @return Set of earthquakes in specified regions
	 */
	public Set<Earthquake> getByRegion(Region... regiones) {
		Set<Earthquake> set = new HashSet<Earthquake>();

		for (Region r : regiones) {
			if (r == null) {
				throw new IllegalArgumentException("Region must be not null!");
			}

			for (Earthquake quake : getEarthquakes()) {
				if (r.equals(quake.getRegion())) {
					set.add(quake);
				}
			}
		}
		return set;
	}

	/**
	 * Get earthquakes set by specific month
	 * 
	 * @param month
	 *            Month to fetch earthquakes
	 * 
	 * @return Set of earthquakes for specified month
	 */
	public Set<Earthquake> getByMonth(int month) {
		Set<Earthquake> set = new HashSet<Earthquake>();

		Calendar c = Calendar.getInstance();
		for (Earthquake quake : getEarthquakes()) {
			c.setTime(quake.getDateTime());
			if (c.get(Calendar.MONTH) == month) {
				set.add(quake);
			}
		}

		return set;
	}

	/**
	 * Get earthquakes set by magnitude limit(greater than), exclusive
	 * 
	 * @param gt
	 *            Lower magnitude limit
	 * 
	 * @return Set of corresponding earthquakes
	 */
	public Set<Earthquake> getByMagnitudeGT(double gt) {
		Set<Earthquake> set = new HashSet<Earthquake>();

		for (Earthquake quake : getEarthquakes()) {
			if (quake.getMagnitude() > gt) {
				set.add(quake);
			}
		}

		return set;
	}

	/**
	 * Get earthquakes set by magnitude limit(lower than), exclusive
	 * 
	 * @param lt
	 *            Upper magnitude limit
	 * 
	 * @return Set of corresponding earthquakes
	 */
	public Set<Earthquake> getByMagnitudeLT(double lt) {
		Set<Earthquake> set = new HashSet<Earthquake>();

		for (Earthquake quake : getEarthquakes()) {
			if (quake.getMagnitude() < lt) {
				set.add(quake);
			}
		}

		return set;
	}

	/**
	 * Get earthquakes set by magnitude limit(greater than), exclusive, and
	 * region
	 * 
	 * @param gt
	 *            Lower magnitude limit
	 * @param r
	 *            Region
	 * 
	 * @return Set of corresponding earthquakes
	 */
	public Set<Earthquake> getByMagnitudeGTRegion(Region r, double gt) {
		Set<Earthquake> set = new HashSet<Earthquake>();

		for (Earthquake quake : getEarthquakes()) {
			if (quake.getMagnitude() > gt && r.equals(quake.getRegion())) {
				set.add(quake);
			}
		}

		return set;
	}

	/**
	 * Get earthquakes set by magnitude limit(lower than), exclusive, and region
	 * 
	 * @param lt
	 *            Upper magnitude limit
	 * @param r
	 *            Region
	 * 
	 * @return Set of corresponding earthquakes
	 */
	public Set<Earthquake> getByMagnitudeLTRegion(Region r, double lt) {
		Set<Earthquake> set = new HashSet<Earthquake>();

		for (Earthquake quake : getEarthquakes()) {
			if (quake.getMagnitude() < lt && r.equals(quake.getRegion())) {
				set.add(quake);
			}
		}

		return set;
	}

	/**
	 * Get earthquakes set by magnitude limits, inclusive, and region
	 * 
	 * @param ge
	 *            Lower magnitude limit
	 * @param le
	 *            Upper magnitude limit
	 * @param r
	 *            Region
	 * 
	 * @return Set of corresponding earthquakes
	 */
	public Set<Earthquake> getByMagnitudeLEGERegion(Region r, double ge,
			double le) {
		Set<Earthquake> set = new HashSet<Earthquake>();

		for (Earthquake quake : getEarthquakes()) {
			if (quake.getMagnitude() >= ge && quake.getMagnitude() <= le
					&& r.equals(quake.getRegion())) {
				set.add(quake);
			}
		}

		return set;
	}

	/**
	 * Get earthquakes set by magnitude limits, inclusive
	 * 
	 * @param ge
	 *            Lower magnitude limit
	 * @param le
	 *            Upper magnitude limit
	 * 
	 * @return Set of corresponding earthquakes
	 */
	public Set<Earthquake> getByMagnitudeLEGE(double ge, double le) {
		Set<Earthquake> set = new HashSet<Earthquake>();

		for (Earthquake quake : getEarthquakes()) {
			if (quake.getMagnitude() >= ge && quake.getMagnitude() <= le) {
				set.add(quake);
			}
		}

		return set;
	}

	/**
	 * Get earliest earthquake year
	 * 
	 * @return Year of earliest earthquake
	 */
	public int getFirstYear() {
		Calendar c = Calendar.getInstance();
		c.setTime(getOldest().getDateTime());

		return c.get(Calendar.YEAR);
	}

	/**
	 * Get latest earthquake year
	 * 
	 * @return Year of latest earthquake
	 */
	public int getLastYear() {
		Calendar c = Calendar.getInstance();
		c.setTime(getLatest().getDateTime());

		return c.get(Calendar.YEAR);
	}

	/* private members goes below */

	private Set<Earthquake> earthquakes;

	private void loadBase(boolean loadRemote) throws BaseLoadException {
		try {

			File localBase = new File(System.getProperty("user.home")
					.concat(File.separator).concat("earthquakes.base"));

			boolean loaded = false;
			if (localBase.exists()) {
				List<String> strings = IOUtils.readLines(new FileInputStream(
						localBase));
				parseStrings(strings);
				loaded = true;
			}

			if (!loaded) {
				List<String> strings = IOUtils.readLines(this.getClass()
						.getResourceAsStream(RESOURCE_BASE));
				parseStrings(strings);
			}

			int resourceCount = getEarthquakes().size();

			System.out.println("Resources base count: " + resourceCount);

			if (loadRemote) {
				HttpClient client = new HttpClient();
				GetMethod get = new GetMethod(EMCS_URL);
				client.executeMethod(get);
				if (get.getStatusCode() == 200) {
					parseStrings(IOUtils.readLines(get
							.getResponseBodyAsStream()));
				}
			}

			IOUtils.writeLines(createList(), "\n", new FileOutputStream(
					localBase));

		} catch (Throwable th) {
			th.printStackTrace();
			throw new BaseLoadException(th);
		}
	}

	private Collection<String> createList() {
		List<String> list = new ArrayList<String>();
		for (Earthquake q : getEarthquakes()) {
			StringBuffer b = new StringBuffer();
			String dateString = dateFormat.format(q.getDateTime());
			b.append(dateString.split(" ")[0]).append(";").append(dateString.split(" ")[1]).append(";")
			.append(q.getLatitude()).append(";").append(q.getLongitude()).append(";").append(q.getDepth())
			.append("; ;").append(q.getScaleType().name()).append(";").append(q.getMagnitude()).append(";")
			.append(q.getRegion()).append(";");
			list.add(b.toString());
		}
		return list;
	}

	private void parseStrings(List<String> strings) throws Exception {
		for (String string : strings) {
			if (!string.toUpperCase().contains("WEST CHILE")
					&& !string.toUpperCase().contains("DATE")) {
				String[] array = string.split("\\;");
				Earthquake quake = new Earthquake();
				quake.setDateTime(dateFormat.parse(array[0].concat(" ").concat(
						array[1])));
				quake.setLatitude(Double.valueOf(array[2]));
				quake.setLongitude(Double.valueOf(array[3]));
				quake.setDepth(Double.valueOf(array[4]));
				quake.setScaleType(ScaleType.getScaleType(array[6]));
				quake.setMagnitude(Double.valueOf(array[7]));
				quake.setRegion(Region.getRegion(array[8]));
				if (!getEarthquakes().contains(quake)) {
					getEarthquakes().add(quake);
				}
			}
		}
	}

}
