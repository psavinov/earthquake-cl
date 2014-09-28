package ru.psavinov.chile.earthquake.prediction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Series {
	
	private List<SeriesItem> items;
	private Iterator<SeriesItem> iterator;

	public List<SeriesItem> getItems() {
		if (items == null) {
			items = new ArrayList<SeriesItem>();
		}
		return items;
	}

	public SeriesItem getFirst() {
		Collections.sort(getItems());
		if(getItems().size() > 0) {
			return getItems().get(0);
		} else {
			return null;
		}
	}

	public SeriesItem getNext() {
		if(iterator == null) {
			iterator = getItems().iterator();
		}
		
		if (iterator.hasNext()) {
			return iterator.next();
		}
		
		return null;
	}

	public SeriesItem getLast() {
		Collections.sort(getItems());
		if(getItems().size() > 0) {
			return getItems().get(getItems().size()-1);
		} else {
			return null;
		}
	}

	public SeriesItem get(int t) {
		if (t < getItems().size()) {
			return getItems().get(t);
		} else {
			return null;
		}
	}

}
