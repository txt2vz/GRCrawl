package lucene

import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.MatchAllDocsQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TopScoreDocCollector
import org.apache.lucene.search.TotalHitCountCollector

def indexInfo = IndexInfoStatic.instance
indexInfo.setIndexWriter(false)

IndexReader reader =  indexInfo.iw.getReader();
IndexSearcher searcher = new IndexSearcher(reader);

println " reader max doc " + reader.maxDoc()
//assert reader.maxDoc() ==18846
def querystr = "cat"

Query q = new TermQuery(new Term(indexInfo.FIELD_CONTENTS,  querystr))
//Query q = new QueryParser(indexInfo.FIELD_CONTENTS, indexInfo.analyzer).parse(querystr);

final TermQuery catQ = new TermQuery(new Term(indexInfo.FIELD_CATEGORY,
	"0"));

BooleanQuery.Builder bq = new BooleanQuery.Builder();

bq.add(catQ, BooleanClause.Occur.SHOULD)
//Query qall = new MatchAllDocsQuery()
//bq.add(qall, BooleanClause.Occur.MUST)
//bq.add(q, BooleanClause.Occur.MUST_NOT)

TotalHitCountCollector collector = new TotalHitCountCollector();

searcher.search(bq.build(), collector);

def totalHits = collector.getTotalHits();
println "searching for $querystr Found: $totalHits hits"

TopScoreDocCollector collector2 = TopScoreDocCollector.create(10);
searcher.search(bq.build(), collector2);
ScoreDoc[] hits = collector2.topDocs().scoreDocs;
System.out.println("Query string: " + querystr );
System.out.println("Found " + hits.length + " hits.");

hits.eachWithIndex {h, index ->
	int docId = h.doc;
	Document d = searcher.doc(docId);
	println "$index " + d.get(indexInfo.FIELD_CONTENTS) + "\t" + d.get(indexInfo.FIELD_TWITTER_USERNAME)
}

reader.close();
indexInfo.iw.close()