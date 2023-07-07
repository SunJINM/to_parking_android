package sun.project.toparking.activity.baidu.adapter;
 
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
 
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.List;

import sun.project.toparking.R;
import sun.project.toparking.activity.SoundParkingActivity;
import sun.project.toparking.activity.baidu.DrivingRouteActivity;
import sun.project.toparking.util.LocationUtil;

public class PoiAdapter extends ArrayAdapter<PoiInfo> {
    private int resourceId;
    LocationUtil locationUtil = new LocationUtil();

    public PoiAdapter(@NonNull Context context, int resource, @NonNull List<PoiInfo> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }
 
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PoiInfo poi=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView name=view.findViewById(R.id.carParkName);
        TextView address=view.findViewById(R.id.address);
        name.setText(poi.name);
        address.setText(poi.address);
        Button goBtn=view.findViewById(R.id.daoHang);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* SoundParkingActivity soundParkingActivity=(SoundParkingActivity) getContext();
                soundParkingActivity.setDestat(poi);
                LocationUtil locationUtil1 = new LocationUtil();
                Address address1 = locationUtil1.getALocation(soundParkingActivity);*/
                //BDLocation curLocation=drivingRouteActivity.curLocation();



                /*RoutePlanSearch routePlanSearch=soundParkingActivity.routePlanSear();
                routePlanSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(stNode)
                        .to(enNode));*/
                LatLng enLatLng = poi.location;
                String parkName = poi.name;
                Intent intent = new Intent(parent.getContext(), DrivingRouteActivity.class);
                intent.putExtra("enLatLng", enLatLng);
                intent.putExtra("parkName", parkName);
                parent.getContext().startActivity(intent);

            }
        });
        return view;
    }
}