package br.com.nubank2ynab.android;

import android.os.AsyncTask;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.com.nubank2ynab.core.CreateYNABTransactionFromNuBankNotification;
import br.com.nubank2ynab.core.PayeeToCategoryGateway;


public class NuBankNotificationListener extends NotificationListenerService {

    private static NuBankNotificationListener instance;
    private CreateYNABTransactionFromNuBankNotification usecase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        usecase = new CreateYNABTransactionFromNuBankNotification(
                new YNABGatewayHttp(HardcodedConfig.YNAB_API_TOKEN, HardcodedConfig.BUDGET_ID, HardcodedConfig.ACCOUNT_ID),
                new PayeeToCategoryGateway() {

                    @Override
                    public void put(@NotNull String payee, @NotNull String categoryId) {

                    }
                    @Nullable
                    @Override
                    public String get(@NotNull String payee) {
                        return HardcodedConfig.mappings.get(payee);
                    }
                });
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        String notificationText = String.valueOf(sbn.getNotification().extras.get("android.text"));
        new CreateYNABTransactionFromNuBankNotificationTask(usecase).execute(packageName, notificationText);
    }

    private static class CreateYNABTransactionFromNuBankNotificationTask extends AsyncTask<String, Integer, Void> {
        private CreateYNABTransactionFromNuBankNotification createYNABTransactionFromNuBankNotification;

        CreateYNABTransactionFromNuBankNotificationTask(CreateYNABTransactionFromNuBankNotification createYNABTransactionFromNuBankNotification) {
            this.createYNABTransactionFromNuBankNotification = createYNABTransactionFromNuBankNotification;
        }

        @Override
        protected Void doInBackground(String... objects) {
            this.createYNABTransactionFromNuBankNotification.create(objects[0], objects[1]);
            return null;
        }
    }

    public static NuBankNotificationListener instance() {
        return instance;
    }
}