package yio.tro.antiyoy;

import android.os.Handler;
import android.os.Looper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Clipboard;

import java.util.TimerTask;

public class GetAndroidClipboardContents extends TimerTask{

    private Handler handler = new Handler(Looper.getMainLooper());
    String result;
    boolean complete;


    public GetAndroidClipboardContents() {
        result = "None";
        complete = false;
    }


    @Override
    public void run() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Clipboard clipboard = Gdx.app.getClipboard();
                result = clipboard.getContents();
                complete = true;
                System.out.println("---------------------- 1");
            }
        });
    }


    public boolean isComplete() {
        return complete;
    }


    public String getResult() {
        return result;
    }
}
