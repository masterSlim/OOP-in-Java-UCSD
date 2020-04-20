package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import module4.CityMarker;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	UnfoldingMap map;
	private List<Marker> airportMarkers;
	List<Marker> routeList;
	List<Marker> countryMarkers;
	List<Feature> countries;
	private Marker lastClicked;
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		countries = GeoJSONReader.loadData(this, "countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countries);

		
		// get features from airport data
		List<PointFeature> featuresAirports = ParseFeed.parseAirports(this, "airports.dat");
		
		
		// list for markers, hashmap for quicker access when matching with routes
		airportMarkers = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : featuresAirports) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportMarkers.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}

		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		map.addMarkers(routeList);
		map.addMarkers(countryMarkers);
		map.addMarkers(airportMarkers);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
		}
		else if (lastClicked == null) 
		{
			checkAirportForClick();
			if (lastClicked == null) {
				checkCountryForClick();
			}
		}
	}
	
	private void checkCountryForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker marker : countryMarkers) {
			System.out.println("inside!");
			if (marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (MultiMarker)marker;
				System.out.println("inside!!");
				// Hide all the other earthquakes and hide
				for (Marker mhide : countryMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				/*
				 * for (Marker mhide : quakeMarkers) { EarthquakeMarker quakeMarker =
				 * (EarthquakeMarker)mhide; if (quakeMarker.getDistanceTo(marker.getLocation())
				 * > quakeMarker.threatCircle()) { quakeMarker.setHidden(true); }
				 */
				}
				return;
			}
		}		

	private void checkAirportForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker a : airportMarkers) {
			Marker marker = (Marker) a;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (Marker) marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : airportMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : countryMarkers) {
					if (mhide.getProperty("name").equals(a.getProperty("country"))); 
						{
						mhide.setHidden(true);
					}
				}
				return;
			}
		}
	}
	private void unhideMarkers() {
		for(Marker marker : countryMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : airportMarkers) {
			marker.setHidden(false);
		}
	}
	
	

}
