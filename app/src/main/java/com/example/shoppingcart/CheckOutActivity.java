package com.example.shoppingcart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.CheckOutAdapter;
import com.example.bean.AddressBean;
import com.example.bean.ShoppingCanst;
import com.example.bean.shopBean;
import com.example.layout.NoScrollListView;
import com.example.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.example.bean.ShoppingCanst.list;

public class CheckOutActivity extends Activity {
	
	private Button sureCheckOut;		//确认购买
	private TextView addresseeName;		//收货人姓名
	private TextView smearedAddress;	//收货人区地址
	private TextView detailAddress;		//收货人详细地址
	private TextView checkOutAllPrice;	//结算的总金额
	private TextView title_left;		//title左标题,返回
	private TextView title_center;		//title中间标题
	private RelativeLayout addressRelative;	  //显示收货人信息的布局
	private NoScrollListView checkOutListView;//商品列表
	
	private CheckOutAdapter adapter;
	private List<shopBean> shopBeanList;			  //结算商品数据集合
	private List<AddressBean> addressList;	  //收货人地址数据集合

	private static int REQUESTCODE = 1;		  //跳转请求码
	private float allPrice = 0;				  //购买商品需要的总金额

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case Utils.UPDATA_CONTECTS_ADDRESS:
					int tempIndex = (Integer)msg.obj;
					Toast.makeText(getApplication(),"您更改为"+ShoppingCanst.addressList.get(tempIndex).getName()+"收货", Toast.LENGTH_LONG).show();
					showInfo(tempIndex);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkout);
		initView();
		initData();
	}

	//初始化数据
	private void initData(){
		shopBeanList = new ArrayList<>();
		String shopIndex = getIntent().getStringExtra("shopIndex");
		String[] shopIndexs = shopIndex.split(",");

		for(String s : shopIndexs){
			int position = Integer.valueOf(s);
			shopBean bean = list.get(position);
			allPrice += bean.getShopNumber()*bean.getShopPrice();
			shopBeanList.add(bean);
		}
		//初始化收货人信息
		initAddressData();
		addressList = ShoppingCanst.addressList;
		checkOutAllPrice.setText("总共有"+shopBeanList.size()+"个商品      总价￥"+allPrice);
		showInfo(0);	//默认显示第一条地址信息

		adapter = new CheckOutAdapter(this, shopBeanList);
		checkOutListView.setAdapter(adapter);
	}
	
	//初始化UI界面
	private void initView(){
		
		sureCheckOut = (Button) findViewById(R.id.sureCheckOut);
		addresseeName = (TextView) findViewById(R.id.addresseeName);
		smearedAddress = (TextView) findViewById(R.id.smearedAddress);
		detailAddress = (TextView) findViewById(R.id.detailAddress);
		checkOutAllPrice = (TextView) findViewById(R.id.checkOutAllPrice);
		title_left = (TextView) findViewById(R.id.title_left);
		title_center = (TextView) findViewById(R.id.title_center);
		checkOutListView = (NoScrollListView) findViewById(R.id.checkOutListView);
		addressRelative = (RelativeLayout) findViewById(R.id.addressRelative);
		
		ClickListener cl = new ClickListener();
		title_left.setText(R.string.sureOrder);
		title_center.setText(R.string.checkOut);
		title_left.setOnClickListener(cl);
		sureCheckOut.setOnClickListener(cl);
		addressRelative.setOnClickListener(cl);
	}
	
	//显示收货人姓名地址等信息
	private void showInfo(int index){
		AddressBean bean = addressList.get(index);

		//联系人,省份,具体位置
		addresseeName.setText(bean.getName());
		smearedAddress.setText(bean.getSmearedAddress());
		detailAddress.setText(bean.getDetailAddress());
	}
	
	//获取收货人地址数据集合
	private void initAddressData(){
		ShoppingCanst.addressList = new ArrayList<>();

		AddressBean bean = new AddressBean();
		bean.setName("高敏");
		bean.setSmearedAddress("湖北省黄石市");
		bean.setDetailAddress("桂林北路16号湖北理工学院 15671776703");
		ShoppingCanst.addressList.add(bean);

		AddressBean bean2 = new AddressBean();
		bean2.setName("李文涛");
		bean2.setSmearedAddress("湖北省黄石市");
		bean2.setDetailAddress("桂林北路16号湖北理工学院 15671776233");
		ShoppingCanst.addressList.add(bean2);
	}
	
	//修改地址
	private void updateAddress(){
		Intent intent = new Intent(CheckOutActivity.this,UpdateAddressActivity.class);
		startActivityForResult(intent, REQUESTCODE);
	}
	
	//事件点击监听器
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.sureCheckOut:          //支付界面
					Toast.makeText(getApplicationContext(), "结算完成，总共花费￥"+allPrice, Toast.LENGTH_LONG).show();
					break;
				case R.id.addressRelative:     //更新买家地址
					updateAddress();
					break;
				case R.id.title_left:           //返回键
					finish();
					break;
				default:
					break;
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUESTCODE){        //请求码
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				int addressIndex  = bundle.getInt("addressIndex");  //传过来的买家地址索引
				handler.sendMessage(handler.obtainMessage(Utils.UPDATA_CONTECTS_ADDRESS,addressIndex));
			}
		}
	}
	

}
