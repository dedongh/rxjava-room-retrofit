package com.engineerskasa.rxj.Retrofit;

import com.engineerskasa.rxj.Model.ContactList.Contact;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InstantSearchAPIService {

    @GET("contacts.php")
    // single observable is used because list of contacts will be fetched at once bam just like that you bab
    Single<List<Contact>> getContacts(
            @Query("source") String source,
            @Query("search")String query
            );
}
