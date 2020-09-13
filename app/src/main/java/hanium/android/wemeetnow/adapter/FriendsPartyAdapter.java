package hanium.android.wemeetnow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hanium.android.wemeetnow.R;

public class FriendsPartyAdapter extends RecyclerView.Adapter<FriendsPartyAdapter.ViewHolder> {

    private List<String> mList;

    public FriendsPartyAdapter(List<String> list, OnSelectListener onSelectListener) {
        this.mList = list;
        this.mOnSelectListener = onSelectListener;
    }

    private OnSelectListener mOnSelectListener;

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_friendname);
            checkBox = itemView.findViewById(R.id.cb_friend);
        }
    }

    @NonNull
    @Override
    public FriendsPartyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendsPartyAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends_party, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsPartyAdapter.ViewHolder holder, int position) {
        String item = mList.get(position);
        holder.textView.setText(item);
        holder.checkBox.setOnClickListener(view -> {
            if (holder.checkBox.isChecked()) {
                mOnSelectListener.onSelect(item, true);
            }
            else {
                mOnSelectListener.onSelect(item, false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnSelectListener{
        void onSelect(String name, boolean add);
    }

}
