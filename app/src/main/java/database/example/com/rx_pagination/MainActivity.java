package database.example.com.rx_pagination;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.baoyz.widget.PullRefreshLayout;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import database.example.com.rx_pagination.databinding.ActivityMainBinding;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);

        initRecycler();
        initRefresher();
    }

    private void initRecycler() {
        PaginationAdapter paginationAdapter = new PaginationAdapter();
        activityMainBinding.loadData.setLayoutManager(new LinearLayoutManager(this));
        paginationAdapter.setPropertyList(getAllData());
        activityMainBinding.loadData.setAdapter(paginationAdapter);
    }

    private void initRefresher() {
        activityMainBinding.swipeRefreshLayout.
                setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                subscribeForData();

                activityMainBinding.swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activityMainBinding.swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private boolean loading = false;
    private int pageNumber = 1;
    private final int VISIBLE_THRESHOLD = 1;
    private int lastVisibleItem, totalItemCount;

    private List<String> getAllData() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add("Item " + i);
        }
        return data;
    }

    private void subscribeForData() {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        PublishProcessor<Integer> paginator = PublishProcessor.create();

        Disposable disposable = paginator
                .onBackpressureDrop()
                .concatMap(new Function<Integer, Publisher<List<String>>>() {
                    @Override
                    public Publisher<List<String>> apply(@NonNull Integer page) throws Exception {
                        return dataFromNetwork(page);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(@NonNull List<String> items) throws Exception {
                        PaginationAdapter paginationAdapter = new PaginationAdapter();
                        paginationAdapter.setPropertyList(items);
                    }
                });

        compositeDisposable.add(disposable);

        paginator.onNext(1);
    }

    private Flowable<List<String>> dataFromNetwork(final int page) {
        return Flowable.just(true)
                .delay(1, TimeUnit.SECONDS)
                .map(new Function<Boolean, List<String>>() {
                    @Override
                    public List<String> apply(@NonNull Boolean value) throws Exception {
                        List<String> items = new ArrayList<>();
                        for (int i = 1; i <= 10; i++) {
                            items.add("Item " + (page * 10 + i));
                        }
                        return items;
                    }
                });
    }

//    private void setUpLoadMoreListener() {
//        activityMainBinding.loadData.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                totalItemCount = layoutManager.getItemCount();
//                lastVisibleItem = layoutManager
//                        .findLastVisibleItemPosition();
//                if (!loading
//                        && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
//                    pageNumber++;
//                    paginator.onNext(pageNumber);
//                    loading = true;
//                }
//            }
//        });
//    }
}
