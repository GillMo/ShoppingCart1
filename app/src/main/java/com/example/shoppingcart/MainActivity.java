package com.example.shoppingcart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adapter.ShopAdapter;
import com.example.bean.ShoppingCanst;
import com.example.bean.shopBean;
import com.example.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

	private CheckBox allCheckBox;        //顶部全选按钮
	private ListView listView;
	private TextView popTotalPrice;		//结算的价格
	private TextView popDelete;			//删除
	private TextView popRecycle;		//收藏
	private TextView popCheckOut;		//结算
	private LinearLayout layout;		//结算布局
	private ShopAdapter adapter;		//自定义适配器
	private List<shopBean> list;		//购物车数据集合
	
	private boolean flag = true;		//全选或全取消
	public Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case Utils.UPDATA_BUTTOM_PRICE :
					float price = (Float)msg.obj;
					if(price > 0){
						popTotalPrice.setText("￥"+price);
						layout.setVisibility(View.VISIBLE);
					}else{
						//layout.setVisibility(View.GONE);
                        popTotalPrice.setText("￥"+0);
                        layout.setVisibility(View.VISIBLE);
					}
					break;
				case Utils.UPDATA_CHECK_BOX:
					flag = !(Boolean)msg.obj;
					allCheckBox.setChecked((Boolean)msg.obj);
					break;
				case Utils.UPDATA_LISTVIEW_TEXTVIEW:
					((TextView)msg.obj).setText(String.valueOf(msg.arg1));
					break;
				case Utils.UPDATA_DIALOG_EDITTEXT:
					((EditText)msg.obj).setText(String.valueOf(msg.arg1));
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		init();
	}
	
	//初始化UI界面
	private void initViews(){
		allCheckBox = (CheckBox) findViewById(R.id.all_check);
		listView = (ListView) findViewById(R.id.main_listView);
		popTotalPrice = (TextView) findViewById(R.id.shopTotalPrice);
		popDelete = (TextView) findViewById(R.id.delete);
		popRecycle = (TextView) findViewById(R.id.collection);
		popCheckOut = (TextView) findViewById(R.id.checkOut);
		layout = (LinearLayout) findViewById(R.id.price_relative);
		
		ClickListener cl = new ClickListener();
		allCheckBox.setOnClickListener(cl);
		popDelete.setOnClickListener(cl);
		popCheckOut.setOnClickListener(cl);
		popRecycle.setOnClickListener(cl);
	}
	
	//初始化数据
	private void init(){
		getListData();
		list = ShoppingCanst.list;
		adapter = new ShopAdapter(this,list);
		listView.setAdapter(adapter);
	}
	
	//获取集合数据
	private void getListData(){
		ShoppingCanst.list = new ArrayList<>();
		shopBean bean = new shopBean();
		bean.setShopId(1);
		bean.setShopPicture(R.drawable.shoes1);
		bean.setStoreName("花花公子");
		bean.setShopName("Simier 斯米尔英伦风日常休闲男鞋单鞋 2016秋季真皮皮鞋鞋子男");
		bean.setShopDescription("颜色：蓝色，尺码：41");
		bean.setShopPrice(199);
		bean.setShopNumber(1);
		bean.setChoosed(false);
		ShoppingCanst.list.add(bean);
		shopBean bean2 = new shopBean();
		bean2.setShopId(2);
		bean2.setShopPicture(R.drawable.shoes2);
		bean2.setStoreName("木林森");
		bean2.setShopName("Camel 骆驼男鞋 男士日常休闲皮鞋 2016秋冬真皮系带休闲鞋子男");
		bean2.setShopDescription("颜色：蓝色，尺码：41");
		bean2.setShopPrice(399);
		bean2.setShopNumber(1);
		bean2.setChoosed(false);
		ShoppingCanst.list.add(bean2);
		shopBean bean3 = new shopBean();
		bean3.setShopId(3);
		bean3.setShopPicture(R.drawable.shoes3);
		bean3.setStoreName("西瑞");
		bean3.setShopName("雷艾新款男鞋子 韩版 潮流 男鞋男休闲鞋 板鞋 鞋子 男 休闲皮鞋");
		bean3.setShopDescription("颜色：黑色，尺码：41");
		bean3.setShopPrice(198);
		bean3.setShopNumber(1);
		bean3.setChoosed(false);
		ShoppingCanst.list.add(bean3);
		shopBean bean4 = new shopBean();
		bean4.setShopId(4);
		bean4.setShopPicture(R.drawable.shoes4);
		bean4.setStoreName("古奇天伦");
		bean4.setShopName("奥康男鞋春秋透气系带板鞋男韩版潮流男士休闲鞋真皮低帮鞋子男");
		bean4.setShopDescription("颜色：蓝色，尺码：41");
		bean4.setShopPrice(599);
		bean4.setShopNumber(1);
		bean4.setChoosed(false);
		ShoppingCanst.list.add(bean4);
	}
	
	//事件点击监听器
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.all_check:		//全选
				selectedAll();
				break;
			case R.id.delete:			//删除
				String shopIndex = deleteOrCheckOutShop();
				showDialogDelete(shopIndex);
				break;
			case R.id.checkOut:			//结算
				goCheckOut();
				break;
			}
		}
	}
	
	//结算
	private void goCheckOut(){
		String shopIndex = deleteOrCheckOutShop();
		Intent checkOutIntent = new Intent(MainActivity.this,CheckOutActivity.class);
		checkOutIntent.putExtra("shopIndex", shopIndex);
		startActivity(checkOutIntent);
	}
	
	//全选或全取消
	private void selectedAll(){
		for(int i=0;i<list.size();i++){
			ShopAdapter.getIsSelected().put(i, flag);
		}
		adapter.notifyDataSetChanged();
	}
	
	//删除或结算商品
	private String deleteOrCheckOutShop(){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<list.size();i++){
			if(ShopAdapter.getIsSelected().get(i)){
				sb.append(i);
				sb.append(",");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	//弹出对话框询问用户是否删除被选中的商品
	public void showDialogDelete(String str){
		final String[] delShopIndex = str.split(",");
		new AlertDialog.Builder(MainActivity.this)
		.setMessage("您确定删除这"+delShopIndex.length+"商品吗？")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//for(String s : delShopIndex){
                //记住删除所有勾选的东西,必须从大的索引开始删除,不然数组会越界
               for(int i = delShopIndex.length -1; i>= 0; i--){
				    String s= String.valueOf(delShopIndex[i]);
					int index = Integer.valueOf(s);
					//list.remove(index);
					ShoppingCanst.list.remove(index);
					//连接服务器之后，获取数据库中商品对应的ID，删除商品
//					list.get(index).getShopId();
				}
				flag = false;
				selectedAll();	//删除商品后，取消所有的选择
//				flag = true;	//刷新页面后，设置Flag为true，恢复全选功能

                //删除了商品全选的钩钩去掉
                Message message2 = handler.obtainMessage();
                message2.what = Utils.UPDATA_CHECK_BOX;
                message2.obj = false;
                handler.sendMessage(message2);

                //更新底部的价格
                Message message3 = handler.obtainMessage();
                message3.what = Utils.UPDATA_BUTTOM_PRICE;
                message3.obj = adapter.getTotalPrice();
                handler.sendMessage(message3);
			}
		}).setNegativeButton("取消", null)
		.create().show();
	}



}
