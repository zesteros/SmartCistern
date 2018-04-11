package itl.angelo.smartcistern.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.util.connection.GlobalData;

/**
 * Created by Angelo on 29/04/2017.
 */

public class ManualModeFragment extends Fragment implements View.OnClickListener {
    private ImageView mSwitch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manual_mode, container, false);
        mSwitch = (ImageView) rootView.findViewById(R.id.pump_switch);
        mSwitch.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(20);
        if (GlobalData.getInstance().isWaterPumpOn()) {
            mSwitch.setImageResource(R.drawable.ic_switch_off);
            GlobalData.getInstance().isWaterPumpOn(false);
        } else {
            mSwitch.setImageResource(R.drawable.ic_switch_on);
            GlobalData.getInstance().isWaterPumpOn(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GlobalData.getInstance().isWaterPumpOn())
            mSwitch.setImageResource(R.drawable.ic_switch_on);
        else mSwitch.setImageResource(R.drawable.ic_switch_off);
    }
}
