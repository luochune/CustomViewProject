package photo.king.com.customviewproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import util.DensityUtils;

/**
 * Created by luochune on 2018/1/23.
 * Introduction: 自定义等级view
 */
public class VipCustomView extends View {
    private int noArriveTextColor;
    private int arriveProgressLineColor;
    private int noArriveProgressLineColor;//还未达到等级的进度条颜色
    private Drawable noArriveNodeDrawable;//还未到达的圆心图标
    private Drawable arriveNodeDrawable;//已到达的圆心图标
    private Drawable currentNodeDrawabl;//当前所处等级图标
    private int nodeRadius;//节点半径
    private int currentNodeRadius;//当前节点半径
    private int currentNodeNO;//当前节点位置,从0 开始
    private int progressLineLength;//进度线条长度
    private int textMarginProgress;//底部文字距离顶部进度条的距离
    private int nodesNum;//节点数
    private int mWidth,mHeight;//控件宽高
    private int progressLineHeight;//进度条高度
    private Bitmap mBitmap; //mCanvas绘制在这上面
    private Paint nodeGrayPaint;//灰色节点和灰色进度线条画笔
    private Paint nodeOrangePaint;//橘色节点和橘色进度线条画笔
    private Paint textGrayPaint;//灰色字体画笔
    private Paint textOrangePaint;//橘色字体画笔
    private ArrayList<Node> nodes;
    private Canvas mCanvas;
    private String[] vipLevelAry;
    private Context mContext;
    private int vipDrawableLineLengh;//会员等级图标的宽和高像素
    private int paddingLeft=40;
    public VipCustomView(Context context) {
        this(context,null);

    }
    public VipCustomView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }
    public VipCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.vipcustomview);
        vipDrawableLineLengh=DensityUtils.dip2px(context,15);//会员等级图标的宽和高像素
        //nodesNum=mTypedArray.getInteger(R.styleable.vipcustomview_nodesNums, 7);
        currentNodeNO=mTypedArray.getInteger(R.styleable.vipcustomview_currentNodeNO,0);
        nodeRadius = mTypedArray.getDimensionPixelSize(R.styleable.vipcustomview_nodeRoundRadius,14);
        progressLineLength=mTypedArray.getDimensionPixelSize(R.styleable.vipcustomview_progressLineLength,140);
        currentNodeRadius=mTypedArray.getDimensionPixelSize(R.styleable.vipcustomview_currentNodeRadius,36);
        progressLineHeight=mTypedArray.getDimensionPixelSize(R.styleable.vipcustomview_progressLineHeight,4);
        textMarginProgress=mTypedArray.getDimensionPixelSize(R.styleable.vipcustomview_textMarginProgress,16);
        currentNodeDrawabl=mTypedArray.getDrawable(R.styleable.vipcustomview_currentNodeDrawable);
        arriveNodeDrawable=mTypedArray.getDrawable(R.styleable.vipcustomview_arriveNodeDrawable);
        noArriveNodeDrawable=mTypedArray.getDrawable(R.styleable.vipcustomview_noArriveNodeDrawable);
        noArriveProgressLineColor=mTypedArray.getColor(R.styleable.vipcustomview_noArriveProgressLineColor, Color.parseColor("#ececed"));
        arriveProgressLineColor=mTypedArray.getColor(R.styleable.vipcustomview_arriveProgressLineColor,Color.parseColor("#ffb31f"));
        noArriveTextColor=mTypedArray.getColor(R.styleable.vipcustomview_noArriveTextColor,Color.parseColor("#999999"));
        //进度条灰色画笔
        nodeGrayPaint=new Paint();
        nodeGrayPaint.setColor(noArriveProgressLineColor);
        nodeGrayPaint.setAntiAlias(true);
        nodeGrayPaint.setStyle(Paint.Style.FILL);
       // nodeGrayPaint.setStrokeWidth(4);
        nodeGrayPaint.setStrokeJoin(Paint.Join.ROUND); // 圆角
        nodeGrayPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角
        //进度条橘色画笔
        nodeOrangePaint=new Paint();
        nodeOrangePaint.setStyle(Paint.Style.FILL);
        nodeOrangePaint.setColor(arriveProgressLineColor);
        nodeOrangePaint.setAntiAlias(true);
        nodeOrangePaint.setStrokeJoin(Paint.Join.ROUND);
        nodeOrangePaint.setStrokeCap(Paint.Cap.ROUND);
        //灰色字体画笔
        textGrayPaint=new Paint();
        textGrayPaint.setColor(noArriveTextColor);
        textGrayPaint.setAntiAlias(true);
        textGrayPaint.setTextSize(DensityUtils.sp2px(mContext,15));
        //橘色字体画笔
        textOrangePaint=new Paint();
        textOrangePaint.setColor(arriveProgressLineColor);
        textOrangePaint.setAntiAlias(true);
        textOrangePaint.setTextSize(DensityUtils.sp2px(mContext,15));
        setVipLevelAry();
        /**
         *   <attr name="noArriveProgressLineColor" format="color"></attr> <!--未到达等级的进度条颜色-->
         <attr name="arriveProgressLineColor" format="color"></attr> <!--已到达等级的进度条颜色-->
         <attr name="noArriveTextColor" format="color"></attr><!--未到达字体颜色-->
         <attr name="nodeRadius" format="dimension"/> <!--非当前等级圆圈半径-->
         <attr name="currentProgressDrawable" format="reference"></attr><!--当前等级图标-->
         <attr name="arriveNodeDrawable" format="reference"></attr><!--已到达圆心点图标-->
         <attr name="noArriveNodeDrawable" format="reference"></attr><!--未到达圆心点图标-->
         <attr name="nodesNum" format="integer"/> <!-- 节点数量 -->
         <attr name="currNodeNO" format="integer"></attr> <!--当前所在节点数-->
         */
    }
    //会员等级名称数组
    public String[] getVipLevelAry() {
        return vipLevelAry;
    }

    public void setVipLevelAry() {
            //this.vipLevelAry = new String[]{"石头", "铜石", "银子", "金子", "铂子", "钻子", ""书子, "奇子"};
        this.vipLevelAry = new String[]{"石头", "铜石", "银子", "金子", "铂子"};
            nodesNum=vipLevelAry.length;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth=getMeasuredWidth();
       // int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        /*if(heightMode==MeasureSpec.EXACTLY)
        {
            mHeight=MeasureSpec.getSize(heightMeasureSpec);
        }*/
        mHeight=getMeasuredHeight();
       /* int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);*/
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mCanvas=new Canvas(mBitmap);
        nodes = new ArrayList<Node>();
        for(int i=0;i<nodesNum;i++)
        {
            Node node = new Node();
            if(i<currentNodeNO)
            {
                //已完成节点
                node.type=0;//未完成节点
            }else if(i==currentNodeNO)
            {
                //当前节点
                node.type=1;
            }else if(i>currentNodeNO)
            {
                //未完成节点
                node.type=2;
            }
            //节点坐标，所有节点y坐标都是currentNodeRadius
            if(i<currentNodeNO)
            {
                //小于当前节点坐标
                //node.mPoint=new Point(i*progressLineLength+(2*i-1)*nodeRadius,currentNodeRadius);
                // node.mPoint=new Point(i*progressLineLength+2*(i-1)*nodeRadius,0);  mHeight/2-nodeRadius
                node.mPoint=new Point((progressLineLength+2*nodeRadius)*i+paddingLeft,mHeight/2-nodeRadius);
            }else if(i==currentNodeNO)
            {
                //等于当前节点坐标 mHeight/2-currentNodeRadius
                node.mPoint=new Point(i*(2*nodeRadius+progressLineLength)+paddingLeft,mHeight/2-currentNodeRadius);
            }else if(i>currentNodeNO)
            {
                //大于当前节点坐标
                node.mPoint=new Point(i*(2*nodeRadius+progressLineLength)-2*nodeRadius+2*currentNodeRadius+paddingLeft,mHeight/2-nodeRadius);
            }
            nodes.add(node);
        }
       // setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       drawProgress();
        if(mBitmap!=null)
        {
            canvas.drawBitmap(mBitmap,new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight()),new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight()),nodeGrayPaint);
        }
        for(int i=0;i<nodes.size();i++) {
            Node node = nodes.get(i);
            Drawable drawable=getResDrawable(i);
            if (i < currentNodeNO)
            {
               //已完成的进度节点
                arriveNodeDrawable.setBounds(node.mPoint.x,node.mPoint.y,node.mPoint.x+2*nodeRadius,node.mPoint.y+2*nodeRadius);
                arriveNodeDrawable.draw(canvas);
                drawable.setBounds(node.mPoint.x+nodeRadius-vipDrawableLineLengh-5,node.mPoint.y+2*nodeRadius+textMarginProgress,node.mPoint.x+nodeRadius-5,node.mPoint.y+2*nodeRadius+textMarginProgress+vipDrawableLineLengh);
                //等级文字
                //int textY=node.mPoint.y+2*nodeRadius+textMarginProgress+(int)vipDrawableLineLengh/2+(int)((textOrangePaint.descent()-textOrangePaint.ascent())/2);
                int textY=node.mPoint.y+2*nodeRadius+textMarginProgress+vipDrawableLineLengh-(int)textOrangePaint.descent();
                canvas.drawText(vipLevelAry[i],node.mPoint.x+nodeRadius,textY,textOrangePaint);
                drawable.draw(canvas);
            }else if (i == currentNodeNO)
            {
               //当前进度节点
               currentNodeDrawabl.setBounds(node.mPoint.x,node.mPoint.y,node.mPoint.x+2*currentNodeRadius,node.mPoint.y+2*currentNodeRadius);
                currentNodeDrawabl.draw(canvas);
                //等级文字
                //int textY=node.mPoint.y+2*currentNodeRadius+textMarginProgress+(int)vipDrawableLineLengh/2+(int)((textOrangePaint.descent()-textOrangePaint.ascent())/2);
                //int textY=node.mPoint.y+2*currentNodeRadius+textMarginProgress+vipDrawableLineLengh-(currentNodeRadius-nodeRadius)*2-(int)textOrangePaint.descent();
                int textY=mHeight/2+nodeRadius+textMarginProgress+vipDrawableLineLengh-(int)textOrangePaint.descent();
                canvas.drawText(vipLevelAry[i],node.mPoint.x+currentNodeRadius,textY,textOrangePaint);
               // drawable.setBounds(node.mPoint.x+currentNodeRadius-vipDrawableLineLengh-5,node.mPoint.y+2*currentNodeRadius+textMarginProgress-(currentNodeRadius-nodeRadius)*2,node.mPoint.x+currentNodeRadius-5,node.mPoint.y+2*currentNodeRadius+textMarginProgress+vipDrawableLineLengh-(currentNodeRadius-nodeRadius)*2);
                drawable.setBounds(node.mPoint.x+currentNodeRadius-vipDrawableLineLengh-5,mHeight/2+nodeRadius+textMarginProgress,node.mPoint.x+currentNodeRadius-5,mHeight/2+nodeRadius+textMarginProgress+vipDrawableLineLengh);
                drawable.draw(canvas);
            }else if(i>currentNodeNO)
            {
              //未完成进度节点
               noArriveNodeDrawable.setBounds(node.mPoint.x,node.mPoint.y,node.mPoint.x+2*nodeRadius,node.mPoint.y+2*nodeRadius);
                noArriveNodeDrawable.draw(canvas);
                //等级文字
                //int textY=node.mPoint.y+2*nodeRadius+textMarginProgress+(int)vipDrawableLineLengh/2+(int)((textOrangePaint.descent()-textOrangePaint.ascent())/2);
                int textY=node.mPoint.y+2*nodeRadius+textMarginProgress+vipDrawableLineLengh-(int)textGrayPaint.descent();
                canvas.drawText(vipLevelAry[i],node.mPoint.x+nodeRadius,textY,textGrayPaint);
                drawable.setBounds(node.mPoint.x+nodeRadius-vipDrawableLineLengh-5,node.mPoint.y+2*nodeRadius+textMarginProgress,node.mPoint.x+nodeRadius-5,node.mPoint.y+2*nodeRadius+textMarginProgress+vipDrawableLineLengh);
                drawable.draw(canvas);
            }
        }
        /*Paint bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#f0f0f0"));
        canvas.drawRect(0, 0, mWidth, mHeight, bgPaint);*/
    }
    private Drawable getResDrawable(int i)
    {
        int[] grayDrawableRes=new int[]{R.mipmap.icon_vip_gray_0,R.mipmap.icon_vip_gray_1,R.mipmap.icon_vip_gray_2,R.mipmap.icon_vip_gray_3,R.mipmap.icon_vip_gray_4
         ,R.mipmap.icon_vip_gray_5,R.mipmap.icon_vip_gray_6,R.mipmap.icon_vip_gray_7};
        int[] orangeDrawableRes=new int[]{R.mipmap.icon_vip_orange_0,R.mipmap.icon_vip_orange_1,R.mipmap.icon_vip_orange_2,R.mipmap.icon_vip_orange_3,R.mipmap.icon_vip_orange_4,R.mipmap.icon_vip_orange_5
        ,R.mipmap.icon_vip_orange_6,R.mipmap.icon_vip_orange_7};
        Drawable drawable;
        if(i<=currentNodeNO)
        {
            //已达到节点
            drawable= ContextCompat.getDrawable(mContext,orangeDrawableRes[i]);
        }else
        {
            drawable=ContextCompat.getDrawable(mContext,grayDrawableRes[i]);
        }
        return drawable;
    }
    private void drawProgress() {
        //先画背景
      /*  Paint bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#f0f0f0"));
        mCanvas.drawRect(0, 0, mWidth, mHeight, bgPaint);*/
      //已到达前半截橙色线段
        mCanvas.drawRect(nodes.get(0).mPoint.x,mHeight/2-progressLineHeight/2,nodes.get(currentNodeNO).mPoint.x+currentNodeRadius,mHeight/2+progressLineHeight/2,nodeOrangePaint);
        //未到达后半截灰色线段
        mCanvas.drawRect(nodes.get(currentNodeNO).mPoint.x+currentNodeRadius,mHeight/2-progressLineHeight/2,nodes.get(nodes.size()-1).mPoint.x+nodeRadius,mHeight/2+progressLineHeight/2,nodeGrayPaint);
    }

    //节点实体对象
    class Node
    {
        Point mPoint;
        int type; //0:已完成  1:当前到达的进度节点 2: 未完成
    }
}
