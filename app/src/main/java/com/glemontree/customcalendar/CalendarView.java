package com.glemontree.customcalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by Administrator on 2017/8/22.
 */

public class CalendarView extends View {

    private static final int TOTAL_COL = 7;
    private static final int TOTAL_ROW = 6;

    // 绘制圆形的画笔
    private Paint mCirclePaint;
    // 绘制文本的画笔
    private Paint mTextPaint;
    // 视图的宽度
    private int mViewWidth;
    // 视图的高度
    private int mViewHeight;
    // 单元格间距
    private int mCellSpace;
    // 行数组，每个元素代表一行
    private Row rows[] = new Row[TOTAL_ROW];
    // 自定义的日期
    private static CustomDate mShowDate;
    private int touchSlop;
    private boolean callBackCellSpace;
    private Cell mClickCell;
    private float mDownX;
    private float mDownY;

    private OnCellClickListener mCellClickListener;

    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CalendarView(Context context, OnCellClickListener listener) {
        super(context);
        this.mCellClickListener = listener;
        init(context);
    }

    private void init(Context context) {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.parseColor("#F24949"));
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        initDate();
    }

    private void initDate() {
        mShowDate = new CustomDate();
        fillDate();
    }

    private void fillDate() {
        // 获得这个月的第几天
        int monthDay = DateUtil.getCurrentMonthDay();
        // 获得上个月有多少天
        int lastMonthDays = DateUtil.getMonthDays(mShowDate.getYear(), mShowDate.getMonth() - 1);
        // 获得这个月有多少天
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.getYear(), mShowDate.getMonth());
        // 获得这个月的第一天是星期几
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.getYear(), mShowDate.getMonth());
        // 是否是当前月
        boolean isCurrentMonth = false;
        // 判断mShowDate是否是当前月
        if (DateUtil.isCurrentMonth(mShowDate)) {
            isCurrentMonth = true;
        }
        int day = 0;
        // j: row number; i: column number
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL;
                if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) { // 当前位置是这个月的日期
                    day++;
                    rows[j].cells[i] = new Cell(CustomDate.modifyDayForObject(mShowDate, day), State.CURRENT_MONTH_DAY, i, j);
                    if (isCurrentMonth && day == monthDay) {
                        CustomDate date = CustomDate.modifyDayForObject(mShowDate, day);
                        rows[j].cells[i] = new Cell(date, State.TODAY, i, j);
                    }
                    if (isCurrentMonth && day > monthDay) { // 如果比这个月的今天要大，表示还没到
                        rows[j].cells[i] = new Cell(
                                CustomDate.modifyDayForObject(mShowDate, day),
                                State.UNREACH_DAY, i, j);
                    }
                } else if (position < firstDayWeek) { // 当前位置比这个月的第一天还要小，说明是上个月的日期
                    rows[j].cells[i] = new Cell(new CustomDate(mShowDate.getYear(),
                            mShowDate.getMonth() - 1, lastMonthDays
                            - (firstDayWeek - position - 1)),
                            State.PAST_MONTH_DAY, i, j);
                } else if (position >= firstDayWeek + currentMonthDays) { // 当前位置比这个月的最后一天还要大，说明是下个月的日期
                    rows[j].cells[i] = new Cell((new CustomDate(mShowDate.getYear(),
                            mShowDate.getMonth() + 1, position - firstDayWeek
                            - currentMonthDays + 1)),
                            State.NEXT_MONTH_DAY, i, j);
                }
            }
        }
        mCellClickListener.changeDate(mShowDate); // 把日期回调回去让界面显示
    }

    // 回调接口
    public interface OnCellClickListener {
        void clickDate(CustomDate date);
        void changeDate(CustomDate date);
    }

    // 表示每一行的类
    class Row {

        // 行号
        private int j;
        public Row(int j) {
            this.j = j;
        }

        // 每一行的日期
        public Cell[] cells = new Cell[TOTAL_COL];

        public void drawCells(Canvas canvas) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null) {
                    cells[i].drawSelf(canvas);
                }
            }
        }
    }

    class Cell {
        private CustomDate date; // 当前Cell代表的日期
        private State state; // 表示日期的类型，是上个月的日期、本月的日期、今天还是下个月的日期
        private int i; // 列号
        private int j; // 行号

        public Cell(CustomDate date, State state, int i, int j) {
            this.date = date;
            this.state = state;
            this.i = i;
            this.j = j;
        }

        public CustomDate getDate() {
            return date;
        }

        public void setDate(CustomDate date) {
            this.date = date;
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        public int getJ() {
            return j;
        }

        public void setJ(int j) {
            this.j = j;
        }

        public void drawSelf(Canvas canvas) {
            switch (state) {
                case TODAY:
                    mTextPaint.setColor(Color.parseColor("#fffffe"));
                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
                            (float) ((j + 0.5) * mCellSpace), mCellSpace / 3,
                            mCirclePaint);
                    break;
                case CURRENT_MONTH_DAY: // 当前月日期
                    mTextPaint.setColor(Color.BLACK);
                    break;
                case PAST_MONTH_DAY: // 过去一个月
                case NEXT_MONTH_DAY: // 下一个月
                    mTextPaint.setColor(Color.parseColor("#fffffe"));
                    break;
                case UNREACH_DAY: // 还未到的天
                    mTextPaint.setColor(Color.GRAY);
                    break;
                default:
                    break;
            }
            String content = date.getDay() + "";
            canvas.drawText(content,
                            (float) ((i + 0.5) * mCellSpace - mTextPaint.measureText(content) / 2),
                            (float) ((j + 0.7) * mCellSpace - mTextPaint.measureText(content, 0, 1) / 2),
                            mTextPaint);
        }
    }

    enum State {
        TODAY, CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, UNREACH_DAY;
    }

    public void leftSlide() {
        if (mShowDate.getMonth() == 1) {
            mShowDate.setMonth(12);
            mShowDate.setYear(mShowDate.getYear() - 1);
        } else {
            mShowDate.setMonth(mShowDate.getMonth() - 1);
        }
        update();
    }

    public void rightSlide() {
        if (mShowDate.getMonth() == 12) {
            mShowDate.setMonth(1);
            mShowDate.setYear(mShowDate.getYear() + 1);
        } else {
            mShowDate.setMonth(mShowDate.getMonth() + 1);
        }
        update();
    }

    public void update() {
        fillDate();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewHeight = h;
        mViewWidth = w;
        mCellSpace = Math.min(mViewHeight / TOTAL_ROW, mViewWidth / TOTAL_COL);
        if (!callBackCellSpace) {
            callBackCellSpace = true;
        }
        mTextPaint.setTextSize(mCellSpace / 3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null) {
                rows[i].drawCells(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float distX = event.getX() - mDownX;
                float distY = event.getY() - mDownY;
                if (Math.abs(distX) < touchSlop && Math.abs(distY) < touchSlop) {
                    int col = (int) (mDownX / mCellSpace);
                    int row = (int) (mDownY / mCellSpace);
                    measureClickCell(col, row);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void measureClickCell(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW)
            return;
        if (mClickCell != null) {
            rows[mClickCell.j].cells[mClickCell.i] = mClickCell;
        }
        if (rows[row] != null) {
            mClickCell = new Cell(rows[row].cells[col].date,
                    rows[row].cells[col].state, rows[row].cells[col].i,
                    rows[row].cells[col].j);

            CustomDate date = rows[row].cells[col].date;
            date.setWeek(col);
            mCellClickListener.clickDate(date);

            // 刷新界面
            update();
        }
    }
}
