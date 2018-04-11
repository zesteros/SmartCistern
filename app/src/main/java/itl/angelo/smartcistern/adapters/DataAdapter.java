package itl.angelo.smartcistern.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import itl.angelo.smartcistern.R;

public class DataAdapter extends RecyclerView
        .Adapter<DataAdapter.DataHolder>  {
    private ArrayList<CardData> mDataset;

    public static class DataHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView subtitle1;
        TextView subtitle2;
        TextView content1;
        TextView content2;

        public DataHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.data_title);
            subtitle1 = (TextView) itemView.findViewById(R.id.subtitle_content_1);
            content1 = (TextView) itemView.findViewById(R.id.data_content_1);
            subtitle2 = (TextView) itemView.findViewById(R.id.subtitle_content_2);
            content2 = (TextView) itemView.findViewById(R.id.data_content_2);
        }
    }

    public DataAdapter(ArrayList<CardData> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_data, parent, false);

        DataHolder dataObjectHolder = new DataHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {
        holder.title.setText(mDataset.get(position).getTitle());
        holder.subtitle1.setText(mDataset.get(position).getSubtitle1());
        holder.content1.setText(mDataset.get(position).getContent1());
        holder.subtitle2.setText(mDataset.get(position).getSubtitle2());
        holder.content2.setText(mDataset.get(position).getContent2());
    }

    public void addItem(CardData dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
