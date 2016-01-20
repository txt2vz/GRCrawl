package lucene

import java.nio.file.Path
import java.nio.file.Paths

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TotalHitCountCollector
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory

@Singleton
class IndexInfoStatic {

	// Lucene field names
	public static final String FIELD_CATEGORY = "category";
	public static final String FIELD_CONTENTS = "contents";
	public static final String FIELD_QUERY = "query";
	public static final String FIELD_PATH = "path";
	public static final String FIELD_TEST_TRAIN = "test_train";
	public static final String FIELD_TWITTER_USERNAME = "twitter_username";

	private final indexPath =  "C:\\Users\\laurie\\Java\\indexes2\\crawl7" 
	private Path path = Paths.get(indexPath)
	private Directory directory = FSDirectory.open(path)
	//Analyzer analyzer = new StandardAnalyzer();
	Analyzer analyzer    = new EnglishAnalyzer()
	
	IndexWriter iw;

	IndexWriter setIndexWriter(boolean create){
		IndexWriterConfig iwc
		if (create){
			iwc = new IndexWriterConfig(analyzer).setOpenMode(OpenMode.CREATE);
		}
		else {
			iwc = new IndexWriterConfig(analyzer)
		}
		iw = new IndexWriter(directory, iwc);
	}

	void testQuery(){
		//setIndexWriter(false)
		IndexReader reader =  iw.getReader();

		println " reader max doc " + reader.maxDoc()
		//assert reader.maxDoc() ==18846
		def querystr = "obama"

		IndexSearcher searcher = new IndexSearcher(reader);
		TotalHitCountCollector collector = new TotalHitCountCollector();
		Query q = new TermQuery(new Term(FIELD_CONTENTS,  querystr))

		searcher.search(q, collector);
		def totalHits = collector.getTotalHits();
		println "searching for $querystr Found: $totalHits hits"
		//	assert totalHits==12
		reader.close();
		iw.close()
	}
}