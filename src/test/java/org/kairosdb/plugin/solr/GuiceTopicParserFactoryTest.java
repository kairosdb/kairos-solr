package org.kairosdb.plugin.solr;

import org.junit.Test;
import org.kairosdb.core.Main;
import org.kairosdb.core.exception.KairosDBException;

import java.io.IOException;
import java.util.Properties;

/**
 Created by bhawkins on 2/18/14.
 */
public class GuiceTopicParserFactoryTest
{
	@Test
	public void firstTest()
	{
		Properties properties = new Properties();
		properties.setProperty("kairosdb.kafka.topicparser.stringparser.class", "org.kairosdb.plugin.solr.StringTopicParser");
		properties.setProperty("kairosdb.kafka.topicparser.stringparser.topics", "topic1,topic2");
		properties.setProperty("kairosdb.kafka.topicparser.stringparser.metric", "test_metric");
		//GuiceTopicParserFactory factory = new GuiceTopicParserFactory(null, properties);

		System.out.println("hey");
	}

/**
 This shows that you can start up the entire kairos service from within a unit test
 @throws IOException
 @throws KairosDBException
 @throws InterruptedException
 */
	//@Test
	public void runMainTest() throws IOException, KairosDBException, InterruptedException
	{
		Main main = new Main(null);
		main.startServices();


		main.stopServices();
	}


}
