package hanium.android.wemeetnow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.model.Place;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private List<Place> mList;

    public PlacesAdapter(List<Place> list) {
        this.mList = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name, tv_address;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address = itemView.findViewById(R.id.tv_address);
        }
    }

    @NonNull
    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlacesAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_places, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesAdapter.ViewHolder holder, int position) {
        Place item = mList.get(position);
        holder.tv_name.setText(item.name);
        holder.tv_address.setText(item.address);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
