package firstjavafx;

import javafx.scene.shape.Polygon;

public class MyPolygon extends Polygon{
String name = "name";

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

@Override
	public boolean equals(Object obj) {
		if(obj instanceof MyPolygon)
			return ((MyPolygon)obj).getName().equals(this.getName());
		return super.equals(obj);
	}
}
