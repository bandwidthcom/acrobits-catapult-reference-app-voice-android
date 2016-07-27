package com.bandwidth.androidreference;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import cz.acrobits.ali.Xml;
import cz.acrobits.libsoftphone.Instance;

public class RefApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        setUpAcrobits();
    }

    private void setUpAcrobits() {
        Instance.loadLibrary(getApplicationContext());

        Xml prov = new Xml("provisioning");
        Xml saas = new Xml("saas");
        saas.replaceChild("identifier", getString(R.string.acrobits_license_id));
        prov.replaceChild(saas);
        Instance.init(getApplicationContext(), prov);

        // Monitor activity lifecycle callbacks to update Acrobits' state.
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Instance.State.update(Instance.State.Background);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Instance.State.update(Instance.State.Inactive);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Instance.State.update(Instance.State.Active);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Instance.State.update(Instance.State.Inactive);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Instance.State.update(Instance.State.Background);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) { }

            @Override
            public void onActivityDestroyed(Activity activity) { }
        });
    }
}
