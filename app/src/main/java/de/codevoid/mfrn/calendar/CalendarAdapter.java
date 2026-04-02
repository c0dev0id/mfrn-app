package de.codevoid.mfrn.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import de.codevoid.mfrn.R;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(CalendarEvent event);
    }

    private final List<CalendarEvent> events;
    private final OnEventClickListener listener;

    public CalendarAdapter(List<CalendarEvent> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        CalendarEvent event = events.get(position);
        h.title.setText(event.title);
        h.date.setText(event.dateLabel);
        h.author.setText(event.author);
        h.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() { return events.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title, date, author;
        ViewHolder(View v) {
            super(v);
            title  = v.findViewById(R.id.tv_title);
            date   = v.findViewById(R.id.tv_date);
            author = v.findViewById(R.id.tv_author);
        }
    }
}
