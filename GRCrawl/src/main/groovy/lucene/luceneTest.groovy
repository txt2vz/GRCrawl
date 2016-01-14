package lucene

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;


	StandardAnalyzer analyzer = new StandardAnalyzer();
//	def index = IndexInfoStatic.instance
//	def w = index.iw
	
	def indexInfo = IndexInfoStatic.instance	
	IndexWriter w =  indexInfo.getIndexWriter(false)
	

	// 2. query
	String querystr =  "today";

	// the "title" arg specifies the default field to use
	// when no field is explicitly specified in the query.
	Query q = new QueryParser( "contents", analyzer).parse(querystr);

	// 3. search
	int hitsPerPage = 20;
	IndexReader reader = w.getReader();
	IndexSearcher searcher = new IndexSearcher(reader);
	TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
	searcher.search(q, collector);
	ScoreDoc[] hits = collector.topDocs().scoreDocs;
	
	// 4. display results
	println "Found " + hits.length + " hits."
	hits.each{
		int docId = it.doc;
		Document d = searcher.doc(docId);
		println(d.get("contents") + "\t " +
			" uname:" + d.get("uname"));
               //" url:" + d.get("url"));
	}

	// reader can only be closed when there
	// is no need to access the documents any more.
	reader.close();
	w.close();
