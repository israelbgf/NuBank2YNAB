package br.com.nubank2ynab.android;

import android.os.AsyncTask;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import br.com.nubank2ynab.core.CreateYNABTransactionFromNuBankNotification;


public class NuBankNotificationListener extends NotificationListenerService {

    private static NuBankNotificationListener instance;
    private CreateYNABTransactionFromNuBankNotification createYNABTransactionFromNuBankNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        createYNABTransactionFromNuBankNotification = new CreateYNABTransactionFromNuBankNotification(new YNABGatewayHttp(YNAB_API_TOKEN, BUDGET_ID, ACCOUNT_ID));
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        String notificationText = String.valueOf(sbn.getNotification().extras.get("android.text"));
        new CreateYNABTransactionFromNuBankNotificationTask(createYNABTransactionFromNuBankNotification).execute(packageName, notificationText);
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