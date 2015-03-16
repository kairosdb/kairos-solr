package org.kairosdb.plugin.solr;

import com.google.inject.Inject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.kairosdb.core.annotation.PluginName;
import org.kairosdb.core.datastore.DatastoreMetricQuery;
import org.kairosdb.datastore.cassandra.CassandraRowKeyPlugin;
import org.kairosdb.datastore.cassandra.DataPointsRowKey;

import java.util.*;

/**
 An instance of this class is created for each query.
 Created by bhawkins on 11/23/14.
 */
@PluginName(name = "solr", description = "Solr Search Plugin")
public class QueryPlugin implements org.kairosdb.core.datastore.QueryPlugin,
		CassandraRowKeyPlugin
	{
		public static final String METRIC_NAME = "metric_name";
		public static final String DATA_TYPE = "data_type";
		public static final String TIMESTAMP = "timestamp";
		public static final String TAG_PREFIX = "tag_";

		private String m_query;
		private final HttpSolrClient m_solrClient;

		@Inject
		public QueryPlugin(HttpSolrClient solrClient)
		{
			m_solrClient = solrClient;
		}

		public void setQuery(String query)
		{
			m_query = query;
		}

		@Override
		public Iterator<DataPointsRowKey> getKeysForQueryIterator(DatastoreMetricQuery datastoreMetricQuery)
		{
			SolrQuery solrQuery = new SolrQuery();

			solrQuery.set("q", m_query);

			List<DataPointsRowKey> ret = new ArrayList<DataPointsRowKey>();

			try
			{
				QueryResponse response = m_solrClient.query(solrQuery);

				SolrDocumentList list = response.getResults();

				for (SolrDocument document : list)
				{
					String metricName;
					long timestamp = 0L;
					String dataType;

					metricName = (String)document.get(METRIC_NAME);
					//timestamp = document.get(TIMESTAMP);
					dataType = (String)document.get(DATA_TYPE);

					DataPointsRowKey rowKey = new DataPointsRowKey(metricName, timestamp, dataType);

					for (String fieldName : document.getFieldNames())
					{
						if (fieldName.startsWith(TAG_PREFIX))
						{

						}
					}

				}
			}
			catch (SolrServerException e)
			{
				e.printStackTrace();
			}

			System.out.println("*******I Got Called ***********");
			SortedMap<String, String> tags = new TreeMap<String, String>();
			tags.put("host", "kairos-mini");

			DataPointsRowKey rowKey = new DataPointsRowKey("collectd.load.load.shortterm", 1426118400000L, "kairos_double", tags);


			return Collections.singletonList(rowKey).iterator();
		}

		@Override
		public String getName()
		{
			return "solr";
		}
	}
