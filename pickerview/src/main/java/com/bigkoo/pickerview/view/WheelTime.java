package com.bigkoo.pickerview.view;

import android.view.View;

import com.bigkoo.pickerview.R;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.adapter.NumericWheelAdapter;
import com.bigkoo.pickerview.adapter.IntegerTimeWheelAdapter;
import com.bigkoo.pickerview.listener.ISelectTimeCallback;
import com.bigkoo.pickerview.utils.ChinaDate;
import com.bigkoo.pickerview.utils.LunarCalendar;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class WheelTime {
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private View view;
    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;
    private WheelView wv_hours;
    private WheelView wv_minutes;
    private WheelView wv_seconds;
    private int gravity;

    private boolean[] type;
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;
    private static final int DEFAULT_START_MONTH = 1;
    private static final int DEFAULT_END_MONTH = 12;
    private static final int DEFAULT_START_DAY = 1;
    private static final int DEFAULT_END_DAY = 31;
    private static final int DEFAULT_END_HOUR = 23;
    private static final int DEFAULT_END_MIN = 59;
    private static final int DEFAULT_END_SECOND = 59;
    private static final int DEFAULT_START_HOUR = 00;
    private static final int DEFAULT_START_MIN = 00;
    private static final int DEFAULT_START_SECOND = 00;

    private int startYear = DEFAULT_START_YEAR;
    private int endYear = DEFAULT_END_YEAR;
    private int startMonth = DEFAULT_START_MONTH;
    private int endMonth = DEFAULT_END_MONTH;
    private int startDay = DEFAULT_START_DAY;
    private int endDay = DEFAULT_END_DAY; //表示31天的
    private int endHour = DEFAULT_END_HOUR;
    private int endMin = DEFAULT_END_MIN;
    private int endSecond = DEFAULT_START_SECOND;
    private int startHour = DEFAULT_START_HOUR;
    private int startMin = DEFAULT_START_MIN;
    private int startSecond = DEFAULT_END_SECOND;
    private int currentYear;

    private int textSize;

    //文字的颜色和分割线的颜色
    private int textColorOut;
    private int textColorCenter;
    private int dividerColor;

    private float lineSpacingMultiplier;
    private WheelView.DividerType dividerType;
    private boolean isLunarCalendar = false;
    private ISelectTimeCallback mSelectChangeCallback;

    private int currentMinHour;
    private int currentMaxHour;
    private int currentMinMin;
    private int currentMaxMin;
    private int currentMinSecond;
    private int currentMaxSecond;

    public WheelTime(View view, boolean[] type, int gravity, int textSize) {
        super();
        this.view = view;
        this.type = type;
        this.gravity = gravity;
        this.textSize = textSize;
    }

    public void setLunarMode(boolean isLunarCalendar) {
        this.isLunarCalendar = isLunarCalendar;
    }

    public boolean isLunarMode() {
        return isLunarCalendar;
    }

    public void setPicker(int year, int month, int day) {
        this.setPicker(year, month, day, 0, 0, 0);
    }

    public void setPicker(int year, final int month, int day, int h, int m, int s) {
        if (isLunarCalendar) {
            int[] lunar = LunarCalendar.solarToLunar(year, month + 1, day);
            setLunar(lunar[0], lunar[1] - 1, lunar[2], lunar[3] == 1, h, m, s);
        } else {
            setSolar(year, month+1, day, h, m, s);
        }
    }

    /**
     * 设置农历
     *
     * @param year
     * @param month
     * @param day
     * @param h
     * @param m
     * @param s
     */
    private void setLunar(int year, final int month, int day, boolean isLeap, int h, int m, int s) {
        // 年
        wv_year = (WheelView) view.findViewById(R.id.year);
        wv_year.setAdapter(new ArrayWheelAdapter(ChinaDate.getYears(startYear, endYear)));// 设置"年"的显示数据
        wv_year.setLabel("");// 添加文字
        wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据
        wv_year.setGravity(gravity);

        // 月
        wv_month = (WheelView) view.findViewById(R.id.month);
        wv_month.setAdapter(new ArrayWheelAdapter(ChinaDate.getMonths(year)));
        wv_month.setLabel("");
        
        int leapMonth = ChinaDate.leapMonth(year);
        if (leapMonth != 0 && (month > leapMonth - 1 || isLeap)) { //选中月是闰月或大于闰月
            wv_month.setCurrentItem(month + 1);
        } else {
            wv_month.setCurrentItem(month);
        }
        
        wv_month.setGravity(gravity);

        // 日
        wv_day = (WheelView) view.findViewById(R.id.day);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (ChinaDate.leapMonth(year) == 0) {
            wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year, month))));
        } else {
            wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year))));
        }
        wv_day.setLabel("");
        wv_day.setCurrentItem(day - 1);
        wv_day.setGravity(gravity);

        wv_hours = (WheelView) view.findViewById(R.id.hour);
        wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        //wv_hours.setLabel(context.getString(R.string.pickerview_hours));// 添加文字
        wv_hours.setCurrentItem(h);
        wv_hours.setGravity(gravity);

        wv_minutes = (WheelView) view.findViewById(R.id.min);
        wv_minutes.setAdapter(new NumericWheelAdapter(0, 59));
        //wv_minutes.setLabel(context.getString(R.string.pickerview_minutes));// 添加文字
        wv_minutes.setCurrentItem(m);
        wv_minutes.setGravity(gravity);

        wv_seconds = (WheelView) view.findViewById(R.id.second);
        wv_seconds.setAdapter(new NumericWheelAdapter(0, 59));
        //wv_seconds.setLabel(context.getString(R.string.pickerview_minutes));// 添加文字
        wv_seconds.setCurrentItem(m);
        wv_seconds.setGravity(gravity);

        // 添加"年"监听
        wv_year.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int year_num = index + startYear;
                // 判断是不是闰年,来确定月和日的选择
                wv_month.setAdapter(new ArrayWheelAdapter(ChinaDate.getMonths(year_num)));
                if (ChinaDate.leapMonth(year_num) != 0 && wv_month.getCurrentItem() > ChinaDate.leapMonth(year_num) - 1) {
                    wv_month.setCurrentItem(wv_month.getCurrentItem() + 1);
                } else {
                    wv_month.setCurrentItem(wv_month.getCurrentItem());
                }

                int maxItem = 29;
                if (ChinaDate.leapMonth(year_num) != 0 && wv_month.getCurrentItem() > ChinaDate.leapMonth(year_num) - 1) {
                    if (wv_month.getCurrentItem() == ChinaDate.leapMonth(year_num) + 1) {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year_num))));
                        maxItem = ChinaDate.leapDays(year_num);
                    } else {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, wv_month.getCurrentItem()))));
                        maxItem = ChinaDate.monthDays(year_num, wv_month.getCurrentItem());
                    }
                } else {
                    wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, wv_month.getCurrentItem() + 1))));
                    maxItem = ChinaDate.monthDays(year_num, wv_month.getCurrentItem() + 1);
                }

                if (wv_day.getCurrentItem() > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });

        // 添加"月"监听
        wv_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int month_num = index;
                int year_num = wv_year.getCurrentItem() + startYear;
                int maxItem = 29;
                if (ChinaDate.leapMonth(year_num) != 0 && month_num > ChinaDate.leapMonth(year_num) - 1) {
                    if (wv_month.getCurrentItem() == ChinaDate.leapMonth(year_num) + 1) {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year_num))));
                        maxItem = ChinaDate.leapDays(year_num);
                    } else {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, month_num))));
                        maxItem = ChinaDate.monthDays(year_num, month_num);
                    }
                } else {
                    wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, month_num + 1))));
                    maxItem = ChinaDate.monthDays(year_num, month_num + 1);
                }

                if (wv_day.getCurrentItem() > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });

        setChangedListener(wv_day);
        setChangedListener(wv_hours);
        setChangedListener(wv_minutes);
        setChangedListener(wv_seconds);

        if (type.length != 6) {
            throw new RuntimeException("type[] length is not 6");
        }
        wv_year.setVisibility(type[0] ? View.VISIBLE : View.GONE);
        wv_month.setVisibility(type[1] ? View.VISIBLE : View.GONE);
        wv_day.setVisibility(type[2] ? View.VISIBLE : View.GONE);
        wv_hours.setVisibility(type[3] ? View.VISIBLE : View.GONE);
        wv_minutes.setVisibility(type[4] ? View.VISIBLE : View.GONE);
        wv_seconds.setVisibility(type[5] ? View.VISIBLE : View.GONE);
        setContentTextSize();
    }

    /**
     * 设置公历
     *
     * @param year
     * @param month
     * @param day
     * @param h
     * @param m
     * @param s
     */
    private void setSolar(int year, final int month, int day, int h, int m, int s) {
        Calendar tempStartCalendar = Calendar.getInstance();
        Calendar tempEndCalendar = Calendar.getInstance();
        Calendar tempCurrentCalendar = Calendar.getInstance();
        tempStartCalendar.set(0,0,0,0,0,0);
        tempEndCalendar.set(0,0,0,0,0,0);
        tempCurrentCalendar.set(0,0,0,0,0,0);

        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        currentYear = year;
        // 年
        wv_year = (WheelView) view.findViewById(R.id.year);
        ArrayList<Integer> yearList = new ArrayList<>();
        for(int i = startYear ; i <= endYear ; i++) {
            yearList.add(i);
        }
        IntegerTimeWheelAdapter yearAdapter = new IntegerTimeWheelAdapter(yearList);
        wv_year.setAdapter(yearAdapter);// 设置"年"的显示数据


        wv_year.setCurrentValue(year);// 初始化时显示的数据
        wv_year.setGravity(gravity);

        int start = 0;
        int end = 0;
        // 月
        {
            wv_month = (WheelView) view.findViewById(R.id.month);
            tempStartCalendar.set(startYear,0,1,0,0,0);
            tempEndCalendar.set(endYear,0,1,0,0,0);
            tempCurrentCalendar.set(year,0,1,0,0,0);

            if(tempStartCalendar.getTimeInMillis()/1000 == tempEndCalendar.getTimeInMillis()/1000) {
                start = startMonth;
                end = endMonth;
            }else if(tempCurrentCalendar.getTimeInMillis()/1000 <= tempStartCalendar.getTimeInMillis()/1000) {
                start = startMonth;
                end = 12;
            }else if(tempCurrentCalendar.getTimeInMillis()/1000 >= tempEndCalendar.getTimeInMillis()/1000) {
                start = 1;
                end = endMonth;
            }else {
                start = 1;
                end = 12;
            }
            ArrayList<Integer> list = new ArrayList<>();
            for(int i = start ; i <= end ; i++) {
                list.add(i);
            }
            IntegerTimeWheelAdapter adapter = new IntegerTimeWheelAdapter(list);
            wv_month.setAdapter(adapter);
            wv_month.setCurrentValue(month);
            wv_month.setGravity(gravity);
        }

        // 日
        {
            int dayEnd = 0;
            if (list_big.contains(String.valueOf(month))) {
                dayEnd = 31;
            } else if (list_little.contains(String.valueOf(month))) {
                dayEnd = 30;
            } else {
                // 闰年
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                    dayEnd = 29;
                } else {
                    dayEnd = 28;
                }
            }

            wv_day = (WheelView) view.findViewById(R.id.day);
            tempStartCalendar.add(Calendar.MONTH,startMonth);
            tempEndCalendar.add(Calendar.MONTH,endMonth);
            tempCurrentCalendar.add(Calendar.MONTH,month);

            if(tempStartCalendar.getTimeInMillis()/1000 == tempEndCalendar.getTimeInMillis()/1000) {
                start = startDay;
                end = endDay;
            }else if(tempCurrentCalendar.getTimeInMillis()/1000 <= tempStartCalendar.getTimeInMillis()/1000) {
                start = startDay;
                end = dayEnd;
            }else if(tempCurrentCalendar.getTimeInMillis()/1000 >= tempEndCalendar.getTimeInMillis()/1000) {
                start = 1;
                end = endDay;
            }else {
                start = 1;
                end = dayEnd;
            }
            ArrayList<Integer> list = new ArrayList<>();
            for(int i = start ; i <= end ; i++) {
                list.add(i);
            }
            IntegerTimeWheelAdapter adapter = new IntegerTimeWheelAdapter(list);
            wv_day.setAdapter(adapter);
            wv_day.setCurrentValue(day);
            wv_day.setGravity(gravity);
        }

        //时
        {
            wv_hours = (WheelView) view.findViewById(R.id.hour);
            tempStartCalendar.add(Calendar.DAY_OF_MONTH,startDay);
            tempEndCalendar.add(Calendar.DAY_OF_MONTH,endDay);
            tempCurrentCalendar.add(Calendar.DAY_OF_MONTH,day);

            if(tempStartCalendar.getTimeInMillis()/1000 == tempEndCalendar.getTimeInMillis()/1000) {
                start = startHour;
                end = endHour;
            }else if(tempCurrentCalendar.getTimeInMillis()/1000 <= tempStartCalendar.getTimeInMillis()/1000) {
                start = startHour;
                end = 23;
            }else if(tempCurrentCalendar.getTimeInMillis()/1000 >= tempEndCalendar.getTimeInMillis()/1000) {
                start = 0;
                end = endHour;
            }else {
                start = 0;
                end = 23;
            }
            ArrayList<Integer> list = new ArrayList<>();
            for(int i = start ; i <= end ; i++) {
                list.add(i);
            }
            IntegerTimeWheelAdapter adapter = new IntegerTimeWheelAdapter(list);
            wv_hours.setAdapter(adapter);
            wv_hours.setCurrentValue(h);
            wv_hours.setGravity(gravity);
        }

        //分
        {
            wv_minutes = (WheelView) view.findViewById(R.id.min);
            tempStartCalendar.add(Calendar.HOUR_OF_DAY,startHour);
            tempEndCalendar.add(Calendar.HOUR_OF_DAY,endHour);
            tempCurrentCalendar.add(Calendar.HOUR_OF_DAY,h);

            if(tempStartCalendar.getTimeInMillis()/1000 == tempEndCalendar.getTimeInMillis()/1000) {
                start = startMin;
                end = endMin;
            }else if(tempCurrentCalendar.getTimeInMillis()/1000 <= tempStartCalendar.getTimeInMillis()/1000) {
                start = startMin;
                end = 59;
            }else if(tempCurrentCalendar.getTimeInMillis()/1000 >= tempEndCalendar.getTimeInMillis()/1000) {
                start = 0;
                end = endMin;
            }else {
                start = 0;
                end = 59;
            }
            ArrayList<Integer> list = new ArrayList<>();
            for(int i = start ; i <= end ; i++) {
                list.add(i);
            }
            IntegerTimeWheelAdapter adapter = new IntegerTimeWheelAdapter(list);
            wv_minutes.setAdapter(adapter);
            wv_minutes.setCurrentValue(m);
            wv_minutes.setGravity(gravity);
        }

        //秒
        {
            wv_seconds = (WheelView) view.findViewById(R.id.second);
            tempStartCalendar.add(Calendar.MINUTE,startMin);
            tempEndCalendar.add(Calendar.MINUTE,endMin);
            tempCurrentCalendar.add(Calendar.MINUTE,m);

            if(tempStartCalendar.getTimeInMillis()/1000 == tempEndCalendar.getTimeInMillis()/1000) {
                start = startSecond;
                end = endSecond;
            }else if(tempCurrentCalendar.getTimeInMillis()/1000 <= tempStartCalendar.getTimeInMillis()/1000) {
                start = startSecond;
                end = 59;
            }else if(tempCurrentCalendar.getTimeInMillis()/1000 >= tempEndCalendar.getTimeInMillis()/1000) {
                start = 0;
                end = endSecond;
            }else {
                start = 0;
                end = 59;
            }
            ArrayList<Integer> list = new ArrayList<>();
            for(int i = start ; i <= end ; i++) {
                list.add(i);
            }
            IntegerTimeWheelAdapter adapter = new IntegerTimeWheelAdapter(list);
            wv_seconds.setAdapter(adapter);
            wv_seconds.setCurrentValue(s);
            wv_seconds.setGravity(gravity);
        }

        // 添加"年"监听
        wv_year.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                wv_year.setCurrentValue((int)wv_year.getCurrentValue(index));
                setSolar((int)wv_year.getCurrentValue(),(int)wv_month.getCurrentValue(),(int)wv_day.getCurrentValue(),
                        (int)wv_hours.getCurrentValue(),(int)wv_minutes.getCurrentValue(),(int)wv_seconds.getCurrentValue());

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });


        // 添加"月"监听
        wv_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                wv_month.setCurrentValue((int)wv_month.getCurrentValue(index));
                setSolar((int)wv_year.getCurrentValue(),(int)wv_month.getCurrentValue(),(int)wv_day.getCurrentValue(),
                        (int)wv_hours.getCurrentValue(),(int)wv_minutes.getCurrentValue(),(int)wv_seconds.getCurrentValue());

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });

        setChangedListener(wv_day);
        setChangedListener(wv_hours);
        setChangedListener(wv_minutes);
        setChangedListener(wv_seconds);

        if (type.length != 6) {
            throw new IllegalArgumentException("type[] length is not 6");
        }
        wv_year.setVisibility(type[0] ? View.VISIBLE : View.GONE);
        wv_month.setVisibility(type[1] ? View.VISIBLE : View.GONE);
        wv_day.setVisibility(type[2] ? View.VISIBLE : View.GONE);
        wv_hours.setVisibility(type[3] ? View.VISIBLE : View.GONE);
        wv_minutes.setVisibility(type[4] ? View.VISIBLE : View.GONE);
        wv_seconds.setVisibility(type[5] ? View.VISIBLE : View.GONE);
        setContentTextSize();
    }

    private void setChangedListener(final WheelView wheelView) {
        if (mSelectChangeCallback != null) {
            wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    wheelView.setCurrentValue((int)wheelView.getCurrentValue(index));
                    setSolar((int)wv_year.getCurrentValue(),(int)wv_month.getCurrentValue(),(int)wv_day.getCurrentValue(),
                            (int)wv_hours.getCurrentValue(),(int)wv_minutes.getCurrentValue(),(int)wv_seconds.getCurrentValue());
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            });
        }

    }

    private void setContentTextSize() {
        wv_day.setTextSize(textSize);
        wv_month.setTextSize(textSize);
        wv_year.setTextSize(textSize);
        wv_hours.setTextSize(textSize);
        wv_minutes.setTextSize(textSize);
        wv_seconds.setTextSize(textSize);
    }

    private void setTextColorOut() {
        wv_day.setTextColorOut(textColorOut);
        wv_month.setTextColorOut(textColorOut);
        wv_year.setTextColorOut(textColorOut);
        wv_hours.setTextColorOut(textColorOut);
        wv_minutes.setTextColorOut(textColorOut);
        wv_seconds.setTextColorOut(textColorOut);
    }

    private void setTextColorCenter() {
        wv_day.setTextColorCenter(textColorCenter);
        wv_month.setTextColorCenter(textColorCenter);
        wv_year.setTextColorCenter(textColorCenter);
        wv_hours.setTextColorCenter(textColorCenter);
        wv_minutes.setTextColorCenter(textColorCenter);
        wv_seconds.setTextColorCenter(textColorCenter);
    }

    private void setDividerColor() {
        wv_day.setDividerColor(dividerColor);
        wv_month.setDividerColor(dividerColor);
        wv_year.setDividerColor(dividerColor);
        wv_hours.setDividerColor(dividerColor);
        wv_minutes.setDividerColor(dividerColor);
        wv_seconds.setDividerColor(dividerColor);
    }

    private void setDividerType() {

        wv_day.setDividerType(dividerType);
        wv_month.setDividerType(dividerType);
        wv_year.setDividerType(dividerType);
        wv_hours.setDividerType(dividerType);
        wv_minutes.setDividerType(dividerType);
        wv_seconds.setDividerType(dividerType);

    }

    private void setLineSpacingMultiplier() {
        wv_day.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_month.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_year.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_hours.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_minutes.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_seconds.setLineSpacingMultiplier(lineSpacingMultiplier);
    }

    public void setLabels(String label_year, String label_month, String label_day, String label_hours, String label_mins, String label_seconds) {
        if (isLunarCalendar) {
            return;
        }

        if (label_year != null) {
            wv_year.setLabel(label_year);
        } else {
            wv_year.setLabel(view.getContext().getString(R.string.pickerview_year));
        }
        if (label_month != null) {
            wv_month.setLabel(label_month);
        } else {
            wv_month.setLabel(view.getContext().getString(R.string.pickerview_month));
        }
        if (label_day != null) {
            wv_day.setLabel(label_day);
        } else {
            wv_day.setLabel(view.getContext().getString(R.string.pickerview_day));
        }
        if (label_hours != null) {
            wv_hours.setLabel(label_hours);
        } else {
            wv_hours.setLabel(view.getContext().getString(R.string.pickerview_hours));
        }
        if (label_mins != null) {
            wv_minutes.setLabel(label_mins);
        } else {
            wv_minutes.setLabel(view.getContext().getString(R.string.pickerview_minutes));
        }
        if (label_seconds != null) {
            wv_seconds.setLabel(label_seconds);
        } else {
            wv_seconds.setLabel(view.getContext().getString(R.string.pickerview_seconds));
        }

    }

    public void setTextXOffset(int x_offset_year, int x_offset_month, int x_offset_day,
                               int x_offset_hours, int x_offset_minutes, int x_offset_seconds) {
        wv_year.setTextXOffset(x_offset_year);
        wv_month.setTextXOffset(x_offset_month);
        wv_day.setTextXOffset(x_offset_day);
        wv_hours.setTextXOffset(x_offset_hours);
        wv_minutes.setTextXOffset(x_offset_minutes);
        wv_seconds.setTextXOffset(x_offset_seconds);
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        wv_year.setCyclic(cyclic);
        wv_month.setCyclic(cyclic);
        wv_day.setCyclic(cyclic);
        wv_hours.setCyclic(cyclic);
        wv_minutes.setCyclic(cyclic);
        wv_seconds.setCyclic(cyclic);
    }


    public String getTime() {
        if (isLunarCalendar) {
            //如果是农历 返回对应的公历时间
            return getLunarTime();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(((int)wv_year.getCurrentValue())).append("-")
                .append(((int)wv_month.getCurrentValue())).append("-")
                .append(((int)wv_day.getCurrentValue())).append(" ")
                .append((int)wv_hours.getCurrentValue()).append(":")
                .append((int)wv_minutes.getCurrentValue()).append(":")
                .append((int)wv_seconds.getCurrentValue());

        return sb.toString();
    }


    /**
     * 农历返回对应的公历时间
     *
     * @return
     */
    private String getLunarTime() {
        StringBuilder sb = new StringBuilder();
        int year = wv_year.getCurrentItem() + startYear;
        int month = 1;
        boolean isLeapMonth = false;
        if (ChinaDate.leapMonth(year) == 0) {
            month = wv_month.getCurrentItem() + 1;
        } else {
            if ((wv_month.getCurrentItem() + 1) - ChinaDate.leapMonth(year) <= 0) {
                month = wv_month.getCurrentItem() + 1;
            } else if ((wv_month.getCurrentItem() + 1) - ChinaDate.leapMonth(year) == 1) {
                month = wv_month.getCurrentItem();
                isLeapMonth = true;
            } else {
                month = wv_month.getCurrentItem();
            }
        }
        int day = wv_day.getCurrentItem() + 1;
        int[] solar = LunarCalendar.lunarToSolar(year, month, day, isLeapMonth);

        sb.append(solar[0]).append("-")
                .append(solar[1]).append("-")
                .append(solar[2]).append(" ")
                .append(wv_hours.getCurrentItem()).append(":")
                .append(wv_minutes.getCurrentItem()).append(":")
                .append(wv_seconds.getCurrentItem());
        return sb.toString();
    }

    public View getView() {
        return view;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }


    public void setRangDate(Calendar startDate, Calendar endDate) {

        if (endDate != null) {
            int year = endDate.get(Calendar.YEAR);
            int month = endDate.get(Calendar.MONTH) + 1;
            int day = endDate.get(Calendar.DAY_OF_MONTH);
            int hour = endDate.get(Calendar.HOUR_OF_DAY);
            int min = endDate.get(Calendar.MINUTE);
            int second = endDate.get(Calendar.SECOND);

            this.endYear = year;
            this.endMonth = month;
            this.endDay = day;
            this.endHour = hour;
            this.endMin = min;
            this.endSecond = second;

        }

        if (startDate != null) {
            int year = startDate.get(Calendar.YEAR);
            int month = startDate.get(Calendar.MONTH) + 1;
            int day = startDate.get(Calendar.DAY_OF_MONTH);
            int hour = startDate.get(Calendar.HOUR_OF_DAY);
            int min = startDate.get(Calendar.MINUTE);
            int second = startDate.get(Calendar.SECOND);

            this.startMonth = month;
            this.startDay = day;
            this.startYear = year;
            this.startHour = hour;
            this.startMin = min;
            this.startSecond = second;
        }
    }

    /**
     * 设置间距倍数,但是只能在1.0-4.0f之间
     *
     * @param lineSpacingMultiplier
     */
    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        setLineSpacingMultiplier();
    }

    /**
     * 设置分割线的颜色
     *
     * @param dividerColor
     */
    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        setDividerColor();
    }

    /**
     * 设置分割线的类型
     *
     * @param dividerType
     */
    public void setDividerType(WheelView.DividerType dividerType) {
        this.dividerType = dividerType;
        setDividerType();
    }

    /**
     * 设置分割线之间的文字的颜色
     *
     * @param textColorCenter
     */
    public void setTextColorCenter(int textColorCenter) {
        this.textColorCenter = textColorCenter;
        setTextColorCenter();
    }

    /**
     * 设置分割线以外文字的颜色
     *
     * @param textColorOut
     */
    public void setTextColorOut(int textColorOut) {
        this.textColorOut = textColorOut;
        setTextColorOut();
    }

    /**
     * @param isCenterLabel 是否只显示中间选中项的
     */
    public void isCenterLabel(boolean isCenterLabel) {
        wv_day.isCenterLabel(isCenterLabel);
        wv_month.isCenterLabel(isCenterLabel);
        wv_year.isCenterLabel(isCenterLabel);
        wv_hours.isCenterLabel(isCenterLabel);
        wv_minutes.isCenterLabel(isCenterLabel);
        wv_seconds.isCenterLabel(isCenterLabel);
    }

    public void setSelectChangeCallback(ISelectTimeCallback mSelectChangeCallback) {
        this.mSelectChangeCallback = mSelectChangeCallback;
    }
}
