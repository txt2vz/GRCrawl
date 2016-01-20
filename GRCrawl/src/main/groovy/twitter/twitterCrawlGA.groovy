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

def queryStrings = ["dog", "cat", "mouse"]

queryStrings.eachWithIndex{queryString, catNumber ->

	Query query = new Query(queryString + "-filter:retweets");
	//GeoLocation sheffield = new GeoLocation(53.377127, -1.467705);
	//query.setGeoCode(sheffield, 800, Query.KILOMETERS)

	int numberOfTweets = 400;
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
		};
		query.setMaxId(lastID-1);
	}

	println "collected ********** " + tweets.size() + " for catNumber $catNumber query $queryString"

	tweets.each {

		def uname = it.getUser().getScreenName()
		def txt =  it.getText()
		def plc = it.getPlace()?.getFullName()

		//	println "username: $uname text: $txt"
		//	println "place is $plc"

		addDoc(iw, txt, uname, queryString, catNumber)

		def geo = it.getGeoLocation();

		if (geo!=null){
			def lat = it.getGeoLocation().getLatitude()
			def	lng = it.getGeoLocation().getLongitude()

			println "lat: $lat geo is $geo"
			println ""
		}
		//	println ""
	}
}

println " iw maxdoc " + iw.maxDoc()
println "closing indexwriter"
iw.close()
println "closed"

private Twitter getTwitterAuth(){

	ConfigurationBuilder cb = new ConfigurationBuilder();
	cb.setDebugEnabled(true)
			.setOAuthConsumerKey("u59ay8TtPUn5p9VTHxdFg")
			.setOAuthConsumerSecret("LOkS2Vl9KTWXH5VMuwhb9RfIcXBXzkvyzzwD0HQtr14")
			.setOAuthAccessToken("560297710-4UmsMLOILUgIkLx6V5mdH1lbvG8ew8xvQm5YgBhY")
			.setOAuthAccessTokenSecret("mlwDtpbx9bUKTTk4wpBYVdUagGmBX6bzAYJbktoNM");
	TwitterFactory tf = new TwitterFactory(cb.build());
	Twitter twitter = tf.getInstance();

	return tf.getInstance();
}

def addDoc(IndexWriter w, String twtext, String twuname, String q, int cat) throws IOException {
	//println "in add doc text is $twtext"
	Document doc = new Document();
	doc.add(new TextField(IndexInfoStatic.FIELD_CONTENTS, twtext, Field.Store.YES));

	// use a string field for year because we don't want it tokenized
	doc.add(new StringField(IndexInfoStatic.FIELD_TWITTER_USERNAME, twuname, Field.Store.YES));
	doc.add(new StringField(IndexInfoStatic.FIELD_CATEGORY, cat.toString(), Field.Store.YES));
	doc.add(new StringField(IndexInfoStatic.FIELD_QUERY, q, Field.Store.YES));
	doc.add(new StringField(IndexInfoStatic.FIELD_TEST_TRAIN, "train", Field.Store.YES));
	w.addDocument(doc);
}
