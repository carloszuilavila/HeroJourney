package dev.carloszuil.herojourney;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SystemTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void test_habits_journey() throws InterruptedException{
        onView(withId(R.id.buttonAddHabit)).perform(click());
        onView(withId(R.id.inputHabitName)).perform(replaceText("Habit 1"));
        onView(withId(R.id.inputHabitDesc)).perform(replaceText("Description 1"));
        onView(withId(R.id.buttonSave_habit)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.buttonAddHabit)).perform(click());
        onView(withId(R.id.inputHabitName)).perform(replaceText("Habit 2"));
        onView(withId(R.id.inputHabitDesc)).perform(replaceText("Description 2"));
        onView(withId(R.id.buttonSave_habit)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.buttonAddHabit)).perform(click());
        onView(withId(R.id.inputHabitName)).perform(replaceText("Habit 3"));
        onView(withId(R.id.inputHabitDesc)).perform(replaceText("Description 3"));
        onView(withId(R.id.buttonSave_habit)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.recyclerHabits))
                .perform(actionOnItem(
                        hasDescendant(withText("Habit 1")),
                        clickChildViewWithId(R.id.habitCheckbox)
                ));
        Thread.sleep(300);

        onView(withId(R.id.recyclerHabits))
                .perform(actionOnItem(
                        hasDescendant(withText("Habit 2")),
                        clickChildViewWithId(R.id.habitCheckbox)
                ));
        Thread.sleep(300);

        onView(withId(R.id.recyclerHabits))
                .perform(actionOnItem(
                        hasDescendant(withText("Habit 3")),
                        clickChildViewWithId(R.id.habitCheckbox)
                ));
        Thread.sleep(500); // dejar que SharedViewModel dispare enViaje

        onView(withId(R.id.journeyFragment)).perform(click());
        Thread.sleep(500);

        onView(withId(R.id.imageSwitcher)).check(matches(isDisplayed()));
        onView(withId(R.id.text_timer)).check(matches(isDisplayed()));

        // Esperar 1 segundo para ver que el timer no sea “00:00”
        Thread.sleep(1200);
        onView(withId(R.id.text_timer)).check(matches(not(withText("00:00"))));
    }

    public static ViewAction clickChildViewWithId(final int id){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View child = view.findViewById(id);
                if (child != null && child.isClickable()) {
                    child.performClick();
                }
            }
        };
    }
}
