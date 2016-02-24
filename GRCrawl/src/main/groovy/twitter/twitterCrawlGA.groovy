import groovy.json.*
import lucene.*

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter

import twitter4j.*
import twitter4j.conf.*

Twitter twitter = getTwitterAuth();
def indexInfo = IndexInfoStatic.instance
indexInfo.setIndexWriter(true)//(true)
def iw= indexInfo.iw;

def queryStrings = ["trump", "clinton", "sanders" ]//["dog", "cat", "mouse"]

queryStrings.eachWithIndex{queryString, catNumber ->

	Query query = new Query(queryString + "-filter:retweets");
	//GeoLocation sheffield = new GeoLocation(53.377127, -1.467705);
	//query.setGeoCode(sheffield, 800, Query.KILOMETERS)

	int numberOfTweets = 5000;
	long lastID = Long.MAX_VALUE;
	List<Status> tweets = []

	while (tweets.size () < numberOfTweets) {
		if (numberOfTweets - tweets.size() > 100)
			query.setCount(100);
		else
			query.setCount(numberOfTweets - tweets.size());
		try {
			QueryResult result = twitter.search(query);
			tweets.addAll(result.getTweets());
			println("Gathered " + tweets.size() + " tweets");
			for (Status t: tweets)
				if(t.getId() < lastID) lastID = t.getId();
		}

		catch (TwitterException te) {
			println("Couldn't connect: " + te);
			if (te.toString().contains("429")) {
				println ("sleeping for 15mins");
				Thread.sleep(900000);
			}
		};
		query.setMaxId(lastID-1);
	}

	println "collected ********** " + tweets.size() + " for catNumber $catNumber query $queryString"

	tweets.eachWithIndex {t, index ->

		def uname = t.getUser().getScreenName()
		def txt =  t.getText()
		def plc = t.getPlace()?.getFullName()

		def train = index % 5==0 ? "test" : "train"

		addDoc(iw, txt, uname, queryString, catNumber, train)

		def geo = t.getGeoLocation();

		if (geo!=null){
			def lat = t.getGeoLocation().getLatitude()
			def	lng = t.getGeoLocation().getLongitude()

			println "lat: $lat geo is $geo"
			println ""
		}	
	}
}

println " iw maxdoc " + iw.maxDoc()
println "closing indexwriter"
iw.close()
println "closed"

private Twitter getTwitterAuth(){

	ConfigurationBuilder cb = new ConfigurationBuilder();
	cb.setDebugEnabled(true)
			.setOAuthConsumerKey("**")
			.setOAuthConsumerSecret("**")
			.setOAuthAccessToken("**")
			.setOAuthAccessTokenSecret("**");
	TwitterFactory tf = new TwitterFactory(cb.build());
	Twitter twitter = tf.getInstance();

	return tf.getInstance();
}

def addDoc(IndexWriter w, String twtext, String twuname, String q, int cat, String trn) throws IOException {
	//println "in add doc text is $twtext"

	Document doc = new Document();
	doc.add(new TextField(IndexInfoStatic.FIELD_CONTENTS, twtext, Field.Store.YES));

	// use a string field for year because we don't want it tokenized
	doc.add(new StringField(IndexInfoStatic.FIELD_TWITTER_USERNAME, twuname, Field.Store.YES));
	doc.add(new StringField(IndexInfoStatic.FIELD_CATEGORY, cat.toString(), Field.Store.YES));
	doc.add(new StringField(IndexInfoStatic.FIELD_QUERY, q, Field.Store.YES));
	doc.add(new StringField(IndexInfoStatic.FIELD_TEST_TRAIN, trn, Field.Store.YES));
	w.addDocument(doc);
}
