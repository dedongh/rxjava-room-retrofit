package com.engineerskasa.rxj.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.engineerskasa.rxj.Model.Notes;
import com.engineerskasa.rxj.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NotesActivity extends AppCompatActivity {

    private static String TAG = NotesActivity.class.getSimpleName();

    CompositeDisposable disposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        disposable.add(
                getNotesObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<Notes, Notes>() {
                            @Override
                            public Notes apply(Notes notes) throws Exception {
                                // converts all notes to uppercase
                                notes.setNote(notes.getNote().toUpperCase());
                                return notes;
                            }
                        })
                        .subscribeWith(getNotesObserver())
        );
    }

    // observable
    private DisposableObserver getNotesObserver() {
        return new DisposableObserver<Notes>() {

            @Override
            public void onNext(Notes notes) {
                Log.d(TAG, "Note: " + notes.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All notes are emitted!");
            }
        };
    }

    // emits stream of data
    private Observable<Notes> getNotesObservable() {
        final List<Notes> notes = prepareNotes();

        return Observable.create(new ObservableOnSubscribe<Notes>() {
            @Override
            public void subscribe(ObservableEmitter<Notes> emitter) throws Exception {
                for (Notes notes1 : notes) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(notes1);
                    }
                }
            }
        });
    }

    // create notes
    private List<Notes> prepareNotes() {
        List<Notes> notes = new ArrayList<>();
        notes.add(new Notes(1, "buy tooth paste!"));
        notes.add(new Notes(2, "call brother!"));
        notes.add(new Notes(3, "watch narcos tonight!"));
        notes.add(new Notes(4, "pay power bill!"));
        return notes;
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
        super.onDestroy();
    }
}
