/*
 * Copyright 2013 Proofpoint Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kairosdb.plugin.solr;


import com.google.inject.*;
import com.google.inject.name.Named;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SolrModule extends AbstractModule
{
	public static final Logger logger = LoggerFactory.getLogger(SolrModule.class);
	public static final String SOLR_URL_PROPERTY = "solr.url";

	@Override
	protected void configure()
	{
		logger.info("Configuring module SolrModule");

		bind(RowKeyListener.class).in(Singleton.class);

		bind(QueryPlugin.class).in(Singleton.class);

		bind(HttpSolrClient.class).toProvider(SolrClientProvider.class);

		bind(SolrResource.class).in(Scopes.SINGLETON);
	}

	public static class SolrClientProvider implements Provider<HttpSolrClient>
	{
		private HttpSolrClient m_solrClient;

		@Inject
		public SolrClientProvider(@Named(SOLR_URL_PROPERTY)String solrUrl)
		{
			m_solrClient = new HttpSolrClient(solrUrl);
		}

		@Override
		public HttpSolrClient get()
		{
			return m_solrClient;
		}
	}
}
