package lucene

import java.nio.file.Path
import java.nio.file.Paths

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory

@Singleton
class IndexInfoStatic {
	final indexPath =  "C:\\Users\\laurie\\Java\\indexes2\\crawl4"
	private Path path = Paths.get(indexPath)
	private Directory directory = FSDirectory.open(path)
	private Analyzer analyzer = new StandardAnalyzer();
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
}