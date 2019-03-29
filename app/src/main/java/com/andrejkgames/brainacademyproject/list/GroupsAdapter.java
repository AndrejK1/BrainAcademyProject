package com.andrejkgames.brainacademyproject.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrejkgames.brainacademyproject.R;
import com.andrejkgames.brainacademyproject.database.Product;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

public class GroupsAdapter extends ExpandableRecyclerAdapter<Groups, Product, GroupsViewHolder, ProductViewHolder> {

    private LayoutInflater mInflater;
    private ProductViewHolder.Updatable activity;

    /**
     * Primary constructor. Sets up {@link #mParentList} and {@link #mFlatItemList}.
     * <p>
     * Any changes to {@link #mParentList} should be made on the original instance, and notified via
     * {@link #notifyParentInserted(int)}
     * {@link #notifyParentRemoved(int)}
     * {@link #notifyParentChanged(int)}
     * {@link #notifyParentRangeInserted(int, int)}
     * {@link #notifyChildInserted(int, int)}
     * {@link #notifyChildRemoved(int, int)}
     * {@link #notifyChildChanged(int, int)}
     * methods and not the notify methods of RecyclerView.Adapter.
     *
     * @param parentList List of all parents to be displayed in the RecyclerView that this
     *                   adapter is linked to
     */
    public GroupsAdapter(Context context, @NonNull List<Groups> parentList, ProductViewHolder.Updatable activity) {
        super(parentList);
        mInflater = LayoutInflater.from(context);
        this.activity = activity;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View groupView = mInflater.inflate(R.layout.list_view, parentViewGroup, false);
        return new GroupsViewHolder(groupView);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View productView = mInflater.inflate(R.layout.element_view, childViewGroup, false);
        return new ProductViewHolder(productView, activity);
    }

    @Override
    public void onBindParentViewHolder(@NonNull GroupsViewHolder parentViewHolder, int parentPosition, @NonNull Groups parent) {
        parentViewHolder.bind(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull ProductViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Product child) {
        childViewHolder.bind(child);
    }

}
