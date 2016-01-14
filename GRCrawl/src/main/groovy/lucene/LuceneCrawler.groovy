package lucene;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.IOException;
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.document.Field;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;


public class LuceneCrawler extends WebCrawler {

	private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)\$");
	int x=1;
	IndexWriter iw;

	public void onStart(){
			def indexInfo = IndexInfoStatic.instance	
	        iw =  indexInfo.iw
		// iw = IndexInfoStatic.instance.iw	
	};

	public void onBeforeExit(){
		//println "closing index"
	//	iw.close()
	//	println "closed"
	};
	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		println "in should visit"
		String href = url.getURL().toLowerCase();
		// Ignore the url if it has an extension that matches our defined set of image extensions.
		if (IMAGE_EXTENSIONS.matcher(href).matches()) {
			return false;
		}

		// Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
		return href.startsWith("http://www.ics.uci.edu/");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		println "in visit $x"
		x++;

		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();

		logger.debug("Docid: {}", docid);
		logger.info("URL: {}", url);
		logger.debug("Domain: '{}'", domain);
		logger.debug("Sub-domain: '{}'", subDomain);
		logger.debug("Path: '{}'", path);
		logger.debug("Parent page: {}", parentUrl);
		logger.debug("Anchor text: {}", anchor);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			System.out.println(" text is " + text.substring(0, 200));
			addDoc(iw, text, url);
			print "passed add doc"

			logger.debug("Text length: {}", text.length());
			logger.debug("Html length: {}", html.length());
			logger.debug("Number of outgoing links: {}", links.size());
		}

		Header[] responseHeaders = page.getFetchResponseHeaders();
		if (responseHeaders != null) {
			logger.debug("Response headers:");
			for (Header header : responseHeaders) {
				logger.debug("\t{}: {}", header.getName(), header.getValue());
			}
		}

		logger.debug("=============");
	}

	def addDoc(IndexWriter w, String text, String url) throws IOException {
		println "in add doc"
		Document doc = new Document();
		doc.add(new TextField("contents", text, Field.Store.YES));

		// use a string field for year because we don't want it tokenized
		doc.add(new StringField("url", url, Field.Store.YES));
		w.addDocument(doc);
	}
}