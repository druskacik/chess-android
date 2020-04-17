package com.example.rbert.chessrandom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends Activity {

    int sirka, vyska;
    int rozmer;
    int hore, lavo;
    int[] tk = new int[2];
    int naTahu = 0;
    boolean jeOznacenaFigurka = false;
    boolean jeSach = false;
    int[] krali = new int[2];
    Paint pnt = new Paint();
    Bitmap btm;
    Canvas cnv;
    ImageView img;
    static RelativeLayout rl;
    int[][] plocha = {{10,8,9,11,12,9,8,10},
        {7,7,7,7,7,7,7,7},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {1,1,1,1,1,1,1,1},
        {4,2,3,5,6,3,2,4}};
    Policko[][] doska = new Policko[8][8];

    public void vykresliPlochu(ImageView iv) {
        int i,j;
        Log.i("Malujem", "plochu");
        btm = Bitmap.createBitmap(sirka, sirka + hore, Bitmap.Config.ARGB_8888);
        iv.setLayoutParams(new RelativeLayout.LayoutParams(sirka, sirka + hore));     //bitmap a imageview musia mat rovnake rozmery
        iv.setImageBitmap(btm);
        cnv = new Canvas(btm);
        pnt.setColor(Color.BLACK);
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                doska[i][j].namaluj(i,j,cnv,pnt, false);
                if (doska[i][j].figurka != null) {
                    doska[i][j].figurka.kresli();
                }
            }
        }
        Log.i(String.valueOf(krali[0]), String.valueOf(krali[1]));
        //Log.i("vykreslil", "som");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView)findViewById(R.id.imagePlocha);
        rl = findViewById(R.id.relativeLayout);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                doska[i][j] = new Policko();
                if (plocha[i][j] != 0) {
                    switch (plocha[i][j] % 6) {
                        case 1:
                            doska[i][j].figurka = (plocha[i][j] < 7) ? new Pesiak('b') : new Pesiak('c');
                            break;
                        case 2:
                            doska[i][j].figurka = (plocha[i][j] < 7) ? new Jazdec('b') : new Jazdec('c');
                            break;
                        case 3:
                            doska[i][j].figurka = (plocha[i][j] < 7) ? new Strelec('b') : new Strelec('c');
                            break;
                        case 4:
                            doska[i][j].figurka = (plocha[i][j] < 7) ? new Veza('b') : new Veza('c');
                            break;
                        case 5:
                            doska[i][j].figurka = (plocha[i][j] < 7) ? new Dama('b') : new Dama('c');
                            break;
                        case 0:
                            doska[i][j].figurka = (plocha[i][j] < 7) ? new Kral('b') : new Kral('c');
                            break;
                    }
                }
                if (doska[i][j].figurka != null) {
                    doska[i][j].figurka.nastavPoziciu(i, j);
                    if (doska[i][j].figurka.getClass().equals(Kral.class)) {
                        Log.i("niekde tu je", "kral");
                        krali[doska[i][j].figurka.farba] = i * 8 + j;
                    }
                }
            }
        }
        img.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { //zabezpeci mi, ze sirka a vyska nebudu 0
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    img.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    img.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                if (sirka == 0) {
                    sirka = img.getWidth();
                }
                if (vyska == 0) {
                    vyska = img.getHeight();
                }
                rozmer = sirka/8 - 5;
                hore = vyska/6;
                lavo = (sirka - rozmer*8)/2;
                vykresliPlochu(img);                // vykresli plochu
                Log.i("A zase", "som vykreslil");
                img.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (event.getX() < lavo || event.getX() >= lavo + 8*rozmer || event.getY() < hore || event.getY() >= hore + 8*rozmer)
                                return true;
                            int r = ((int)event.getY() - hore)/rozmer;
                            int s = ((int)event.getX() - lavo)/rozmer;
                            Log.i(String.valueOf(r), String.valueOf(s));
                            if (jeOznacenaFigurka) {
                                Log.i("som","tu");
                                if (doska[tk[0]][tk[1]].figurka.moznosti().contains(r*8 + s)) {
                                    doska[r][s].figurka = doska[tk[0]][tk[1]].figurka;
                                    doska[tk[0]][tk[1]].figurka = null;
                                    doska[r][s].figurka.nastavPoziciu(r,s);
                                    Log.i("som","kde treba");
                                    vykresliPlochu(img);
                                    skontrolujSach(r, s, naTahu);
                                    naTahu = 1 - naTahu;
                                    jeOznacenaFigurka = false;
                                }
                            }
                        }
                        return true;
                    }
                });
            }
        });
    }

    class Figurka {
        public int riadok;
        public int stlpec;
        public int farba;
        public ImageView imgView = new ImageView(getApplicationContext());
        public Drawable obr;
        public void nastavPoziciu(int r, int s) {
            this.riadok = r;
            this.stlpec = s;
        }
        public void kresli() {
            //imgView = new ImageView(getApplicationContext());
            rl.removeView(imgView);
            imgView.setImageDrawable(obr);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(rozmer, rozmer);
            lp.leftMargin = lavo + rozmer*stlpec;
            lp.topMargin = hore + rozmer*riadok;
            imgView.setLayoutParams(lp);
            rl.addView(imgView);
        }
        public List<Integer> moznosti() {
            return null;
        }
        public View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    if (moznosti() != null) {
                        Log.i("Moznosti",String.valueOf(moznosti()));
                    } else {
                        Log.i("Moznosti", "je null");
                    }
                    if (jeOznacenaFigurka) {
                        /*if (riadok == tk[0] && stlpec == tk[1]) {
                            break;
                        }*/
                        Log.i("Sem", "som");
                        if (naTahu != farba) {
                            if (doska[tk[0]][tk[1]].figurka.moznosti().contains(riadok*8 + stlpec)) {
                                rl.removeView(doska[riadok][stlpec].figurka.imgView);
                                doska[riadok][stlpec].figurka = doska[tk[0]][tk[1]].figurka;
                                doska[tk[0]][tk[1]].figurka = null;
                                doska[riadok][stlpec].figurka.nastavPoziciu(riadok,stlpec);
                                jeOznacenaFigurka = false;
                                vykresliPlochu(img);
                                skontrolujSach(riadok, stlpec, naTahu);
                                naTahu = 1 - naTahu;
                                return true;
                            }
                        }
                    }
                    if (naTahu == farba) {
                        jeOznacenaFigurka = true;
                        //doska[riadok][stlpec].namaluj(riadok, stlpec, cnv, pnt, true);
                        zvyrazniMoznosti(moznosti(), cnv, pnt);
                        tk[0] = riadok;
                        tk[1] = stlpec;
                        /*if (doska[riadok][stlpec].figurka.moznosti().contains(krali[1 - farba])) {
                            Toast.makeText(MainActivity.this, "Sach!!", Toast.LENGTH_SHORT).show();
                        }*/
                        Log.i("Zobrazil som", "kokso");
                    }
                    break;
                    case MotionEvent.ACTION_UP: Log.i("action","up");
                    //v.performClick();
                    break;
                    default: break;
                }
                return true;
            }
        };
    }
    class Pesiak extends Figurka {
        public Pesiak(char z) {
            obr = (z == 'b') ? getResources().getDrawable(R.drawable.bpesiak) : getResources().getDrawable(R.drawable.cpesiak);
            farba = (z == 'b') ? 0 : 1;
            imgView.setOnTouchListener(listener);
        }
        public List<Integer> moznosti() {
            List<Integer> mm = new ArrayList<Integer>();
            if (riadok == 0 || riadok == 7) return  null;
            int d = (farba == 0) ? -1 : 1;
            if (doska[riadok + d][stlpec].figurka == null) {
                mm.add((riadok + d)*8 + stlpec);
                if ((riadok == 6 && d == -1) || (riadok == 1 && d == 1)) {
                    if (doska[riadok + 2*d][stlpec].figurka == null) {
                        mm.add((riadok + 2*d)*8 + stlpec);
                    }
                }
            }
            for (int i = -1; i <= 1; i += 2) {
                if (stlpec + i >= 0 && stlpec + i <= 7 && doska[riadok + d][stlpec + i].figurka != null
                        && doska[riadok + d][stlpec + i].figurka.farba != farba) {
                    mm.add((riadok + d)*8 + stlpec + i);
                }
            }
            return mm;
        }
    }
    class Jazdec extends Figurka {
        public Jazdec(char z) {
            obr = (z == 'b') ? getResources().getDrawable(R.drawable.bjazdec) : getResources().getDrawable(R.drawable.cjazdec);
            farba = (z == 'b') ? 0 : 1;
            imgView.setOnTouchListener(listener);
        }
        public List<Integer> moznosti() {
            List<Integer> mm = new ArrayList<Integer>();
            int[] l = {-2,-1,1,2};
            for (int i : l) {
                for (int j : l) {
                    if (Math.abs(i + j)%2 == 1) {
                        if (riadok + i >= 0 && riadok + i <= 7 && stlpec + j >= 0 && stlpec + j <= 7) {
                            if (doska[riadok + i][stlpec + j].figurka == null || doska[riadok + i][stlpec + j].figurka.farba != farba) {
                                mm.add((riadok + i)*8 + stlpec + j);
                            }
                        }
                    }
                }
            }
            return mm;
        }
    }
    class Strelec extends Figurka {
        public Strelec(char z) {
            obr = (z == 'b') ? getResources().getDrawable(R.drawable.bstrelec) : getResources().getDrawable(R.drawable.cstrelec);
            farba = (z == 'b') ? 0 : 1;
            imgView.setOnTouchListener(listener);
        }
        public List<Integer> moznosti() {
            return strelcoveMoznosti(riadok, stlpec, farba);
        }
    }
    class Veza extends Figurka {
        public Veza(char z) {
            obr = (z == 'b') ? getResources().getDrawable(R.drawable.bveza) : getResources().getDrawable(R.drawable.cveza);
            farba = (z == 'b') ? 0 : 1;
            imgView.setOnTouchListener(listener);
        }
        public List<Integer> moznosti() {
            return vezineMoznosti(riadok, stlpec, farba);
        }
    }
    class Dama extends Figurka {
        public Dama(char z) {
            obr = (z == 'b') ? getResources().getDrawable(R.drawable.bdama) : getResources().getDrawable(R.drawable.cdama);
            farba = (z == 'b') ? 0 : 1;
            imgView.setOnTouchListener(listener);
        }
        public List<Integer> moznosti() {
            List<Integer> mm = new ArrayList<Integer>();
            mm.addAll(strelcoveMoznosti(riadok, stlpec, farba));
            mm.addAll(vezineMoznosti(riadok, stlpec, farba));
            return mm;
        }
    }
    class Kral extends Figurka {
        public Kral(char z) {
            obr = (z == 'b') ? getResources().getDrawable(R.drawable.bkral) : getResources().getDrawable(R.drawable.ckral);
            farba = (z == 'b') ? 0 : 1;
            imgView.setOnTouchListener(listener);
        }
        public List<Integer> moznosti()
        {
            List<Integer> mm = new ArrayList<Integer>();
            for (int i = -1; i <= 1; i++)
            {
                for (int j = -1; j <= 1; j++)
                {
                    if (riadok + i >= 0 && riadok + i <= 7 && stlpec + j >= 0 && stlpec + j <= 7 &&
                            (doska[riadok + i][stlpec + j].figurka == null || doska[riadok + i][stlpec + j].figurka.farba != farba))
                    {
                        mm.add((riadok + i)*8 + stlpec + j);
                    }
                }
            }
            return mm;
        }

        @Override
        public void nastavPoziciu(int r, int s) {
            this.riadok = r;
            this.stlpec = s;
            krali[farba] = r*8 + s;
        }
    }

    class Policko {
        public Figurka figurka;
        public int farba;
        public boolean oznacene;
        public void namaluj(int r, int s, Canvas canvas, Paint paint, boolean jeMedziMoznostami) {
            if (jeMedziMoznostami) {
                //canvas.drawRect(lavo + rozmer*s, hore + rozmer*r, lavo + rozmer*(s + 1), hore + rozmer*(r + 1), paint);
                if (doska[r][s].figurka == null) {
                    paint.setColor(Color.rgb(70, 160, 80));
                    //paint.setColor(R.);
                    canvas.drawCircle(lavo + rozmer * s + rozmer / 2, hore + rozmer * r + rozmer / 2, rozmer / 8, paint);
                } else {
                    paint.setColor(Color.rgb(70, 160, 80));
                    canvas.drawRect(lavo + rozmer*s, hore + rozmer*r, lavo + rozmer*(s + 1), hore + rozmer*(r + 1), paint);
                }
            } else {
                farba = ((r + s) % 2 == 0) ? Color.WHITE : Color.LTGRAY;
                paint.setColor(farba);
                canvas.drawRect(lavo + rozmer*s, hore + rozmer*r, lavo + rozmer*(s + 1), hore + rozmer*(r + 1), paint);
            }

        }
    }
    public void zvyrazniMoznosti(List<Integer> m, Canvas canvas, Paint paint) {     // z nejakeho dovodu to nefunguje, pozri sa na to zajtra
        if (jeOznacenaFigurka) {
            if (doska[tk[0]][tk[1]].figurka != null) {
                for (int i : doska[tk[0]][tk[1]].figurka.moznosti()) {
                    doska[i / 8][i % 8].namaluj(i / 8, i % 8, cnv, pnt, false);
                }
            }
        }
        for (int i : m) {
            Log.i("Zvyraznujem", String.valueOf(i));
            paint.setColor(Color.CYAN);
            doska[i/8][i%8].namaluj(i/8, i%8, cnv, pnt, true);
        }
        Log.i("Zvyraznujem", "Moznosti");
        img.invalidate();
    }
    public void skontrolujSach(int riadok, int stlpec, int farba) {
        if (doska[riadok][stlpec].figurka.moznosti().contains(krali[1 - farba])) {
            Toast.makeText(MainActivity.this, "Sach!!", Toast.LENGTH_SHORT).show();
            jeSach = true;
        } else {
            jeSach = false;
        }
    }
    public List<Integer> strelcoveMoznosti(int riadok, int stlpec, int ff)
    {
        List<Integer> mm = new ArrayList<Integer>();
        for (int i = -1; i <= 1; i += 2)
        {
            for (int j = -1; j <= 1; j += 2)
            {
                int rr = riadok;
                int ss = stlpec;
                while(rr + i >= 0 && rr + i <= 7 && ss + j >= 0 && ss + j <= 7 && doska[rr + i][ss + j].figurka == null)
                {
                    mm.add((rr + i)*8 + ss + j);
                    rr += i;
                    ss += j;
                }
                if (rr + i >= 0 && rr + i <= 7 && ss + j >= 0 && ss + j <= 7 && doska[rr + i][ss + j].figurka.farba != ff)
                {
                    mm.add((rr + i)*8 + ss + j);
                }
            }
        }
        return mm;
    }
    public List<Integer> vezineMoznosti(int riadok, int stlpec, int ff)
    {
        List<Integer> mm = new ArrayList<Integer>();
        for (int i = -1; i <= 1; i += 2)
        {
            int rr = riadok;
            int ss = stlpec;
            while (rr + i >= 0 && rr + i <= 7 && doska[rr + i][stlpec].figurka == null)
            {
                mm.add((rr + i)*8 + stlpec);
                rr += i;
            }
            if (rr + i >= 0 && rr + i <= 7 && doska[rr + i][stlpec].figurka.farba != ff)
            {
                mm.add((rr + i)*8 + stlpec);
            }
            while (ss + i >= 0 && ss + i <= 7 && doska[riadok][ss + i].figurka == null)
            {
                mm.add(riadok*8 + ss + i);
                ss += i;
            }
            if (ss + i >= 0 && ss + i <= 7 && doska[riadok][ss + i].figurka.farba != ff)
            {
                mm.add(riadok*8 + ss + i);
            }
        }
        return mm;
    }
}
