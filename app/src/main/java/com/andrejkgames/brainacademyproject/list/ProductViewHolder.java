package com.andrejkgames.brainacademyproject.list;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andrejkgames.brainacademyproject.R;
import com.andrejkgames.brainacademyproject.database.Product;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductViewHolder extends ChildViewHolder {
    private CardView mProductView;
    private TextView mTextView;
    private TextView mTextDateBought;
    private CheckBox mSetBought;
    private ImageButton mDeleteButton;
    private Product mProduct;
    private Updatable activity;
    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public ProductViewHolder(@NonNull View itemView, Updatable activity) {
        super(itemView);
        mProductView = itemView.findViewById(R.id.list_element);
        mTextView = mProductView.findViewById(R.id.element_title);
        mTextDateBought = mProductView.findViewById(R.id.element_date_bought);
        mSetBought = mProductView.findViewById(R.id.set_element_bought);
        mDeleteButton = mProductView.findViewById(R.id.button_element_delete);
        this.activity = activity;
    }

    public void bind(final Product product) {
        mProduct = product;
        mTextView.setText(product.getTitle());
        mSetBought.setChecked(product.isBought());
        if (!product.isBought()) {
            mDeleteButton.setVisibility(View.GONE);
            mTextDateBought.setVisibility(View.GONE);
        } else {
            mTextDateBought.setText(product.getDateBought());
        }

        mProductView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                View addView = LayoutInflater.from(v.getContext()).inflate(R.layout.show_product, null);

                ((TextView)addView.findViewById(R.id.show_title)).setText(v.getContext().getResources().getText(R.string.text_show_title) + " " + mProduct.getTitle());
                ((TextView)addView.findViewById(R.id.show_group)).setText(v.getContext().getResources().getText(R.string.text_show_group) + " " + mProduct.getGroupName());
                ((TextView)addView.findViewById(R.id.show_price)).setText(v.getContext().getResources().getText(R.string.text_show_price) + " " + String.valueOf(mProduct.getPrice()));
                ((TextView)addView.findViewById(R.id.show_description)).setText(v.getContext().getResources().getText(R.string.text_show_description) + " " + mProduct.getDescription());
                ((TextView)addView.findViewById(R.id.show_date_added)).setText(v.getContext().getResources().getText(R.string.text_show_date_added) + " " + mProduct.getDateAdded());
                ((CheckBox)addView.findViewById(R.id.check_status)).setChecked(mProduct.isBought());
                if (mProduct.isBought())
                    ((TextView) addView.findViewById(R.id.show_date_bought)).setText(v.getContext().getResources().getText(R.string.text_show_date_bought) + " " + mProduct.getDateBought());
                 else
                    addView.findViewById(R.id.show_date_bought).setVisibility(View.GONE);

                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.text_show_element)
                        .setView(addView)
                        .create().show();
            }
        });

        mSetBought.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                product.setBought(isChecked);
                product.setDateBought( new SimpleDateFormat().format(new Date()) );
                activity.setProductStatus(ProductViewHolder.this);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteProduct(ProductViewHolder.this);
            }
        });
    }

    public Product getProduct() {
        return mProduct;
    }

    public interface Updatable {
        void updateRecycler();
        void setProductStatus(ProductViewHolder pvh);
        void deleteProduct(ProductViewHolder pvh);
    }
}
