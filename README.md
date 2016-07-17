# ShoppingCart1
Android简单的购物车实现,编码参照别人的东西去做成自己的东西

主要的问题解决ScrollView嵌套ListView事件冲突


public class NoScrollListView extends ListView {

	public NoScrollListView(Context context, AttributeSet attrs) {
	
		super(context,attrs);
		
	}
	
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){  
	
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST); 
        
        super.onMeasure(widthMeasureSpec, mExpandSpec);  
        
   } 
   
}




购物界面

![image](https://github.com/GillMo/ShoppingCart1/blob/master/images/2.png)

结算界面

![image](https://github.com/GillMo/ShoppingCart1/blob/master/images/3.png)

收货人地址界面

![image](https://github.com/GillMo/ShoppingCart1/blob/master/images/1.png)
