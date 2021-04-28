package ai.hyperlearning.ontology.services.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Apache Maven Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class MavenUtils {
	
	private static final MavenXpp3Reader MAVEN_READER = new MavenXpp3Reader();
	private static final String POM_FILENAME = "pom.xml";
	
	private static Model getMavenModel() 
			throws FileNotFoundException, IOException, XmlPullParserException {
		return MAVEN_READER.read(new FileReader(POM_FILENAME));
	}
	
	/**
	 * Get the current version of the artifact produced by this project
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	
	public static String getMavenModelVersion() 
			throws FileNotFoundException, IOException, XmlPullParserException {
		return MavenUtils.getMavenModel().getVersion();
	}

}
