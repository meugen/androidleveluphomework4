package ua.meugen.android.homework4;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by meugen on 12.12.16.
 */

public class AssetsProvider extends ContentProvider {

    private static final String TAG = AssetsProvider.class.getSimpleName();

    @Nullable
    @Override
    public AssetFileDescriptor openAssetFile(final Uri uri, final String mode) throws FileNotFoundException {
        AssetFileDescriptor descriptor = null;
        try {
            descriptor = getContext().getAssets().openFd(uri.getLastPathSegment());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return descriptor;
    }

    @Override
    public int delete(final Uri uri, final String s, final String[] strings) {
        return 0;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(final Uri uri, final String[] strings, final String s, final String[] strings1, final String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(final Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(final Uri uri, final ContentValues contentValues) {
        return null;
    }

    @Override
    public int update(final Uri uri, final ContentValues contentValues, final String s, final String[] strings) {
        return 0;
    }
}
