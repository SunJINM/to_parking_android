package sun.project.toparking.activity;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.LocationClient;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.RoutePlanSearch;

import java.util.List;
import java.util.Map;

import sun.project.toparking.R;
import sun.project.toparking.activity.baidu.adapter.PoiAdapter;
import sun.project.toparking.util.LocationUtil;

public class SoundParkingActivity extends AppCompatActivity {

    private  RoutePlanSearch mRoutePlanS=null;
    private PoiSearch mPoiSearch = null;
    private String city;
    private PoiInfo mDestat=null;
    String keyWord = "停车场";
    private LatLng latLng;


    public RoutePlanSearch routePlanSear() {
        return mRoutePlanS;
    }

    public void setDestat(PoiInfo poi){mDestat=poi;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_parking);

        LocationClient.setAgreePrivacy(true);

        LocationUtil locationUtil = new LocationUtil();
        Address address = locationUtil.getALocation(SoundParkingActivity.this);
        city = address.getLocality();
        latLng = new LatLng(address.getLatitude(), address.getLongitude());
        System.out.println(city);


        //创建poi检索实例
        mPoiSearch = PoiSearch.newInstance();

        //设置监听器
        mPoiSearch.setOnGetPoiSearchResultListener(poiSearchListener);

        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(latLng)
                .radius(1000)
                .keyword(keyWord)
                .pageNum(0));
    }

    //创建poi检索监听器
    OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            //显示搜索结果
            List<PoiInfo> poiList = poiResult.getAllPoi();
            PoiAdapter adapter = new PoiAdapter(SoundParkingActivity.this, R.layout.sount_parking_list, poiList);
            ListView listView = findViewById(R.id.list_carPark);
            listView.setAdapter(adapter);
            listView.setVisibility(View.VISIBLE);

            //当滑动到底部时加载更多搜索结果
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        // 判断是否滚动到底部
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            //加载更多
                            int curPage = poiResult.getCurrentPageNum();
                            int totalPage = poiResult.getTotalPageNum();
                            if (curPage < totalPage) {
                                poiResult.setCurrentPageNum(curPage + 1);


                                mPoiSearch.searchNearby(new PoiNearbySearchOption()
                                        .location(latLng)
                                        .radius(1000)
                                        .keyword(keyWord)
                                        .pageNum(curPage + 1)
                                        .pageCapacity(30));
                            }
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                }
            });

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };




}