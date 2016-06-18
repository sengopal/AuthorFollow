package com.capstone.authorfollow.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AuthorListService {
    //http://authoralerts.com/autocomplete.jsp?q=James
    @GET("autocomplete.jsp")
    Call<List<String>> findAuthors(@Query(value = "q") String param);
}
