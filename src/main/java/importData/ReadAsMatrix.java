package importData;

import java.util.Map;

import org.ejml.simple.SimpleMatrix;

import model.Link;

public class ReadAsMatrix {
	Map<String,Link> linksMap=ReadAsLink.readAsLink();
	SimpleMatrix A = new SimpleMatrix(2,3,true,new double[]{1,2,3,4,5,6});

}
