package de.codevoid.mfrn.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.codevoid.mfrn.App;
import de.codevoid.mfrn.R;

public class CalendarFragment extends Fragment {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = view.findViewById(R.id.recycler);
        ProgressBar progress = view.findViewById(R.id.progress);
        TextView tvError = view.findViewById(R.id.tv_error);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        App app = (App) requireActivity().getApplication();
        CalendarRepository repo = new CalendarRepository(app.getClient());

        executor.execute(() -> {
            try {
                List<CalendarEvent> events = repo.getUpcomingEvents();
                requireActivity().runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    rv.setAdapter(new CalendarAdapter(events, event -> {
                        Intent intent = new Intent(requireContext(), EventDetailActivity.class);
                        intent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, event.id);
                        intent.putExtra(EventDetailActivity.EXTRA_EVENT_URL, event.url);
                        intent.putExtra(EventDetailActivity.EXTRA_EVENT_TITLE, event.title);
                        startActivity(intent);
                    }));
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(e.getMessage());
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown();
    }
}
