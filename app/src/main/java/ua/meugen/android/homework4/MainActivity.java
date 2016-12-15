package ua.meugen.android.homework4;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final Handler handler = new Handler();
    private final ResultReceiver receiver = new ResultReceiver(handler) {

        @Override
        protected void onReceiveResult(final int resultCode, final Bundle resultData) {
            onResultReceived(resultData);
        }
    };

    private View navigationBar;
    private ImageView imageView;
    private ImageButton nextButton;
    private ImageButton playOrPauseButton;
    private ImageButton prevButton;
    private Button startButton;
    private ServiceConnectionImpl connection;

    private Bitmap bitmap;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imageView = (ImageView) findViewById(R.id.image);

        this.nextButton = (ImageButton) findViewById(R.id.next);
        this.nextButton.setOnClickListener(this);
        this.nextButton.setEnabled(false);

        this.prevButton = (ImageButton) findViewById(R.id.prev);
        this.prevButton.setOnClickListener(this);
        this.prevButton.setEnabled(false);

        this.playOrPauseButton = (ImageButton) findViewById(R.id.play_pause);
        this.playOrPauseButton.setOnClickListener(this);
        this.playOrPauseButton.setEnabled(false);

        this.startButton = (Button) findViewById(R.id.start);
        this.startButton.setOnClickListener(this);
        this.navigationBar = findViewById(R.id.navigation_bar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectToService();
    }

    private void setVisibilities(final int navigationVisibility, final int startVisibility) {
        this.navigationBar.setVisibility(navigationVisibility);
        this.startButton.setVisibility(startVisibility);
    }

    private void connectToService() {
        setVisibilities(View.GONE, View.GONE);

        final Intent intent = new Intent(this, ImagesService.class);
        startService(intent);
        connection = new ServiceConnectionImpl();
        bindService(intent, connection, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (connection != null) {
            unbindService(connection);
            connection = null;
        }

        imageView.setImageBitmap(null);
        if (this.bitmap != null) {
            this.bitmap.recycle();
        }
    }

    @Override
    public void onClick(final View view) {
        final int viewId = view.getId();
        if (viewId == R.id.prev) {
            prev();
        } else if (viewId == R.id.play_pause) {
            playOrPause();
        } else if (viewId == R.id.next) {
            next();
        } else if (viewId == R.id.start) {
            connectToService();
        }
    }

    private void prev() {
        connection.prev();
    }

    private void playOrPause() {
        connection.playOrPause();
    }

    private void next() {
        connection.next();
    }

    private void onResultReceived(final Bundle data) {
        final Uri uri = data.getParcelable(ImagesService.IMAGE_KEY);
        InputStream input = null;
        try {
            input = getContentResolver().openInputStream(uri);

            imageView.setImageBitmap(null);
            if (bitmap != null) {
                bitmap.recycle();
            }
            bitmap = BitmapFactory.decodeStream(input);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {}
            }
        }

        final boolean playing = data.getBoolean(ImagesService.PLAYING_KEY);
        if (playing) {
            nextButton.setEnabled(false);
            prevButton.setEnabled(false);
            playOrPauseButton.setEnabled(true);
            playOrPauseButton.setImageResource(R.drawable.ic_pause_black_24dp);
        } else {
            nextButton.setEnabled(true);
            prevButton.setEnabled(true);
            playOrPauseButton.setEnabled(true);
            playOrPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
    }

    private class ServiceConnectionImpl implements ServiceConnection {

        private IImagesService service;

        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
            this.service = IImagesService.Stub.asInterface(iBinder);
            try {
                this.service.attachReceiver(receiver);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            setVisibilities(View.VISIBLE, View.GONE);
        }

        @Override
        public void onServiceDisconnected(final ComponentName componentName) {
            setVisibilities(View.GONE, View.VISIBLE);

            this.service = null;
            connection = null;
        }

        public void next() {
            try {
                this.service.next();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        public void playOrPause() {
            try {
                this.service.playOrPause();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        public void prev() {
            try {
                this.service.prev();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
