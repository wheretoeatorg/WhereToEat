
package com.wheretoeat.activities;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.widget.ImageButton;

import com.activeandroid.app.Application;

@RunWith(RobolectricTestRunner.class)
public class MyActivityTest extends Application {

    private DetailsActivity detailsActivity;
    private ImageButton imgBtnGetDirection;

    @Before
    public void setUp() throws Exception {
        detailsActivity = Robolectric.buildActivity(DetailsActivity.class).create().visible().get();
        imgBtnGetDirection = (ImageButton) detailsActivity.findViewById(R.id.imgBtnGetDirection);
    }

    // Sanity check for map button not null;
    @Test
    public void ShouldHaveImageButton() throws Exception {
        assertThat(imgBtnGetDirection, notNullValue());
    }

    // Validate intent if fired with correct value on click of direction button;

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldHaveHappySmiles() throws Exception {

    }

}
