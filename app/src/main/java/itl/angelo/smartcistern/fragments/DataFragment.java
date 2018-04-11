package itl.angelo.smartcistern.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.adapters.DataAdapter;
import itl.angelo.smartcistern.adapters.CardData;
import itl.angelo.smartcistern.util.connection.GlobalData;
import itl.angelo.smartcistern.util.volumecomputer.VolumeCalculator;

import static itl.angelo.smartcistern.util.volumecomputer.VolumeCalculator.CENTIMETERS_BY_SECOND_LABEL;

/**
 * Created by Angelo on 06/04/2017.
 * In this class we pretend extend a fragment and a recycler view for showing
 * all data about cistern as volume total, average of volume in one hour, in
 * one day and mSpeed of emptying, average of level (percentage), times the
 * container has been filled and emptied in one day,
 */

public class DataFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private DataAdapter mAdapter;
    private View rootView;
    private VolumeCalculator mVolume;
    private FloatingActionButton mRefreshButton;
    private DecimalFormat mDecimalFormat;
    private DateFormat mDateFormat;
    private static final String DATE_FORMAT = "mm:ss";
    private Date mDumpDuration;
    private Date mFillingDuration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_data, container, false);


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.data_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mVolume = new VolumeCalculator(getActivity());
        mRefreshButton = (FloatingActionButton) rootView.findViewById(R.id.refresh_button);
        mRefreshButton.setOnClickListener(this);
        mDecimalFormat = new DecimalFormat(MonitorFragment.DECIMAL_FORMAT);
        mDateFormat = new SimpleDateFormat(DATE_FORMAT);
        updateCard();
        return rootView;
    }

    public void updateCard() {
        ArrayList data = new ArrayList<CardData>();
        mDumpDuration = new Date(GlobalData.getInstance().getDumpTime());
        mFillingDuration = new Date(GlobalData.getInstance().getFillTime());
        data.add(new CardData(
                getString(R.string.volume),
                getString(R.string.volume_total),
                mDecimalFormat.format(
                        mVolume.getVolumeTotal()) +
                        VolumeCalculator.MILILITERS_LABEL,
                getString(R.string.volume_average),
                mDecimalFormat.format(
                        GlobalData.getInstance().getVolumeAverage()) +
                        VolumeCalculator.MILILITERS_LABEL
        ));
        data.add(new CardData(
                getString(R.string.speed),
                getString(R.string.dump_speed),
                mDecimalFormat.format(GlobalData.getInstance().getDumpSpeed()) +
                        CENTIMETERS_BY_SECOND_LABEL,
                getString(R.string.filling_speed),
                mDecimalFormat.format(GlobalData.getInstance().getFillingSpeed()) +
                        CENTIMETERS_BY_SECOND_LABEL
        ));
        data.add(new CardData(
                getString(R.string.dump_and_filling_speed),
                getString(R.string.dump_times),
                GlobalData.getInstance().getDumpTimes() + " " + getString(R.string.times),
                getString(R.string.filling_times),
                GlobalData.getInstance().getFillTimes() + " " + getString(R.string.times)
        ));
        data.add(new CardData(
                getString(R.string.estimated_time),
                getString(R.string.dump_time),
                mDateFormat.format(mDumpDuration),
                getString(R.string.filling_time),
                mDateFormat.format(mFillingDuration)
        ));
        mAdapter = new DataAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        updateCard();
    }

}
