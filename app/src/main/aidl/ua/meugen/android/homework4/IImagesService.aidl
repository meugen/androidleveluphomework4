// IImagesService.aidl
package ua.meugen.android.homework4;

import android.os.ResultReceiver;

interface IImagesService {

    oneway void attachReceiver(in ResultReceiver receiver);

    oneway void next();

    oneway void playOrPause();

    oneway void prev();
}
