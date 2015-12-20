package builder.trendymovies.Utils;

import android.util.Log;

/**
 * Created by Shabaz on 19-Dec-15.
 */
public class BuilderLogger
{
    String defTag;
    public BuilderLogger(String tag)
    {
        this.defTag = tag;
    }
    public void d(String msg)
    {
        Log.d(defTag,msg);
    }
    public void e(String msg)
    {
        Log.d(defTag,msg);
    }
    public void i(String msg)
    {
        Log.d(defTag,msg);
    }
    public void d(String customTag,String msg)
    {
        Log.d(customTag,msg);
    }
    public void e(String customTag,String msg)
    {
        Log.d(customTag,msg);
    }
    public void i(String customTag,String msg)
    {
        Log.d(customTag,msg);
    }
}
