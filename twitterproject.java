package maven.twitterproject;
/**
 * Twitter Word Cloud 
 */
import twitter4j.*;
import java.io.*;
import java.util.*;
import java.awt.Dimension;
import twitter4j.conf.ConfigurationBuilder;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class twitterproject {
    public static void main(String[] args) throws IOException{
                // Twitter API credentials to access tweets
                //System.out.println("This does something");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true).setOAuthConsumerKey("##############")
                .setOAuthConsumerSecret("##############")
                .setOAuthAccessToken("##############")
                .setOAuthAccessTokenSecret("##############");

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        System.out.println("Hashtag to search for: ");
        Scanner input = new Scanner(System.in);
        String searchTerm = input.nextLine();
        Query query = new Query(searchTerm + " -filter:retweets");
        query.setLang("en");
        long lastID = Long.MAX_VALUE;


        int numberOfTweets = 2000;
        ArrayList<Status> tweets = new ArrayList<Status>();
        while (tweets.size() < numberOfTweets) {
            if (numberOfTweets - tweets.size() > 100) {
                query.setCount(100);
            } else {
                query.setCount(numberOfTweets - tweets.size());
            }
            try {
                QueryResult result = twitter.search(query);
                tweets.addAll(result.getTweets());
                System.out.println("Gathered " + tweets.size() + " tweets.");
                for (Status t : tweets) {
                    if (t.getId() < lastID) {
                        lastID = t.getId();
                    }
                }
            } catch (TwitterException te) {
                System.out.println("Couldn't connect: " + te);
            }
            ;
            query.setMaxId(lastID - 1);
            
        }
        for (int i = 0; i < tweets.size(); i++) {
            Status t = (Status) tweets.get(i);
            // String user = t.getUser().getScreenName();
            String msg = t.getText();
            // System.out.println(i + " USER: " + user + " wrote " + msg + "\n");
            String cleanedTweet = (cleanTweet(msg));
            String[] arr = cleanedTweet.split(" ");
            for (String s : arr) {
                    writeToFile(s);

            }
        }
        getCloud();
    }
    
    public static String cleanTweet(String msg) {
        String cleanedMsg = msg.toLowerCase();
        cleanedMsg = cleanedMsg.replaceAll("http\\S+", "");
        cleanedMsg = cleanedMsg.replaceAll("[^A-Za-z0-9 ]+", "");
        // System.out.println(cleanedMsg);
        return cleanedMsg;
    }

   public static void writeToFile(String tweet) throws java.io.IOException {
        java.io.File file = new java.io.File("cleanedtweets.txt");
        //System.out.println("will be added to file.");
        java.io.FileWriter fw = new FileWriter(file, true);
        java.io.PrintWriter output = new PrintWriter(fw);
        output.print(tweet + " ");
        output.close();
    }
    
    public static void getCloud() throws IOException{
    	final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
    	frequencyAnalyzer.setMinWordLength(3);
    	frequencyAnalyzer.setWordFrequenciesToReturn(300);
    	List<String> stopwords = Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "dont", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now");
		frequencyAnalyzer.setStopWords(stopwords);
    	final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load("cleanedtweets.txt");
    	final Dimension dimension = new Dimension(600, 600);
    	final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
    	wordCloud.setPadding(2);
    	wordCloud.setBackground(new CircleBackground(300));
    	wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
    	wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
    	wordCloud.build(wordFrequencies);
        wordCloud.writeToFile("cloud.png");
    }
}

//Sources:
//https://github.com/kennycason/kumo
//https://www.youtube.com/watch?v=x8sMN4tossY
