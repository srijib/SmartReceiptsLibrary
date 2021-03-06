package co.smartreceipts.android.sync;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import java.io.File;
import java.sql.Date;
import java.util.List;

import co.smartreceipts.android.sync.errors.CriticalSyncError;
import co.smartreceipts.android.sync.errors.SyncErrorType;
import co.smartreceipts.android.sync.model.RemoteBackupMetadata;
import co.smartreceipts.android.sync.model.impl.Identifier;
import co.smartreceipts.android.sync.network.NetworkStateChangeListener;
import rx.Observable;

/**
 * A top level interface to track the core behaviors that are shared by all automatic backup providers
 */
public interface BackupProvider {

    /**
     * Initialize the backup provider
     *
     * @param activity the current {@link FragmentActivity} if one is required for connection error resolutions
     */
    void initialize(@Nullable FragmentActivity activity);

    /**
     * De-initialize the backup provider to stop it from being used
     */
    void deinitialize();

    /**
     * Passes an activity result along to this provider for processing if required
     *
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data any intent data
     * @return {@code true} if we handle the request, {@code false} otherwse
     */
    boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    /**
     * @return an {@link Observable} containing all of our remote backups
     */
    @NonNull
    Observable<List<RemoteBackupMetadata>> getRemoteBackups();

    /**
     * @return the sync {@link Identifier} for the current device or {@code null} if none is defined
     */
    @Nullable
    Identifier getDeviceSyncId();

    /**
     * @return the date for the last time our database was synced
     */
    @NonNull
    Date getLastDatabaseSyncTime();

    /**
     * Restores an existing backup
     *
     * @param remoteBackupMetadata the metadata to restore
     * @param overwriteExistingData if we should overwrite the existing data
     * @return an {@link Observable} for the restore operation with a success boolean
     */
    @NonNull
    Observable<Boolean> restoreBackup(@NonNull RemoteBackupMetadata remoteBackupMetadata, boolean overwriteExistingData);

    /**
     * Deletes an existing backup
     *
     * @param remoteBackupMetadata the metadata to delete
     * @return an {@link Observable} for the delete operation with a success boolean
     */
    @NonNull
    Observable<Boolean> deleteBackup(@NonNull RemoteBackupMetadata remoteBackupMetadata);


    /**
     * Attempts to clear out the current backup configuration
     *
     * @return an {@link Observable} for the delete operation with a success boolean
     */
    Observable<Boolean> clearCurrentBackupConfiguration();

    /**
     * Downloads an existing backup to a specific location
     *
     * @param remoteBackupMetadata the metadata to download
     * @param downloadLocation the {@link File} location to download it to
     * @return an {@link Observable} that contains the downloaded images
     */
    @NonNull
    Observable<List<File>> downloadAllData(@NonNull final RemoteBackupMetadata remoteBackupMetadata, @NonNull final File downloadLocation);

    /**
     * Downloads an existing backup to a specific location in a debug friendly manner
     *
     * @param remoteBackupMetadata the metadata to download
     * @param downloadLocation the {@link File} location to download it to
     * @return an {@link Observable} that contains the downloaded images
     */
    @NonNull
    Observable<List<File>> debugDownloadAllData(@NonNull final RemoteBackupMetadata remoteBackupMetadata, @NonNull final File downloadLocation);

    /**
     * @return an {@link Observable} that emits {@link CriticalSyncError} instances whenever they occur,
     * allowing us to respond as appropriately for these items
     */
    @NonNull
    Observable<CriticalSyncError> getCriticalSyncErrorStream();

    /**
     * Call this method to mark this particular error as resolve
     *
     * @param syncErrorType the {@link SyncErrorType} to mark as resolved
     */
    void markErrorResolved(@NonNull SyncErrorType syncErrorType);


}
