package com.capstone.authorfollow.service;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by sengopal on 6/16/16.
 */
public interface Services {
    public static final String AMAZON_URL = "http://webservices.amazon.com";
    public static final String GR_URL = "http://www.goodreads.com";

    public interface AmazonService {
        @GET("/onca/xml")
        Call<ItemSearchResponse> findBooks(@QueryMap(encoded = true) Map<String, String> params);
    }

    public interface GoodReadsService {
        //https://www.goodreads.com/author/show/1077326?format=xml&key=We96lMbi0gpn6i9oHKd0dA
        @GET("/author/show/{authorId}")
        Call<AuthorDetail> getAuthorDetailById(@Path("authorId") String authorId, @QueryMap Map<String, String> params);

        //https://www.goodreads.com/api/author_url/Orson%20Scott%20Card?key=We96lMbi0gpn6i9oHKd0dA
        @GET("/api/author_url/{name}")
        Call<AuthorInfo> getAuthorId(@Path("name") String name, @Query("key") String key);

        //https://www.goodreads.com/api/author_url/Orson%20Scott%20Card?key=We96lMbi0gpn6i9oHKd0dA
        @GET("/book/isbn/{isbn}")
        Call<GRBookResponse> getBookInfo(@Path("isbn") String isbn, @Query("key") String key);
    }

    public interface AuthorAlertService {
        //http://authoralerts.com?q=
        @GET("/autocomplete.jsp")
        Call<List<String>> getAuthorDetailById(@Query("q") String query);
    }

    @Root(name="ItemSearchResponse", strict = false)
    public static class ItemSearchResponse {
        @Element(name = "Items")
        public Items resultItems;
    }

    @Root(name="Items", strict = false)
    public static class Items{
        @Element(name="TotalResults")
        public int totalResults;

        @Element(name="TotalPages")
        public int totalPages;

        @ElementList(inline = true, required = false)
        public List<Item> itemList;
    }

    @Root(name="Item", strict = false)
    public static class Item {
        @Element(name="ASIN")
        public String asin;

        @Element(name="DetailPageURL")
        public String amazonUrl;

        @Element(name="URL")
        @org.simpleframework.xml.Path("MediumImage")
        public String mediumImageUrl;

        @Element(name="URL")
        @org.simpleframework.xml.Path("LargeImage")
        public String largeImageUrl;

        @Element(name="ISBN", required = false)
        @org.simpleframework.xml.Path("ItemAttributes")
        public String isbn;

        @Element(name="Title")
        @org.simpleframework.xml.Path("ItemAttributes")
        public String title;

        @Element(name="PublicationDate")
        @org.simpleframework.xml.Path("ItemAttributes")
        public Date pubDate;

        @ElementList(name="BrowseNodes", inline = true)
        @org.simpleframework.xml.Path("BrowseNodes")
        public List<BrowseNode> browseNodeList;
    }

    @Root(name="BrowseNode", strict = false)
    public static class BrowseNode{
        @Element(name="Name")
        public String name;
    }

    @Root(name = "GoodreadsResponse", strict = false)
    public static class AuthorInfo {
        @Attribute(name = "id")
        @org.simpleframework.xml.Path("author")
        public String authorId;
    }

    @Root(name = "GoodreadsResponse", strict = false)
    public static class GRBookResponse {
        @Element(name = "id")
        @org.simpleframework.xml.Path("book")
        public String grBookId;

        @Element(name = "publisher")
        @org.simpleframework.xml.Path("book")
        public String publisher;

        @Element(name = "description", data = true, required = false)
        @org.simpleframework.xml.Path("book")
        public String description;

        @Element(name = "average_rating")
        @org.simpleframework.xml.Path("book")
        public float rating;

        @Element(name = "url", data = true)
        @org.simpleframework.xml.Path("book")
        public String grLinkUrl;

        @Element(name = "image_url", data = true)
        @org.simpleframework.xml.Path("book")
        public String grImageUrl;

    }

    @Root(name = "GoodreadsResponse", strict = false)
    public static class AuthorDetail{
        @Element(name = "name")
        @org.simpleframework.xml.Path("author")
        public String name;

        @Element(name = "link", data = true)
        @org.simpleframework.xml.Path("author")
        public String grPageLink;

        @Element(name = "large_image_url", data = true)
        @org.simpleframework.xml.Path("author")
        public String imageUrl;

        @Element(name = "hometown")
        @org.simpleframework.xml.Path("author")
        public String homeTown;

        @Element(name = "fans_count")
        @org.simpleframework.xml.Path("author")
        public String fanCount;

        @Element(name = "about", data = true)
        @org.simpleframework.xml.Path("author")
        public String desc;
    }
}