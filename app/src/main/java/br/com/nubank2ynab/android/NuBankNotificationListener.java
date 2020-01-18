package br.com.nubank2ynab.android;

import android.os.AsyncTask;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.com.nubank2ynab.core.ConfigurationGateway;
import br.com.nubank2ynab.core.ConfigurationGatewayInMemory;
import br.com.nubank2ynab.core.CreateYNABTransactionFromNuBankNotification;
import br.com.nubank2ynab.core.PayeeToCategoryGateway;


public class NuBankNotificationListener extends NotificationListenerService {

    private static NuBankNotificationListener instance;
    private CreateYNABTransactionFromNuBankNotification usecase;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ConfigurationGateway configurationGateway = new ConfigurationGatewayInMemory();
        configurationGateway.put("NuBankAccountID", HardcodedConfig.NUBANK_ACCOUNT_ID);
        configurationGateway.put("NuContaAccountID", HardcodedConfig.NUCONTA_ACCOUNT_ID);

        usecase = new CreateYNABTransactionFromNuBankNotification(
                new YNABGatewayHttp(HardcodedConfig.YNAB_API_TOKEN, HardcodedConfig.BUDGET_ID),
                new PayeeToCategoryGateway() {

                    @Override
                    public void put(@NotNull String payee, @NotNull String categoryId) {

                    }
                    @Nullable
                    @Override
                    public String get(@NotNull String payee) {
                        return HardcodedConfig.mappings.get(payee);
                    }
                }, configurationGateway);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        String notificationText = String.valueOf(sbn.getNotification().extras.get("android.text"));
        new Task(usecase).execute(packageName, notificationText);
    }

    private static class Task extends AsyncTask<String, Integer, Void> {
        private CreateYNABTransactionFromNuBankNotification usecase;

        Task(CreateYNABTransactionFromNuBankNotification usecase) {
            this.usecase = usecase;
        }

        @Override
        protected Void doInBackground(String... objects) {
            this.usecase.create(objects[0], objects[1]);
            return null;
        }
    }

    public static NuBankNotificationListener instance() {
        return instance;
    }
}