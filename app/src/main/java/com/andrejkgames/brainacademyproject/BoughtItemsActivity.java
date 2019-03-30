package com.andrejkgames.brainacademyproject;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.andrejkgames.brainacademyproject.database.Product;
import com.andrejkgames.brainacademyproject.database.RoomDb;
import com.andrejkgames.brainacademyproject.list.Groups;
import com.andrejkgames.brainacademyproject.list.GroupsAdapter;
import com.andrejkgames.brainacademyproject.list.ProductViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class BoughtItemsActivity extends AppCompatActivity implements ProductViewHolder.Updatable {

    RecyclerView recycler;
    GroupsAdapter mAdapter;
    RoomDb myDb;
    Handler mainHandler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bought_items);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // my db
        myDb = Room.databaseBuilder(getApplicationContext(), RoomDb.class, "globalDatabase").build();

        // recycler
        recycler = findViewById(R.id.recycler_bought_list);
        mAdapter =  new GroupsAdapter(this,  new ArrayList<Groups>(), this);

        FloatingActionButton fab = findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(BoughtItemsActivity.this)
                        .setTitle(R.string.delete_all_question)
                        .setMessage(R.string.delete_all_description)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myDb.getTableDAO().deleteBought();
                                        updateRecycler();
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create().show();
            }
        });

        mainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                List<Groups> groups = msg.getData().getParcelableArrayList(MainActivity.GET_GROUPS);
                if (groups == null) {
                    groups = new ArrayList<>();
                }
                mAdapter = new GroupsAdapter(BoughtItemsActivity.this, groups, BoughtItemsActivity.this);
                mAdapter.onRestoreInstanceState(savedInstanceState);
                mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
                    @Override
                    public void onParentExpanded(int parentPosition) {
                        mAdapter.collapseAllParents();
                        mAdapter.expandParent(parentPosition);
                    }

                    @Override
                    public void onParentCollapsed(int parentPosition) {

                    }
                });
                recycler.setAdapter(mAdapter);
                recycler.setLayoutManager(new LinearLayoutManager(BoughtItemsActivity.this));
            }
        };
        updateRecycler();
    }

    @Override
    public void updateRecycler() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Groups> groups = new ArrayList<>();

                next: for (Product product : myDb.getTableDAO().getBoughtItems()) {
                    for (Groups group : groups) {
                        if (product.getGroupName().equals(group.getGroupName())) {
                            group.addProduct(product);
                            continue next;
                        }
                    }
                    groups.add(new Groups(product.getGroupName(), product));
                }

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(MainActivity.GET_GROUPS, new ArrayList<>(groups));
                message.setData(bundle);
                mainHandler.sendMessage(message);
            }
        }).start();
    }

    @Override
    public void updateProduct(final Product product) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myDb.getTableDAO().updateItem(product);
                updateRecycler();
            }
        }).start();
    }

    @Override
    public void deleteProduct(final Product product) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myDb.getTableDAO().deleteItem(product);
                updateRecycler();
            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        saveAdapter(savedInstanceState);
    }

    private void saveAdapter(Bundle savedInstanceState){
        mAdapter.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//создаем меню .inflate передаем меню
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//отрабатывае каждый раз когда мы выбираем один из пунктов меню
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
