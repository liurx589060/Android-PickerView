package com.bigkoo.pickerview.adapter;

import com.contrarywind.adapter.WheelAdapter;
import java.util.List;

public class IntegerTimeWheelAdapter implements WheelAdapter {
    private List<Integer> mList;

    public IntegerTimeWheelAdapter(List<Integer> list) {
        mList = list;
    }

    @Override
    public int getItemsCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int index) {
        if(mList.size() < index) return 0;
        return mList.get(index);
    }

    @Override
    public int indexOf(Object o) {
        for(int i = 0 ; i < getItemsCount() ; i++) {
            if(((int)o) == mList.get(i)) {
                return i;
            }
        }
        return -1;
    }
}
