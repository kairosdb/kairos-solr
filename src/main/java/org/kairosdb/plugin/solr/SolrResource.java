package org.kairosdb.plugin.solr;

import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import org.kairosdb.core.datastore.*;
import org.kairosdb.core.datastore.QueryPlugin;
import org.kairosdb.datastore.cassandra.CassandraDatastore;
import org.kairosdb.datastore.cassandra.DataPointsRowKey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.Response;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 Created by bhawkins on 3/15/15.
 */
@Path("/api/v1/solr")
public class SolrResource
{
	private final RowKeyListener m_rowKeyListener;
	private final CassandraDatastore m_cassandraDatastore;

	@Inject
	public SolrResource(CassandraDatastore cassandraDatastore, RowKeyListener rowKeyListener)
	{
		m_cassandraDatastore = cassandraDatastore;
		m_rowKeyListener = rowKeyListener;
	}

	/**
	 The purpose of this api end point is to allow the rebuilding of the solr index
	 You can specify a metric name and a time range.  The code queries into
	 CassandraDatastore and gets back the row keys that are stored in cassandra.
	 The Cassandra row keys are then used to rebuild the index.
	 @param metricName Name of metric to rebuild row keys for
	 @param from Start time to get keys
	 @param to End time to get keys
	 @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("reload")
	public Response reload(String metricName, Date from, Date to)
	{
		//TODO Finish and test this.
		/*DatastoreMetricQuery query = new QueryMetric(from.getTime(), to.getTime(), 0, metricName);

		Iterator<DataPointsRowKey> keysForQueryIterator = m_cassandraDatastore.getKeysForQueryIterator(query);

		while (keysForQueryIterator.hasNext())
		{
			DataPointsRowKey key = keysForQueryIterator.next();
			m_rowKeyListener.addRowKey(key.getMetricName(), key, 0);
		}*/

		return null;
	}
}
