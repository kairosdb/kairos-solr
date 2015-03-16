package org.kairosdb.plugin.solr;

import com.google.inject.Inject;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.kairosdb.datastore.cassandra.DataPointsRowKey;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;

import static org.kairosdb.plugin.solr.QueryPlugin.*;

/**
 Created by bhawkins on 1/7/15.
 */
public class RowKeyListener implements org.kairosdb.datastore.cassandra.RowKeyListener
{
	private Charset UTF8 = Charset.forName("UTF-8");
	//YYYY-MM-DDThh:mm:ssZ
	private static SimpleDateFormat s_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	private HttpSolrClient m_solrClient;

	@Inject
	public RowKeyListener(HttpSolrClient solrClient)
	{
		System.out.println("++++++++++RowKeyListener++++++++++++++");
		m_solrClient = solrClient;
	}

	/**
	 Each document in solr needs an id.  Because our documents are all unique
	 we will use a hash of each field to create and id.
	 @param rowKey
	 @return
	 */
	private String hashRowKey(DataPointsRowKey rowKey)
	{
		MessageDigest messageDigest = null;
		try
		{
			messageDigest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {}

		messageDigest.update(rowKey.getMetricName().getBytes(UTF8));
		messageDigest.update(rowKey.getDataType().getBytes(UTF8));

		//Hash timestamp
		long timestamp = rowKey.getTimestamp();
		for (int I = 7; I >= 0; --I)
		{
			messageDigest.update((byte) (timestamp & 0xff));
			timestamp >>= 8;
		}

		//Hash tags
		SortedMap<String, String> tags = rowKey.getTags();
		for (String tagName : tags.keySet())
		{
			messageDigest.update(tagName.getBytes(UTF8));
			messageDigest.update(tags.get(tagName).getBytes(UTF8));
		}

		byte[] digest = messageDigest.digest();

		return new BigInteger(1, digest).toString(16);
	}

	@Override
	public void addRowKey(String metricName, DataPointsRowKey dataPointsRowKey, int rowKeyTtl)
	{

		System.out.println("Got a row key: " + metricName);
		System.out.println(dataPointsRowKey);

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", hashRowKey(dataPointsRowKey));
		doc.addField(METRIC_NAME, metricName);
		doc.addField(DATA_TYPE, dataPointsRowKey.getDataType());
		doc.addField(TIMESTAMP, s_dateFormat.format(new Date(dataPointsRowKey.getTimestamp())));

		/*
			Tags are prefixed with tag_ so as to not stomp on the above
			fields.  This will have to be accounted for when searching
		 */
		SortedMap<String, String> tags = dataPointsRowKey.getTags();
		for (String tagName : tags.keySet())
		{
			doc.addField(TAG_PREFIX+tagName, tags.get(tagName));
		}

		try
		{
			//TODO may run into race condition with solr client.
			//idea - run commit on a background timer
			//wrap client and count if anything has been added
			m_solrClient.add(doc);
			m_solrClient.commit();
		}
		catch (SolrServerException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
