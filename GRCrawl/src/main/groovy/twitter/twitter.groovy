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
Query query = new Query("jesus");
GeoLocation sheffield = new GeoLocation(53.377127, -1.467705);
query.setGeoCode(sheffield, 800, Query.KILOMETERS)
QueryResult result = twitter.search(query);

def indexInfo = IndexInfoStatic.instance
indexInfo.setIndexWriter(true)
def iw= indexInfo.iw;	

List<Status> tweets = []

for   (i in 0..10){
	result = twitter.search(query);
	tweets.addAll(result.getTweets())
	query = result.nextQuery()
	if (query == null) break;
}

println " tweets size is " + tweets.size();

tweets.each {

	def uname = it.getUser().getScreenName()
	def txt =  it.getText()
	def plc = it.getPlace()?.getFullName()

	println "username: $uname text: $txt"
	println "place is $plc"
	
	addDoc(iw, txt, uname)

	def geo = it.getGeoLocation();

	if (geo!=null){
		def lat = it.getGeoLocation().getLatitude()
		def	lng = it.getGeoLocation().getLongitude()

		println "lat: $lat geo is $geo"
		println ""
	}
	println ""
	
}
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

def addDoc(IndexWriter w, String text, String uname) throws IOException {
	println "in add doc text is $text"
	Document doc = new Document();
	doc.add(new TextField("contents", text, Field.Store.YES));

	// use a string field for year because we don't want it tokenized
	doc.add(new StringField("uname", uname, Field.Store.YES));
	w.addDocument(doc);
}
