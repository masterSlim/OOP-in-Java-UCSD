import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class ErthquakeCityMap extends PApplet {
private UnfoldingMap map;
public void setup() {
	size(950, 600, OPENGL);
	map = new UnfoldingMap(this, 200,50, 700, 500, new Google.GoogleMapProvider());
	map.zoomToLevel(2);
	MapUtils.createDefaultEventDispatcher(this, map);
	}
public void draw() {
	background(10);
	map.draw();
	//addKey();
}
}
