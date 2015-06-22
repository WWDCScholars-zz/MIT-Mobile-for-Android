package edu.mit.mitmobile2.events.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.Locale;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.activities.CalendarsActivity;
import edu.mit.mitmobile2.events.adapters.CalendarDayPagerAdapter;
import edu.mit.mitmobile2.events.adapters.CalendarWeekPagerAdapter;
import edu.mit.mitmobile2.events.model.MITCalendar;
import edu.mit.mitmobile2.news.fragments.SearchFragment;

public class EventsFragment extends Fragment {

    public static final String TAG = "EventsFragment";

    public static final long WEEK_OFFSET = 604800000;
    public static final long DAY_OFFSET = 86400000;

    private TextView dateTextView;
    private CalendarWeekPagerAdapter weekPagerAdapter;
    private CalendarDayPagerAdapter dayPagerAdapter;
    private ViewPager calendarDayViewPager;
    private ViewPager calendarWeekViewPager;
    private Menu menu;

    private boolean triggeredFromTopViewPager = false;
    private boolean triggeredFromBottomViewPager = false;

    private MITCalendar filterCategory;

    public EventsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_events, null);

        dateTextView = (TextView) view.findViewById(R.id.date_text);

        calendarWeekViewPager = (ViewPager) view.findViewById(R.id.calendar_viewpager);
        buildCalendarWeekPager(Calendar.getInstance(Locale.US));

        dateTextView.setText(DateFormat.format("EEEE, MMMM d, yyyy", Calendar.getInstance().getTime()));

        calendarDayViewPager = (ViewPager) view.findViewById(R.id.events_viewpager);
        buildCalendarDayPager();

        TextView todayButton = (TextView) view.findViewById(R.id.today_button);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weekPagerAdapter.isCenteredAroundToday()) {
                    int newPositionWeek = CalendarWeekPagerAdapter.SIZE / 2;
                    triggeredFromBottomViewPager = true;
                    triggeredFromTopViewPager = true;

                    calendarWeekViewPager.setCurrentItem(newPositionWeek);
                    weekPagerAdapter.update(weekPagerAdapter.getStartPointOffsetInWeek());
                    weekPagerAdapter.setFragmentPosition(newPositionWeek);

                    int newPositionDay = CalendarDayPagerAdapter.SIZE / 2;
                    dayPagerAdapter.setLastSeenPosition(newPositionDay);
                    calendarDayViewPager.setCurrentItem(newPositionDay);
                    dateTextView.setText(weekPagerAdapter.getDate());
                } else {
                    buildCalendarWeekPager(Calendar.getInstance());
                    buildCalendarDayPager();
                    dateTextView.setText(DateFormat.format("EEEE, MMMM d, yyyy", Calendar.getInstance()));
                }
            }
        });

        TextView calendarButton = (TextView) view.findViewById(R.id.calendars_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalendarsActivity.class);
                startActivityForResult(intent, CalendarsActivity.REQUEST_CODE_SELECT_EVENTS_CATEGORY);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CalendarsActivity.REQUEST_CODE_SELECT_EVENTS_CATEGORY: {
                if (resultCode == Activity.RESULT_OK && data.hasExtra(Constants.Events.CALENDAR)) {
                    filterCategory = data.getParcelableExtra(Constants.Events.CALENDAR);
                }
            }
        }
    }

    private void goToDate() {
        Calendar today = Calendar.getInstance(Locale.US);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance(Locale.US);
                newDate.set(year, monthOfYear, dayOfMonth);
                buildCalendarWeekPager(newDate);
                buildCalendarDayPager();
                dateTextView.setText(DateFormat.format("EEEE, MMMM d, yyyy", newDate.getTime()));
            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        dialog.setInverseBackgroundForced(true);
        dialog.show();
    }

    private void buildCalendarDayPager() {
        dayPagerAdapter = new CalendarDayPagerAdapter(getFragmentManager());
        dayPagerAdapter.setWeekPagerAdapterReference(weekPagerAdapter);
        calendarDayViewPager.setAdapter(dayPagerAdapter);

        int position = CalendarDayPagerAdapter.SIZE / 2;
        calendarDayViewPager.setCurrentItem(position);
        weekPagerAdapter.setFragmentPosition(position);

        calendarDayViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!triggeredFromTopViewPager) {
                    int weekPagerIncrementAmount = weekPagerAdapter.updateIncremental(position - dayPagerAdapter.getLastSeenPosition());
                    dayPagerAdapter.setLastSeenPosition(position);
                    int newPosition = weekPagerAdapter.getFragmentPosition() + weekPagerIncrementAmount;
                    if (newPosition != 0) {
                        triggeredFromBottomViewPager = true;
                        calendarWeekViewPager.setCurrentItem(newPosition);
                        weekPagerAdapter.setFragmentPosition(newPosition);
                    }
                    String date = weekPagerAdapter.getDate();
                    dateTextView.setText(date);
                } else {
                    triggeredFromTopViewPager = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void buildCalendarWeekPager(Calendar c) {
        weekPagerAdapter = new CalendarWeekPagerAdapter(getFragmentManager(), c);
        calendarWeekViewPager.setAdapter(weekPagerAdapter);
        calendarWeekViewPager.setCurrentItem(CalendarWeekPagerAdapter.SIZE / 2); // put it in the middle

        calendarWeekViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!triggeredFromBottomViewPager) {
                    int dayOffset = weekPagerAdapter.setFragmentPosition(position);
                    if (dayOffset != 0) {
                        int newDayPosition = dayPagerAdapter.getLastSeenPosition() + (dayOffset * 7);
                        triggeredFromTopViewPager = true;
                        dayPagerAdapter.setLastSeenPosition(newDayPosition);
                        calendarDayViewPager.setCurrentItem(newDayPosition);
                    }
                    String date = weekPagerAdapter.getDate();
                    if (date != null) {
                        dateTextView.setText(date);
                    }
                } else {
                    triggeredFromBottomViewPager = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING && triggeredFromBottomViewPager) {
                    triggeredFromBottomViewPager = false;
                }
            }
        });
    }

    @Subscribe
    public void dateHasChanged(OttoBusEvent.ChangeDateTextEvent event) {
        String date = event.getDateText();
        dateTextView.setText(date);

        int dayOffset = weekPagerAdapter.update(event.getPosition());
        if (dayOffset != 0) {
            triggeredFromTopViewPager = true;
            int newDayPosition = dayPagerAdapter.getLastSeenPosition() + dayOffset;
            dayPagerAdapter.setLastSeenPosition(newDayPosition);
            calendarDayViewPager.setCurrentItem(newDayPosition);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_events, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setQueryHint(getString(R.string.search_hint));

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                SearchEventsFragment searchFragment = SearchEventsFragment.newInstance(filterCategory);
                getFragmentManager().beginTransaction().replace(R.id.content_frame, searchFragment).commit();
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });

        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.go_to_date:
                goToDate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        MitMobileApplication.bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        MitMobileApplication.bus.register(this);
    }

    @Override
    public void onDestroy() {
        if (menu != null) {
            menu.removeItem(R.id.search);
            menu.clear();
        }
        super.onDestroy();
    }
}
