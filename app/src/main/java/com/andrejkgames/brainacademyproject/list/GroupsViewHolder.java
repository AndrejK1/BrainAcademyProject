package com.andrejkgames.brainacademyproject.list;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrejkgames.brainacademyproject.R;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

public class GroupsViewHolder extends ParentViewHolder {
    private TextView mGroupView;
    private ImageButton mExpand;
    /**
     * Default constructor
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public GroupsViewHolder(@NonNull final View itemView) {
        super(itemView);
        mGroupView = itemView.findViewById(R.id.list_title);
        mExpand = itemView.findViewById(R.id.expand_button);

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded()) {
                    collapseView();
                } else {
                    expandView();
                }
                mExpand.animate()
                        .rotationBy(180)
                        .setDuration(100)
                        .start();
            }
        };

        mGroupView.setOnClickListener(click);
        mExpand.setOnClickListener(click);
    }

    public void bind(Groups groups) {
        mGroupView.setText(groups.getGroupName());
    }
}
