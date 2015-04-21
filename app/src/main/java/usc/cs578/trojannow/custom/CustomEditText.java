package usc.cs578.trojannow.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.EditText;

import usc.cs578.com.trojannow.R;

/*
 * Created by Ekasit_Ja on 17-Apr-15.
 */
public class CustomEditText extends EditText {

    private Paint mPaint;
    int widthMsSize;
    int heightMsSize;

    // we need this constructor for LayoutInflater
    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(getResources().getColor(R.color.red_viterbi));
    }

    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        // Extract the Ms (Measure Spec) parameters
        widthMsSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMsSize = MeasureSpec.getSize(heightMeasureSpec);

        // Satisfy contract by calling setMeasuredDimension
        setMeasuredDimension(widthMsSize,
                heightMsSize);
    }

    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawLine(5, heightMsSize - 10, widthMsSize - 5, heightMsSize - 10, mPaint); //draw underline
        /*canvas.drawLine(8, heightMsSize - 10, 8, heightMsSize - 20, mPaint); //draw left corner
        canvas.drawLine(widthMsSize - 8, heightMsSize - 10, widthMsSize - 8, heightMsSize - 20, mPaint); //draw right corner*/
        super.onDraw(canvas);
    }
}