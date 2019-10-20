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

import com.engineerskasa.rxj.Adapter.InstantSearch.ContactsAdapter;
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

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class RemoteSearchActivity extends AppCompatActivity implements ContactsAdapter.ContactsAdapterListener {
    private static final String TAG = RemoteSearchActivity.class.getSimpleName();

    private CompositeDisposable disposable = new CompositeDisposable();
    private PublishSubject<String> publishSubject = PublishSubject.create();

    private EditText searchText;
    private RecyclerView recyclerView;

    private InstantSearchAPIService apiService;

    private List<Contact> contactList = new ArrayList<>();
    private ContactsAdapter contactsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_search);

        searchText = (EditText) findViewById(R.id.input_search);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        contactsAdapter = new ContactsAdapter(this, contactList, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(contactsAdapter);

        apiService = Common.getSearchAPI();
        
        // create an observer
        DisposableObserver<List<Contact>> observer = getSearchObserver();

        /*
        PublishSubject emits the events at the time of subscription.
        switchMapSingle() plays very important role here. When there are multiple search requests in the queue,
        SwitchMap() ignores the previous emission and considers only the current search query.
         So the list will always displays the latest search results.
        * */

        disposable.add(publishSubject.debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMapSingle(new Function<String, Single<List<Contact>>>() {
                    @Override
                    public Single<List<Contact>> apply(String s) throws Exception {
                        return apiService.getContacts(null, s)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribeWith(observer));

        // skipInitialValue() - skip for the first time when EditText empty
        disposable.add(RxTextView.textChangeEvents(searchText)
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchContactsTextWatcher()));

        disposable.add(observer);

        publishSubject.onNext("");
    }

    private DisposableObserver<TextViewTextChangeEvent> searchContactsTextWatcher() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                publishSubject.onNext(textViewTextChangeEvent.text().toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private DisposableObserver<List<Contact>> getSearchObserver() {
        return new DisposableObserver<List<Contact>>() {
            @Override
            public void onNext(List<Contact> contacts) {
                contactList.clear();
                contactList.addAll(contacts);
                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
    }

    @Override
    public void onContactSelected(Contact contact) {
        Toast.makeText(this, "You selectedd: "+ contact.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
        super.onDestroy();
    }
}
