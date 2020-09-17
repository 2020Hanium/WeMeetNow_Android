package hanium.android.wemeetnow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hanium.android.wemeetnow.R;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private List<String> mList;

    public FriendListAdapter(List<String> list) {
        this.mList = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_friendname);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = mList.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
