package com.illidan.tao;

import java.util.ArrayList;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

public class MyAutoCompleteTextView extends EditText {

	Context context = null;
	MyTextWatcher myTextWatcher =null;
	public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		setPopw();
		this.addTextChangedListener(watcher);
	}
	
	/**
	 * 设置要将试图加进去的父布局
	 * @param layout 只能是Relative父布局，如果是linear请用另一个方法
	 */
	public void setFatherRelativeLayouyt(RelativeLayout layout){
		this.relativeLayout = layout;
		isRLayout = true;
	}
	
	/**
	 * 设置要将试图加进去的父布局
	 * @param layout 只能是Linear父布局，如果是Relative请用另一个方法
	 */
	public void setFatherLinearLayout(LinearLayout layout){
		this.linearLayout = layout;
		isRLayout = false;
	}
	
	/**
	 * 设置下拉内容的文字库
	 * @param list
	 */
	public void setMemoryData(ArrayList<String> list){
		this.memoryData = list;
	}
	/**
	 * 如果要对此输入框添加TextWatch监听，请使用此方法，不要用系统的
	 * @param myTextWatcher
	 */
	public void addMyTextWatcher(MyTextWatcher myTextWatcher){
		this.myTextWatcher = myTextWatcher;
	}
	
	public void removeMyTextWatcher(){
		this.myTextWatcher = null;
	}
	/**
	 * 手动隐藏掉这个下拉提示
	 */
	public void removeTheShowView(){
		if(popView.isShown()){
			if(isRLayout){
				relativeLayout.removeView(popView);
			}else{
				linearLayout.removeView(popView);
			}
		}
	}
	
	public boolean isListShowing(){
		return popView.isShown();
	}
	TextWatcher watcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			mList.clear();
			mList.addAll(getSimilarString(String.valueOf(s), memoryData));
			if(mList.size()>0){
				mAdapter.notifyDataSetInvalidated();
				if(!popView.isShown()){
					int[] top = new int[2];
					MyAutoCompleteTextView.this.getLocationInWindow(top);
					//显示位置稍有不和，可自行修改，这里我就偷懒了
					layoutParams.topMargin = top[1]-15;
					layoutParams.leftMargin = top[0];
					if(isRLayout){
						relativeLayout.addView(popView,layoutParams);
					}else{
						linearLayout.addView(popView,layoutParams);
					}
					popView.setFocusable(true);
				}
			}else{
				if(isRLayout){
					relativeLayout.removeView(popView);	
				}else{
					linearLayout.removeView(popView);	
				}
			}
			if(myTextWatcher!=null){
				myTextWatcher.onTextChanged(s, start, before, count);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			if(myTextWatcher!=null){
				myTextWatcher.beforeTextChanged(s, start, count, after);
			}
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(s.length() == 0){
				removeTheShowView();
			}
			if(myTextWatcher!=null){
				myTextWatcher.afterTextChanged(s);
			}
		}
	};
	
	ArrayList<String> memoryData = null;
	LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	private View popView = null;
	private ListView mlistView = null;
	private ArrayList<String> mList = null;
	private ArrayAdapter<String> mAdapter = null;//popw的listview的适配器
	RelativeLayout relativeLayout = null;
	LinearLayout linearLayout =null;
	private boolean isRLayout = false;
	
	private void setPopw(){
		if(this.popView == null){
			popView = View.inflate(context, R.layout.popview, null);			
		}
		
		if(mlistView == null){
			mlistView = (ListView) popView.findViewById(R.id.pop_listview);
			mlistView.setItemsCanFocus(true);
			mlistView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					MyAutoCompleteTextView.this.setText(mList.get(position));
					if(isRLayout){
						relativeLayout.removeView(popView);
					}else{
						linearLayout.removeView(popView);
					}
				}
			});
		}
		mList =new ArrayList<String>();
		if(mAdapter == null){
			mAdapter = new ArrayAdapter<String>(context, R.layout.list_item, R.id.txt_item, mList);
		}
		mlistView.setAdapter(mAdapter);
	}
	
	/**
	 * 从某字符集合中获取前部分字符串相似的字符集合
	 * <p> 比如，基准字符串为asd的时候，从集合里取出全部以asd打头的字符串
	 * @param edt 拿来比较的基准字符
	 * @param datas 字符集和
	 * @return 匹配的字符集合
	 */
	private ArrayList<String> getSimilarString(String edt,ArrayList<String> datas){
		ArrayList<String> similars = new ArrayList<String>();
		for(String s :datas){
			if(s.startsWith(edt)){
				similars.add(s);
			}
		}
		return similars;
	}
	/**
	 * 因为控件内部已经做了此系统接口的实现监听，这个接口是自己做的留给外部调用的
	 *<p>触发机制、字段都和系统自带的一样,就不赘述了
	 * @author hz
	 *
	 */
	public interface MyTextWatcher{
		/**
		 * 
		 * @param s
		 * @param start
		 * @param before
		 * @param count
		 */
		public void onTextChanged(CharSequence s, int start, int before, int count);
		public void beforeTextChanged(CharSequence s, int start, int count,int after);
		public void afterTextChanged(Editable s);
	}
}
