package lucene

import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.Term
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
def querystr = "obama"
Query q = new TermQuery(new Term(indexInfo.FIELD_CONTENTS,  querystr))

BooleanQuery.Builder bq = new BooleanQuery.Builder();

Query qall = new MatchAllDocsQuery()
bq.add(qall, BooleanClause.Occur.MUST)
bq.add(q, BooleanClause.Occur.MUST_NOT)

TotalHitCountCollector collector = new TotalHitCountCollector();


//BooleanQuery bq = new BooleanQuery();
//bq.add(q, BooleanClause.Occur.MUST_NOT)

searcher.search(bq.build(), collector);


def totalHits = collector.getTotalHits();
println "searching for $querystr Found: $totalHits hits"
//	assert totalHits==12

TopScoreDocCollector collector2 = TopScoreDocCollector.create(20);
searcher.search(bq.build(), collector2);
ScoreDoc[] hits = collector2.topDocs().scoreDocs;
System.out.println("Query string: " + querystr );
System.out.println("Found " + hits.length + " hits.");
for (int i = 0; i < hits.length; ++i) {
	int docId = hits[i].doc;
	Document d = searcher.doc(docId);
	System.out.println((i + 1) + ". " + d.get(indexInfo.FIELD_CONTENTS) + "\t" + d.get(indexInfo.FIELD_TWITTER_USERNAME));
}


reader.close();
indexInfo.iw.close()