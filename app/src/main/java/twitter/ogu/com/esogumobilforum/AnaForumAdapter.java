package twitter.ogu.com.esogumobilforum;

/**
 * Created by eGo on 10/11/16.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AnaForumAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    String[] basliklar;
    int[] likelar;
    int[] yorumlar;
    String[] saatler;
    String[] acan;
    String[] acanurl;
    String nestedjsons="";
    String[] baslikicerik;
    String[] baslikid;
    String[] baslikresimleri;
    LayoutInflater inflater;

    //ListviewAdapter constructor
    //Gelen değerleri set ediyor
    public AnaForumAdapter(Context context, String[] basliklar, int[] likelar, int[] yorumlar, String[] saatler, String[] baslikresimleri,String[] acanurl,String[] acan,String[] baslikicerik,String[] baslikid,String nestedjsons) {
        this.context = context;
        this.basliklar = basliklar;
        this.likelar = likelar;
        this.baslikid=baslikid;
        this.baslikicerik=baslikicerik;
        this.nestedjsons=nestedjsons;
        this.yorumlar = yorumlar;
        this.acan = acan;
        this.acanurl = acanurl;
        this.saatler = saatler;
        this.baslikresimleri = baslikresimleri;
    }

    @Override
    public int getCount() {
        return basliklar.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {

        // Declare Variables
        TextView baslik;
        TextView like;
        TextView yorum;
        TextView saat;
        ImageView baslikresmi;
        ImageView acanresmi;
        String acann;
        Button gozat;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.belike, parent, false);//list_item_row dan yeni bir view oluşturuyoruz

        // oluşan itemviewin içindeki alanları Anasayfadan gelen değerler ile set ediyoruz
        //acanresmi=(ImageView)itemView.findViewById(R.id.rowfotokim);
        baslik = (TextView) itemView.findViewById(R.id.yourum);
        //like = (TextView) itemView.findViewById(R.id.rowlike);
        yorum = (TextView) itemView.findViewById(R.id.rowname);
        saat = (TextView) itemView.findViewById(R.id.rowtarih);
        gozat=(Button)itemView.findViewById(R.id.gozat);
        //baslikresmi = (ImageView) itemView.findViewById(R.id.yorumfoto);

        gozat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(parent.getContext(), AnaForumKonuIzle.class);
                i.putExtra("baslikicerik",baslikicerik[position]);
                i.putExtra("baslikid",baslikid[position]);
                i.putExtra("baslikresimurl",baslikresimleri[position]);
                i.putExtra("baslikacan",acan[position]);
                i.putExtra("baslikacanrul",acanurl[position]);
                i.putExtra("basliksaat",saatler[position]);
                i.putExtra("baslikbaslik",basliklar[position]);
                i.putExtra("nestedjsons",nestedjsons);
                Log.e("yollanan intent test:",basliklar[position]+acanurl[position]);
                Log.e("yollanan json test:",nestedjsons);
                parent.getContext().startActivity(i);
            }
        });
        yorum.setText(acan[position]);
        baslik.setText(basliklar[position]);
        // like.setText(""+likelar[position]);
        //yorum.setText(""+yorumlar[position]);
        new DownloadImage((ImageView)itemView.findViewById(R.id.yorumfoto)).execute("http://begodev.com/oguforum/"+baslikresimleri[position].replaceAll("[^\\p{L}\\p{Z}]","")+".jpg");
        new DownloadImage2((ImageView)itemView.findViewById(R.id.rowfotokim)).execute(acanurl[position]);
        System.out.print(acanurl[position]+"gogogooggogo");

        /**
        Picasso.with(context)
                .load(acanurl[position])
                   .into(acanresmi);
        Picasso.with(context)
                .load("http://begodev.com/oguforum/"+baslikresimleri[position].replaceAll("[^\\p{L}\\p{Z}]","")+".jpg")
                .into(baslikresmi);**/

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateandTime = format.format(new Date());
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(saatler[position]);
            d2 = format.parse(currentDateandTime);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24-10;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");
            saat.setText(diffDays+" gün "+diffHours+" saat "+diffMinutes+" dakika");

        } catch (Exception e) {
        Log.e("zamanlamadan hata: ",e.toString());
        }
        Log.e("Normal: "+currentDateandTime," aslında olan: "+saatler[position]);
        return itemView;
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11=BitmapFactory.decodeStream(in);
                //mIcon11=getResizedBitmap(BitmapFactory.decodeStream(in),175,175);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
        public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                    matrix, false);
            return resizedBitmap;
        }
    }

    public class DownloadImage2 extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImage2(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11=BitmapFactory.decodeStream(in);
                //mIcon11=getResizedBitmap(BitmapFactory.decodeStream(in),125,125);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
        public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                    matrix, false);
            return resizedBitmap;
        }
    }


}