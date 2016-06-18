package com.capstone.authorfollow.service;

import com.capstone.authorfollow.data.types.UpcomingBook;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sengopal on 5/8/16.
 */
public class NetworkHelper {
    private static final DateFormat PUB_DATE_FORMAT = new SimpleDateFormat("MM-yyyy");
    public static List<String> searchForAuthors(){
        return null;
    }

    public static List<UpcomingBook> findBooks(String author, int noOfForwardDays){
        SignedRequestsHelper instance = null;
        try {
            instance = SignedRequestsHelper.getInstance("webservices.amazon.com", "AKIAIV32PE3ER4WKYHOA", "Flxr9aHgX82CfH/W+yKeWsPWW5m6DMMJegDmAIWB");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("AssociateTag", "sengopalme-20");
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("ResponseGroup", "ItemAttributes,Images");
        params.put("SearchIndex", "Books");
        //params.put("BrowseNode", "154606011");
        StringBuilder sb = new StringBuilder();
        //Use for recent releases and recent releases as configuration
        //TODO: Make this a configuration too
        sb.append("language:english");
        sb.append("and pubdate: after ").append(getPubDate(0));
        sb.append("and pubdate: before ").append(getPubDate(noOfForwardDays));
        params.put("Power",sb.toString());
        //params.put("Author", "James Patterson");
        params.put("Author", author);
        params.put("Sort","-publication_date");
        String url = instance.sign(params);
        System.out.println(url);
        //Unirest.get(url).asString().getBody().toString()));
        return null;
    }

    private static String getPubDate(int forwardDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, forwardDays);
        return PUB_DATE_FORMAT.format(calendar.getTime());
    }

    /*
    private static String fetchTitle(String xmlStr) {
        String title = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xmlStr)));
            int length = doc.getElementsByTagName("PublicationDate").getLength();
            for (int i = 0; i < length; i++) {
                Node titleNode = doc.getElementsByTagName("PublicationDate").item(i);
                System.out.println(titleNode.getTextContent());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return title;
    }*/
}
