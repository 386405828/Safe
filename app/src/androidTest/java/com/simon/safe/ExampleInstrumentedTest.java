package com.simon.safe;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.simon.safe.db.dao.BlackNumberDao;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.simon.safe", appContext.getPackageName());
        insert(appContext);
    }

    public void insert(Context appContext) {
        BlackNumberDao dao = BlackNumberDao.getInstance(appContext);
        dao.insert("110", "1");
    }
}
