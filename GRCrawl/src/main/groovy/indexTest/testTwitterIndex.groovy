package indexTest

//import static org.junit.Assert.*;
import lucene.*

import org.junit.Test;

class testTwitterIndex {
	
	//def indexInfo = IndexInfoStatic.instance
	//indexInfo.setIndexWriter(true)
	//IndexInfoStatic is = IndexInfoStatic.instance;
	 // indexInfo.testQuery()
	//}

	@Test
	public void test() {
		println "hhh"
		
		def indexInfo = IndexInfoStatic.instance
		indexInfo.setIndexWriter(false)
		indexInfo.testQuery()
		//fail("Not yet implemented");
	}

}
