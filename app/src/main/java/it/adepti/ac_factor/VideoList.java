package it.adepti.ac_factor;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class VideoList extends ArrayAdapter<VideoItem> {

    private int resource;
    private LayoutInflater inflater;
    private Fragment fragment;

    public VideoList(Context context, int resource, List<VideoItem> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoItem videoItem = getItem(position);

        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.textVideoName = (TextView)convertView.findViewById(R.id.textVideo);
            //holder.likeView = (LikeView)convertView.findViewById(R.id.likeButton);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.textVideoName.setText(videoItem.getVideoName());

        /* Future Implementations
        holder.likeView.setLikeViewStyle(LikeView.Style.BUTTON);
        holder.likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);
        holder.likeView.setHorizontalAlignment(LikeView.HorizontalAlignment.LEFT);
        holder.likeView.setObjectIdAndType(videoItem.getLikeLink(), LikeView.ObjectType.OPEN_GRAPH);
        holder.likeView.setFragment(fragment);
        */

        return convertView;
    }

    private static class ViewHolder{
        TextView textVideoName;
        //LikeView likeView;
    }
}
