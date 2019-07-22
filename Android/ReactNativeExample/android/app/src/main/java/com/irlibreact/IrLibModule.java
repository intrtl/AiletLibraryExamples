package com.irlibreact;

import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.intelligenceretail.www.lib.IntRtl;

import org.json.JSONObject;


public class IrLibModule extends ReactContextBaseJavaModule {

    public IrLibModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override    
    public String getName() {
        return "IrModule";
    }
    
    @ReactMethod
    public IntRtl.Results start(String user_name,
                                String password,
                                String broadcast,
                                String store_id,
                                String visit_id) {
        IntRtl ir = new IntRtl(getReactApplicationContext());
        IntRtl.Results res = ir.init(user_name, password, broadcast);
        if (res == IntRtl.Results.RESULT_OK) {
            res = ir.start(store_id, visit_id);
        }

        return res;
    }

    @ReactMethod
    public IntRtl.Results showSummaryReport(String external_visit_id) {
        IntRtl ir = new IntRtl(getReactApplicationContext());
        return ir.showSummaryReport(external_visit_id);
    }

    @ReactMethod
    public JSONObject reports(String external_visit_id) {
        IntRtl ir = new IntRtl(getReactApplicationContext());
        return ir.reports(external_visit_id);
    }

    public IntRtl.IrLastVisit getLastVisit(String external_store_id){
        IntRtl ir = new IntRtl(getReactApplicationContext());
        return ir.getLastVisit(external_store_id);
    }

    public String getVersion() {
        IntRtl ir = new IntRtl(getReactApplicationContext());
        return ir.getVersion();
    }

    public IntRtl.Results syncData() {
        IntRtl ir = new IntRtl(getReactApplicationContext());
        return ir.syncData();
    }
}
