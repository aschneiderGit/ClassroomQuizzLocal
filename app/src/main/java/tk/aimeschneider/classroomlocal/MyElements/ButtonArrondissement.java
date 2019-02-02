package tk.aimeschneider.classroomlocal.MyElements;
import android.content.Context;
import android.util.AttributeSet;

import tk.aimeschneider.classroomlocal.models_only.QuestionArrondissement;


public class ButtonArrondissement extends android.support.v7.widget.AppCompatButton {

    private  QuestionArrondissement questionArrond;

    public ButtonArrondissement(Context context) {
        super(context);
    }

    public ButtonArrondissement(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonArrondissement(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, width); // make it square
    }

    public QuestionArrondissement getQuestionArrond() {
        return questionArrond;
    }
    public void setQuestionArrond(QuestionArrondissement qa)
    {
        questionArrond = qa;
    }
}
