package com.engineerskasa.rxj.Activity.InstantSearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.engineerskasa.rxj.Adapter.InstantSearch.ContactsAdapterFilterable;
import com.engineerskasa.rxj.Model.ContactList.Contact;
import com.engineerskasa.rxj.R;
import com.engineerskasa.rxj.Retrofit.InstantSearchAPIService;
import com.engineerskasa.rxj.Util.Common;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LocalSearchActivity extends AppCompatActivity implements ContactsAdapterFilterable.ContactsAdapterListener {

    private static final String TAG = LocalSearchActivity.class.getSimpleName();

    private CompositeDisposable disposable = new CompositeDisposable();

    private InstantSearchAPIService apiService;
    ContactsAdapterFilterable adapterFilterable;
    private EditText searchText;
    private RecyclerView recyclerView;
    private List<Contact> contactList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_search);

        searchText = (EditText) findViewById(R.id.input_search);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        adapterFilterable = new ContactsAdapterFilterable(this, contactList, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapterFilterable);

        apiService = Common.getSearchAPI();

        /*
        * distinctUntilChanged() avoids making same search request again
        * debounce(300, TimeUnit.MILLISECONDS) – Emits the search query every 300 milliseconds.
        * */
        //
        disposable.add(RxTextView.textChangeEvents(searchText)
        .skipInitialValue()
        .debounce(300, TimeUnit.MILLISECONDS)
        .distinctUntilChanged()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(searchContacts()));

        /*fetching all contacts on app launch
        * source: `gmail` or `linkedin`*/
        fetchContacts("gmail");
    }

    // fetch contacts from API
    private void fetchContacts(String source) {
        disposable.add(apiService.getContacts(source, null)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<List<Contact>>(){

            @Override
            public void onSuccess(List<Contact> contacts) {
                contactList.clear();
                contactList.addAll(contacts);
                adapterFilterable.notifyDataSetChanged();

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: "+ e.getMessage());
            }
        }));
    }

    // triggers an event whenever the text is changed in the search edittext
    // an observer that will be called when search query is emitted
    // By calling adapterFilterable.getFilter().filter(), the search query will filter the data on ArrayList.
    private DisposableObserver<TextViewTextChangeEvent> searchContacts() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                adapterFilterable.getFilter().filter(textViewTextChangeEvent.text());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: "+ e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
        super.onDestroy();
    }

    @Override
    public void onContactSelected(Contact contact) {
        Toast.makeText(this, "You clicked: "+contact.getName(), Toast.LENGTH_SHORT).show();
    }
}
