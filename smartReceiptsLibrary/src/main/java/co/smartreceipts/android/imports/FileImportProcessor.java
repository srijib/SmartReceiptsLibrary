package co.smartreceipts.android.imports;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;

import rx.Observable;

public interface FileImportProcessor {

    /**
     * Processes a uri to get an Android file
     * @param uri the desired {@link Uri}
     * @return the resultant {@link File}
     */
    @NonNull
    Observable<File> process(@NonNull Uri uri);
}
