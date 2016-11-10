package com.example.audacia.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jan on 2016. 10. 30..
 */

public class TravelListView extends Activity {

    private ListView listView;
    private EditText inputText;
    private Button inputBtn;

    private ArrayList<HomeItem> homeItems = new ArrayList<HomeItem>();
    private HomeAdapter homeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        homeItems.add(new HomeItem("San Francisco, US"));
        homeItems.add(new HomeItem("Seoul, Korea"));
        homeItems.add(new HomeItem("New York, US"));
        homeItems.add(new HomeItem("London, UK"));
        homeItems.add(new HomeItem("Tyoko, Japan"));
        homeItems.add(new HomeItem("Hong Kong, China"));
        homeItems.add(new HomeItem("Massachusetts, US"));

        listView = (ListView) findViewById(R.id.listView);
        inputText = (EditText) findViewById(R.id.inputText);
        inputBtn = (Button) findViewById(R.id.inputBtn);
        homeAdapter = new HomeAdapter(this, R.layout.home_item, homeItems);
        listView.setAdapter(homeAdapter);

        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.inputText에 있는 텍스트 받아오기
                //2.받아온 텍스트를 ArrayList에 추가하기
                //3.ArrayList에 추가된 내용을 새로고침하기
                //4.inputText에 있는 내용 모두 지우기
                String str = inputText.getText().toString();
                homeItems.add(new HomeItem(str));
                homeAdapter.notifyDataSetChanged();
                inputText.setText("");
            }
        });
    }

    class HomeAdapter extends ArrayAdapter<HomeItem> { //데이터를 원하는 방법으로 보여주는 어댑터 작성
        private ArrayList<HomeItem> items;

        public HomeAdapter(Context context, int textViewResourceId, ArrayList<HomeItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) { //화면을 보여준다
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.home_item, null);
            }
            HomeItem item = items.get(position);

            if (item != null) {//아이템이 있으면 아이템을 가져온다.
                int i = position;
                TextView item_text = (TextView) v.findViewById(R.id.item_text); //텍스트뷰 찾기
                item_text.setText(item.getItemText());
                ImageView item_img = (ImageView) v.findViewById(R.id.item_img); //이미지뷰 찾기
                switch (i % 3) { //순환하면서 색이 변하도록 조건문 작성
                    case 0:
                        item_img.setBackgroundColor(Color.GRAY);
                        break;
                    case 1:
                        item_img.setBackgroundColor(Color.LTGRAY);
                        break;
                    case 2:
                        item_img.setBackgroundColor(Color.BLACK);
                        break;
                }
            }
            return v;
        }
    }

    class HomeItem { //원하는 데이터를 담는 클래스 작성 이를 이용하여 ArrayList를 만든다.
        private String itemText;

        public HomeItem(String itemText) {
            this.itemText = itemText;
        }

        public String getItemText() {
            return itemText;
        }
    }

}