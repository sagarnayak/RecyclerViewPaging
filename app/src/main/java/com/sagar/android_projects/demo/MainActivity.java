package com.sagar.android_projects.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;

/**
 * Created by sagar on 10/23/2017.
 * this is the activity to show the paging functionality in recyclerview android.
 * this activity has a simple recyclerview in the layout.
 * it will set data to the recyclerview at start and at every end of dataset. and dummy server call
 * will happen. after this the extra data will be set the recyclerview.
 * all the variable and method explanation are given at the respective places.
 */
public class MainActivity extends AppCompatActivity {

    /*
    recyclerview to bind show data.
     */
    RecyclerView recyclerView;

    /*
    data set for the adapter.
     */
    ArrayList<String> data;
    /*
    linearLayoutManager for the adapter.
     */
    LinearLayoutManager linearLayoutManager;
    /*
    adapter for the recyclerview.
     */
    Adapter adapter;

    private static final String TAG = "tag_for_recyclerview";

    /*
    boolean to keep track if server call is being done currently.
    if the server data call is currently in progress this value will be true. and this will stop
    any further server call until the current one ends.
     */
    boolean isLoading;
    /*
    boolean to keep track if the server dataset is ended. if there are no more data to show from the
    server of the source of data this will be set to true. and no further server call will be done.
     */
    boolean isLastPage;

    /*
    this is the constant for number of items form the server at a single server call. if the server
    call is done and the number if items is less then this then it is assumed that there is no more
    data available in the server, and this will stop any further server call for data.
    this can be a constant which can be hardcoded.
    or this can be dynamic by setting the value after the initial server call.
     */
    private static final int DATA_PAGE_SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //view binding
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        ////////////////////////////////////////////////////////////////////////////////////////////

        /*
        set the layout manager.
         */
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        /*
        initialise the dataset and set to adapter.
         */
        data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add("data : " + (i + 1));
        }
        adapter = new Adapter(data);
        recyclerView.setAdapter(adapter);

        /*
        set on scroll listener for the recyclerview.
        in the onScrolled() method we will perform the required logic for the paging.
        LOGIC- (LOADING MORE DATA)
        --------------------------------------------------------------------------------------------
        we perform the server call for more data once the last item of the dataset is visible on the
        screen. this also means that the user has finished scrolling to the end of the list and new
        we have to get more data, if available, to display.
        so how to know if the last item in the dataset is showing or not.
        this can be done by adding the total item that are visible currently on the screen and the
        first visible item on the screen.
        if the addition result is greater then the total item currently present in the dataset.
        then the last item is showing on the screen and we need to proceed.
        lets take a example.
        lets say that the total item that can be shown on the screen at a time is 5.
        and the current first visible item is 1.
        the dataset contains 20 items.
        so what is the last visible item? its 5 + 1 = 6.
        is this the last item ? no. because the last item is in index 19. we do this checking by
        5 + 1 >= 20. as long as this is false the last item is not being shown.
        so lets say user is scrolling and now the first visible item position is 15.
        check your condition. 5 + 15 >= 20. yes not its 20 >= 20. this is true and last item is visible.
        now we need to call the server for more data.
        set the isLoading to true. this will stop any further server call until the current one is
        finished.
        once we get the data from server set the isLoading again to false.
        --------------------------------------------------------------------------------------------
         */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        Log.i(TAG, "onScrolled: " + visibleItemCount + ":" + totalItemCount + ":" + firstVisibleItemPosition);
                        getDataAndSet(linearLayoutManager.getItemCount() - 1);
                    }
                }
            }
        });
    }

    /**
     * method to get the data from server and set to recyclerview.
     * this method will not do any server call. rather it will do a dummy wait for 4 seconds and set
     * a new dataset to the recyclerview.
     * in real app we will first set the isLoading to true.
     * then do the server call.
     * get the data from server.
     * check if this fetched data length is less then the DATA_PAGE_SIZE. if yes then there is no more
     * data in the server side. so call the reachedToEnd(). this will set the isLastPage to true. and
     * also the noMoreDataAvailable to the adapter.
     * after that set append the new data to dataset and notify the adapter.
     * and at last set the isLoading to false.
     * @param dataSetStartingPosition the position from which the data is to be fetched.
     */
    private void getDataAndSet(final int dataSetStartingPosition) {
        isLoading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = dataSetStartingPosition; i < dataSetStartingPosition + DATA_PAGE_SIZE; i++) {
                                data.add("data : " + (i + 1));
                            }
                            /*if (dataFromServer.size < DATA_PAGE_SIZE){
                                reachedToEndOfData();
                                }*/
                            adapter.notifyDataSetChanged();
                            isLoading = false;
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * method to take the required action when there is no more data available in the server or the
     * data source.
     * it will set the noMoreDataAvailable to true in adapter so that no more progress view will be shown
     * to user.
     * it will set the isLastPage to true so that no more server call for data will be done.
     */
    private void reachedToEndOfData() {
        adapter.setNoMoreDataAvailable(true);
        isLastPage = true;
    }
}
