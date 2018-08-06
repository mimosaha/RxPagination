package database.example.com.rx_pagination;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import database.example.com.rx_pagination.databinding.ActivityCustomBinding;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;

public class PaginationActivity extends AppCompatActivity {

    private ActivityCustomBinding activityCustomBinding;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCustomBinding = DataBindingUtil.setContentView(this, R.layout.activity_custom);

        // Init all things
        initData();
        initRecycler();

        // Functional area
        recyclerListener();
        subscribeForData();
    }

    private PaginationAdapter paginationAdapter;
    private int amountOfLoadData = 20;

    private void initRecycler() {
        paginationAdapter = new PaginationAdapter();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);

        activityCustomBinding.loadData.setLayoutManager(linearLayoutManager);
        activityCustomBinding.loadData.setAdapter(paginationAdapter);
    }

    private int pageNumber = 1, minimumViewable = 2;
    private boolean isLoading = false;

    private void recyclerListener() {
        activityCustomBinding.loadData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItem = linearLayoutManager.getItemCount();
                int lastItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && (totalItem <= (lastItemPosition + minimumViewable))) {
                    pageNumber++;
                    paginator.onNext(pageNumber);
                    isLoading = true;
                }
            }
        });
    }

    private CompositeDisposable compositeDisposable;
    private PublishProcessor<Integer> paginator;

    private void initData() {
        compositeDisposable = new CompositeDisposable();
        paginator = PublishProcessor.create();
    }

    private void subscribeForData() {

        Disposable disposable = paginator
                .onBackpressureDrop()
                .concatMap(new Function<Integer, Publisher<List<String>>>() {
                    @Override
                    public Publisher<List<String>> apply(@NonNull Integer page) throws Exception {
                        isLoading = true;
                        activityCustomBinding.progressBar.setVisibility(View.VISIBLE);
                        return dataFromNetwork(page);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(@NonNull List<String> items) throws Exception {
                        isLoading = false;
                        activityCustomBinding.progressBar.setVisibility(View.INVISIBLE);
                        paginationAdapter.setPropertyList(items);
                    }
                });

        compositeDisposable.add(disposable);

        paginator.onNext(pageNumber);
    }

    private Flowable<List<String>> dataFromNetwork(final int page) {
        return Flowable.just(true)
                .delay(1, TimeUnit.SECONDS)
                .map(new Function<Boolean, List<String>>() {
                    @Override
                    public List<String> apply(@NonNull Boolean value) throws Exception {
                        List<String> items = new ArrayList<>();
                        for (int i = 1; i <= 20; i++) {
                            items.add("Item " + (page * amountOfLoadData + i));
                        }
                        return items;
                    }
                });
    }
}
