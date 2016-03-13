package apsu.csci.iwanski.simon;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Iwanski on 3/13/2016.
 */





public class ButtonView extends View {


    private static final int BUTTON_GRID_SIZE=2;
    private static final float BUTTON_PADDING=0.01f;
    private float buttonSize;
    private float size;

    //variables for scaling of pictures

    private Bitmap redOn;
    private Bitmap redOff;
    private Bitmap blueOn;
    private Bitmap blueOff;
    private Bitmap greenOn;
    private Bitmap greenOff;
    private Bitmap yellowOn;
    private Bitmap yellowOff;


    //Creates background

    private Simon model;

    public ButtonView(Context context){
        super (context);
        init();

    }
    public ButtonView(Context context, AttributeSet attrs){
        super (context, attrs);
        init();

    }
    public ButtonView(Context context, AttributeSet attrs, int defStyleAttr){
        super (context, attrs, defStyleAttr);
        init();
    }


    private void init(){
        initpictures();
    }
    private void initpictures(){
        Resources resources = getContext().getResources();

        //used to scale pictures to different phone sizes
        redOff = BitmapFactory.decodeResource(resources, R.drawable.red_off);
        blueOn = BitmapFactory.decodeResource(resources, R.drawable.blue_on);
        blueOff = BitmapFactory.decodeResource(resources, R.drawable.blue_off);
        greenOn = BitmapFactory.decodeResource(resources, R.drawable.green_on);
        greenOff = BitmapFactory.decodeResource(resources, R.drawable.green_off);
        yellowOn = BitmapFactory.decodeResource(resources, R.drawable.yellow_on);
        yellowOff = BitmapFactory.decodeResource(resources, R.drawable.yellow_off);

    }


    public void setSimonModel(Simon model){
        if (model!=null){
            model.removeListener(this);
        }
        this.model=model;
        if(model!=null){
            model.addListener(this);
        }
    }

    //creating buttons
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        size=canvas.getClipBounds().width();
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(size,size);
        drawButtons(canvas);
        canvas.restore();
    }
    private void drawButtons(Canvas canvas){
        for (int row=0; row<BUTTON_GRID_SIZE; row++){
            for (int column=0; column<BUTTON_GRID_SIZE; column++){
                drawButtons(canvas,row,column);
            }
        }
    }
    private void drawButtons(Canvas canvas, int row, int column) {
        buttonSize=1.0f/BUTTON_GRID_SIZE;

        float buttonTop=row*buttonSize;
        float buttonLeft=column*buttonSize;

        float buttontop=buttonTop+BUTTON_PADDING;
        float buttonleft=buttonLeft+BUTTON_PADDING;

        float Buttonsize=(buttonSize-BUTTON_PADDING *2);

        Bitmap bitmap = getBitmapForButton(row,column);

        float pixelSize=canvas.getClipBounds().width();
        float ScaleX=(pixelSize/bitmap.getWidth())*Buttonsize;
        float ScaleY=(pixelSize/bitmap.getHeight())*Buttonsize;

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(ScaleX, ScaleY);
        canvas.drawBitmap(bitmap, buttonleft / ScaleX, buttontop / ScaleY, null);
        canvas.restore();

    }

    private Bitmap getBitmapForButton(int row, int column) {
       boolean pressed=model.isButtonPressed(getButtonIndex(row,column));
        int buttonnumber=getButtonIndex(row, column);
        switch(buttonnumber) {
            case 1:
                return pressed ?
                        greenOn : greenOff;
            case 2:
                return pressed ?
                        yellowOn : yellowOff;
            case 3:
                return pressed ?
                        redOn : redOff;
            case 4:
                return pressed ?
                        blueOn : blueOff;
            default:
                //this may not work...double check once error is produced

                return null;
            Log.i("SWITCH", "Check default status on switch statement ");
        }
    }
    private int getButtonIndex(int row, int column){
        return row*BUTTON_GRID_SIZE+column;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);

        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private int chooseDimension(int chooseMode, int chooseSize) {
        if (chooseMode == MeasureSpec.AT_MOST || chooseMode == MeasureSpec.EXACTLY) {
            return chooseSize;
        } else {
            //this means it was unspecified
            return 300;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int buttonIndex = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                buttonIndex = getCoords(event.getX(), event.getY());
                if (buttonIndex != -1) {
                    model.pressButton(buttonIndex);
                }
                return true;
            case MotionEvent.ACTION_UP:
                buttonIndex = getCoords(event.getX(), event.getY());
                if (buttonIndex != -1) {
                    model.releaseButton(buttonIndex);
                }
                model.releaseAllButtons();
                return true;

        }
        return false;
    }

    private int getCoords(float x, float y) {
        float ScaledX=x/size;
        float ScaledY=y/size;

        float areaX=Math.floor(ScaledX/buttonSize);
        float areaY=Math.floor(ScaledY/buttonSize);

        return getButtonIndex((int) areaY, (int) areaX);

    }
    @Override
    public void buttonStateChanged(int index){
        invalidate();
    }

    @Override
    public void multipleButtonStateChanged(){
        invalidate();
    }

}
