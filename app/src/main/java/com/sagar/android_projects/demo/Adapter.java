package com.sagar.android_projects.demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sagar on 10/23/2017.
 * this is the adapter class for the recyclerview in main activity.
 * as this is being used for paging its structure is little different from what we generally use in
 * adapter.
 * this has 2 view holder class. 1. Item 2. Loading view
 * there are 2 constants for identifying the view type. those are ITEM and PROGRESS.
 * when the adapter class start it will do the following -
 * 1. get the item count.
 * 2. get the view type.
 * 3. pass the view type to the onCreateViewHolder and inflate the required viewHolder.
 * 4. send view to onBindViewHolder and bind data.
 * the logic for each function is explained above the respective method.
 */
class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /*
    data set from the calling activity to set those data to the recyclerview.
     */
    private ArrayList<String> data;
    /*
    boolean variable to keep track of if there is any data left in the server of any data source.
    it will help to decide if we have send data request again or not.
    and if we should show the progress view or not.
    if this variable is false then there is some more data in source. and we have to keep requesting
    for the data, and keep showing the progress view after data set ends in recyclerview.
     */
    private boolean noMoreDataAvailable;

    /*
    constants to identify if a variable is item or progress.
    this is used by the method getItemViewType(int position).
    this method will decide if a position should contain a item view or a progress view.
    and return a view type accordingly.
    this method is internally called by onCreateViewHolder() to decide which view to inflate for the
    next item in the list.
     */
    private static final int ITEM = 34;
    private static final int PROGRESS = 43;

    /**
     * default constructor for the adapter. this takes the dataset for the recyclerview and set it to
     * the local variable.
     *
     * @param data data set for the recyclerview
     */
    Adapter(ArrayList<String> data) {
        this.data = data;
    }

    /**
     * method to create the views for the recyclerview.
     * this method will call the getItemViewType() to get the view to be inflated, as this adapter
     * has two views.
     * after getting the view type this the respective viewholder will be generated and returned
     * for data binding.
     *
     * @param parent   parent
     * @param viewType view type (item or progress)
     * @return inflated view holder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM:
                return new ViewHolderItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.singleview, parent, false));
            case PROGRESS:
                return new ViewHolderProgress(LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false));
        }
        return null;
    }

    /**
     * after the onCreateViewHolder is finished with the view inflation the view holder is passed to
     * onBindViewHolder for data biding.
     * this method will check what is the type of holder is has received in params.
     * and according to the type of holder is has got it will do the view binding.
     * it will perform the holder checking with the help of instanceof keyword.
     *
     * @param holder   holder to bind data
     * @param position position for which data is to be bind
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderItem) {
            ((ViewHolderItem) holder).textView.setText(data.get(position));
        } else if (holder instanceof ViewHolderProgress) {
            //bind the data for progress view after type casting to viewHolderProgress
            //((ViewHolderProgress) holder).progress.setMax(100);
        }
    }

    /**
     * this method is responsible for deciding what type of viewholder to be inflated for which
     * position.
     * in this case if the length of the dataset is greater then the position then there is a data
     * available for binding. so this the view type will be ITEM.
     * and if the length is dataset is not greater then the position then there is no data available
     * for that position. therefore the view holder type will be progress.
     *
     * @param position position for which viewholder to be generated
     * @return viewholder type to be inflated for the position passed
     */
    @Override
    public int getItemViewType(int position) {
        if (position < data.size())
            return ITEM;
        return PROGRESS;
    }

    /**
     * this is the method to return the length of dataset in the recyclerview.
     * in this case if there are 20 data then this will return 20. but there is a twist.
     * if there are more data in server then the length of dataset will be +1. for showing the progress
     * view.
     * this is decided by the variable noMoreDataAvailable. if this variable is false then we have
     * more data in the server. and the count will be dataset length +1 (for progress).
     * and if the variable noMoreDataAvailable is true then there is no more data available in the
     * server. so there is no need to show any progress view. hence the count will the dataset size.
     *
     * @return size if items to be inflated for the recyclerview
     */
    @Override
    public int getItemCount() {
        if (noMoreDataAvailable)
            return data.size();
        return data.size() + 1;
    }

    /**
     * this is the view holder for the item. this will contain the actual data to be shown in
     * recyclerview.
     */
    private class ViewHolderItem extends RecyclerView.ViewHolder {

        TextView textView;

        ViewHolderItem(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textview_item);
        }
    }

    /**
     * this is the viewholder for progress. this will be inflated for showing the progress for data
     * loading at the end of list.
     */
    private class ViewHolderProgress extends RecyclerView.ViewHolder {

        ViewHolderProgress(View itemView) {
            super(itemView);
        }
    }

    /**
     * method to set the noMoreDataAvailable variable value from the calling activity.
     * as the server call is being done from the activity. the activity is well informed about the
     * availability of the data in the server. if there is no more data present in the server then
     * we dont have to show the progress view at the end of list. and for that we need to set the
     * required value to the noMoreDataAvailable.
     *
     * @param noMoreDataAvailable set to true of no data available
     */
    void setNoMoreDataAvailable(boolean noMoreDataAvailable) {
        this.noMoreDataAvailable = noMoreDataAvailable;
    }
}
