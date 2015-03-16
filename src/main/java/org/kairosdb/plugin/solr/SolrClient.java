package org.kairosdb.plugin.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 Created by bhawkins on 3/13/15.
 Going to use this to collect multiple updates and send them to
 solr on a timer.
 */
public class SolrClient
{
	private HttpSolrClient m_solrClient;
	private List<SolrInputDocument> m_documents = new ArrayList<SolrInputDocument>();
	private Object m_lock;

	public SolrClient()
	{
	}

	public void add(SolrInputDocument inputDocument)
	{
		synchronized (m_lock)
		{
			m_documents.add(inputDocument);
		}
	}
}
