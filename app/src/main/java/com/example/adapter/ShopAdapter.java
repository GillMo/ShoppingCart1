package com.example.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bean.shopBean;
import com.example.shoppingcart.MainActivity;
import com.example.shoppingcart.R;
import com.example.utils.Utils;

import java.util.HashMap;
import java.util.List;

@SuppressLint("HandlerLeak")
public class ShopAdapter extends BaseAdapter {
	public  Handler handler =null;
	private Context context;			//上下文
	private List<shopBean> list;		//数据集合List
	private LayoutInflater inflater;	//布局填充器
	private static HashMap<Integer, Boolean> isSelected;
	@SuppressLint("UseSparseArrays")
	public ShopAdapter(Context context,List<shopBean> list){

		this.list = list;
		this.context = context;
		handler = ((MainActivity)context).handler;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<>();
		initDate();
	}
	
	// 初始化isSelected的数据  
    private void initDate() {  
        for (int i = 0; i < list.size(); i++) {  
            getIsSelected().put(i, false);  
        }  
    }  
    
    public static HashMap<Integer, Boolean> getIsSelected() {  
        return isSelected;  
    }
    
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		shopBean bean = list.get(position);
		ViewHolder holder ;

		if(convertView == null){
			convertView = inflater.inflate(R.layout.shop_listview_item,null);
			holder = new ViewHolder();

			holder.shop_photo = (ImageView) convertView.findViewById(R.id.shop_photo);
			holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
			holder.shop_description = (TextView) convertView.findViewById(R.id.shop_description);
			holder.shop_price = (TextView) convertView.findViewById(R.id.shop_price);
			holder.shop_number = (TextView) convertView.findViewById(R.id.shop_number);
			holder.shop_check = (CheckBox) convertView.findViewById(R.id.shop_check);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}

		holder.shop_photo.setImageResource(bean.getShopPicture());
		holder.shop_name.setText(bean.getShopName());
		holder.shop_description.setText(bean.getShopDescription());
		holder.shop_price.setText("￥"+bean.getShopPrice());
		holder.shop_number.setTag(position);
		holder.shop_number.setText(String.valueOf(bean.getShopNumber()));

		holder.shop_number.setOnClickListener(new ShopNumberClickListener());
		holder.shop_check.setTag(position);
		holder.shop_check.setChecked(getIsSelected().get(position));
		holder.shop_check.setOnCheckedChangeListener(new CheckBoxChangedListener());

		return convertView;
	}
	//数量TextView点击监听器
	private final class ShopNumberClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//获取商品的数量
			int shopNum = Integer.valueOf(((TextView)v).getText().toString().trim());
			//弹出Dialog
			showDialog(shopNum,(TextView)v);
		}
	}
	private int number = 0;			//记录对话框中的数量
	private EditText editText;		//对话框中数量编辑器
	/**
	 * 弹出对话框更改商品的数量
	 * @param shopNum	商品原来的数量
	 * @param textNum	Item中显示商品数量的控件
	 */
	private void showDialog(int shopNum,final TextView textNum){
		View view = inflater.inflate(R.layout.number_update, null);

		Button btnSub = (Button)view.findViewById(R.id.numSub);
		Button btnAdd = (Button)view.findViewById(R.id.numAdd);
		editText = (EditText)view.findViewById(R.id.edt);

		editText.setText(String.valueOf(shopNum));

		btnSub.setOnClickListener(new ButtonClickListener());
		btnAdd.setOnClickListener(new ButtonClickListener());

		number = shopNum;

		new AlertDialog.Builder(context)
		.setView(view)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//将用户更改的商品数量更新到服务器
				int position = (Integer)textNum.getTag();
					//更改对象的数据
					list.get(position).setShopNumber(number);
					//把listView里面的数目Textview控件发过去,
					Message ms = handler.obtainMessage();
					ms.what = Utils.UPDATA_LISTVIEW_TEXTVIEW;
					ms.obj = textNum;
					ms.arg1 = number;
					handler.sendMessage(ms);

					Message message = handler.obtainMessage();
					message.what = Utils.UPDATA_BUTTOM_PRICE;
					message.obj = getTotalPrice();
					handler.sendMessage(message);
			}
		}).setNegativeButton("取消", null)
		.create().show();
	}

	//Button点击监听器
	private final class ButtonClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.numSub:
					if(number > 1){
						number--;
					}else {
						Toast.makeText(context,"商品已经不能再减少了",Toast.LENGTH_LONG).show();
					}
					break;
				case R.id.numAdd:
					number++;
					break;
			}
			//发送消息更新Dialog里面的EditText的数量
			if(editText!= null){
				Message message2 = handler.obtainMessage();
				message2.what = Utils.UPDATA_DIALOG_EDITTEXT;
				message2.obj = editText;
				message2.arg1 = number;
				handler.sendMessage(message2);
			}

		}
	}
	
	//CheckBox选择改变监听器
	private final class CheckBoxChangedListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton cb, boolean flag) {
			int position = (Integer)cb.getTag();
			getIsSelected().put(position, flag);
			shopBean bean = list.get(position);
			bean.setChoosed(flag);

			//更新下面的价格
			Message message = handler.obtainMessage();
			message.what = Utils.UPDATA_BUTTOM_PRICE;
			message.obj = getTotalPrice();
			handler.sendMessage(message);

			//如果所有的物品全部被选中，则全选按钮也默认被选中
			Message message2 = handler.obtainMessage();
			message2.what = Utils.UPDATA_CHECK_BOX;
			message2.obj = isAllSelected();
			handler.sendMessage(message2);

		}
	}
	/**
	 * 计算选中商品的金额
	 * @return	返回需要付费的总金额
	 */
	public float getTotalPrice(){
		shopBean bean;
		float totalPrice = 0;
		for(int i=0;i<list.size();i++){
			bean = list.get(i);
			if(bean.isChoosed()){
				totalPrice += bean.getShopNumber()*bean.getShopPrice();
			}
		}
		return totalPrice;
	}
	
	/**
	 * 判断是否购物车中所有的商品全部被选中
	 * @return	true所有条目全部被选中
	 * 			false还有条目没有被选中
	 */
	public boolean isAllSelected(){
		boolean flag = true;
		for(int i=0;i<list.size();i++){
			if(!getIsSelected().get(i)){
				flag = false;
				break;
			}
		}
		return flag;
	}

	class ViewHolder{
		public ImageView shop_photo;		//商品图片
		public TextView shop_name;			//商品名称
		public TextView shop_description;	//商品描述
		public TextView shop_price;			//商品价格
		public TextView shop_number;		//商品数量
		public CheckBox shop_check;			//商品选择按钮
	}
}
