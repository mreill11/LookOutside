package com.example.matt.lookoutside;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

public class SkyconsDrawable extends Drawable implements Animatable {

    public static final float STROKE = 0.08f;
    private static final double TAU = 2.0 * Math.PI;
    private static final double TWO_OVER_SQRT_2 = 2.0 / Math.sqrt(2);
    private boolean mIsRunning = false;

    PorterDuffXfermode dst_out = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    PorterDuffXfermode src_over = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);

    Paint mPaint = new Paint() {{
        setStrokeCap(Paint.Cap.ROUND);
        setStrokeJoin(Paint.Join.ROUND);
        setStyle(Paint.Style.STROKE);
        setAntiAlias(true);
    }};



    Path path = new Path();
    private Bitmap b;
    private Canvas c;
    private RectF rectf = new RectF();

    @Override
    public void start() {
        onBoundsChange(getBounds());
        if (!isRunning()) {
            mIsRunning = true;
            scheduleSelf(mUpdater, SystemClock.uptimeMillis() + 1000 / 60);
            invalidateSelf();
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            mIsRunning = false;
            unscheduleSelf(mUpdater);
        }
    }

    @Override
    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int w = bounds.width();
        int h = bounds.height();

        int oldw = (c == null) ? 0 : c.getWidth();
        int oldh = (c == null) ? 0 : c.getHeight();

        if (w != oldw || h != oldh) {
            b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            c = new Canvas(b);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        b.eraseColor(Color.TRANSPARENT);
        onUpdate(c, System.currentTimeMillis(), 0xff000000);
        canvas.drawBitmap(b, 0, 0, null);
    }

    public void onUpdate(Canvas aCanvas, long time, int color) {}

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    void sun(Canvas ctx, long t, float cx, float cy,float cw,float s,int color) {
        double time = (((double) t) / 120000.0);
        float a = cw * 0.25f - s * 0.5f,
                b = cw * 0.32f + s * 0.5f,
                c = cw * 0.50f - s * 0.5f;
        double p, cos, sin;

        mPaint.setColor(color);
        mPaint.setStrokeWidth(s);

        ctx.drawCircle(cx, cy, a, mPaint);
        for(float i = 7; i >= 0; i--) {
            p = (time + (i / 8.0)) * TAU;
            cos = Math.cos(p);
            sin = Math.sin(p);
            ctx.drawLine((float) (cx + cos * b),
                    (float) (cy + sin * b),
                    (float) (cx + cos * c),
                    (float) (cy + sin * c),
                    mPaint);
        }
    }

    void snow(Canvas ctx, double t, double cx, double cy, double cw, float s, int color) {
        t /= 3000;

        double a  = cw * 0.16,
                b  = s * 0.75,
                u  = t * TAU * 0.7,
                ux = Math.cos(u) * b,
                uy = Math.sin(u) * b,
                v  = u + TAU / 3,
                vx = Math.cos(v) * b,
                vy = Math.sin(v) * b,
                w  = u + TAU * 2 / 3,
                wx = Math.cos(w) * b,
                wy = Math.sin(w) * b, p, x, y;

        mPaint.setColor(color);
        mPaint.setStrokeWidth(s * 0.5f);

        for(float i = 3; i >= 0; --i) {
            p = (t + i / 4) % 1;
            x = cx + Math.sin((p + i / 4) * TAU) * a;
            y = cy + p * cw;

            ctx.drawLine((float) (x - ux), (float) (y - uy), (float) (x + ux), (float) (y + uy), mPaint);
            ctx.drawLine((float) (x - vx), (float) (y - vy), (float) (x + vx), (float) (y + vy), mPaint);
            ctx.drawLine((float) (x - wx), (float) (y - wy), (float) (x + wx), (float) (y + wy), mPaint);
        }
    }

    void clouds(Canvas ctx, double t, float cx, float cy, float cw, float s, int color) {
        float a = cw * 0.21f, b = cw * 0.12f, c = cw * 0.24f, d = cw * 0.28f;

        mPaint.setColor(color);
        mPaint.setStrokeWidth(s);
        mPaint.setStyle(Paint.Style.FILL);

        t  /= 30000;
        puffs(ctx, t, cx, cy, a, b, c, d);

        mPaint.setXfermode(dst_out);

        puffs(ctx, t, cx, cy, a, b, c - s, d - s);

        mPaint.setXfermode(src_over);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    void fogbank(Canvas ctx, double t, double cx, double cy, double cw, double s, int color) {
        t /= 30000;

        double a = cw * 0.21f,
                b = cw * 0.06f,
                c = cw * 0.21f,
                d = cw * 0.28f;

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        puffs(ctx, t, cx, cy, a, b, c, d);
        mPaint.setXfermode(dst_out);
        puffs(ctx, t, cx, cy, a, b, c - s, d - s);
        mPaint.setXfermode(src_over);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void puffs(Canvas ctx, double t, double cx, double cy, double a, double b, double c, double d) {
        for(double i = 4; i >= 0; --i) {
            double time = (t + i / 5.0);
            puff(ctx, time, cx, cy, a, b, c, d);
        }
    }

    private void puff(Canvas ctx, double t, double cx, double cy, double rx, double ry, double rmin, double rmax) {
        double cos = Math.cos(t * TAU), sin = Math.sin(t * TAU);

        rmax -= rmin;

        ctx.drawCircle((float) (cx - sin * rx), (float) (cy + cos * ry + rmax * 0.5),
                (float) (rmin + (1 - cos * 0.5) * rmax), mPaint);
    }


    void rain(Canvas ctx, double t, double cx, double cy, double cw, float s, int color) {
        t /= 1350.0;

        double a = cw * 0.16,
                b = TAU * 11.0/12.0,
                c = TAU * 7.0/12.0;

        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        for (double i = 3; i >= 0; --i) {
            double p = (t + i/4.0) % 1;
            double x = cx + ((i - 1.5) / 1.5) * ((i == 1 || i == 2) ? -1 : 1) * a;
            double y = cy + p * p * cw;

            path.reset();
            path.moveTo((float) x, (float) (y - s * 1.5));
            rectf.left = (float) (x - s * 0.75);
            rectf.right = (float) (x + s * 0.75);
            rectf.top = (float) (y - s * 1.5);
            rectf.bottom = (float) (y + s * 0.75);
            path.addArc(rectf, (float) b, (float) c);
            ctx.drawPath(path, mPaint);
        }
    }

    void moon(Canvas ctx, double t, double cx, double cy, double cw, float s, int color) {
        t /= 15000;

        double a = cw * 0.29 - s * 0.5,
                b = cw * 0.05,
                c = Math.cos(t * TAU),
                p = c * TAU / -16;

        cx += c * b;

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(s * 0.5f);

        rectf.top = (float) (cy - a);
        rectf.bottom = (float) (cy + a);
        rectf.left = (float) (cx - a);
        rectf.right = (float) (cx + a);
        double sa = Math.toDegrees(p + TAU / 8);
        double ea = Math.toDegrees(p + TAU * 7 / 8);

        ctx.drawArc(rectf, (float) sa, (float) (ea - sa),  false, mPaint);

        cx = cx + Math.cos(p) * a * TWO_OVER_SQRT_2;
        cy = cy + Math.sin(p) * a * TWO_OVER_SQRT_2;

        sa = Math.toDegrees(p + TAU * 5 / 8);
        ea = Math.toDegrees(p + TAU * 3 / 8);

        rectf.top = (float) (cy - a);
        rectf.bottom = (float) (cy + a);
        rectf.left = (float) (cx - a);
        rectf.right = (float) (cx + a);

        ctx.drawArc(rectf, (float) sa, (float) (ea - sa),  false, mPaint);
    }

    /*
    void swoosh(Canvas ctx, double t, double cx, double cy, double cw, float s, int index, int total, int color) {
        t /= 2500;
        double[] path = {WIND_PATHS[index],
                a = (t + index - WIND_OFFSETS[index].start) % total,
                c = (t + index - WIND_OFFSETS[index].end  ) % total,
                e = (t + index                            ) % total,
                b, d, f, i};
        ctx.strokeStyle = color;
        ctx.lineWidth = s;
        ctx.lineCap = "round";
        ctx.lineJoin = "round";
        if(a < 1) {
            ctx.beginPath();
            a *= path.length / 2 - 1;
            b  = Math.floor(a);
            a -= b;
            b *= 2;
            b += 2;
            ctx.moveTo(
                    cx + (path[b - 2] * (1 - a) + path[b    ] * a) * cw,
                    cy + (path[b - 1] * (1 - a) + path[b + 1] * a) * cw
            );
            if(c < 1) {
                c *= path.length / 2 - 1;
                d  = Math.floor(c);
                c -= d;
                d *= 2;
                d += 2;
                for(i = b; i !== d; i += 2)
                    ctx.lineTo(cx + path[i] * cw, cy + path[i + 1] * cw);
                ctx.lineTo(
                        cx + (path[d - 2] * (1 - c) + path[d    ] * c) * cw,
                        cy + (path[d - 1] * (1 - c) + path[d + 1] * c) * cw
                );
            }
            else
                for(i = b; i !== path.length; i += 2)
                    ctx.lineTo(cx + path[i] * cw, cy + path[i + 1] * cw);
            ctx.stroke();
        }
        else if(c < 1) {
            ctx.beginPath();
            c *= path.length / 2 - 1;
            d  = Math.floor(c);
            c -= d;
            d *= 2;
            d += 2;
            ctx.moveTo(cx + path[0] * cw, cy + path[1] * cw);
            for(i = 2; i !== d; i += 2)
                ctx.lineTo(cx + path[i] * cw, cy + path[i + 1] * cw);
            ctx.lineTo(
                    cx + (path[d - 2] * (1 - c) + path[d    ] * c) * cw,
                    cy + (path[d - 1] * (1 - c) + path[d + 1] * c) * cw
            );
            ctx.stroke();
        }
        if(e < 1) {
            e *= path.length / 2 - 1;
            f  = Math.floor(e);
            e -= f;
            f *= 2;
            f += 2;
            leaf(
                    ctx,
                    t,
                    cx + (path[f - 2] * (1 - e) + path[f    ] * e) * cw,
                    cy + (path[f - 1] * (1 - e) + path[f + 1] * e) * cw,
                    cw,
                    s,
                    color
            );
        }
    }
    */

    void sleet(Canvas ctx, double t, double cx, double cy, double cw, double s, int color) {
        t /= 750;

        double a = cw * 0.1875,
                b = TAU * 11 / 12,
                c = TAU *  7 / 12,
                i, p, x, y;

        mPaint.setColor(color);
        mPaint.setStrokeWidth((float)(s * 0.5));

        for(i = 4; i > 0; i-- ) {
            p = (t + i / 4) % 1;
            x = Math.floor(cx + ((i - 1.5) / 1.5) * (i == 1 || i == 2 ? -1 : 1) * a) + 0.5;
            y = cy + p * cw;
            ctx.drawLine((float) x, (float) (y - s * 1.5), (float) x, (float) (y + s * 1.5), mPaint);
        }
    }

    public static final class ClearDay extends SkyconsDrawable  {
        public void onUpdate(Canvas ctx, long t, int color) {
            int w = ctx.getWidth();
            int h = ctx.getHeight();
            int s = Math.min(w, h);

            this.sun(ctx, t, w * 0.5f, h * 0.5f, s, s * STROKE, color);
        }
    };

    public static final class PartlyCloudy extends SkyconsDrawable {
        public void onUpdate(Canvas ctx, long t, int color) {
            int w = ctx.getWidth();
            int h = ctx.getHeight();
            int s = Math.min(w, h);

            this.sun(ctx, t, w * 0.625f, h * 0.375f, s * 0.75f, s * STROKE, color);
            this.clouds(ctx, t, w * 0.375f, h * 0.625f, s * 0.75f, s * STROKE, color);
        }
    };

    public static final class Cloudy extends SkyconsDrawable {
        public void onUpdate(Canvas ctx, long t, int color) {
            int w = ctx.getWidth();
            int h = ctx.getHeight();
            int s = Math.min(w, h);

            this.clouds(ctx, t, w * 0.5f, h * 0.5f, s, s * STROKE, color);
        }
    };


    public static final class Fog extends SkyconsDrawable {
        public void onUpdate(Canvas ctx, long time, int color) {
            double w = ctx.getWidth(),
                    h = ctx.getHeight(),
                    s = Math.min(w, h),
                    k = s * STROKE;

            double t = time;
            fogbank(ctx, time, w * 0.5, h * 0.32, s * 0.75, k, color);

            t /= 5000;

            double a = Math.cos((t       ) * TAU) * s * 0.02,
                    b = Math.cos((t + 0.25) * TAU) * s * 0.02,
                    c = Math.cos((t + 0.50) * TAU) * s * 0.02,
                    d = Math.cos((t + 0.75) * TAU) * s * 0.02,
                    n = h * 0.936,
                    e = Math.floor(n - k * 0.5) + 0.5,
                    f = Math.floor(n - k * 2.5) + 0.5;

            mPaint.setColor(color);
            mPaint.setStrokeWidth((float)k);

            ctx.drawLine((float) (a + w * 0.2 + k * 0.5), (float) e, (float) (b + w * 0.8 - k * 0.5), (float) e, mPaint);
            ctx.drawLine((float) (c + w * 0.2 + k * 0.5), (float) f, (float) (d + w * 0.8 - k * 0.5), (float) f, mPaint);
        }
    };

    public static final class Sleet extends SkyconsDrawable {
        public void onUpdate(Canvas ctx, long t, int color) {
            int w = ctx.getWidth();
            int h = ctx.getHeight();
            int s = Math.min(w, h);

            sleet(ctx, t, w * 0.5, h * 0.37, s * 0.9, s * STROKE, color);
            clouds(ctx, t, (float) (w * 0.5), (float) (h * 0.37), (float) (s * 0.9), s * STROKE, color);
        }
    }

    public static final class Rain extends SkyconsDrawable {
        public void onUpdate(Canvas ctx, long t, int color) {
            int w = ctx.getWidth();
            int h = ctx.getHeight();
            int s = Math.min(w, h);

            this.rain(ctx, t, w * 0.5f, h * 0.37f, s * 0.9f, s * STROKE, color);
            this.clouds(ctx, t, w * 0.5f, h * 0.37f, s * 0.9f, s * STROKE, color);
        }
    };

    public static final class Snow extends SkyconsDrawable {
        public void onUpdate(Canvas ctx, long t, int color) {
            int w = ctx.getWidth();
            int h = ctx.getHeight();
            int s = Math.min(w, h);

            this.snow(ctx, t, w * 0.5f, h * 0.37f, s * 0.9f, s * STROKE, color);
            this.clouds(ctx, t, w * 0.5f, h * 0.37f, s * 0.9f, s * STROKE, color);
        }
    };

    public static final class ClearNight extends SkyconsDrawable  {
        public void onUpdate(Canvas ctx, long t, int color) {
            int w = ctx.getWidth();
            int h = ctx.getHeight();
            int s = Math.min(w, h);

            this.moon(ctx, t, w * 0.5f, h * 0.5f, s, s * STROKE, color);
        }
    };

    public static final class PartlyCloudyNight extends SkyconsDrawable {
        public void onUpdate(Canvas ctx, long t, int color) {
            int w = ctx.getWidth();
            int h = ctx.getHeight();
            int s = Math.min(w, h);

            this.moon(ctx, t, w * 0.625f, h * 0.375f, s * 0.75f, s * STROKE, color);
            this.clouds(ctx, t, w * 0.375f, h * 0.625f, s * 0.75f, s * STROKE, color);
        }
    };

    Runnable mUpdater = new Runnable() {
        @Override
        public void run() {
            scheduleSelf(mUpdater, SystemClock.uptimeMillis() + 30);
            invalidateSelf();
        }
    };
}
