package twitter.ogu.com.esogumobilforum;

/**
 * HACKED by HEYKIReGo on 10/11/16.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class AnaFKonuAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    String[] yorum;
    String[] url;
    String[] date;
    String[] name;

    LayoutInflater inflater;

    //ListviewAdapter constructor
    //Gelen değerleri set ediyor
    public AnaFKonuAdapter(Context context, String[] yorum, String[] url,String[] date,String[] name) {
        this.context = context;
        this.yorum=yorum;
        this.url=url;
        this.date=date;
        this.name=name;
    }

    @Override
    public int getCount() {
        return yorum.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Declare Variables
        TextView yorumyorum;
        ImageView yorumresim;
        TextView yorumisim;
        TextView yorumdate;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.list_item_row_icerde, parent, false);//list_item_row dan yeni bir view oluşturuyoruz

        // oluşan itemviewin içindeki alanları Anasayfadan gelen değerler ile set ediyoruz
        yorumisim=(TextView)itemView.findViewById(R.id.textView4);
        yorumdate=(TextView)itemView.findViewById(R.id.textView5);
        yorumresim=(ImageView)itemView.findViewById(R.id.yorumfoto);
        yorumyorum = (TextView) itemView.findViewById(R.id.yourum);
        yorumyorum.setText(yorum[position]);
        yorumisim.setText(name[position]);
        yorumdate.setText(date[position]);
        Picasso.with(context)
                .load(url[position])
                .into(yorumresim);
        return itemView;
    }

}