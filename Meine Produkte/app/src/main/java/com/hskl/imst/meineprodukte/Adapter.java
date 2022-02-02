package com.hskl.imst.meineprodukte;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {

    private LayoutInflater inflater;
    private List<Produkt> produkte;
    private List<Produkt> notesFull;

    Adapter(Context context, List<Produkt> produkte) {
        this.inflater = LayoutInflater.from(context);
        this.produkte = produkte;
        notesFull = new ArrayList<>(produkte);
    }


    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.custom_list_view,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String title = produkte.get(i).getTitle();
        String verfallsdatum = produkte.get(i).getVerfallsdatum();

        viewHolder.nTitle.setText(title);
        viewHolder.verfallsdatum.setText(verfallsdatum);
        byte[] noteImage = produkte.get(i).getImage();
        if (noteImage != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(noteImage, 0, noteImage.length);
            viewHolder.noteImage.setImageBitmap(bitmap);
        } else {
            viewHolder.noteImage.setImageResource(R.drawable.imagetobbar);
        }
    }

    @Override
    public int getItemCount() {
        return produkte.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering (CharSequence constraint) {
            List<Produkt> filteredList = new ArrayList<>(); //nur gefilterte items

            if (constraint == null || constraint.length () == 0) {
                filteredList.addAll(notesFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();

               for (Produkt item: notesFull)  {
                   if (item.getTitle().toLowerCase().contains(filterPattern)) {
                       filteredList.add(item);
                   }
               }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            produkte.clear();
            produkte.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nTitle, verfallsdatum;
        ImageView noteImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteImage = itemView.findViewById(R.id.noteImage);
            nTitle = itemView.findViewById(R.id.nTitle);
            verfallsdatum = itemView.findViewById(R.id.startVerfallsdatum);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(),Details.class);
                    i.putExtra("ID", produkte.get(getAdapterPosition()).getID());
                    view.getContext().startActivity(i);
                }
            });
        }
    }
}
