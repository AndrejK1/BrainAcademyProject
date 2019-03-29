package com.andrejkgames.brainacademyproject.list;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.andrejkgames.brainacademyproject.R;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

public class GroupsViewHolder extends ParentViewHolder {
    private TextView mGroupView;
    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public GroupsViewHolder(@NonNull View itemView) {
        super(itemView);
        mGroupView = itemView.findViewById(R.id.list_title);
    }

    public void bind(Groups groups) {
        mGroupView.setText(groups.getGroupName());
    }
}
