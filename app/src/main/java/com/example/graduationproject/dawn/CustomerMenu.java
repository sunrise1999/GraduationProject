package com.example.graduationproject.dawn;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.graduationproject.R;
import com.example.graduationproject.overlayutil.BikingRouteOverlay;
import com.example.graduationproject.overlayutil.DrivingRouteOverlay;
import com.example.graduationproject.overlayutil.OverlayManager;
import com.example.graduationproject.overlayutil.PoiOverlay;
import com.example.graduationproject.overlayutil.TransitRouteOverlay;
import com.example.graduationproject.overlayutil.WalkingRouteOverlay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * 进行骑行路线搜索并在地图使用RouteOverlay、TransitOverlay绘制
 */
public class CustomerMenu extends Activity implements OnMapClickListener, OnGetRoutePlanResultListener,
		OnClickListener, OnGetSuggestionResultListener, OnGetPoiSearchResultListener, OnItemClickListener {
	private static final int REQUEST_CODE_TAKE_PICTURE = 1;
	// 浏览路线节点相关
	private String localcity;// 记录当前城市
	Button mBtnPre = null; // 上一个节点
	Button mBtnNext = null; // 下一个节点
	int nodeIndex = -1; // 节点索引,供浏览节点时使用
	//private TextView locAltitude; // 海拔信息显示,已用城市信息包括
	private Button findroute;// 路线规划
	private Button findroute2;// 路线规划
	RouteLine route = null;
	OverlayManager routeOverlay = null;
	private Button requestLocButton;
	private LocationMode mCurrentMode;
	private ImageButton my_back;// 返回按钮
	private ImageButton open_camera;// 打开相机
	private LinearLayout edit_layout;// 底部目的地栏
	private LinearLayout choosemode;// 选择导航方式
	private ListView search_end;// 推荐目的地
	private LinearLayout guide_layout;
	private LinearLayout locationLayout;// 定位框
	BitmapDescriptor mCurrentMarker;
	boolean useDefaultIcon = false;
	private TextView popupText = null, customer_city; // 泡泡view
	private TextView mylocation;
	private EditText start_edit, end_edit;
	boolean isFirstLoc = true; // 是否首次定位

	// 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	// 地图控件
	private TextureMapView mMapView = null;
	private BaiduMap mBaidumap;

	// 搜索相关
	RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	// 搜索周边
	private LinearLayout poilayout;
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	private ImageButton customer_find_btn;

	/**
	 * 搜索关键字输入窗口
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index = 0;

	// 定位相关
	LocationClient mLocClient;
	BDLocation mLocation = new BDLocation();
	LatLng currentPt;
	public MyLocationListenner myListener = new MyLocationListenner();

	//方向传感器的监听器
	private MyOrientationListener myOrientationListener;
	 //方向传感器X方向的值
	private int mXDirection;

	// 点击地图事件
	private LinearLayout click_layout;
	private TextView endlocation;
	private Button go_end;
	private LatLng endPt;
	private GeoCoder geoCoder;

	// 动画效果
	Animation slide_in_above;
	Animation slide_in_bottom;
	Animation slide_out_above;
	Animation slide_out_bottom;
	// 交通图
	private ImageButton officient;
	private boolean flag = false;

	//文件相关
	File cameraFile;//文件路径
	String cameraFileName;//相片名称

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置标题栏不可用
		setContentView(R.layout.customer_menu);
		// 初始化控件
		initview();
		// 初始化地图
		inintmap();
		// 初始化传感器
		initOritationListener();

		mCurrentMode = LocationMode.COMPASS;
		requestLocButton.setText("罗");
		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
				case NORMAL:
					requestLocButton.setText("跟");
					mCurrentMode = LocationMode.FOLLOWING;
					mBaidumap.setMyLocationConfiguration(
							new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
					hideclickLayout(false);
					break;
				case COMPASS:
					requestLocButton.setText("普");
					mCurrentMode = LocationMode.NORMAL;
					mBaidumap.setMyLocationConfiguration(
							new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));

					locationLayout.startAnimation(slide_in_bottom);
					locationLayout.setVisibility(View.VISIBLE);
					findroute.setVisibility(View.GONE);
					hideclickLayout(false);
					break;
				case FOLLOWING:
					requestLocButton.setText("罗");
					mCurrentMode = LocationMode.COMPASS;
					mBaidumap.setMyLocationConfiguration(
							new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));

					locationLayout.startAnimation(slide_in_bottom);
					locationLayout.setVisibility(View.VISIBLE);
					findroute.setVisibility(View.GONE);
					hideclickLayout(false);
					break;
				default:
					break;
				}
			}
		};
		requestLocButton.setOnClickListener(btnClickListener);
		CharSequence titleLable = "路线规划功能";
		setTitle(titleLable);

		// 地图点击事件处理
		mBaidumap.setOnMapClickListener(this);
		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);
		// 点击地图获取点的坐标
		mBaidumap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapPoiClick(MapPoi arg0) {
				if (locationLayout.getVisibility() == View.VISIBLE) {
					locationLayout.setVisibility(View.GONE);
					locationLayout.startAnimation(slide_out_bottom);
				}
				hideclickLayout(true);
				findroute.setVisibility(View.GONE);
				end_edit.setText(arg0.getName());
				endlocation.setText(arg0.getName());
				endPt = arg0.getPosition();
				mBaidumap.clear();
				mydraw(arg0.getPosition(), R.mipmap.icon_en);
				return;
			}

			@Override
			public void onMapClick(LatLng Ll) {
				if (locationLayout.getVisibility() == View.VISIBLE) {
					locationLayout.setVisibility(View.GONE);
					locationLayout.startAnimation(slide_out_bottom);
				}
				findroute.setVisibility(View.GONE);
				hideclickLayout(true);
				endPt = Ll;
				mBaidumap.clear();
				mydraw(endPt, R.mipmap.icon_en);
				// 创建地理编码检索实例
				geoCoder = GeoCoder.newInstance();
				// 设置反地理经纬度坐标,请求位置时,需要一个经纬度
				geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(endPt));
				// 设置地址或经纬度反编译后的监听,这里有两个回调方法,
				geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
					@Override
					public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

						// addre = "地址："+reverseGeoCodeResult.getAddress();
						// Log.i(TAG, "onGetReverseGeoCodeResult: "+reverseGeoCodeResult.getAddress());
					}

					/**
					 *
					 * @param reverseGeoCodeResult
					 */
					@Override
					public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

						if (reverseGeoCodeResult == null
								|| reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {

						} else {
							end_edit.setText(reverseGeoCodeResult.getAddress());
							endlocation.setText(reverseGeoCodeResult.getAddress());
						}
					}
				});
			}
		});
	}

	// 地图初始化
	public void inintmap() {
		// 地图初始化
		mMapView = (TextureMapView) findViewById(R.id.mTexturemap);
		mBaidumap = mMapView.getMap();

		// 不显示缩放比例尺
		mMapView.showZoomControls(false);
		// 不显示百度地图Logo
		mMapView.removeViewAt(1);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true); // 打开gps
		//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
		option.setIsNeedAltitude(true);
		option.setNeedDeviceDirect(false);
		//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setLocationNotify(true);
		//设置是否需要过滤GPS仿真结果，默认需要，即参数为false
		option.setEnableSimulateGps(true);
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		mLocClient.setLocOption(option);
	}

	public void initview() {

		start_edit = (EditText) findViewById(R.id.start);
		end_edit = (EditText) findViewById(R.id.end);
		customer_city = (TextView) findViewById(R.id.customer_city);
		my_back = (ImageButton) findViewById(R.id.my_back);
		open_camera = (ImageButton) findViewById(R.id.camera);
		mylocation = (TextView) findViewById(R.id.mylocation);
		requestLocButton = (Button) findViewById(R.id.change);
		findroute = (Button) findViewById(R.id.findroute);
		findroute2 = (Button) findViewById(R.id.findroute2);
		guide_layout = (LinearLayout) findViewById(R.id.guide_layout);
		edit_layout = (LinearLayout) findViewById(R.id.edit_layout);
		search_end = (ListView) findViewById(R.id.search_end);
		locationLayout = (LinearLayout) findViewById(R.id.locationLayout);
		poilayout = (LinearLayout) findViewById(R.id.poilayout);
		choosemode = (LinearLayout) findViewById(R.id.choosemode);
		//已用城市信息包括
		//locAltitude = (TextView)findViewById(R.id.locAltitude);
		// 交通图
		officient = (ImageButton) findViewById(R.id.officient);
		officient.setOnClickListener(this);
		// 地图点击事件
		click_layout = (LinearLayout) findViewById(R.id.click_layout);
		endlocation = (TextView) findViewById(R.id.endlocation);

		my_back.setOnClickListener(this);
		open_camera.setOnClickListener(this);
		findroute.setOnClickListener(this);
		findroute2.setOnClickListener(this);

		/****************** 动画 ***************/
		slide_in_above = AnimationUtils.loadAnimation(this, R.anim.slide_in_above);// 显示
		slide_out_above = AnimationUtils.loadAnimation(this, R.anim.slide_out_above);// 消失
		slide_in_bottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);// 显示
		slide_out_bottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);// 消失

		// ListView中推荐地址选择
		search_end.setOnItemClickListener(this);// 推荐地址的监听

		// *************搜索周边******************
		customer_find_btn = (ImageButton) findViewById(R.id.customer_find_btn);
		customer_find_btn.setOnClickListener(this);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchpoi);
		sugAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		search_end.setAdapter(sugAdapter);
		keyWorldsView.setAdapter(sugAdapter);

		/**
		 * 当输入关键字变化时，动态更新建议列表
		 */
		keyWorldsView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				if (cs.length() <= 0) {
					return;
				}
				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				mSuggestionSearch
						.requestSuggestion((new SuggestionSearchOption()).keyword(cs.toString()).city(localcity));
			}
		});

		/**
		 * 目的地关键字变化时
		 * 
		 */
		end_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() <= 0) {
					return;
				}
				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				mSuggestionSearch
						.requestSuggestion((new SuggestionSearchOption()).keyword(s.toString()).city(localcity));

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 发起路线规划搜索示例
	 *
	 * @param v
	 */
	public void searchButtonProcess(View v) {
		// 选择导航方式
		TextView bike = (TextView) findViewById(R.id.go_bike);
		TextView walk = (TextView) findViewById(R.id.go_walk);
		// 重置浏览节点的路线数据
		route = null;
		mBaidumap.clear();
		// 设置起终点信息，对于tranist search 来说，城市名无意义
		PlanNode stNode = PlanNode.withCityNameAndPlaceName(localcity, start_edit.getText().toString());
		PlanNode enNode = PlanNode.withCityNameAndPlaceName(localcity, end_edit.getText().toString());

		// 实际使用中请对起点终点城市进行正确的设定
		switch (v.getId()) {
		case R.id.go_bike:
			bike.setSelected(true);
			walk.setSelected(false);
			mSearch.bikingSearch(new BikingRoutePlanOption().from(stNode).to(enNode));
			hideguide();
			break;
		case R.id.go_walk:
			bike.setSelected(false);
			walk.setSelected(true);
			mSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode));
			hideguide();
			break;
		case R.id.go_end:
			click_layout.setVisibility(View.GONE);
			bike.setSelected(true);
			walk.setSelected(false);
			PlanNode startPlanNode = PlanNode.withLocation(currentPt); // lat long
			PlanNode endPlanNode = PlanNode.withLocation(endPt);
			mSearch.bikingSearch(new BikingRoutePlanOption().from(startPlanNode).to(endPlanNode));
			hideall();
			showguide();
			search_end.setVisibility(View.GONE);
			edit_layout.setVisibility(View.GONE);
			choosemode.setVisibility(View.VISIBLE);
			break;
		}

	}

	/**
	 * 节点浏览示例
	 *
	 * @param savedInstanceState
	 */

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	// 步行
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			MyToast("抱歉，未找到结果");
			hideall();
			showguide();
			edit_layout.setVisibility(View.VISIBLE);
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			result.getSuggestAddrInfo();
			hideall();
			showguide();
			edit_layout.setVisibility(View.VISIBLE);
			
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			route = result.getRouteLines().get(0);
			WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaidumap);
			routeOverlay = overlay;
			mBaidumap.setOnMarkerClickListener(overlay);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	// 公交	未使用
	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
			hideall();
			showguide();
			edit_layout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

	}

	// 驾车 未使用
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			MyToast("抱歉，未找到结果");
			hideall();
			showguide();
			edit_layout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

	}

	// 骑行
	@Override
	public void onGetBikingRouteResult(BikingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			MyToast("抱歉，未找到结果");
			hideall();
			showguide();
			edit_layout.setVisibility(View.VISIBLE);
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			result.getSuggestAddrInfo();
			hideall();
			showguide();
			edit_layout.setVisibility(View.VISIBLE);
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			route = result.getRouteLines().get(0);
			BikingRouteOverlay overlay = new BikingRouteOverlay(mBaidumap);
			routeOverlay = overlay;
			mBaidumap.setOnMarkerClickListener(overlay);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	// 定制RouteOverly
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.mipmap.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.mipmap.icon_en);
			}
			return null;
		}
	}

	@Override
	public void onMapClick(LatLng point) {
		mBaidumap.hideInfoWindow();
	}

	@Override
	public void onMapPoiClick(MapPoi poi) {
		MyToast("当前位置海拔高度" + mLocation.getAltitude());
		return;
	}

	@Override
	protected void onStart() {
		mLocClient.start();// 启动sdk
		// 开启定位图层
		mBaidumap.setMyLocationEnabled(true);
		// 开启方向传感器
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// 关闭图层定位
		mBaidumap.setMyLocationEnabled(false);
		mLocClient.stop();

		// 关闭方向传感器
		myOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mSearch.destroy();
		mMapView.onDestroy();
		super.onDestroy();
	}

	/**
	 * 弹出信息框简化
	 * @param s 弹出信息
	 */
	public void MyToast(String s) {
		Toast.makeText(CustomerMenu.this, s, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner extends BDAbstractLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			String locationDescribe = location.getLocationDescribe(); // 获取位置描述信息
			String startLocation = locationDescribe.substring(1);
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaidumap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				currentPt = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(currentPt).zoom(17.5f);
				mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
				start_edit.setText(startLocation);
				MyToast("当前所在位置：" + locationDescribe);
				mLocation = location;
				//已用城市信息包括
				//locAltitude.setText("当前位置海拔高度" + mLocation.getAltitude());
				mylocation.setText(locationDescribe);
				localcity = location.getCity();
				customer_city.setText(location.getCity()+"，此地海拔高度" + mLocation.getAltitude());
				String mm = "customer " + "location " + location.getLatitude() + " " + location.getLongitude() + "\n";
			}
			StringBuilder currentPosition = new StringBuilder();
			currentPosition.append("定位方式：");
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				currentPosition.append("GPS");
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				currentPosition.append("网络");
			}
			MyToast(currentPosition.toString());
			//后发现不需要，百度带有
			//changeIconDir();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	/**
	 * 初始化方向传感器
	 */
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(
				getApplicationContext());
		myOrientationListener
				.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mXDirection = (int) x;

						// 构造定位数据
						MyLocationData locData = new MyLocationData.Builder()
								.accuracy(mLocation.getRadius())
								// 此处设置开发者获取到的方向信息，顺时针0-360
								.latitude(mLocation.getLatitude())
								.longitude(mLocation.getLongitude())
								.direction(mXDirection)
								.build();
						// 设置定位数据
						mBaidumap.setMyLocationData(locData);
					}
				});
	}

	/**
	 * 改变图标与方向
	 */
	protected void changeIconDir(){
		// 设置自定义图标
		MyLocationConfiguration emptyConfig = new MyLocationConfiguration(mCurrentMode, false, null);
		mBaidumap.setMyLocationConfiguration(emptyConfig);
		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.mipmap.navi_map_gps_locked);
		Bitmap newBitIcon = rotateBitmap(mCurrentMarker.getBitmap(), mXDirection);
		mCurrentMarker = BitmapDescriptorFactory.fromBitmap(newBitIcon);
		Log.i("mXDirection1", mXDirection + "");
		newBitIcon.recycle();
		MyLocationConfiguration config = new MyLocationConfiguration(
				mCurrentMode, false, mCurrentMarker);
		mBaidumap.setMyLocationConfiguration(config);
		mCurrentMarker.recycle();
		MyLocationConfiguration newConfig = mBaidumap.getLocationConfiguration();
		BitmapDescriptor newMarker = newConfig.customMarker;
	}

	/**
	 * 位图旋转
	 * @param bitmap 源位图
	 * @param degress 旋转角度
	 * @return 结果位图
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {

		if (bitmap != null) {

			Matrix m = new Matrix();

			m.postRotate(degress);

			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m,

					true);

			return bitmap;

		}

		return bitmap;

	}

	/**
	 * 影响搜索按钮点击事件 public void searchPoiProcess(View v) {
	 * 
	 * }
	 * 
	 * @param result
	 */

	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			MyToast("未找到结果");
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaidumap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaidumap);
			mBaidumap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			MyToast(strInfo);
		}
	}

	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			MyToast("抱歉，未找到结果");
		} else {
			endPt = result.getLocation();
			endlocation.setText(result.getName() + ": " + result.getAddress());
			
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

	}

	@Override
	public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
			if (locationLayout.getVisibility() == View.VISIBLE) {
				locationLayout.setVisibility(View.GONE);
				locationLayout.startAnimation(slide_out_bottom);
			}
			findroute.setVisibility(View.GONE);
			hideclickLayout(true);
			// }
			return true;
		}
	}

	// ----------------------------------------------------------
	public void mydraw(LatLng location, int a) {
		// 定义Maker坐标点 LatLng location

		// 构建Marker图标

		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(a);

		// 构建MarkerOption，用于在地图上添加Marker

		OverlayOptions option = new MarkerOptions().position(location).icon(bitmap);

		// 在地图上添加Marker，并显示
		mBaidumap.addOverlay(option);
	}

	/**
	 * 点击事件
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.findroute:
			showguide();
			findroute.setVisibility(View.GONE);
			requestLocButton.setVisibility(View.GONE);
			officient.setVisibility(View.GONE);
			poilayout.setVisibility(View.GONE);
			break;
		case R.id.findroute2:
			locationLayout.setVisibility(View.GONE);
			poilayout.setVisibility(View.GONE);
			findroute.setVisibility(View.GONE);
			requestLocButton.setVisibility(View.GONE);
			officient.setVisibility(View.GONE);
			showguide();
			break;
		case R.id.customer_find_btn:
			EditText editSearchKey = (EditText) findViewById(R.id.searchpoi);
			mPoiSearch.searchNearby(new PoiNearbySearchOption().location(currentPt)
					.keyword(editSearchKey.getText().toString()).radius(3000).pageNum(15)
					// 以currentPt为搜索中心1000米半径范围内的自行车点
					.pageNum(load_Index));
			break;
		case R.id.my_back:
			/*hideguide();
			requestLocButton.setVisibility(View.VISIBLE);
			officient.setVisibility(View.VISIBLE);
			findroute.setVisibility(View.VISIBLE);
			poilayout.setVisibility(View.VISIBLE);
			edit_layout.setVisibility(View.VISIBLE);
			guide_layout.setVisibility(View.GONE);
			locationLayout.setVisibility(View.GONE);
			click_layout.setVisibility(View.GONE);*/
			OpenCamera();
			break;
		case R.id.camera:
			// 打开相机
			OpenCamera();
			break;
		case R.id.officient:
			// 交通图
			if (flag) {
				mBaidumap.setTrafficEnabled(true);
				MyToast("打开交通图");
				flag = false;
			} else {
				mBaidumap.setTrafficEnabled(false);
				flag = true;
				MyToast("关闭交通图");
			}
		}
	}

	// ListView中点击事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// 通过view获取其内部的组件，进而进行操作
		String text = search_end.getItemAtPosition(position) + "";
		end_edit.setText(text);
		if (search_end.getVisibility() == View.VISIBLE) {
			search_end.setVisibility(View.GONE);
			search_end.startAnimation(slide_out_bottom);
		}
	}

	/**
	 * 显示导航
	 */
	private void showguide() {
		if (guide_layout.getVisibility() == View.GONE) {
			guide_layout.setVisibility(View.VISIBLE);
			guide_layout.startAnimation(slide_in_above);
		}
		if (search_end.getVisibility() == View.GONE) {
			search_end.setVisibility(View.VISIBLE);
			search_end.startAnimation(slide_in_bottom);
		}
		MyToast("当前位置海拔高度" + mLocation.getAltitude());
	}

	/**
	 * 隐藏导航
	 */
	private void hideguide() {
		mBaidumap.clear();
		if (edit_layout.getVisibility() == View.VISIBLE) {
			edit_layout.setVisibility(View.GONE);
			edit_layout.startAnimation(slide_out_above);
		}
		if (search_end.getVisibility() == View.VISIBLE) {
			search_end.setVisibility(View.GONE);
			search_end.startAnimation(slide_out_bottom);
		}

	}

	private void hideclickLayout(boolean flag) {
		if (flag) {
			if (click_layout.getVisibility() == View.GONE) {
				click_layout.setVisibility(View.VISIBLE);
				click_layout.startAnimation(slide_in_bottom);
			}

		} else {
			if (click_layout.getVisibility() == View.VISIBLE) {
				click_layout.setVisibility(View.GONE);
				click_layout.startAnimation(slide_out_bottom);
			}
		}
	}

	private void hideall() {
		edit_layout.setVisibility(View.VISIBLE);
		requestLocButton.setVisibility(View.GONE);
		officient.setVisibility(View.GONE);
		poilayout.setVisibility(View.GONE);
	}

	/**
	 * 调用系统相机
	 */
	private void OpenCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String f = System.currentTimeMillis()+".jpg";
		cameraFile = new File(getExternalFilesDir(null)+"/"+f);
		cameraFileName = f;
		Uri fileUri = MyFileProvider.getUriForFile(this,"com.example.GP.myFileProvider", cameraFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //指定图片存放位置，指定后，在onActivityResult里得到的Data将为null
		openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		startActivityForResult(openCameraIntent, REQUEST_CODE_TAKE_PICTURE);

		/*Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //系统常量， 启动相机的关键
		startActivityForResult(openCameraIntent, REQUEST_CODE_TAKE_PICTURE); // 参数常量为自定义的request code, 在取返回结果时有用*/
	}

	/**
	 * 保存得到的图片
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				Log.i("TestFile",
						"SD card is not avaiable/writeable right now.");
				return;
			}
			if(data == null)
			{
				Log.i("Data:","Data为空！！！！！！！");
			}
			List<String> permissionList = new ArrayList<>();
			permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
			permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			String[] permissions = permissionList.toArray(new String[permissionList.size()]);
			this.requestPermissions(permissions,1);
			boolean permission_readStorage = (PackageManager.PERMISSION_GRANTED ==
					this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
			boolean permission_writeStorage = (PackageManager.PERMISSION_GRANTED ==
					this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
			Log.i("Read&WritePermission",permission_readStorage+"&"+permission_writeStorage+"!!!!");
				Bitmap imgBt = BitmapFactory.decodeFile(cameraFile.getAbsolutePath());
			if(imgBt == null)
			{
				Log.i("imgBt:","imgBt为空！！！！！！！");
			}
				String cameraPath = "/sdcard/DCIM/Camera/Navi/"+cameraFileName;
				saveBitmap(imgBt,cameraPath);


			/*new DateFormat();
			String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
			Toast.makeText(this, name, Toast.LENGTH_LONG).show();
			Bundle bundle = data.getExtras();
			Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

			FileOutputStream b = null;
			File file = new File("/sdcard/Image/");
			file.mkdirs();// 创建文件夹
			String fileName = "/sdcard/Image/"+name;

			try {
				b = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try
			{
				//view.setImageBitmap(bitmap);// 将图片显示在ImageView里
			}catch(Exception e)
			{
				Log.e("error", e.getMessage());
			}

		}*/
		}
	}

	public static void saveBitmap(Bitmap bitmap, String path){
		String savePath;
		File filePic;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			savePath = path;
		} else {
			Log.e("tag", "saveBitmap failure : sdcard not mounted");
			return;
		}
		try {
			filePic = new File(savePath);
			if (!filePic.exists()) {
				filePic.getParentFile().mkdirs();
				filePic.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(filePic);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			Log.e("tag", "saveBitmap: " + e.getMessage());
			return;
		}
		Log.i("tag", "saveBitmap success: " + filePic.getAbsolutePath());
	}
}
