package com.example.admin.nursemaid1;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.sinopulsar.nursemaid.R;
import com.sinopulsar.nursemaid1.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment implements View.OnClickListener{                            //"溫度曲線"的顯示和處理
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static boolean isadd = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    View chart_view;
    TextView currentDate,min,unit;
    RelativeLayout RLayout;
    ImageButton first_Hour,previous_Hour,next_Hour,final_Hour;
    LinearLayout LLayout;

    static SQLite sqLite;
    static ArrayList<ArrayList<String>> UserTimeTemp = new ArrayList<ArrayList<String>>();
    String xdata[], ydata[], time_x[], temp_35[], temp_38[];

    static String[] StartTime = { null, null, null, null, null, null };
    static String[] timeforline = new String[2];
    String box_timeline[],box_timeline_D[],box_timeline_H[];
    int Year, Month, Day, Hour, Minute, Second;
    Date minDate,maxDate;

    Date[] line_x;
    double[] line_y;

    List<Date[]> time = new ArrayList<Date[]>();
    List<double[]> temper = new ArrayList<double[]>();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    TimeSeries time_series;
    private XYMultipleSeriesRenderer renderer;
    private XYMultipleSeriesDataset dataset ;
    private String[] titles = {"Temperature chart", "35", "38"};
    int[] colors = new int[] { Color.BLUE, Color.RED, Color.RED };// 折線的顏色
    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.CIRCLE, PointStyle.CIRCLE}; // 折線點的形狀

    double minX, maxX;
    String[] day;
    String getTimeData;
    DisplayMetrics dm;
    String[] highItems,lowItems;
    float highTemp, lowTemp;
    Double yMin, yMax;

    public ChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(/*String param1, String param2*/) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
   //     args.putString(ARG_PARAM1, param1);
   //     args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = lf.inflate(R.layout.fragment_chart, container, false);
        RLayout = (RelativeLayout) view.findViewById(R.id.RLayout);
        first_Hour = (ImageButton)view.findViewById(R.id.first_Hour);
        previous_Hour = (ImageButton)view.findViewById(R.id.previous_Hour);
        next_Hour = (ImageButton)view.findViewById(R.id.next_Hour);
        final_Hour = (ImageButton)view.findViewById(R.id.final_Hour);
        currentDate = (TextView) view.findViewById(R.id.currentDate);
        min = (TextView)view.findViewById(R.id.min);
        min.setText(getResources().getString(R.string.minute));

        unit = (TextView)view.findViewById(R.id.unit);
        LLayout = (LinearLayout)view.findViewById(R.id.LLayout);

        first_Hour.setOnClickListener(this);
        previous_Hour.setOnClickListener(this);
        next_Hour.setOnClickListener(this);
        final_Hour.setOnClickListener(this);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
     //   isadd = true;
   /*     if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.first_Hour:
                getTimeColumn(1,getTimeData);
                break;
            case R.id.previous_Hour:
                getTimeColumn(2,getTimeData);
                break;
            case R.id.next_Hour:
                getTimeColumn(3,getTimeData);
                break;
            case R.id.final_Hour:
                getTimeColumn(4,getTimeData);
                break;
        }
    }

    public void getTimeColumn(int i, String data){
        getTimeData = sqLite.ToGetTimeData(i, data);  //變成現在要看的時間
        //Log.e("getTimeData", getTimeData);
        day = getTimeData.split("-");
        Log.e("day", String.valueOf(day));
        sqLite.ToGetUserTT(day);
        toChartFragment(day[0]+"-"+day[1]+"-"+day[2]+"-"+day[3]+"-"+"00"+"-"+"00");
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void toChartFragment(String t) {
        Log.e("toChartFragment","=================================");
        RLayout.setVisibility(View.INVISIBLE);
        unit.setVisibility(View.INVISIBLE);
        currentDate.setVisibility(View.INVISIBLE);
        LLayout.setVisibility(View.INVISIBLE);
        min.setVisibility(View.INVISIBLE);

        Boolean isC = MainActivity.settings.getBoolean("isC",true);
        Log.e("isC", String.valueOf(isC));
        if(isC) {
            //拿到選取的數字
            highItems = getResources().getStringArray(R.array.highc);
            highTemp = Float.parseFloat(highItems[MainActivity.settings.getInt("highListPostion", 4)]);
            Log.e("highTemp", String.valueOf(highTemp));
            lowItems = getResources().getStringArray(R.array.lowc);
            lowTemp = Float.parseFloat(lowItems[MainActivity.settings.getInt("lowListPostion", 2)]);
            Log.e("lowTemp", String.valueOf(lowTemp));
            yMin = Double.valueOf(30);
            yMax = Double.valueOf(45);
            unit.setText(getResources().getString(R.string.Temperature_unit)+"°C");
        }else{
            highItems = getResources().getStringArray(R.array.highf);
            highTemp = Float.parseFloat(highItems[MainActivity.settings.getInt("highListPostion", 4)]);
            Log.e("highTemp", String.valueOf(highTemp));
            lowItems = getResources().getStringArray(R.array.lowf);
            lowTemp = Float.parseFloat(lowItems[MainActivity.settings.getInt("lowListPostion", 2)]);
            Log.e("lowTemp", String.valueOf(lowTemp));
            yMin = Double.valueOf(90);
            yMax = Double.valueOf(100);
            unit.setText(getResources().getString(R.string.Temperature_unit)+"°F");
        }

        sqLite=new SQLite(getContext());

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        RLayout.removeAllViews();
        time.clear();
        temper.clear();

        StartTime = t.split("-");
        timeforline[0] = StartTime[0] + "/" + StartTime[1] + "/" + StartTime[2] + " " + StartTime[3] + ":00" + ":00";
        timeforline[1] = StartTime[0] + "/" + StartTime[1] + "/" + StartTime[2] + " " + StartTime[3] + ":59" + ":59";

        box_timeline = timeforline[0].split(" ");
        box_timeline_D = box_timeline[0].split("/");
        box_timeline_H = box_timeline[1].split(":");

        Year = Integer.valueOf(box_timeline_D[0])-1900;
        Month = Integer.valueOf(box_timeline_D[1])-1;
        Day = Integer.valueOf(box_timeline_D[2]);
        Hour = Integer.valueOf(box_timeline_H[0]);
        minDate = new Date(Year, Month, Day, Hour, 0, 0);
        maxDate = new Date(Year, Month, Day, Hour, 59, 59);

        minX = minDate.getTime();
        maxX = maxDate.getTime();

        UserTimeTemp = sqLite.ToGetUserTT(StartTime);
        if (UserTimeTemp.get(0).size()>0){
            getTimeData = StartTime[0]+"-"+StartTime[1]+"-"+StartTime[2]+"-"+StartTime[3];
            currentDate.setText(Integer.parseInt(StartTime[0])+"-"+Integer.parseInt(StartTime[1])+"-"+Integer.parseInt(StartTime[2])+" "+Integer.parseInt(StartTime[3])+getResources().getString(R.string.hour));

            xdata = new String[UserTimeTemp.get(0).size()];
            ydata = new String[UserTimeTemp.get(1).size()];

            for (int i = 0; i < UserTimeTemp.get(1).size(); i++) {
                xdata[i] = UserTimeTemp.get(0).get(i);
                ydata[i] = UserTimeTemp.get(1).get(i);
                //Log.e("xdata[i]",i+"--"+xdata[i]);
                //Log.e("ydata[i]",i+"--"+ydata[i]);
            }

            time_x = new String[]{Integer.parseInt(StartTime[0])+"-"+Integer.parseInt(StartTime[1])+"-"+Integer.parseInt(StartTime[2])+" "
                                    +Integer.parseInt(StartTime[3])+":"+Integer.parseInt("0")+":"+Integer.parseInt("0"),
                                    Integer.parseInt(StartTime[0])+"-"+Integer.parseInt(StartTime[1])+"-"+Integer.parseInt(StartTime[2])+" "
                                     +Integer.parseInt(StartTime[3])+":"+Integer.parseInt("59")+":"+Integer.parseInt("59")};
            temp_35 = new String[]{String.valueOf(lowTemp), String.valueOf(lowTemp)};
            temp_38 = new String[]{String.valueOf(highTemp), String.valueOf(highTemp)};

            InputXData(xdata);
            InputYData(ydata);

            InputXData(time_x);
            InputYData(temp_35);

            InputXData(time_x);
            InputYData(temp_38);

            dataset = new XYMultipleSeriesDataset();
            renderer = new XYMultipleSeriesRenderer();

            buildDatset(titles);
            buildRenderer(colors, styles, true);
            chart_view = null;
            if (chart_view == null) {
                RLayout.setVisibility(View.VISIBLE);
                unit.setVisibility(View.VISIBLE);
                currentDate.setVisibility(View.VISIBLE);
                LLayout.setVisibility(View.VISIBLE);
                min.setVisibility(View.VISIBLE);
                setChartSettings(renderer, getResources().getString(R.string.Temperature_graph), getResources().getString(R.string.time), getResources().getString(R.string.temperature)+"(℃)", minX, maxX, yMin, yMax, Color.BLACK);// 定義折線圖
                chart_view = ToGetChart(this.getContext().getApplicationContext(), "mm");
                RLayout.addView(chart_view);
            }
        }else{
            new AlertDialog.Builder(getContext())
                    .setTitle(getResources().getString(R.string.chinsice0))
                    .setMessage(getResources().getString(R.string.notempdata))
                    .setPositiveButton(getResources().getString(R.string.confirm_button),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getTimeColumn(4,getTimeData);
                        }
                    }).show();
        }
    }

    public void InputXData(String[] x) {
        //time.clear();
        line_x = new Date[x.length];
        for (int i = 0; i < x.length; i++) {
            //Log.e("InputXData",x[i]);
            String str = x[i];
            try {
                line_x[i] = sdf.parse(str);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        time.add(line_x);
    }

    public void InputYData(String[] y) {
        //temper.clear();
        line_y = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            line_y[i] = Double.parseDouble(y[i]);
        }
        temper.add(line_y);
    }

    public void buildDatset(String tittle[]) {
        dataset.removeSeries(time_series);
        if (time_series!=null){
            Log.e("time_series","clear");
            time_series.clear();
        }

        Time t = new Time();
        t.setToNow();

        int length = time.size();
        for (int i = 0; i < length; i++) {
            time_series = new TimeSeries(titles[i]);

            double[] Vy = temper.get(i);
            Date[] Vx = time.get(i);

            int seriesLength = Vy.length;
            for (int j = 0; j < seriesLength; j++) {
                time_series.add(Vx[j], Vy[j]);
            }
            dataset.addSeries(time_series);
        }
    }

    public void buildRenderer(int[] colors, PointStyle[] styles, boolean fill) {
        int length = colors.length;
        XYSeriesRenderer r = null;
        renderer.removeSeriesRenderer(r);
        for (int i = 0; i < length; i++) {
            r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            r.setFillPoints(fill);
            r.setDisplayChartValues(false);         //是否顯示圖標上的數據
            r.setLineWidth(2 * dm.density);        //設置線的寬度
            //r.setChartValuesTextSize(10 * dm.density);          // 數值的文字大小
            r.setFillBelowLine(false);		       //是否填充折線圖的下方
            //r.setFillBelowLineColor(colors[i]);                       //填充的顏色，如果不設置就默認與線的顏色一致

            if(i==length-1){
                r.setFillBelowLine(false);
            }
            renderer.addSeriesRenderer(r);         // 增加一个renderer到multiple  renderer中
        }
    }

    // 定義折線圖名稱
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
                                    String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor) {
        renderer.setYLabelsAlign(Paint.Align.RIGHT, 0); //設置標籤居Y軸的方向
        renderer.setXAxisMin(minX);                             // X軸顯示最小值
        renderer.setXAxisMax(maxX);                             // X軸顯示最大值
        renderer.setXLabelsColor(Color.BLACK);                 // X軸線顏色
        renderer.setYAxisMin(yMin);                              // Y軸顯示最小值
        renderer.setYAxisMax(yMax);                              // Y軸顯示最大值
        renderer.setAxesColor(axesColor);                        // 設定坐標軸顏色
        renderer.setYLabelsColor(0, Color.BLACK);       // Y軸線顏色
        renderer.setLabelsColor(Color.BLACK);                  // 設定標籤顏色
        renderer.setMarginsColor(Color.rgb(153,217,234)); // 設定背景顏色
        renderer.setApplyBackgroundColor(true);                   // 允許設置背景顏色;
        renderer.setGridColor(Color.GRAY);                     // 設置網格顏色
        renderer.setShowGrid(true);                              // 設定格線
        renderer.setLabelsTextSize(14 * dm.density);            // x.y軸字體大小
        renderer.setPointSize(0);                                // 點的大小
        //renderer.setChartValuesTextSize(45);                     // 點的值的文字大小
        renderer.setShowLegend(false);                            // 不顯示底部說明
        renderer.setXLabels(12);                                 // 設置X軸顯示的刻度標簽的個數
        renderer.setYLabels(8);                                  // 設置Y軸顯示的刻度標簽的個數
        renderer.setMargins(new int[] { 0, 65, 0, 55 });         // 設置外邊距離，顺序為：上左下右
        renderer.setZoomButtonsVisible(false);                    // 是否顯示放大縮小按鈕
        renderer.setZoomRate((float) 3.0);                        // 放大幾倍
        renderer.setPanLimits(new double[]{ minX, maxX, yMin-20, yMax+20});  // 設置拖動时X軸Y軸允許的最大值最小值
        renderer.setZoomLimits(new double [] { minX, maxX, yMin-20, yMax+20});// 設置放大缩小时X軸Y軸允許的最大最小值
    }

    public View ToGetChart(Context a, String b) {
        View view = ChartFactory.getTimeChartView(a, dataset, renderer, b);
        return view;
    }

}
