package ua.meugen.android.homework4;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;

import java.util.Timer;
import java.util.TimerTask;

public class ImagesService extends Service {

    public static final String IMAGE_KEY = "image";
    public static final String PLAYING_KEY = "playing";

    private static final String[] IMAGES = {
            "image1.png", "image2.png", "image3.png", "image4.png" };
    private static final long PERIOD = 5_000L;

    private ResultReceiver receiver;
    private int activeImage = 0;
    private boolean playing = false;

    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        this.receiver = null;
        return super.onUnbind(intent);
    }

    private void nextImage() {
        if (++activeImage >= IMAGES.length) {
            activeImage = 0;
        }
        sendToReceiver();
    }

    private void prevImage() {
        if (--activeImage < 0) {
            activeImage = IMAGES.length - 1;
        }
        sendToReceiver();
    }

    private void sendToReceiver() {
        if (receiver == null) {
            return;
        }

        final Bundle bundle = new Bundle();
        bundle.putParcelable(IMAGE_KEY, Uri.parse("file:///android_asset/" + IMAGES[activeImage]));
        bundle.putBoolean(PLAYING_KEY, playing);
        receiver.send(0, bundle);
    }

    private void doPlayOrPause() {
        if (playing) {
            playing = false;

            timer.cancel();
            timer = null;
        } else {
            playing = true;

            timer = new Timer();
            timer.schedule(new NextTask(), PERIOD, PERIOD);
        }
    }

    private class Binder extends IImagesService.Stub {

        @Override
        public void attachReceiver(final ResultReceiver receiver) throws RemoteException {
            ImagesService.this.receiver = receiver;
            sendToReceiver();
        }

        @Override
        public void next() throws RemoteException {
            nextImage();
        }

        @Override
        public void playOrPause() throws RemoteException {
            doPlayOrPause();
        }

        @Override
        public void prev() throws RemoteException {
            prevImage();
        }
    }

    private class NextTask extends TimerTask {

        @Override
        public void run() {
            nextImage();
        }
    }
}
