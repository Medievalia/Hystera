package br.edu.fatecsaocaetano.hystera.navegabilidade;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import java.util.Set;

public class EventDecorator implements DayViewDecorator {

    private final Set<CalendarDay> dates;
    private final int color;

    public EventDecorator(int color, Set<CalendarDay> dates) {
        this.color = color;
        this.dates = dates;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(createCircleDrawable(color));
    }

    private Drawable createCircleDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }
}

