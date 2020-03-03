package com.xm.letterindex;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.xm.demo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    public static int ITEM_TYPE_HEADER = 0;
    public static int ITEM_TYPE_CONTENT = 1;
    private RecyclerView recycler;
    private LetterIndexAdapter adapter;
    private LetterIndexView vLetterIndex;
    private View vLoading;

    private Map<String, Integer> indexMap = new HashMap<>();

    private List<LetterIndexModel> starList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vLoading = findViewById(R.id.vLoading);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LetterIndexAdapter();
        recycler.setAdapter(adapter);

        vLetterIndex = findViewById(R.id.vLetterIndex);
        vLetterIndex.setOnStateChangeListener(new LetterIndexView.OnStateChangeListener() {
            @Override
            public void onStateChange(int eventAction, int position, String letter, int itemCenterY) {
                Integer pos = indexMap.get(letter);
                if (pos != null) {
                    recycler.scrollToPosition(pos);
                    LinearLayoutManager mLayoutManager = (LinearLayoutManager) recycler.getLayoutManager();
                    mLayoutManager.scrollToPositionWithOffset(pos, 0);
                }
            }
        });
        vLetterIndex.addLetter(0, "#");

        loadData();
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<LetterIndexModel> list = new ArrayList<>();
                for (int i = 0; i < 500; i++) {
                    LetterIndexModel model = new LetterIndexModel();
                    model.name = NameUtils.randomName(true, 3);
                    model.pinyin = Utils.chineneToSpell(model.name);
                    model.itemType = ITEM_TYPE_CONTENT;
                    model.image = ImageUtils.getImageUrl();
                    list.add(model);
                }

                List<String> letterList = new ArrayList<>();
                List<LetterIndexModel> tmpList = new ArrayList<>();
                for (LetterIndexModel model : list) {
                    String letter = model.pinyin.substring(0, 1).toUpperCase();
                    if (!letterList.contains(letter)) {
                        LetterIndexModel newModel = new LetterIndexModel();
                        newModel.name = letter;
                        newModel.pinyin = letter.toLowerCase();
                        newModel.itemType = ITEM_TYPE_HEADER;
                        tmpList.add(newModel);
                        letterList.add(letter);
                    }
                }
                list.addAll(tmpList);

                Collections.sort(list, new ContactsPinyinComparator());

                loadStarData();

                for (int i = 0; i < list.size(); i++) {
                    LetterIndexModel model = list.get(i);
                    if (model.itemType == ITEM_TYPE_HEADER)
                        indexMap.put(model.name, i + starList.size());
                }

                list.addAll(0, starList);

                setNewData(list);
            }
        }).start();
    }

    private void loadStarData() {
        for (int i = 0; i < 8; i++) {
            LetterIndexModel model = new LetterIndexModel();
            if (i == 0) {
                model.name = "#";
                model.itemType = ITEM_TYPE_HEADER;
            } else {
                model.name = NameUtils.randomName(true, 3);
                model.pinyin = Utils.chineneToSpell(model.name);
                model.itemType = ITEM_TYPE_CONTENT;
                model.image = ImageUtils.getImageUrl();
            }
            starList.add(model);
        }
        indexMap.put("#", 0);
    }

    private void setNewData(final List<LetterIndexModel> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vLoading.setVisibility(View.GONE);
                adapter.setNewData(list);
                vLetterIndex.setVisibility(View.VISIBLE);
            }
        });
    }

    class ContactsPinyinComparator implements Comparator<LetterIndexModel> {
        public int compare(LetterIndexModel o1, LetterIndexModel o2) {
            if (TextUtils.isEmpty(o1.pinyin) || TextUtils.isEmpty(o2.pinyin)) {
                return 0;
            }
            return o1.pinyin.compareTo(o2.pinyin);
        }
    }

}
