package com.andrejkgames.brainacademyproject;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import java.util.Objects;

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

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // my db
        myDb = Room.databaseBuilder(getApplicationContext(), RoomDb.class, "globalDatabase").build();

        // recycler
        recycler = findViewById(R.id.recycler_bought_list);
        mAdapter =  new GroupsAdapter(this,  new ArrayList<Groups>(), this);

        mainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case 0: // полное обновление адаптера
                        List<Groups> groups = (ArrayList<Groups>)msg.obj;
                        mAdapter.setParentList(groups, false);
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
                        if (savedInstanceState != null && recycler.getLayoutManager() != null) {
                            ((LinearLayoutManager) recycler.getLayoutManager()).scrollToPositionWithOffset(
                                    savedInstanceState.getInt(MainActivity.SCROLL_KEY, 0),0);
                        }
                        break;
                    case 1: // delete all from adapter
                        mAdapter.notifyParentRangeRemoved(0, mAdapter.getParentList().size());
                        break;
                    case 2: // delete product
                        mAdapter.notifyChildRemoved(msg.arg1, msg.arg2);
                        mAdapter.getParentList().get(msg.arg1).removeProduct(msg.arg2);
                        if (mAdapter.getParentList().get(msg.arg1).getChildList().size() == 0) {
                            mAdapter.notifyParentDataSetChanged(true);
                            mAdapter.notifyParentRemoved(msg.arg1);
                            mAdapter.getParentList().remove(msg.arg1);
                        }
                        mAdapter.notifyParentDataSetChanged(true);
                        recycler.setAdapter(mAdapter);
                        break;
                }

            }
        };

        FloatingActionButton fab = findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // запрос на удаление
                new AlertDialog.Builder(BoughtItemsActivity.this)
                        .setTitle(R.string.delete_all_question)
                        .setMessage(R.string.delete_all_description)
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myDb.getTableDAO().deleteBought(); // удаление купленных из бд
                                        mainHandler.sendEmptyMessage(1);
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

        updateRecycler();
    }

    @Override
    public void updateRecycler() { // полное обновление из бд
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Groups> groups = new ArrayList<>();
                // load data from database
                next: for (Product product : myDb.getTableDAO().getBoughtItems()) {
                    for (Groups group : groups) {
                        if (product.getGroupName().equals(group.getGroupName())) {
                            group.addProduct(product);
                            continue next;
                        }
                    }
                    groups.add(new Groups(product.getGroupName(), product));
                }
                // send data
                mainHandler.obtainMessage(0, 0, 0, groups).sendToTarget();
            }
        }).start();
    }

    @Override
    public void setProductStatus(final ProductViewHolder pvh) { // отменить покупку
        new Thread(new Runnable() {
            @Override
            public void run() {
                myDb.getTableDAO().updateItem(pvh.getProduct());
                mainHandler.obtainMessage(2, pvh.getParentAdapterPosition(),
                        pvh.getChildAdapterPosition()).sendToTarget();
            }
        }).start();
    }

    @Override
    public void deleteProduct(final ProductViewHolder pvh) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myDb.getTableDAO().deleteItem(pvh.getProduct());
                mainHandler.obtainMessage(2, pvh.getParentAdapterPosition(),
                        pvh.getChildAdapterPosition()).sendToTarget();
            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (recycler.getLayoutManager() != null) {
            savedInstanceState.putInt(MainActivity.SCROLL_KEY,
                    ((LinearLayoutManager)recycler.getLayoutManager()).findFirstVisibleItemPosition());
        }
        mAdapter.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//создаем меню .inflate передаем меню
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // кнопка-стрелка
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // очистить стак активностей
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
