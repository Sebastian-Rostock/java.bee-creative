package bee.creative.ds;

import bee.creative.util.Property2;
import bee.creative.util.Set2;


interface DE {

	DS owner();
	
}


interface DL extends DE{

}


interface DN extends DE {


	DNVal sourceFrom(DL link);
	
	DNSet sourcesFrom(DL link);

}

interface DNSet extends DE, Set2<DN> {

}

interface DNVal extends DE, Property2<DN> {

}